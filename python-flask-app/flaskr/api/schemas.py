from marshmallow import Schema, fields

class CarMakeChange(Schema):
    name = fields.Str(required=True)

class CarMakeEntry(CarMakeChange):
    id = fields.Int(dump_only=True)


class CarModelChange(Schema):
    name = fields.Str(required=True)
    year = fields.Int(required=True)
    price = fields.Float(required=True)
    make_id = fields.Int(required=True)

class CarModelEntry(CarModelChange):
    id = fields.Int(dump_only=True)


class CarPartChange(Schema):
    name = fields.Str(required=True)
    price = fields.Float(required=True)

class CarPartEntry(CarPartChange):
    id = fields.Int(dump_only=True)

# ------------------------------ Combo schemas ---------------------------------------

class CarMake(CarMakeEntry):
    models = fields.List(fields.Nested("CarModel"), dump_only=True)

class CarModel(CarModelEntry):
    make = fields.Nested(CarMakeEntry(), dump_only=True)
    parts = fields.List(fields.Nested("CarPart"), dump_only=True)

class CarPart(CarPartEntry):
    models = fields.List(fields.Nested(CarModelEntry()), dump_only=True)
