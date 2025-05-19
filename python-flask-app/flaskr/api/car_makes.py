from http import HTTPStatus
from flask import request
from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from flask_jwt_extended import jwt_required
from sqlalchemy.exc import SQLAlchemyError, IntegrityError

from flaskr.db import DB
from flaskr.orm import CarMakeORM

BLP = Blueprint("Car Makes", __name__, description="Operations on car makes")

def get_car_make(make_id):
    make = DB.session.get(CarMakeORM, make_id)

    if not make:
        abort(HTTPStatus.NOT_FOUND, message="Make not found.")

    return make

class CarMakeChange(Schema):
    name = fields.Str(required=True)

class CarMakeEntry(CarMakeChange):
    id = fields.Int(dump_only=True)

class CarMake(CarMakeEntry):
    models = fields.List(fields.Nested("CarModel"), dump_only=True)

@BLP.route("/car-makes/<string:make_id>")
class CarMakesWithId(MethodView):
    @BLP.response(HTTPStatus.OK, CarMake)
    def get(self, make_id):
        return get_car_make(make_id)

    @jwt_required()
    def delete(self, make_id):
        make = get_car_make(make_id)
        DB.session.delete(make)
        DB.session.commit()
        return {"message": "Make deleted"}, HTTPStatus.NO_CONTENT

    @jwt_required()
    @BLP.arguments(CarMakeChange)
    @BLP.response(HTTPStatus.NO_CONTENT)
    def put(self, make_data, make_id):
        make = get_car_make(make_id)
        make.name = make_data["name"]

        DB.session.add(make)
        DB.session.commit()

@BLP.route("/car-makes")
class CarMakes(MethodView):
    @jwt_required()
    @BLP.arguments(CarMakeChange)
    @BLP.response(HTTPStatus.CREATED, CarMakeEntry)
    def post(self, make_data):
        make = CarMakeORM(**make_data)
        try:
            DB.session.add(make)
            DB.session.commit()
        except IntegrityError:
            abort(HTTPStatus.BAD_REQUEST, message="A make with that name already exists.")
        except SQLAlchemyError:
            abort(HTTPStatus.INTERNAL_SERVER_ERROR, message="An error occurred creating the make.")

        headers = {'location': request.base_url + "/" + str(make.id)}

        return make, headers

    @BLP.response(HTTPStatus.OK, CarMakeEntry(many=True))
    def get(self):
        return DB.session.query(CarMakeORM)
