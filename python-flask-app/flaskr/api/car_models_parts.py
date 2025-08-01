from http import HTTPStatus
from flask.views import MethodView
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError
from flask_jwt_extended import jwt_required
from flaskr.api.car_parts import CarPartEntry

from flaskr.db import DB
from flaskr.api import car_models, car_parts

BLP = Blueprint("Car Models Parts", __name__, description="Operations on car models parts")

@BLP.route("/car-models/<string:model_id>/parts")
class CarModelPartsWithId(MethodView):
    @BLP.response(HTTPStatus.OK, CarPartEntry(many=True))
    def get(self, model_id):
        model = car_models.get_car_model(model_id)
        parts = model.parts

        return parts

@BLP.route("/car-models/<string:model_id>/parts/<string:part_id>")
class CarModelParts(MethodView):
    @jwt_required()
    @BLP.response(HTTPStatus.CREATED, CarPartEntry)
    def post(self, model_id, part_id):
        model = car_models.get_car_model(model_id)
        part = car_parts.get_car_part(part_id)

        model.parts.append(part)

        try:
            DB.session.add(model)
            DB.session.commit()
        except SQLAlchemyError:
            abort(500, message="An error occurred while inserting the model part.")

        return part

    @jwt_required()
    def delete(self, model_id, part_id):
        model = car_models.get_car_model(model_id)
        part = car_parts.get_car_part(part_id)
        model.parts.remove(part)

        try:
            DB.session.add(model)
            DB.session.commit()
        except SQLAlchemyError:
            abort(500, message="An error occurred while deleting the model part.")

        return {"message": "Car model part deleted"}, HTTPStatus.NO_CONTENT
