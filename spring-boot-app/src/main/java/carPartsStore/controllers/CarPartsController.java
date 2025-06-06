package carPartsStore.controllers;

import carPartsStore.db.CarPart;
import carPartsStore.db.CarPartRepository;
import carPartsStore.dto.CarModelDTO;
import carPartsStore.dto.CarPartDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(CarPartsController.ROOT)
public class CarPartsController {
    static public final String NAME = "car-parts";
    static public final String ROOT = "/" + NAME;

    private final CarPartRepository repository;

    private CarPartsController(CarPartRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    private ResponseEntity<CarPartDTO> getById(@PathVariable Long id) {
        var entity = repository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        var dto = new CarPartDTO(entity);
        dto.setCarModels(entity.getCarModels().stream().map(CarModelDTO::new).toList());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    private ResponseEntity<List<CarPartDTO>> findAll(Pageable pageable) {
        var page = repository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "name"))));

        return ResponseEntity.ok(page.getContent().stream().map(CarPartDTO::new).toList());
    }

    @PostMapping
    private ResponseEntity<Void> create(@RequestBody CarPartDTO request, UriComponentsBuilder ucb) {
        var entity = repository.save(new CarPart(request));
        URI location = ucb.path(NAME + "/{id}").buildAndExpand(entity.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CarPartDTO request) {
        var entity = repository.findById(id).orElse(null);
        if (entity == null) return ResponseEntity.notFound().build();

        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
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