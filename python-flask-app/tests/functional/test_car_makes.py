import json
from http import HTTPStatus
from tests.functional import common

API = common.CAR_MAKES_API

# TODO: Add tests showing that login is required to change makes.

def test_create_car_make(test_client):
    common.login_fred(test_client)
    reply = common.add_car_make(test_client, "Ford")
    common.assert_ford_make(json.loads(reply.data.decode()))

    location = reply.headers["location"]
    reply = test_client.get(location)
    assert reply.status_code == HTTPStatus.OK

def test_get_car_make(test_client):
    common.setup(test_client)
    common.logout(test_client)

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.OK
    make = json.loads(reply.data.decode())
    common.assert_ford_make(make)

    assert 'models' in make
    models = make["models"]
    assert len(models) == 1
    common.assert_mustang_model(models[0])

def test_get_car_make_list(test_client):
    common.setup(test_client)
    common.logout(test_client)

    reply = test_client.get(f'{API}')
    makes = json.loads(reply.data.decode())
    common.assert_ford_make(makes[0])
    assert makes[1]["id"] == 2
    assert makes[1]["name"] == "Chevy"

    assert 'models' not in makes[1]

def test_update_car_make(test_client):
    common.setup(test_client)

    reply = test_client.put(f'{API}/1', json={"name": "Bogus2"})
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    make = json.loads(reply.data.decode())
    assert make["id"] == 1
    assert make["name"] == "Bogus2"

def test_delete_car_make(test_client):
    common.setup(test_client)

    reply = test_client.delete(f'{API}/1')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.NOT_FOUND

def test_delete_car_make_deletes_children(test_client):
    common.setup(test_client)

    reply = test_client.get(f'{common.CAR_MODELS_API}/1')
    assert reply.status_code == HTTPStatus.OK

    reply = test_client.delete(f'{API}/1')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.NOT_FOUND

    reply = test_client.get(f'{common.CAR_MODELS_API}/1')
    assert reply.status_code == HTTPStatus.NOT_FOUND
