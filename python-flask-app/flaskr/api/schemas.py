from marshmallow import Schema, fields

class CarMakeUpdate(Schema):
    name = fields.Str(required=True)

class CarMakeEntry(CarMakeUpdate):
    id = fields.Int(dump_only=True)


class CarModelUpdate(Schema):
    name = fields.Str(required=True)
    year = fields.Int(required=True)
    price = fields.Float(required=True)
    make_id = fields.Int(required=True)

class CarModelEntry(CarModelUpdate):
    id = fields.Int(dump_only=True)


class CarPartUpdate(Schema):
    name = fields.Str(required=True)
    price = fields.Float(required=True)

class CarPartEntry(CarPartUpdate):
    id = fields.Int(dump_only=True)

# ------------------------------ Combo schemas ---------------------------------------

class CarMake(CarMakeEntry):
    car_models = fields.List(fields.Nested(CarModelEntry()), dump_only=True)

class CarModel(CarModelEntry):
    car_make_id = fields.Int(required=True, load_only=True)
    car_make = fields.Nested(CarMakeEntry(), dump_only=True)

class CarPart(CarPartEntry):
    pass

