package carPartsStore.controllers;

import carPartsStore.data.CarPart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CarPartDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;

    @JsonIgnoreProperties("carParts")
    @JsonProperty("car-models")
    private List<CarModelDTO> carModels;

    public CarPartDTO() {
    }

    public CarPartDTO(CarPart carPart) {
        this.id = carPart.getId();
        this.name = carPart.getName();
        this.price = carPart.getPrice();
        this.description = carPart.getDescription();
    }
}
