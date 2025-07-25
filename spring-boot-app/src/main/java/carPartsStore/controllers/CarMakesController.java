package carPartsStore.controllers;

import carPartsStore.db.CarMake;
import carPartsStore.db.CarMakeRepository;
import carPartsStore.dto.CarMakeDTO;
import carPartsStore.dto.CarModelDTO;
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

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping(value = CarMakesController.ROOT)
@Tag(name = CarMakesController.NAME, description = CarMakesController.NAME + " management API")
public class CarMakesController {
    static public final String NAME = "car-makes";
    static public final String ROOT = "/" + NAME;

    private final CarMakeRepository repository;

    private CarMakesController(CarMakeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve " + NAME + " entry by id")
    private ResponseEntity<CarMakeDTO> getById(@PathVariable Long id) {
        var entity = repository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        var dto = new CarMakeDTO(entity);
        dto.setCarModels(entity.getCarModels().stream().map(CarModelDTO::new).toList());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "Get list of " + NAME + " entries")
    private ResponseEntity<Iterable<CarMakeDTO>> findAll(Pageable pageable) {
        var page = repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return ResponseEntity.ok(page.getContent().stream().map(CarMakeDTO::new).toList());
    }

    @PostMapping
    @Operation(summary = "Create new " + NAME + " entry")
    private ResponseEntity<Void> create(@RequestBody CarMakeDTO request, UriComponentsBuilder ucb) {
        var entity = repository.save(new CarMake(request.getName()));
        URI location = ucb.path(NAME + "/{id}").buildAndExpand(entity.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update " + NAME + " entry")
    private ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CarMakeDTO request) {
        var entity = repository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        entity.setName(request.getName());
        repository.save(entity);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete " + NAME + " entry")
    private ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}