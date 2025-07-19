package carPartsStore.controllers;

import carPartsStore.db.*;
import carPartsStore.dto.CarPartDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = CarModelsPartsController.ROOT)
@Tag(name = CarModelsPartsController.NAME, description = CarModelsPartsController.NAME + " management API")
public class CarModelsPartsController {
    static public final String NAME = CarModelsController.NAME + "/{modelId}/parts";
    static public final String ROOT = "/" + NAME;

    private final CarPartRepository partRepository;
    private final CarModelRepository modelRepository;

    private CarModelsPartsController(CarPartRepository partRepository, CarModelRepository modelRepository) {
        this.partRepository = partRepository;
        this.modelRepository = modelRepository;
    }

    @GetMapping
    @Operation(summary = "Get list of " + NAME + " entries")
    private ResponseEntity<List<CarPartDTO>> findAll(@PathVariable Long modelId) {
        var model = modelRepository.findById(modelId).orElse(null);
        if (model == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(model.getCarParts().stream().map(CarPartDTO::new).toList());
    }

    @PostMapping("/{partId}")
    @Operation(summary = "Create new " + NAME + " entry")
    private ResponseEntity<Void> create(@PathVariable Long modelId, @PathVariable Long partId, UriComponentsBuilder ucb) {
        var model = modelRepository.findById(modelId).orElse(null);
        if (model == null) return ResponseEntity.notFound().build();

        var part = partRepository.findById(partId).orElse(null);
        if (part == null) return ResponseEntity.notFound().build();

        model.getCarParts().add(part);
        modelRepository.save(model);
        URI location = ucb.path(NAME + "/{partId}").buildAndExpand(modelId, partId).toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{partId}")
    @Operation(summary = "Delete " + NAME + " entry")
    private ResponseEntity<Void> delete(@PathVariable Long modelId, @PathVariable Long partId) {
        var model = modelRepository.findById(modelId).orElse(null);
        if (model == null) return ResponseEntity.notFound().build();

        var part = partRepository.findById(partId).orElse(null);
        if (part == null) return ResponseEntity.notFound().build();

        model.getCarParts().remove(part);
        modelRepository.save(model);

        return ResponseEntity.noContent().build();
    }
}
