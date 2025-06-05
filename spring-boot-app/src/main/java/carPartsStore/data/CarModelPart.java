package carPartsStore.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "t_car_model_part")
public class CarModelPart {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CarPart carPart;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CarModel carModel;

    public CarModelPart() {
    }

    public CarModelPart(CarModel carModel, CarPart carPart) {
        this.carModel = carModel;
        this.carPart = carPart;
    }
}
