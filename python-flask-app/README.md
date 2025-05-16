# Features:

1. REST API web application with CRUD operations. Featuring Flask, SQLAlchemy ORM, Marshmallow for serialization, 
   and Smorest for API documentation presented in swagger-ui.

# Usage:

After cloning the project:

- CD into the project directory
- Create a virtual environment if necessary: 
  ```bash 
  python3 -m venv venv
  ```
- Activate the virtual environment:
  ```bash
  source .venv/Scripts/activate
  ```
- Install the dependencies:
  ```bash
  pip install -r requirements.txt
  ```
- Update requirements.txt using:
  ```bash
  pip freeze > requirements.txt
  ```
- Run tests and report coverage:
  ```bash
  pytest tests -v --cov=.
  ```
- For coverage options see: https://pytest-cov.readthedocs.io/en/latest/config.html

- Run flask app from the clone directory:
  ```bash
  export FLASK_APP=$(pwd)/flaskr/app.py
  flask run
  ```
- Swagger documentation is available at: http://localhost:5000/swagger-ui

- Optional environment variables:
  - See flaskr.config.Config doc string.
