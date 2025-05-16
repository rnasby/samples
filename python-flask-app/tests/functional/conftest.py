import pytest
import flaskr.app

@pytest.fixture(scope='function')
def test_client():
    overrides = {
        'DB_SHOW_SQL': '1',
        'DB_URL': 'sqlite://',
        'AUTH_IMPL': 'flaskr.auth.BogusAuth'
    }

    app = flaskr.app.create_app(overrides)

    with app.test_client() as client:
        with app.app_context():
            yield client
