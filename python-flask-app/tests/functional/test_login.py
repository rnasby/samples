from http import HTTPStatus
from tests.functional import common

def test_invalid_user_login(test_client):
    reply = common.login(test_client, "stone", "pebbles")
    assert reply.status_code == HTTPStatus.UNAUTHORIZED
    assert reply.json["message"] == "Invalid user id"

def test_invalid_user_password_login(test_client):
    reply = common.login(test_client, "fred", "i dont know")
    assert reply.status_code == HTTPStatus.UNAUTHORIZED
    assert reply.json["message"] == "Invalid password"

def test_logout(test_client):
    common.login_fred(test_client)
    common.logout(test_client)

def test_logout_without_jwt(test_client):
    reply = test_client.post(f'{common.LOGOUT_API}')
    assert reply.status_code == HTTPStatus.UNAUTHORIZED
    assert reply.json["description"] == "Request does not contain an access token."

def test_refresh_token(test_client):
    reply = common.login_fred(test_client)
    token_str1 = reply.json["access_token"]
    refresh_token_str = reply.json["refresh_token"]

    reply = common.refresh_token_ok(test_client, refresh_token_str)
    token_str2 = reply.json["access_token"]

    assert token_str1 != token_str2

    common.logout(test_client)
