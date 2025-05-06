from flask import request
from http import HTTPStatus
from flask.views import MethodView
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import db
from flaskr.orm import CarPartORM
from flaskr.api.schemas import CarPart, CarPartEntry, CarPartUpdate

blp = Blueprint("car-parts", __name__, description="Operations on car-parts")


@blp.route("/car-parts/<string:part_id>")
class CarPartsWithId(MethodView):
    @blp.response(HTTPStatus.OK, CarPart)
    def get(self, part_id):
        return self.__get(part_id)

    def __get(self, part_id):
        part = db.session.get(CarPartORM, part_id)

        if not part:
            abort(HTTPStatus.NOT_FOUND, message="Car part not found.")

        return part

    def delete(self, part_id):
        make = self.__get(part_id)
        db.session.delete(make)
        db.session.commit()
        return {"message": "Car part deleted"}, HTTPStatus.NO_CONTENT

    @blp.arguments(CarPartUpdate)
    @blp.response(HTTPStatus.NO_CONTENT)
    def put(self, part_data, part_id):
        part = self.__get(part_id)
        part.price = part_data["price"]
        part.name = part_data["name"]

        db.session.add(part)
        db.session.commit()


@blp.route("/car-parts")
class CarParts(MethodView):
    @blp.response(HTTPStatus.OK, CarPartEntry(many=True))
    def get(self):
        return db.session.query(CarPartORM)

    @blp.arguments(CarPartUpdate)
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
