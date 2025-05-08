import json
from http import HTTPStatus
import tests.functional.common as common

API = common.CAR_MAKES_API

def assert_ford(make):
    assert make["id"] == 1
    assert make["name"] == "Ford"

def test_create_car_make(test_client):
    reply = test_client.post(f'{API}', json={"name": "Ford"})
    assert reply.status_code == HTTPStatus.CREATED
    location = reply.headers["location"]
    assert location is not None
    assert_ford(json.loads(reply.data.decode()))

    reply = test_client.get(location)
    assert reply.status_code == HTTPStatus.OK

def test_get_car_make(test_client):
    reply = test_client.get(f'{API}/1')
    assert reply.status_code == HTTPStatus.OK
    make = json.loads(reply.data.decode())
    assert_ford(make)
    assert make["models"][0]["id"] == 1

def test_get_car_make_list(test_client):
    reply = test_client.post(f'{API}', json={"name": "Chevy"})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.get(f'{API}')
    makes = json.loads(reply.data.decode())
    assert_ford(makes[0])
    assert makes[1]["id"] == 2
    assert makes[1]["name"] == "Chevy"

def test_update_car_make(test_client):
    reply = test_client.post(f'{API}', json={"name": "Bogus"})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.put(f'{API}/3', json={"name": "Bogus2"})
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/3')
    make = json.loads(reply.data.decode())
    assert make["id"] == 3
    assert make["name"] == "Bogus2"

def test_delete_car_make(test_client):
    reply = test_client.delete(f'{API}/3')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/3')
    assert reply.status_code == HTTPStatus.NOT_FOUND