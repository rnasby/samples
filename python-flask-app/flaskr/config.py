import importlib
import os
import uuid

DEFAULT_DB_URL = "sqlite://"
DEFAULT_SECRET_KEY_FILE = "secret_key.txt"

class Config:
    """
    Server configuration class. Following environment variables are used:

    DB_URL - SQLAlchemy database url to use. Defaults to DEFAULT_DB_URL.
    DB_CREATE - Either 0 or 1. Defaults to 1 if DB_URL equals DEFAULT_DB_URL.
    DB_SHOW_SQL - Either 0 or 1. Defaults to 0. When set to 1 will show executed SQL.
    AUTH_IMPL - AbstractAuth implementation specified as "{fully qualified module name}.{class name}"
    SECRET_KEY_FILE - Secret key file name used to store secret key. Defaults to DEFAULT_SECRET_KEY_FILE.
    """

    @staticmethod
    def _get_config_value(key, overrides, default = None):
        if overrides and key in overrides:
            return overrides[key]
        if key in os.environ:
            return os.getenv(key)
        if default is not None:
            return default

        raise ValueError(f"Missing required configuration value: {key}")

    @staticmethod
    def _build_auth_impl(auth_impl_name):
        last_dot_pos = auth_impl_name.rfind(".")
        module_name = auth_impl_name[:last_dot_pos]
        class_name = auth_impl_name[last_dot_pos + 1:]
        module = importlib.import_module(module_name)

        if class_name not in module.__dict__:
            raise ValueError(f"AUTH_IMPL class [{class_name}] NOT found")

        clazz = module.__dict__[class_name]
        return clazz()

    @staticmethod
    def _get_secret_key(secret_key_file) -> str:
        """
        Get the secret key for JWT from a file. If file doesn't exist, create a new one and save to file.
        :return: Secret key as a string
        """
        is_exist = os.path.exists(secret_key_file)

        if is_exist:
            with open(secret_key_file, 'r', encoding='utf-8') as f:
                secret_key = f.read()
        else:
            secret_key = str(uuid.uuid4())
            with open(secret_key_file, 'w', encoding='utf-8') as f:
                f.write(secret_key)

        return secret_key

    def __init__(self):
        self.db_url = None
        self.is_db_create = None
        self.is_db_show_sql = None

        self.auth_impl = None
        self.secret_key = None

        # TODO: Replace set with database for persisting blocked tokens between restarts.
        self.jwt_tokens_blocked = set()

    def load(self, overrides=None):
        self.db_url = Config._get_config_value("DB_URL", overrides, DEFAULT_DB_URL)
        self.is_db_show_sql = Config._get_config_value("DB_SHOW_SQL", overrides, "0") == "1"

        db_create_default = "1" if self.db_url == DEFAULT_DB_URL else "0"
        self.is_db_create = Config._get_config_value("DB_CREATE", overrides, db_create_default) == "1"

        name = Config._get_config_value("AUTH_IMPL", overrides)
        self.auth_impl = self._build_auth_impl(name)

        file = Config._get_config_value("SECRET_KEY_FILE", overrides, DEFAULT_SECRET_KEY_FILE)
        self.secret_key = Config._get_secret_key(file)
