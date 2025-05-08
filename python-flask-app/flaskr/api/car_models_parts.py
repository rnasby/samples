from http import HTTPStatus
from flask.views import MethodView
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import db
import flaskr.api.car_models as car_models
import flaskr.api.car_parts as car_parts
from flaskr.api.schemas import CarPartEntry

blp = Blueprint("Car Models Parts", __name__, description="Operations on car models parts")


@blp.route("/car-models/<string:model_id>/parts")
class CarModelPartsWithId(MethodView):
    @blp.response(HTTPStatus.OK, CarPartEntry(many=True))
    def get(self, model_id):
        return car_models.get_car_model(model_id).parts

@blp.route("/car-models/<string:model_id>/parts/<string:part_id>")
class CarModelParts(MethodView):
    @blp.response(HTTPStatus.CREATED, CarPartEntry)
    def post(self, model_id, part_id):
        model = car_models.get_car_model(model_id)
        part = car_parts.get_car_part(part_id)

        model.parts.append(part)

        try:
            db.session.add(model)
            db.session.commit()
        except SQLAlchemyError:
            abort(500, message="An error occurred while inserting the model part.")

        return part

    def delete(self, model_id, part_id):
        model = car_models.get_car_model(model_id)
        part = car_parts.get_car_part(part_id)
        model.parts.remove(part)

        try:
            db.session.add(model)
            db.session.commit()
        except SQLAlchemyError:
            abort(500, message="An error occurred while deleting the model part.")

        return {"message": "Car model part deleted"}, HTTPStatus.NO_CONTENT