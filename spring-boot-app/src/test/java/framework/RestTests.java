package framework;

import carPartsStore.auth.TokenDTO;
import lombok.Setter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class RestTests {
    private final TestRestTemplate restTemplate;

    @Setter private String lastAccessToken;
    @Setter private String lastRefreshToken;

    public RestTests(TestRestTemplate restTemplate) {
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

    public <RES> Call<RES> newCall(String uri, HttpMethod method, Class<RES> replyType) {
        return new Call<RES>(uri, method, replyType);
    }

    public void clearTokens() {
        lastAccessToken = null;
        lastRefreshToken = null;
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