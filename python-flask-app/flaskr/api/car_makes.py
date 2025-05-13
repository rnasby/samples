from http import HTTPStatus
from flask import request
from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError, IntegrityError

from flaskr.db import db
from flaskr.orm import CarMakeORM

blp = Blueprint("Car Makes", __name__, description="Operations on car makes")

def get_car_make(make_id):
    make = db.session.get(CarMakeORM, make_id)

    if not make:
        abort(HTTPStatus.NOT_FOUND, message="Make not found.")

    return make

class CarMakeChange(Schema):
    name = fields.Str(required=True)

class CarMakeEntry(CarMakeChange):
    id = fields.Int(dump_only=True)

class CarMake(CarMakeEntry):
    models = fields.List(fields.Nested("CarModel"), dump_only=True)

@blp.route("/car-makes/<string:make_id>")
class CarMakesWithId(MethodView):
    @blp.response(HTTPStatus.OK, CarMake)
    def get(self, make_id):
        return get_car_make(make_id)

    def delete(self, make_id):
        make = get_car_make(make_id)
        db.session.delete(make)
        db.session.commit()
        return {"message": "Make deleted"}, HTTPStatus.NO_CONTENT

    @blp.arguments(CarMakeChange)
    @blp.response(HTTPStatus.NO_CONTENT)
    def put(self, make_data, make_id):
        make = get_car_make(make_id)
        make.name = make_data["name"]

        db.session.add(make)
        db.session.commit()

@blp.route("/car-makes")
class CarMakes(MethodView):
    @blp.response(HTTPStatus.OK, CarMakeEntry(many=True))
    def get(self):
        return db.session.query(CarMakeORM)

    @blp.arguments(CarMakeChange)
    @blp.response(HTTPStatus.CREATED, CarMakeEntry)
    def post(self, make_data):
        make = CarMakeORM(**make_data)
        try:
            db.session.add(make)
            db.session.commit()
        except IntegrityError:
            abort(HTTPStatus.BAD_REQUEST, message="A make with that name already exists.")
        except SQLAlchemyError:
            abort(HTTPStatus.INTERNAL_SERVER_ERROR, message="An error occurred creating the make.")

        headers = {'location': request.base_url + "/" + str(make.id)}

        return make, headers
