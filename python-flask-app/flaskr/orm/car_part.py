from flaskr.db import DB
import sqlalchemy as sa


class CarPartORM(DB.Model):
    __tablename__ = "car_part"

    id = sa.Column("id", sa.Integer, primary_key=True)
    name = sa.Column("name", sa.String(255), unique=False, nullable=False)
    price = sa.Column("price", sa.Float(precision=2), unique=False, nullable=False)

    models = DB.relationship("CarModelORM", back_populates="parts", secondary="car_model_part")