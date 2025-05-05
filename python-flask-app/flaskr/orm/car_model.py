from flaskr.db import db
import sqlalchemy as sa
import sqlalchemy.orm as sa_orm


class CarModelORM(db.Model):
    __tablename__ = "car_model"

    id = sa.Column("id", sa.Integer, primary_key=True)
    name = sa.Column("name", sa.String(255), unique=False, nullable=False)
    make_id = sa.Column("make_id", sa.Integer, sa.ForeignKey("car_make.id"), unique=False, nullable=False)

    car_make = sa_orm.relationship("CarMakeORM", back_populates="models")
