from http import HTTPStatus
from flask import request
from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import db
from flaskr.orm import CarModelORM

blp = Blueprint("Car Models", __name__, description="Operations on car models")

def get_car_model(model_id):
    model = db.session.get(CarModelORM, model_id)

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

@blp.route("/car-models/<string:model_id>")
class CarModelsId(MethodView):
    @blp.response(HTTPStatus.OK, CarModel)
    def get(self, model_id):
        return get_car_model(model_id)

    def delete(self, model_id):
        model = get_car_model(model_id)
        db.session.delete(model)
        db.session.commit()
        return {"message": "Car model deleted"}, HTTPStatus.NO_CONTENT

    @blp.arguments(CarModelChange)
    @blp.response(HTTPStatus.NO_CONTENT)
    def put(self, req_data, model_id):
        model = get_car_model(model_id)
        model.name = req_data["name"]
        model.year = req_data["year"]
        model.price = req_data["price"]
        model.make_id = req_data["make_id"]

        db.session.add(model)
        db.session.commit()


@blp.route("/car-models")
class CarModels(MethodView):
    @blp.response(HTTPStatus.OK, CarModelEntry(many=True))
    def get(self):
        return db.session.query(CarModelORM)

    @blp.arguments(CarModelChange)
    @blp.response(HTTPStatus.CREATED, CarModelEntry)
    def post(self, req_data):
        model = CarModelORM(**req_data)

        try:
            db.session.add(model)
            db.session.commit()
        except SQLAlchemyError:
            abort(HTTPStatus.INTERNAL_SERVER_ERROR, message="An error occurred while inserting the car model.")

        headers = {'location': request.base_url + "/" + str(model.id)}

        return model, headers
