package carPartsStore.controllers;

import carPartsStore.db.CarMake;
import carPartsStore.db.CarMakeRepository;
import carPartsStore.dto.CarMakeDTO;
import carPartsStore.dto.CarModelDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(CarMakesController.ROOT)
public class CarMakesController {
    static public final String NAME = "car-makes";
    static public final String ROOT = "/" + NAME;

    private final CarMakeRepository repository;

    private CarMakesController(CarMakeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    private ResponseEntity<CarMakeDTO> getById(@PathVariable Long id) {
        var entity = repository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        var dto = new CarMakeDTO(entity);
        dto.setCarModels(entity.getCarModels().stream().map(CarModelDTO::new).toList());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    private ResponseEntity<Iterable<CarMakeDTO>> findAll(Pageable pageable) {
        var page = repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return ResponseEntity.ok(page.getContent().stream().map(CarMakeDTO::new).toList());
    }

    @PostMapping
    private ResponseEntity<Void> create(@RequestBody CarMakeDTO request, UriComponentsBuilder ucb) {
        var entity = repository.save(new CarMake(request.getName()));
        URI location = ucb.path(NAME + "/{id}").buildAndExpand(entity.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CarMakeDTO request) {
        var entity = repository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        entity.setName(request.getName());
        repository.save(entity);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}