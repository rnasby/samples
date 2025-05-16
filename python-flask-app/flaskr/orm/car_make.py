from flaskr.db import DB
import sqlalchemy as sa
import sqlalchemy.orm as sa_orm


class CarMakeORM(DB.Model):
    __tablename__ = "car_make"

    id = sa.Column("id", sa.Integer(), primary_key=True)
    name = sa.Column("name", sa.String(255), unique=True, nullable=False)

    models = sa_orm.relationship("CarModelORM", back_populates="make", cascade="all, delete-orphan")
