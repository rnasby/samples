from http import HTTPStatus

LOGIN_API = '/login'
LOGOUT_API = '/logout'
REFRESH_API = '/refresh'

CAR_MAKES_API = '/car-makes'
CAR_MODELS_API = '/car-models'
CAR_PARTS_API = '/car-parts'

def login(test_client, user_id, password):
    return test_client.post(f'{LOGIN_API}', json={"user_id" : user_id, "password" : password})

def login_with_success(test_client, user_id, password):
    reply = login(test_client, user_id, password)
    assert reply.status_code == HTTPStatus.OK
    assert reply.json["access_token"] is not None
    assert reply.json["refresh_token"] is not None
    test_client.environ_base['HTTP_AUTHORIZATION'] = 'Bearer ' + reply.json["access_token"]

    return reply

def assert_ford_make(make):
    assert make["id"] == 1
    assert make["name"] == "Ford"

def assert_mustang_model(model):
    assert model["id"] == 1
    assert model["name"] == "Mustang"
    assert model["make_id"] == 1
    assert model["year"] == 1979
    assert model["price"] == 6700.00

def assert_alternator_part(part):
    assert part["id"] == 1
    assert part["name"] == "Alternator"
    assert part["price"] == 500.50

def add_car_make(test_client, name):
    reply = test_client.post(f'{CAR_MAKES_API}', json={"name": name})
    assert reply.status_code == HTTPStatus.CREATED

    return reply

def add_car_makes(test_client):
    add_car_make(test_client, "Ford")
    add_car_make(test_client, "Chevy")

def add_car_model(test_client, name, make_id, year, price):
    reply = test_client.post(f'{CAR_MODELS_API}', json={"name": name, "make_id": make_id, "year": year, "price": price})
    assert reply.status_code == HTTPStatus.CREATED

    return reply

def add_car_models(test_client):
    add_car_model(test_client, "Mustang", 1, 1979, 6700.00)
    add_car_model(test_client, "Corvette", 2, 1981, 15000.00)

def add_car_part(test_client, name, price):
    reply = test_client.post(f'{CAR_PARTS_API}', json={"name": name, "price": price})
    assert reply.status_code == HTTPStatus.CREATED

    return reply

def add_car_parts(test_client):
    add_car_part(test_client, "Alternator", 500.50)
    add_car_part(test_client, "Motor", 9500.50)

def add_car_model_part(test_client, model_id, part_id):
    reply = test_client.post(f'{CAR_MODELS_API}/{model_id}/parts/{part_id}')
    assert reply.status_code == HTTPStatus.CREATED

    return reply

def add_car_model_parts(test_client):
    add_car_model_part(test_client, 1, 1)
    add_car_model_part(test_client, 1, 2)
    add_car_model_part(test_client, 2, 1)
    add_car_model_part(test_client, 2, 2)

def add_all(test_client):
    add_car_makes(test_client)
    add_car_models(test_client)
    add_car_parts(test_client)
    add_car_model_parts(test_client)
