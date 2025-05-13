from http import HTTPStatus
from flask import request
from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import db
from flaskr.orm import CarPartORM

blp = Blueprint("Car Parts", __name__, description="Operations on car parts")

def get_car_part(part_id):
    part = db.session.get(CarPartORM, part_id)

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


@blp.route("/car-parts/<string:part_id>")
class CarPartsWithId(MethodView):
    @blp.response(HTTPStatus.OK, CarPart)
    def get(self, part_id):
        return get_car_part(part_id)

    def delete(self, part_id):
        make = get_car_part(part_id)
        db.session.delete(make)
        db.session.commit()
        return {"message": "Car part deleted"}, HTTPStatus.NO_CONTENT

    @blp.arguments(CarPartChange)
    @blp.response(HTTPStatus.NO_CONTENT)
    def put(self, part_data, part_id):
        part = get_car_part(part_id)
        part.price = part_data["price"]
        part.name = part_data["name"]

        db.session.add(part)
        db.session.commit()


@blp.route("/car-parts")
class CarParts(MethodView):
    @blp.response(HTTPStatus.OK, CarPartEntry(many=True))
    def get(self):
        return db.session.query(CarPartORM)

    @blp.arguments(CarPartChange)
    @blp.response(HTTPStatus.CREATED, CarPartEntry)
    def post(self, part_data):
        part = CarPartORM(**part_data)

        try:
            db.session.add(part)
            db.session.commit()
        except SQLAlchemyError:
            abort(HTTPStatus.INTERNAL_SERVER_ERROR, message="An error occurred while inserting car part.")

        headers = {'location': request.base_url + "/" + str(part.id)}

        return part, headers
