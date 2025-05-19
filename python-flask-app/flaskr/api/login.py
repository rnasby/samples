from flask.views import MethodView
from marshmallow import Schema, fields
from flask_smorest import Blueprint, abort
from flask_jwt_extended import create_access_token, create_refresh_token, get_jwt_identity, get_jwt, jwt_required

import flaskr.app

BLP = Blueprint("Users", "users", description="Operations on users")

class UserLoginValues(Schema):
    user_id = fields.Str(required=True)
    password = fields.Str(required=True, load_only=True)

@BLP.route("/login")
class UserLogin(MethodView):
    @BLP.arguments(UserLoginValues)
    def post(self, values):
        user_id = values["user_id"]
        password = values["password"]

        try:
            cfg = flaskr.app.CONFIG
            user = cfg.auth_impl.auth_user(user_id, password)
        except ValueError as e:
            abort(401, message=str(e))
        except NotImplementedError:
            abort(401, message="Authentication method not implemented")

        # TODO: Add flask_jwt_extended.JWTManager.user_identity_loader to convert user to a string.
        #  And pass that to create tokens.

        refresh_token = create_refresh_token(identity=user_id)
        access_token = create_access_token(identity=user_id, fresh=True)

        return {"access_token": access_token, "refresh_token": refresh_token}, 200

@BLP.route("/logout")
class UserLogout(MethodView):
    @jwt_required()
    def post(self):
        jti = get_jwt()["jti"]
        flaskr.app.CONFIG.jwt_tokens_blocked.add(jti)
        return {"message": "Successfully logged out"}, 200

@BLP.route("/refresh")
class TokenRefresh(MethodView):
    @jwt_required(refresh=True)
    def post(self):
        current_user = get_jwt_identity()
        new_token = create_access_token(identity=current_user, fresh=False)
        jti = get_jwt()["jti"]
        flaskr.app.CONFIG.jwt_tokens_blocked.add(jti)

        return {"access_token": new_token}, 200
