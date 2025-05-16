import sqlalchemy
import flask_sqlalchemy.extension

INIT_PROGRESS = []
SHOW_SQL_PROGRESS = "show_sql"
DB = flask_sqlalchemy.extension.SQLAlchemy()

def init(app, is_create=False, is_show_sql=False):
    """
    Initialize the database with the Flask app.

    :param app: Flask app instance
    :param is_create: True if the database objects should be created
    :param is_show_sql: True if the SQL statements should be printed
    :return:
    """
    DB.init_app(app)

    if is_create:
        with app.app_context():
            DB.create_all()

    if is_show_sql and SHOW_SQL_PROGRESS not in INIT_PROGRESS:
        INIT_PROGRESS.append(SHOW_SQL_PROGRESS)

        @sqlalchemy.event.listens_for(sqlalchemy.Engine, "before_cursor_execute")
        def before_cursor_execute(conn, cursor, statement, parameters, context, executemany):
            print("SQL:", statement, parameters)
