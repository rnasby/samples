from flask import request
from flask.views import MethodView
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError

from flaskr.db import db
from flaskr.orm import CarModelORM
from flaskr.api.schemas import CarModel, CarModelUpdate

blp = Blueprint("car-models", __name__, description="Operations on car-makes")


@blp.route("/car-models/<string:model_id>")
class CarModelsId(MethodView):
    @blp.response(200, CarModel)
    def get(self, model_id):
        return CarModelORM.query.get_or_404(model_id)

    def delete(self, model_id):
        model = CarModelORM.query.get_or_404(model_id)
        db.session.delete(model)
        db.session.commit()
        return {"message": "Model deleted."}

    @blp.arguments(CarModelUpdate)
    @blp.response(200, CarModel)
    def put(self, req_data, model_id):
        model = CarModelORM.query.get(model_id)

        if model:
            model.price = req_data["price"]
            model.name = req_data["name"]
        else:
            model = CarModelORM(id=model_id, **req_data)

        db.session.add(model)
        db.session.commit()

        return model


@blp.route("/car-models")
class CarModels(MethodView):
    @blp.response(200, CarModel(many=True))
    def get(self):
        return CarModelORM.query.all()

    @blp.arguments(CarModel)
    @blp.response(201, CarModel)
    def post(self, req_data):
        model = CarModelORM(**req_data)

        try:
            db.session.add(model)
            db.session.commit()
        except SQLAlchemyError:
            abort(500, message="An error occurred while inserting the car model.")

        headers = {'location': request.base_url + "/" + str(model.id)}

        return model, headers
