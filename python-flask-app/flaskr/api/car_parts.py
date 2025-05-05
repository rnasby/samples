from flask import request
from flask.views import MethodView
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import db
from flaskr.orm import CarPartORM
from flaskr.api.schemas import CarPart, CarPartUpdate

blp = Blueprint("car-parts", __name__, description="Operations on car-parts")


@blp.route("/car-parts/<string:part_id>")
class CarPartsWithId(MethodView):
    @blp.response(200, CarPart)
    def get(self, part_id):
        return CarPartORM.query.get_or_404(part_id)

    def delete(self, part_id):
        make = CarPartORM.query.get_or_404(part_id)
        db.session.delete(make)
        db.session.commit()
        return {"message": "Part deleted."}

    @blp.arguments(CarPartUpdate)
    @blp.response(200, CarPart)
    def put(self, part_data, part_id):
        part = CarPartORM.query.get(part_id)

        if part:
            part.price = part_data["price"]
            part.name = part_data["name"]
        else:
            part = CarPartORM(id=part_id, **part_data)

        db.session.add(part)
        db.session.commit()

        return part


@blp.route("/car-parts")
class CarParts(MethodView):
    @blp.response(200, CarPart(many=True))
    def get(self):
        return CarPartORM.query.all()

    @blp.arguments(CarPart)
    @blp.response(201, CarPart)
    def post(self, part_data):
        part = CarPartORM(**part_data)

        try:
            db.session.add(part)
            db.session.commit()
        except SQLAlchemyError:
            abort(500, message="An error occurred while inserting car part.")

        headers = {'location': request.base_url + "/" + str(part.id)}

        return part, headers
