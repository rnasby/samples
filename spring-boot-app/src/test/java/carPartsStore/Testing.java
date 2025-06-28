package carPartsStore;

import carPartsStore.auth.AuthController;
import carPartsStore.auth.TokenDTO;
import carPartsStore.controllers.CarMakesController;
import carPartsStore.controllers.CarModelsController;
import carPartsStore.controllers.CarModelsPartsController;
import carPartsStore.controllers.CarPartsController;
import carPartsStore.dto.*;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class Testing {
    private final TestRestTemplate restTemplate;
    private String lastAccessToken, lastRefreshToken;

    Testing(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <RES> Call<RES> newGet(String uri, Class<RES> replyType) {
        return newCall(uri, HttpMethod.GET, replyType);
    }

    public <RES> Call<RES> newPost(String uri, Class<RES> replyType) {
        return newCall(uri, HttpMethod.POST, replyType);
    }

    public <RES> Call<RES> newPut(String uri) {
        return newCall(uri, HttpMethod.PUT, (Class<RES>)Void.class);
    }

    public <RES> Call<RES> newDelete(String uri) {
        return newCall(uri, HttpMethod.DELETE, (Class<RES>)Void.class);
    }

    private <RES> Call<RES> newCall(String uri, HttpMethod method, Class<RES> replyType) {
        return new Call<RES>(uri, method, replyType);
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

        lastAccessToken = dto.accessToken();
        assertThat(lastAccessToken).isNotNull();

        return dto;
    }

    public TokenDTO loginOk(String username, String password) {
        var dto = validateTokenRequest(login(username, password));

        lastRefreshToken = dto.refreshToken();
        assertThat(lastRefreshToken).isNotNull();

        return dto;
    }

    public TokenDTO loginFred() {
        return loginOk("fred", "pebbles");
    }

    public ResponseEntity<String> logout() {
        try {
            return newCall(AuthController.ROOT + AuthController.LOGOUT, HttpMethod.POST, String.class).withAuth().call();
        } finally {
            lastAccessToken = null;
            lastRefreshToken = null;
        }
    }

    public ResponseEntity<TokenDTO> refreshToken() {
        return newCall(AuthController.ROOT + AuthController.REFRESH, HttpMethod.POST, TokenDTO.class).withRefreshToken().call();
    }

    public TokenDTO refreshTokenOk() {
        return validateTokenRequest(refreshToken());
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
        return newCall(url, HttpMethod.GET, CarMakeDTO.class).withAuth().call();
    }

    public ResponseEntity<Void> addCarMake(String name) {
        var dto = CarMakeDTO.builder().name(name).build();
        var reply = newCall(CarMakesController.ROOT, HttpMethod.POST, Void.class).withRequest(dto)
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
        return newCall(url, HttpMethod.GET, CarModelDTO.class).withAuth().call();
    }

    public ResponseEntity<Void> addCarModel(String name, Long makeId, Integer year, Double price) {
        var dto = CarModelDTO.builder().name(name).carMakeId(makeId).year(year).price(price).build();
        var reply = newCall(CarModelsController.ROOT, HttpMethod.POST, Void.class).withRequest(dto)
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
        return newCall(url, HttpMethod.GET, CarPartDTO.class).withAuth().call();
    }

    public ResponseEntity<Void> addCarPart(String name, Double price) {
        var dto = CarPartDTO.builder().name(name).price(price).build();
        var reply = newCall(CarPartsController.ROOT, HttpMethod.POST, Void.class).withRequest(dto).withAuth()
                .call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        return reply;
    }

    public void addCarParts() {
        addCarPart("Alternator", 500.50);
        addCarPart("Motor", 9500.50);
    }

    public ResponseEntity<Void> addCarModelPart(Long modelId, Long partId) {
        String url = CarModelsPartsController.ROOT.replace("{modelId}", modelId.toString()) + "/" + partId;
        var reply = newCall(url, HttpMethod.POST, Void.class).withAuth().call();
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

    public class Call<RES> {
        private final String uri;
        private final HttpMethod method;
        private final Class<RES> replyType;

        private String token;
        private Object request;

        private Call(String uri, HttpMethod method, Class<RES> replyType) {
            this.uri = uri;
            this.method = method;
            this.replyType = replyType;
        }

        public Call<RES> withRequest(Object request) {
            this.request = request;
            return this;
        }

        public Call<RES> withAuth() {
            if (lastAccessToken == null) throw new IllegalStateException("No last access token available");
            token = lastAccessToken;
            return this;
        }

        public Call<RES> withRefreshToken() {
            if (lastRefreshToken == null) throw new IllegalStateException("No last refresh token available");
            token = lastRefreshToken;
            return this;
        }

        public Call<RES> withToken(String token) {
            this.token = token;
            return this;
        }

        public ResponseEntity<RES> call() {
            var headers = new HttpHeaders();
            if (token != null) headers.set("Authorization", "Bearer " + token);

            return restTemplate.exchange(uri, method, new HttpEntity<>(request, headers), replyType);
        }
    }
}