import os

from flask import Flask
from flask_smorest import Api
from sqlalchemy import event
from sqlalchemy.engine import Engine

from flaskr.db import db

import flaskr.orm

from flaskr.api.car_makes import blp as car_makes_blp
from flaskr.api.car_models import blp as car_models_blp
from flaskr.api.car_parts import blp as car_parts_blp
from flaskr.api.car_models_parts import blp as car_models_parts_blp

is_check_debug_sql = True
DEFAULT_DB_URL = "sqlite://"

def create_app(db_url=None):
    """
    Will default to an in-memory database.
    """
    global is_check_debug_sql

    app = Flask(__name__)
    app.config["API_TITLE"] = "Cars REST API"
    app.config["API_VERSION"] = "v1"

    app.config["OPENAPI_VERSION"] = "3.0.3"
    app.config["OPENAPI_URL_PREFIX"] = "/"
    app.config["OPENAPI_SWAGGER_UI_PATH"] = "/swagger-ui"
    app.config["OPENAPI_SWAGGER_UI_URL"] = "https://cdn.jsdelivr.net/npm/swagger-ui-dist/"

    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
    app.config["SQLALCHEMY_DATABASE_URI"] = db_url or os.getenv("DATABASE_URL", DEFAULT_DB_URL)

    app.config["PROPAGATE_EXCEPTIONS"] = True

    db.init_app(app)
    api = Api(app)

    if is_check_debug_sql and os.getenv("DEBUG_SQL", "0") == "1" :
        is_check_debug_sql = False
        @event.listens_for(Engine, "before_cursor_execute")
        def before_cursor_execute(conn, cursor, statement, parameters, context, executemany):
            print("SQL:", statement, parameters)

    if app.config["SQLALCHEMY_DATABASE_URI"] == DEFAULT_DB_URL:
        with app.app_context():
            db.create_all()

    api.register_blueprint(car_makes_blp)
    api.register_blueprint(car_models_blp)
    api.register_blueprint(car_parts_blp)
    api.register_blueprint(car_models_parts_blp)

    return app
