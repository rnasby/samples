from http import HTTPStatus
from flask import request
from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import DB
from flaskr.orm import CarModelORM

BLP = Blueprint("Car Models", __name__, description="Operations on car models")

def get_car_model(model_id):
    model = DB.session.get(CarModelORM, model_id)

    if not model:
        abort(HTTPStatus.NOT_FOUND, message="Car model not found.")

    return model

class CarModelChange(Schema):
    name = fields.Str(required=True)
    year = fields.Int(required=True)
    price = fields.Float(required=True)
    make_id = fields.Int(required=True)

class CarModelEntry(CarModelChange):
    id = fields.Int(dump_only=True)

class CarModel(CarModelEntry):
    make = fields.Nested("CarMakeEntry", dump_only=True)
    parts = fields.List(fields.Nested("CarPart"), dump_only=True)

@BLP.route("/car-models/<string:model_id>")
class CarModelsId(MethodView):
    @BLP.response(HTTPStatus.OK, CarModel)
    def get(self, model_id):
        return get_car_model(model_id)

    def delete(self, model_id):
        model = get_car_model(model_id)
        DB.session.delete(model)
        DB.session.commit()
        return {"message": "Car model deleted"}, HTTPStatus.NO_CONTENT

    @BLP.arguments(CarModelChange)
    @BLP.response(HTTPStatus.NO_CONTENT)
    def put(self, req_data, model_id):
        model = get_car_model(model_id)
        model.name = req_data["name"]
        model.year = req_data["year"]
        model.price = req_data["price"]
        model.make_id = req_data["make_id"]

        DB.session.add(model)
        DB.session.commit()


@BLP.route("/car-models")
class CarModels(MethodView):
    @BLP.response(HTTPStatus.OK, CarModelEntry(many=True))
    def get(self):
        return DB.session.query(CarModelORM)

    @BLP.arguments(CarModelChange)
    @BLP.response(HTTPStatus.CREATED, CarModelEntry)
    def post(self, req_data):
        model = CarModelORM(**req_data)

        try:
            DB.session.add(model)
            DB.session.commit()
        except SQLAlchemyError:
            abort(HTTPStatus.INTERNAL_SERVER_ERROR, message="An error occurred while inserting the car model.")

        headers = {'location': request.base_url + "/" + str(model.id)}

        return model, headers
