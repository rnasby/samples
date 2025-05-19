import json
from http import HTTPStatus
from tests.functional import common

API = common.CAR_PARTS_API

# TODO: Add tests showing that login is required to change parts.

def test_create_car_part(test_client):
    common.login_fred(test_client)
    reply = common.add_car_part(test_client, "Alternator", 500.50)
    common.assert_alternator_part(json.loads(reply.data.decode()))

    location = reply.headers["location"]
    reply = test_client.get(location)
    assert reply.status_code == HTTPStatus.OK

def test_get_car_part(test_client):
    common.setup(test_client)
    common.logout(test_client)

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.OK
    car_part = json.loads(reply.data.decode())
    common.assert_alternator_part(car_part)

    assert "models" in car_part
    assert len(car_part["models"]) == 2
    common.assert_mustang_model(car_part["models"][0])

def test_get_car_part_list(test_client):
    common.setup(test_client)
    common.logout(test_client)

    reply = test_client.get(f'{API}')
    parts = json.loads(reply.data.decode())
    common.assert_alternator_part(parts[0])
    assert parts[1]["id"] == 2
    assert parts[1]["name"] == "Motor"
    assert parts[1]["price"] == 9500.50

    assert "models" not in parts[0]

def test_update_car_part(test_client):
    common.setup(test_client)

    reply = test_client.put(f'{API}/1', json={"name": "Bogus2", "price": 0.75})
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    part = json.loads(reply.data.decode())
    assert part["id"] == 1
    assert part["name"] == "Bogus2"
    assert part["price"] == 0.75

def test_delete_car_part(test_client):
    common.setup(test_client)

    reply = test_client.delete(f'{API}/1')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.NOT_FOUND
