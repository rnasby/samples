import json

BASE = '/car-makes'

def test_create_car_make(test_client):
    reply = test_client.post(f'{BASE}', json={"name": "Ford"})

    assert reply.status_code == 201
    location = reply.headers["location"]
    assert location is not None

    reply_obj = json.loads(reply.data.decode())
    assert reply_obj["id"] == 1
    assert reply_obj["name"] == "Ford"

    reply = test_client.get(location)
    assert reply.status_code == 200

def test_get_car_make(test_client):
    reply = test_client.get(f'{BASE}/1')
    assert reply.status_code == 200

    reply_obj = json.loads(reply.data.decode())
    assert reply_obj["id"] == 1
    assert reply_obj["name"] == "Ford"

def test_get_car_make_list(test_client):
    reply = test_client.post(f'{BASE}', json={"name": "Chevy"})
    assert reply.status_code == 201

    reply = test_client.get(f'{BASE}')
    reply_list = json.loads(reply.data.decode())

    assert reply_list[0]["id"] == 1
    assert reply_list[0]["name"] == "Ford"

    assert reply_list[1]["id"] == 2
    assert reply_list[1]["name"] == "Chevy"

def test_update_car_make(test_client):
    reply = test_client.put(f'{BASE}/1', json={"name": "Ford2"})
    assert reply.status_code == 204

    reply = test_client.get(f'{BASE}/1')
    reply_obj = json.loads(reply.data.decode())
    assert reply_obj["id"] == 1
    assert reply_obj["name"] == "Ford2"

def test_delete_car_make(test_client):
    reply = test_client.delete(f'{BASE}/1')
    assert reply.status_code == 204

    reply = test_client.get(f'{BASE}/1')
    assert reply.status_code == 404