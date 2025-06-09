package carPartsStore.dto;

import carPartsStore.db.CarModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CarModelDTO {
    private Long id;
    private Integer year;
    private String name;
    private Double price;

    @JsonProperty("car-make-id")
    private Long carMakeId;

    @JsonIgnoreProperties("carModel")
    @JsonProperty("car-make")
    private CarMakeDTO carMake;

    @JsonIgnoreProperties("carModels")
    @JsonProperty("car-parts")
    private List<CarPartDTO> carParts;

    public CarModelDTO() {
    }

    public CarModelDTO(CarModel carModel) {
        this.id = carModel.getId();
        this.year = carModel.getYear();
        this.name = carModel.getName();
        this.price = carModel.getPrice();
        this.carMakeId = carModel.getCarMake() != null ? carModel.getCarMake().getId() : null;
    }
}
