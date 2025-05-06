import json
from http import HTTPStatus

BASE = '/car-models'
CAR_MAKES_BASE = '/car-makes'

def assert_mustang(make):
    assert make["id"] == 1
    assert make["name"] == "Mustang"
    assert make["make_id"] == 1
    assert make["year"] == 1979
    assert make["price"] == 6700.00

def test_create_car_model(test_client):
    reply = test_client.post(f'{CAR_MAKES_BASE}', json={"name": "Ford"})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.post(f'{CAR_MAKES_BASE}', json={"name": "Chevy"})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.post(f'{BASE}', json={"name": "Mustang", "make_id": 1, "year": 1979, "price": 6700.00})
    assert reply.status_code == HTTPStatus.CREATED
    location = reply.headers["location"]
    assert location is not None
    assert_mustang(json.loads(reply.data.decode()))

    reply = test_client.get(location)
    assert reply.status_code == HTTPStatus.OK

def test_get_car_model(test_client):
    reply = test_client.get(f'{BASE}/1')
    assert reply.status_code == HTTPStatus.OK
    assert_mustang(json.loads(reply.data.decode()))

def test_get_car_model_list(test_client):
    reply = test_client.post(f'{BASE}', json={"name": "Cougar", "make_id": 1, "year": 1980, "price": 9800.00})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.get(f'{BASE}')
    makes = json.loads(reply.data.decode())
    assert_mustang(makes[0])
    assert makes[1]["id"] == 2
    assert makes[1]["name"] == "Cougar"
    assert makes[1]["make_id"] == 1
    assert makes[1]["year"] == 1980
    assert makes[1]["price"] == 9800.00

def test_update_car_model(test_client):
    reply = test_client.post(f'{BASE}', json={"name": "Bogus", "make_id": 1, "year": 1900, "price": 200.00})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.put(f'{BASE}/3', json={"name": "Bogus2", "make_id": 2, "year": 1910, "price": 1.75})
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{BASE}/3')
    make = json.loads(reply.data.decode())
    assert make["id"] == 3
    assert make["name"] == "Bogus2"
    assert make["make_id"] == 2
    assert make["year"] == 1910
    assert make["price"] == 1.75

def test_delete_car_model(test_client):
    reply = test_client.delete(f'{BASE}/3')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{BASE}/3')
    assert reply.status_code == HTTPStatus.NOT_FOUND