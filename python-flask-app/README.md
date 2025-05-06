# Usage:

After cloning the project:

- CD into the project directory
- Create a virtual environment if necessary: 
  ```bash 
  python3 -m venv venv
  ```
-- Activate the virtual environment:
  ```bash
  source .venv/Scripts/activate
  ```
-- Install the dependencies:
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

