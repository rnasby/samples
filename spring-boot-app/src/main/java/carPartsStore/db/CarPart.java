package carPartsStore.db;

import carPartsStore.dto.CarPartDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "T_CAR_PART")
public class CarPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private String name, description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "T_CAR_MODEL_PART",
        joinColumns = {@JoinColumn(name = "CAR_PART_ID", referencedColumnName = "id", insertable = false,
                updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "CAR_MODEL_ID", referencedColumnName = "id", insertable = false,
                updatable = false)}
    )
    private List<CarModel> carModels = new ArrayList<>();

    public CarPart() {
    }

    public CarPart(CarPartDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.description = dto.getDescription();
    }
}