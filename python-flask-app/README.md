# Usage:
Project uses virtualenv to manage dependencies. To set up the environment, run the following commands after clone:
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
- Note, requirements.txt was created using:
  ```bash
  pip freeze > requirements.txt
  ```


