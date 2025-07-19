package carPartsStore.controllers;

import carPartsStore.db.CarMakeRepository;
import carPartsStore.db.CarModel;
import carPartsStore.db.CarModelRepository;
import carPartsStore.dto.CarMakeDTO;
import carPartsStore.dto.CarModelDTO;
import carPartsStore.dto.CarPartDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = CarModelsController.ROOT)
@Tag(name = CarModelsController.NAME, description =  CarModelsController.NAME + " management API")
public class CarModelsController {
    static public final String NAME = "car-models";
    static public final String ROOT = "/" + NAME;

    private final CarMakeRepository makeRepository;
    private final CarModelRepository modelRepository;

    private CarModelsController(CarMakeRepository makeRepository, CarModelRepository modelRepository) {
        this.makeRepository = makeRepository;
        this.modelRepository = modelRepository;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve " + NAME + " entry by id")
    private ResponseEntity<CarModelDTO> getById(@PathVariable Long id) {
        var entity = modelRepository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        var dto = new CarModelDTO(entity);
        dto.setCarMake(new CarMakeDTO(entity.getCarMake()));
        dto.setCarParts(entity.getCarParts().stream().map(CarPartDTO::new).toList());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Get list of " + NAME + " entries")
    private ResponseEntity<List<CarModelDTO>> findAll(Pageable pageable) {
        var page = modelRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return ResponseEntity.ok(page.getContent().stream().map(CarModelDTO::new).toList());
    }

    @PostMapping
    @Operation(summary = "Create new " + NAME + " entry")
    private ResponseEntity<Void> create(@RequestBody CarModelDTO request, UriComponentsBuilder ucb) {
        var makeFind = makeRepository.findById(request.getCarMakeId());
        if (makeFind.isEmpty()) return ResponseEntity.notFound().build();

        var entity = new CarModel(request);
        entity.setCarMake(makeFind.get());
        entity = modelRepository.save(entity);
        URI location = ucb.path(NAME + "/{id}").buildAndExpand(entity.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update " + NAME + " entry")
    private ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CarModelDTO request) {
        var entity = modelRepository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        var makeFind = makeRepository.findById(request.getCarMakeId());
        if (makeFind.isEmpty()) return ResponseEntity.notFound().build();

        entity.setName(request.getName());
        entity.setYear(request.getYear());
        entity.setPrice(request.getPrice());
        entity.setCarMake(makeFind.get());
        modelRepository.save(entity);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete " + NAME + " entry")
    private ResponseEntity<Void> delete(@PathVariable Long id) {
        if (modelRepository.existsById(id)) {
            modelRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}