package carPartsStore.controllers;

import carPartsStore.auth.AuthDTO;
import carPartsStore.auth.AuthController;
import carPartsStore.dto.*;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class Common {
    private TokenDTO lastAuth;
    final TestRestTemplate restTemplate;

    Common(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <REQ, RES> ResponseEntity<RES> call(String uri, HttpMethod method, REQ request, Class<RES> replyType) {
       var headers = new HttpHeaders();
       if (lastAuth != null) headers.set("Authorization", "Bearer " + lastAuth.accessToken());

       return restTemplate.exchange(uri, method, new HttpEntity<>(request, headers), replyType);
    }

    public ResponseEntity<TokenDTO> login(String username, String password) {
        var request = new AuthDTO(username, password);
        return restTemplate.postForEntity(AuthController.ROOT + AuthController.LOGIN, request, TokenDTO.class);
    }

    public void validateTokenRequest(ResponseEntity<TokenDTO> reply) {
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.hasBody()).isTrue();

        var headers = reply.getHeaders().get("Content-Type");
        assertThat(headers).isNotNull();
        assertThat(headers.getFirst()).isEqualTo(MediaType.APPLICATION_JSON.toString());

        lastAuth = reply.getBody();
        assertThat(lastAuth).isNotNull();
    }

    public void loginOk(String username, String password) {
        validateTokenRequest(login(username, password));
    }

    public void loginFred() {
        loginOk("fred", "pebbles");
    }

    public ResponseEntity<String> logout() {
        lastAuth = null;
        return restTemplate.postForEntity(AuthController.LOGOUT, null, String.class);
    }

    public ResponseEntity<TokenDTO> refreshToken(String refreshTokenStr) {
        return restTemplate.postForEntity(AuthController.REFRESH, refreshTokenStr, TokenDTO.class);
    }

    public void refreshTokenOk(String refreshToken) {
        validateTokenRequest(refreshToken(refreshToken));
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
        return call(url, HttpMethod.GET, null, CarMakeDTO.class);
    }

    public ResponseEntity<Void> addCarMake(String name) {
        var dto = CarMakeDTO.builder().name(name).build();
        var reply = call(CarMakesController.ROOT, HttpMethod.POST, dto, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarMakes() {
        addCarMake("Ford");
        addCarMake("Chevy");
    }

    public ResponseEntity<CarModelDTO> getCarModel(Long id) {
        String url = CarModelsController.ROOT + "/" + id;
        return restTemplate.getForEntity(url, CarModelDTO.class);
    }

    public ResponseEntity<Void> addCarModel(String name, Long makeId, Integer year, Double price) {
        var dto = CarModelDTO.builder().name(name).carMakeId(makeId).year(year).price(price).build();
        var reply = call(CarModelsController.ROOT, HttpMethod.POST, dto, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarModels() {
        addCarModel("Mustang", 1L, 1979, 6700.00);
        addCarModel("Corvette", 2L, 1981, 15000.00);
    }

    public ResponseEntity<CarPartDTO> getCarPart(Long id) {
        String url = CarPartsController.ROOT + "/" + id;
        return call(url, HttpMethod.GET, null, CarPartDTO.class);
    }

    public ResponseEntity<Void> addCarPart(String name, Double price) {
        var dto = CarPartDTO.builder().name(name).price(price).build();
        var reply = call(CarPartsController.ROOT, HttpMethod.POST, dto, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarParts() {
        addCarPart("Alternator", 500.50);
        addCarPart("Motor", 9500.50);
    }

    public ResponseEntity<Void> addCarModelPart(Long modelId, Long partId) {
        String url = CarModelsPartsController.ROOT.replace("{modelId}", modelId.toString()) + "/" + partId;
        var reply = call(url, HttpMethod.POST, null, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
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