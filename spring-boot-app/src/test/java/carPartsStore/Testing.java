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
public class Testing {
    public final RestTests rest;

    Testing(TestRestTemplate restTemplate) {
        this.rest = new RestTests(restTemplate);
    }

    public TokenDTO loginFred() {
        return loginOk("fred", "pebbles");
    }

    public TokenDTO loginOk(String username, String password) {
        return validateTokenRequest(login(username, password));
    }

    public RestTests.Reply login(String username, String password) {
        return rest.newPost(AuthController.ROOT + AuthController.LOGIN).withBasicAuth(username, password).call();
    }

    private TokenDTO validateTokenRequest(RestTests.Reply reply) {
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var headers = reply.getHeaders().get("Content-Type");
        assertThat(headers).isNotNull();
        assertThat(headers.getFirst()).isEqualTo(MediaType.APPLICATION_JSON.toString());

        var dto = reply.parseBody(TokenDTO.class);

        assertThat(dto.accessToken()).isNotNull();
        rest.setLastAccessToken(dto.accessToken());

        assertThat(dto.refreshToken()).isNotNull();
        rest.setLastRefreshToken(dto.refreshToken());


        return dto;
    }

    public RestTests.Reply logout() {
        try {
            return rest.newCall(AuthController.ROOT + AuthController.LOGOUT, HttpMethod.POST).withAccessToken().call();
        } finally {
            rest.clearTokens();
        }
    }

    public TokenDTO refreshTokenOk() {
        return validateTokenRequest(refreshToken());
    }

    public RestTests.Reply refreshToken() {
        return rest.newCall(AuthController.ROOT + AuthController.REFRESH, HttpMethod.POST).withLastRefreshToken().call();
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

    public RestTests.Reply getCarMake(Long id) {
        String url = CarMakesController.ROOT + "/" + id;
        return rest.newCall(url, HttpMethod.GET).withAccessToken().call();
    }

    public RestTests.Reply addCarMake(String name) {
        var dto = CarMakeDTO.builder().name(name).build();
        var reply = rest.newCall(CarMakesController.ROOT, HttpMethod.POST).withRequest(dto)
                .withAccessToken().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarMakes() {
        addCarMake("Ford");
        addCarMake("Chevy");
    }

    public RestTests.Reply getCarModel(Long id) {
        String url = CarModelsController.ROOT + "/" + id;
        return rest.newCall(url, HttpMethod.GET).withAccessToken().call();
    }

    public RestTests.Reply addCarModel(String name, Long makeId, Integer year, Double price) {
        var dto = CarModelDTO.builder().name(name).carMakeId(makeId).year(year).price(price).build();
        var reply = rest.newCall(CarModelsController.ROOT, HttpMethod.POST).withRequest(dto).withAccessToken()
                .call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarModels() {
        addCarModel("Mustang", 1L, 1979, 6700.00);
        addCarModel("Corvette", 2L, 1981, 15000.00);
    }

    public RestTests.Reply getCarPart(Long id) {
        String url = CarPartsController.ROOT + "/" + id;
        return rest.newCall(url, HttpMethod.GET).withAccessToken().call();
    }

    public RestTests.Reply addCarPart(String name, Double price) {
        var dto = CarPartDTO.builder().name(name).price(price).build();
        var reply = rest.newCall(CarPartsController.ROOT, HttpMethod.POST).withRequest(dto).withAccessToken().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarParts() {
        addCarPart("Alternator", 500.50);
        addCarPart("Motor", 9500.50);
    }

    public void addCarModelPart(Long modelId, Long partId) {
        String url = CarModelsPartsController.ROOT.replace("{modelId}", modelId.toString()) + "/" + partId;
        var reply = rest.newCall(url, HttpMethod.POST).withAccessToken().call();
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