import json
from http import HTTPStatus
import tests.functional.common as common

API = common.CAR_PARTS_API

def assert_alternator(part):
    assert part["id"] == 1
    assert part["name"] == "Alternator"
    assert part["price"] == 500.50

def test_create_car_part(test_client):
    reply = test_client.post(f'{API}', json={"name": "Alternator", "price": 500.50 })
    assert reply.status_code == HTTPStatus.CREATED
    location = reply.headers["location"]
    assert location is not None
    assert_alternator(json.loads(reply.data.decode()))

    reply = test_client.get(location)
    assert reply.status_code == HTTPStatus.OK

def test_get_car_part(test_client):
    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.OK
    assert_alternator(json.loads(reply.data.decode()))

def test_get_car_part_list(test_client):
    reply = test_client.post(f'{API}', json={"name": "Motor", "price": 9500.50 })
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.get(f'{API}')
    parts = json.loads(reply.data.decode())
    assert_alternator(parts[0])
    assert parts[1]["id"] == 2
    assert parts[1]["name"] == "Motor"
    assert parts[1]["price"] == 9500.50

def test_update_car_part(test_client):
    reply = test_client.post(f'{API}', json={"name": "Bogus", "price": 0.50})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.put(f'{API}/3', json={"name": "Bogus2", "price": 0.75})
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/3')
    part = json.loads(reply.data.decode())
    assert part["id"] == 3
    assert part["name"] == "Bogus2"
    assert part["price"] == 0.75

def test_delete_car_part(test_client):
    reply = test_client.delete(f'{API}/3')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/3')
    assert reply.status_code == HTTPStatus.NOT_FOUND