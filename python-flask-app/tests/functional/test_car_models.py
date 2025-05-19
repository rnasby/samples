import json
from http import HTTPStatus
from tests.functional import common

API = common.CAR_MODELS_API

# TODO: Add tests showing that login is required to change models.

def test_create_car_model(test_client):
    common.login_fred(test_client)
    common.add_car_makes(test_client)

    reply = common.add_car_model(test_client, "Mustang", 1, 1979, 6700.00)
    common.assert_mustang_model(json.loads(reply.data.decode()))

    location = reply.headers["location"]
    reply = test_client.get(location)
    assert reply.status_code == HTTPStatus.OK

def test_get_car_model(test_client):
    common.setup(test_client)
    common.logout(test_client)

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.OK
    model = json.loads(reply.data.decode())
    common.assert_mustang_model(model)

    assert "make" in model
    common.assert_ford_make(model["make"])

    assert "parts" in model
    assert len(model["parts"]) == 2
    common.assert_alternator_part(model["parts"][0])

def test_get_car_model_list(test_client):
    common.setup(test_client)
    common.logout(test_client)

    reply = test_client.get(f'{API}')
    model = json.loads(reply.data.decode())
    common.assert_mustang_model(model[0])
    assert model[1]["id"] == 2
    assert model[1]["name"] == "Corvette"
    assert model[1]["make_id"] == 2
    assert model[1]["year"] == 1981
    assert model[1]["price"] == 15000.00

    assert "make" not in model[1]

def test_update_car_model(test_client):
    common.setup(test_client)

    reply = test_client.put(f'{API}/1', json={"name": "Bogus2", "make_id": 2, "year": 1910, "price": 1.75})
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    make = json.loads(reply.data.decode())
    assert make["id"] == 1
    assert make["name"] == "Bogus2"
    assert make["make_id"] == 2
    assert make["year"] == 1910
    assert make["price"] == 1.75

def test_delete_car_model(test_client):
    common.setup(test_client)

    reply = test_client.delete(f'{API}/1')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.NOT_FOUND
