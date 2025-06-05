package carPartsStore.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarPartRepository extends JpaRepository<CarPart, Long> {
}