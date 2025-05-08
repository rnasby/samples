from flaskr.db import db
import sqlalchemy as sa


class CarModelPartORM(db.Model):
    __tablename__ = "car_model_part"

    id = sa.Column("id", sa.Integer, primary_key=True)
    model_id = sa.Column("model_id", sa.Integer, sa.ForeignKey("car_model.id"))
    part_id = sa.Column("part_id", sa.Integer, sa.ForeignKey("car_part.id"))

