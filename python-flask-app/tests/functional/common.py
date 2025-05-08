from http import HTTPStatus

CAR_MAKES_API = '/car-makes'
CAR_MODELS_API = '/car-models'
CAR_PARTS_API = '/car-parts'

def populate_makes(test_client):
    reply = test_client.post(f'{CAR_MAKES_API}', json={"name": "Ford"})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.post(f'{CAR_MAKES_API}', json={"name": "Chevy"})
    assert reply.status_code == HTTPStatus.CREATED

def populate_models(test_client):
    reply = test_client.post(f'{CAR_MODELS_API}', json={"name": "Mustang", "make_id": 1, "year": 1979, "price": 6700.00})
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.post(f'{CAR_MODELS_API}', json={"name": "Corvette", "make_id": 2, "year": 1981, "price": 15000.00})
    assert reply.status_code == HTTPStatus.CREATED

def populate_parts(test_client):
    reply = test_client.post(f'{CAR_PARTS_API}', json={"name": "Alternator", "price": 500.50 })
    assert reply.status_code == HTTPStatus.CREATED

    reply = test_client.post(f'{CAR_PARTS_API}', json={"name": "Motor", "price": 9500.50 })
    assert reply.status_code == HTTPStatus.CREATED
