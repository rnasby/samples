package carPartsStore.controllers;

import carPartsStore.data.CarMake;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CarMakeDTO {
    private Long id;
    private String name;

    @JsonIgnoreProperties("carMake")
    @JsonProperty("car-models")
    private List<CarModelDTO> carModels;

    public CarMakeDTO() {
    }

    public CarMakeDTO(CarMake carMake) {
        this.id = carMake.getId();
        this.name = carMake.getName();
    }
}
