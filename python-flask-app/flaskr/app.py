import flask
import flask_smorest

import flaskr.db
import flaskr.jwt
import flaskr.auth
import flaskr.config

import flaskr.config
import flaskr.api.login
import flaskr.api.car_makes
import flaskr.api.car_models
import flaskr.api.car_parts
import flaskr.api.car_models_parts

CONFIG = flaskr.config.Config()

def create_app(overrides = None):
    app = flask.Flask(__name__)

    CONFIG.load(overrides)

    app.config["API_TITLE"] = "Cars REST API"
    app.config["API_VERSION"] = "v1"

    app.config["OPENAPI_VERSION"] = "3.0.3"
    app.config["OPENAPI_URL_PREFIX"] = "/"
    app.config["OPENAPI_SWAGGER_UI_PATH"] = "/swagger-ui"
    app.config["OPENAPI_SWAGGER_UI_URL"] = "https://cdn.jsdelivr.net/npm/swagger-ui-dist/"

    app.config["SQLALCHEMY_DATABASE_URI"] = CONFIG.db_url
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

    app.config["PROPAGATE_EXCEPTIONS"] = True
    app.config["JWT_SECRET_KEY"] = CONFIG.secret_key

    flaskr.db.init(app, CONFIG.is_db_create, CONFIG.is_db_show_sql)
    api = flask_smorest.Api(app)
    flaskr.jwt.setup_jwt(app, CONFIG.jwt_tokens_blocked)

    api.register_blueprint(flaskr.api.login.BLP)
    api.register_blueprint(flaskr.api.car_makes.BLP)
    api.register_blueprint(flaskr.api.car_models.BLP)
    api.register_blueprint(flaskr.api.car_parts.BLP)
    api.register_blueprint(flaskr.api.car_models_parts.BLP)

    return app
