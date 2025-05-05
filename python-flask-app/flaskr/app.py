from flask import Flask
from flask_smorest import Api

from flaskr.db import db

import flaskr.orm

from flaskr.api.car_makes import blp as car_make_blp
from flaskr.api.car_models import blp as car_model_blp
from flaskr.api.car_parts import blp as car_part_blp

def create_app(db_url=None):
    default_db_url = "sqlite://"
    # Will default to an in-memory database.

    app = Flask(__name__)
    app.config["API_TITLE"] = "Cars REST API"
    app.config["API_VERSION"] = "v1"

    app.config["OPENAPI_VERSION"] = "3.0.3"
    app.config["OPENAPI_URL_PREFIX"] = "/"
    app.config["OPENAPI_SWAGGER_UI_PATH"] = "/swagger-ui"
    app.config["OPENAPI_SWAGGER_UI_URL"] = "https://cdn.jsdelivr.net/npm/swagger-ui-dist/"

    app.config["SQLALCHEMY_DATABASE_URI"] = db_url or default_db_url
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

    app.config["PROPAGATE_EXCEPTIONS"] = True

    db.init_app(app)
    api = Api(app)

    if db_url is None:
        with app.app_context():
            db.create_all()

    api.register_blueprint(car_make_blp)
    api.register_blueprint(car_model_blp)
    api.register_blueprint(car_part_blp)

    return app
