from flaskr.db import db
import sqlalchemy as sa
import sqlalchemy.orm as sa_orm


class CarModelORM(db.Model):
    __tablename__ = "car_model"

    id = sa.Column("id", sa.Integer, primary_key=True)
    year = sa.Column("year", sa.Integer, unique=False, nullable=False)
    name = sa.Column("name", sa.String(255), unique=False, nullable=False)
    price = sa.Column("price", sa.Float(precision=2), unique=False, nullable=False)
    make_id = sa.Column("make_id", sa.Integer, sa.ForeignKey("car_make.id"), unique=False, nullable=False)

    make = sa_orm.relationship("CarMakeORM", back_populates="models")
    parts = sa_orm.relationship("CarPartORM", back_populates="models", secondary="car_model_part")
