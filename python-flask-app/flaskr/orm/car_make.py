from flaskr.db import db
import sqlalchemy as sa
import sqlalchemy.orm as sa_orm


class CarMakeORM(db.Model):
    __tablename__ = "car_make"

    id = sa.Column("id", sa.Integer(), primary_key=True)

    name = sa.Column("name", sa.String(255), unique=True, nullable=False)

    models = sa_orm.relationship("CarModelORM", back_populates="car_make", lazy="dynamic")
