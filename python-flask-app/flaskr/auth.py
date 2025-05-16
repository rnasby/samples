class User:
    """
    A class representing a user.
    """
    def __init__(self, user_id):
        self.user_id = user_id

    def __repr__(self):
        return f"User(id={self.user_id})"

    def get_id(self):
        return self.user_id

class AbstractAuth:
    """
    Abstract class for user authentication.
    """
    def __init__(self):
        pass

    def auth_user(self, user_id, password) -> User:
        """
        Authenticate a user.
        :param user_id:
        :param password:
        :return: Authenticated user
        """
        raise NotImplementedError

class BogusAuth(AbstractAuth):
    """
    A bogus authentication class for testing purposes. Should be replaced with a real authentication
    class using LDAP, Okta, or similar.
    """

    def __init__(self):
        super().__init__()

        self.users = [
            {"id": "fred", "password" : "pebbles"},
            {"id": "barney", "password" :"bambam"}
        ]

    def auth_user(self, user_id, password) -> User:
        users = [u for u in self.users if u["id"] == user_id]

        if not len(users) == 1:
            raise ValueError("Invalid user")

        user = users[0]
        if not user["password"] == password:
            raise ValueError("Invalid password")

        return User(user["id"])
