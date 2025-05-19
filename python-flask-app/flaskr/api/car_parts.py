from http import HTTPStatus
from flask import request
from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from flask_jwt_extended import jwt_required
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import DB
from flaskr.orm import CarPartORM

BLP = Blueprint("Car Parts", __name__, description="Operations on car parts")

def get_car_part(part_id):
    part = DB.session.get(CarPartORM, part_id)

    if not part:
        abort(HTTPStatus.NOT_FOUND, message="Car part not found.")

    return part

class CarPartChange(Schema):
    name = fields.Str(required=True)
    price = fields.Float(required=True)

class CarPartEntry(CarPartChange):
    id = fields.Int(dump_only=True)

class CarPart(CarPartEntry):
    models = fields.List(fields.Nested("CarModelEntry"), dump_only=True)


@BLP.route("/car-parts/<string:part_id>")
class CarPartsWithId(MethodView):
    @BLP.response(HTTPStatus.OK, CarPart)
    def get(self, part_id):
        return get_car_part(part_id)

    @jwt_required()
    def delete(self, part_id):
        make = get_car_part(part_id)
        DB.session.delete(make)
        DB.session.commit()
        return {"message": "Car part deleted"}, HTTPStatus.NO_CONTENT

    @jwt_required()
    @BLP.arguments(CarPartChange)
    @BLP.response(HTTPStatus.NO_CONTENT)
    def put(self, part_data, part_id):
        part = get_car_part(part_id)
        part.price = part_data["price"]
        part.name = part_data["name"]

        DB.session.add(part)
        DB.session.commit()


@BLP.route("/car-parts")
class CarParts(MethodView):
    @BLP.response(HTTPStatus.OK, CarPartEntry(many=True))
    def get(self):
        return DB.session.query(CarPartORM)

    @jwt_required()
    @BLP.arguments(CarPartChange)
    @BLP.response(HTTPStatus.CREATED, CarPartEntry)
    def post(self, part_data):
        part = CarPartORM(**part_data)

        try:
            DB.session.add(part)
            DB.session.commit()
        except SQLAlchemyError:
            abort(HTTPStatus.INTERNAL_SERVER_ERROR, message="An error occurred while inserting car part.")

        headers = {'location': request.base_url + "/" + str(part.id)}

        return part, headers
