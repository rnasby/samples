from flask import request
from flask.views import MethodView
from flask_smorest import Blueprint, abort
from sqlalchemy.exc import SQLAlchemyError, IntegrityError

from flaskr.db import db
from flaskr.orm import CarMakeORM
from flaskr.api.schemas import CarMake, CarMakeEntry, CarMakeUpdate


blp = Blueprint("car-makes", __name__, description="Operations on car-makes")

@blp.route("/car-makes/<string:make_id>")
class CarMakesWithId(MethodView):
    @blp.response(200, CarMake)
    def get(self, make_id):
        return CarMakeORM.query.get_or_404(make_id)

    def delete(self, make_id):
        make = CarMakeORM.query.get_or_404(make_id)
        db.session.delete(make)
        db.session.commit()
        return {"message": "Make deleted"}, 204

    @blp.arguments(CarMakeUpdate)
    @blp.response(204)
    def put(self, make_data, make_id):
        make = CarMakeORM.query.get_or_404(make_id)
        make.name = make_data["name"]

        db.session.add(make)
        db.session.commit()

@blp.route("/car-makes")
class CarMakes(MethodView):
    @blp.response(200, CarMake(many=True))
    def get(self):
        return CarMakeORM.query.all()

    @blp.arguments(CarMakeUpdate)
    @blp.response(201, CarMake)
    def post(self, make_data):
        make = CarMakeORM(**make_data)
        try:
            db.session.add(make)
            db.session.commit()
        except IntegrityError:
            abort(400, message="A make with that name already exists.")
        except SQLAlchemyError:
            abort(500, message="An error occurred creating the make.")

        headers = {'location': request.base_url + "/" + str(make.id)}

        return make, headers
