import json
from http import HTTPStatus
from tests.functional import common

def test_login(test_client):
    reply = common.login(test_client, "fred", "pebbles")
    assert reply.status_code == HTTPStatus.OK

def test_invalid_user_login(test_client):
    reply = common.login(test_client, "stone", "pebbles")
    assert reply.status_code == HTTPStatus.UNAUTHORIZED
    assert reply.json["message"] == "Invalid credentials."

def test_invalid_user_password_login(test_client):
    reply = common.login(test_client, "fred", "idontknow")
    assert reply.status_code == HTTPStatus.UNAUTHORIZED
    assert reply.json["message"] == "Invalid credentials."

def test_logout(test_client):
    common.login_with_success(test_client, "fred", "pebbles")

    reply = test_client.post(f'{common.LOGOUT_API}')
    assert reply.status_code == HTTPStatus.OK

def test_logout_without_jwt(test_client):
    reply = common.login(test_client, "fred", "pebbles")
    assert reply.status_code == HTTPStatus.OK

    reply = test_client.post(f'{common.LOGOUT_API}')
    assert reply.status_code == HTTPStatus.UNAUTHORIZED
    assert reply.json["description"] == "Request does not contain an access token."
