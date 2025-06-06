package carPartsStore.controllers;

import carPartsStore.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class Common {
    static final String LOGIN_API = "/auth/login";
    static final String LOGOUT_API = "/auth/logout";
    static final String REFRESH_API = "/auth/refresh";

    final TestRestTemplate restTemplate;

    Common(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<TokenDTO> login(String username, String password) {
        LoginDTO loginData = new LoginDTO(username, password);
        return restTemplate.postForEntity(LOGIN_API, loginData, TokenDTO.class);
    }

    public String validateTokenRequest(ResponseEntity<TokenDTO> reply) {
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.hasBody()).isTrue();

        var headers = reply.getHeaders().get("Content-Type");
        assertThat(headers).isNotNull();
        assertThat(headers.getFirst()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        TokenDTO token = reply.getBody();
        String tokenStr = (token == null ? null : token.accessToken());
        assertThat(tokenStr).isNotNull();

        return tokenStr;
    }

    public String loginOk(String username, String password) {
        return validateTokenRequest(login(username, password));
    }

    public String loginFred() {
        return loginOk("fred", "pebbles");
    }

    public ResponseEntity<Void> logout() {
        return restTemplate.postForEntity(LOGOUT_API, null, Void.class);
    }

    public ResponseEntity<TokenDTO> refreshToken(String refreshTokenStr) {
        return restTemplate.postForEntity(REFRESH_API, new TokenDTO(refreshTokenStr), TokenDTO.class);
    }

    public String refreshTokenOk(String refreshToken) {
        return validateTokenRequest(refreshToken(refreshToken));
    }

    public void assertFordMake(CarMakeDTO make) {
        assertThat(make.getId()).isEqualTo(1);
        assertThat(make.getName()).isEqualTo("Ford");
    }

    public void assertMustangModel(CarModelDTO model) {
        assertThat(model.getId()).isEqualTo(1);
        assertThat(model.getName()).isEqualTo("Mustang");
        assertThat(model.getCarMake().getId()).isEqualTo(1);
        assertThat(model.getYear()).isEqualTo(1979);
        assertThat(model.getPrice()).isEqualTo(6700.00);
    }

    public void assertAlternatorPart(CarPartDTO part) {
        assertThat(part.getId()).isEqualTo(1);
        assertThat(part.getName()).isEqualTo("Alternator");
        assertThat(part.getPrice()).isEqualTo(500.50);
    }

    public ResponseEntity<CarMakeDTO> getCarMake(Long id) {
        String url = CarMakesController.ROOT + "/" + id;
        return restTemplate.getForEntity(url, CarMakeDTO.class);
    }

    public ResponseEntity<Void> addCarMake(String name) {
        var vo = CarMakeDTO.builder().name(name).build();
        var reply = restTemplate.postForEntity(CarMakesController.NAME, vo, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarMakes() {
        addCarMake("Ford");
        addCarMake("Chevy");
    }

    public ResponseEntity<CarModelDTO> getCarModel(Long id) {
        String url = CarMakesController.ROOT + "/" + id;
        return restTemplate.getForEntity(url, CarModelDTO.class);
    }

    public ResponseEntity<Void> addCarModel(String name, Long makeId, Integer year, Double price) {
        String url = CarMakesController.ROOT + "/" + makeId + "/car-models";
        var vo = CarModelDTO.builder().name(name).carMakeId(makeId).year(year).price(price).build();
        var reply = restTemplate.postForEntity(url, vo, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarModels() {
        addCarModel("Mustang", 1L, 1979, 6700.00);
        addCarModel("Corvette", 2L, 1981, 15000.00);
    }

    public ResponseEntity<CarPartDTO> getCarPart(Long id) {
        String url = CarPartsController.NAME + "/" + id;
        return restTemplate.getForEntity(url, CarPartDTO.class);
    }

    public ResponseEntity<Void> addCarPart(String name, Double price) {
        var vo = CarPartDTO.builder().name(name).price(price).build();
        var reply = restTemplate.postForEntity(CarPartsController.NAME, vo, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarParts() {
        addCarPart("Alternator", 500.50);
        addCarPart("Motor", 9500.50);
    }

    public ResponseEntity<Void> addCarModelPart(Long model_id, Long part_id) {
        String url = CarModelsPartsController.NAME.replace("{modelId}", model_id.toString()).replace("{partId}",
                part_id.toString());
        var reply = restTemplate.postForEntity(url, null, Void.class);
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