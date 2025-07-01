package carPartsStore;

import carPartsStore.auth.AuthController;
import carPartsStore.auth.TokenDTO;
import carPartsStore.controllers.CarMakesController;
import carPartsStore.controllers.CarModelsController;
import carPartsStore.controllers.CarModelsPartsController;
import carPartsStore.controllers.CarPartsController;
import carPartsStore.dto.*;
import framework.RestTests;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class AppTests {
    public final RestTests rest;
    private final TestRestTemplate restTemplate;

    AppTests(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.rest = new RestTests(restTemplate);
    }

    public TokenDTO loginFred() {
        return loginOk("fred", "pebbles");
    }

    public TokenDTO loginOk(String username, String password) {
        var dto = validateTokenRequest(login(username, password));

        assertThat(dto.refreshToken()).isNotNull();
        rest.setLastRefreshToken(dto.refreshToken());

        return dto;
    }

    public ResponseEntity<TokenDTO> login(String username, String password) {
        return restTemplate.withBasicAuth(username, password).postForEntity(AuthController.ROOT + AuthController.LOGIN,
                null, TokenDTO.class);
    }

    private TokenDTO validateTokenRequest(ResponseEntity<TokenDTO> reply) {
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.hasBody()).isTrue();

        var headers = reply.getHeaders().get("Content-Type");
        assertThat(headers).isNotNull();
        assertThat(headers.getFirst()).isEqualTo(MediaType.APPLICATION_JSON.toString());

        var dto = reply.getBody();
        assertThat(dto).isNotNull();

        assertThat(dto.accessToken()).isNotNull();
        rest.setLastAccessToken(dto.accessToken());

        return dto;
    }

    public ResponseEntity<String> logout() {
        try {
            return rest.newCall(AuthController.ROOT + AuthController.LOGOUT, HttpMethod.POST, String.class).withAuth()
                    .call();
        } finally {
            rest.clearTokens();
        }
    }

    public TokenDTO refreshTokenOk() {
        return validateTokenRequest(refreshToken());
    }

    public ResponseEntity<TokenDTO> refreshToken() {
        return rest.newCall(AuthController.ROOT + AuthController.REFRESH, HttpMethod.POST, TokenDTO.class)
                .withRefreshToken().call();
    }

    public void assertFordMake(CarMakeDTO make) {
        assertThat(make.getId()).isEqualTo(1L);
        assertThat(make.getName()).isEqualTo("Ford");
    }

    public void assertMustangModel(CarModelDTO model) {
        assertThat(model.getId()).isEqualTo(1L);
        assertThat(model.getName()).isEqualTo("Mustang");
        assertThat(model.getCarMakeId()).isEqualTo(1L);
        assertThat(model.getYear()).isEqualTo(1979);
        assertThat(model.getPrice()).isEqualTo(6700.00);
    }

    public void assertAlternatorPart(CarPartDTO part) {
        assertThat(part.getId()).isEqualTo(1L);
        assertThat(part.getName()).isEqualTo("Alternator");
        assertThat(part.getPrice()).isEqualTo(500.50);
    }

    public ResponseEntity<CarMakeDTO> getCarMake(Long id) {
        String url = CarMakesController.ROOT + "/" + id;
        return rest.newCall(url, HttpMethod.GET, CarMakeDTO.class).withAuth().call();
    }

    public ResponseEntity<Void> addCarMake(String name) {
        var dto = CarMakeDTO.builder().name(name).build();
        var reply = rest.newCall(CarMakesController.ROOT, HttpMethod.POST, Void.class).withRequest(dto)
                .withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarMakes() {
        addCarMake("Ford");
        addCarMake("Chevy");
    }

    public ResponseEntity<CarModelDTO> getCarModel(Long id) {
        String url = CarModelsController.ROOT + "/" + id;
        return rest.newCall(url, HttpMethod.GET, CarModelDTO.class).withAuth().call();
    }

    public ResponseEntity<Void> addCarModel(String name, Long makeId, Integer year, Double price) {
        var dto = CarModelDTO.builder().name(name).carMakeId(makeId).year(year).price(price).build();
        var reply = rest.newCall(CarModelsController.ROOT, HttpMethod.POST, Void.class).withRequest(dto)
                .withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarModels() {
        addCarModel("Mustang", 1L, 1979, 6700.00);
        addCarModel("Corvette", 2L, 1981, 15000.00);
    }

    public ResponseEntity<CarPartDTO> getCarPart(Long id) {
        String url = CarPartsController.ROOT + "/" + id;
        return rest.newCall(url, HttpMethod.GET, CarPartDTO.class).withAuth().call();
    }

    public ResponseEntity<Void> addCarPart(String name, Double price) {
        var dto = CarPartDTO.builder().name(name).price(price).build();
        var reply = rest.newCall(CarPartsController.ROOT, HttpMethod.POST, Void.class).withRequest(dto).withAuth()
                .call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarParts() {
        addCarPart("Alternator", 500.50);
        addCarPart("Motor", 9500.50);
    }

    public void addCarModelPart(Long modelId, Long partId) {
        String url = CarModelsPartsController.ROOT.replace("{modelId}", modelId.toString()) + "/" + partId;
        var reply = rest.newCall(url, HttpMethod.POST, Void.class).withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    public void addCarModelParts() {
        addCarModelPart(1L, 1L);
        addCarModelPart(1L, 2L);
        addCarModelPart(2L, 1L);
        addCarModelPart(2L, 2L);
    }

    public void setup() {
        loginFred();
        addCarMakes();
        addCarModels();
        addCarParts();
        addCarModelParts();
    }
}