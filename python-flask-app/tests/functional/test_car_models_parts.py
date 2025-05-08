import json
from http import HTTPStatus
import tests.functional.common as common

API = common.CAR_MODELS_API

def test_add_model_car_part(test_client):
    common.populate_makes(test_client)
    common.populate_models(test_client)
    common.populate_parts(test_client)

    reply = test_client.post(f'{API}/1/parts/1')
    assert reply.status_code == HTTPStatus.CREATED

def test_get_model_parts(test_client):
    reply = test_client.get(f'{API}/1/parts')
    assert reply.status_code == HTTPStatus.OK
    parts = json.loads(reply.data.decode())
    assert len(parts) == 1
    assert parts[0]["id"] == 1
    assert parts[0]["name"] == "Alternator"
    assert parts[0]["price"] == 500.50

def test_delete_model_part(test_client):
    reply = test_client.delete(f'{API}/1/parts/1')
    assert reply.status_code == HTTPStatus.NO_CONTENT

    reply = test_client.get(f'{API}/1/parts')
    assert reply.status_code == HTTPStatus.OK
    parts = json.loads(reply.data.decode())
    assert len(parts) == 0
