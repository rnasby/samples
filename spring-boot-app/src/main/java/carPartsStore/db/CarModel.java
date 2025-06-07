package carPartsStore.db;

import carPartsStore.dto.CarModelDTO;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "T_CAR_MODEL")
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("carModels")
    private CarMake carMake;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "T_CAR_MODEL_PART",
        joinColumns = {@JoinColumn(name = "CAR_MODEL_ID", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "CAR_PART_ID", referencedColumnName = "id")}
    )
    private List<CarPart> carParts = new ArrayList<>();

    private int year;
    private String name;
    private double price;

    public CarModel() {
    }

    public CarModel(CarModelDTO dto) {
        this.name = dto.getName();
        this.year = dto.getYear();
        this.price = dto.getPrice();
    }
}