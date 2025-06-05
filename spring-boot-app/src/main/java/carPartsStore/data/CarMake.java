package carPartsStore.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "T_CAR_MAKE")
public class CarMake {
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "carMake", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarModel> carModels = new ArrayList<>();

    public CarMake() {
    }

    public CarMake(String name) {
        this.name = name;
    }
}