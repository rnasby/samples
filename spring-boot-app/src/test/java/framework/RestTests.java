package framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.io.IOException;

public class RestTests {
    private final TestRestTemplate restTemplate;

    @Setter private String lastAccessToken;
    @Setter private String lastRefreshToken;

    public RestTests(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Call newGet(String uri) {
        return newCall(uri, HttpMethod.GET);
    }

    public Call newPost(String uri) {
        return newCall(uri, HttpMethod.POST);
    }

    public Call newPut(String uri) {
        return newCall(uri, HttpMethod.PUT);
    }

    public Call newDelete(String uri) {
        return newCall(uri, HttpMethod.DELETE);
    }

    public Call newCall(String uri, HttpMethod method) {
        return new Call(uri, method);
    }

    public void clearTokens() {
        lastAccessToken = null;
        lastRefreshToken = null;
    }

    public class Call {
        private final String uri;
        private final HttpMethod method;

        private String token;
        private Object request;
        private String basicAuthUsername, basicAuthPassword;

        private Call(String uri, HttpMethod method) {
            this.uri = uri;
            this.method = method;
        }

        public Call withRequest(Object request) {
            this.request = request;
            return this;
        }

        public Call withAccessToken() {
            if (lastAccessToken == null) throw new IllegalStateException("No last access token available");
            withToken(lastAccessToken);
            return this;
        }

        public Call withLastRefreshToken() {
            if (lastRefreshToken == null) throw new IllegalStateException("No last refresh token available");
            withToken(lastRefreshToken);
            return this;
        }

        public Call withToken(String token) {
            this.token = token;

            basicAuthUsername = null;
            basicAuthPassword = null;
            return this;
        }

        public Call withBasicAuth(String username, String password) {
            token = null;
            basicAuthUsername = username;
            basicAuthPassword = password;
            return this;
        }

        public Reply call() {
            var headers = new HttpHeaders();
            var useTemplate = restTemplate;

            if (token != null) {
                headers.set("Authorization", "Bearer " + token);
            } else if (basicAuthUsername != null && basicAuthPassword != null) {
                useTemplate = restTemplate.withBasicAuth(basicAuthUsername, basicAuthPassword);
            }

            return new Reply(useTemplate.exchange(uri, method, new HttpEntity<>(request, headers), String.class));
        }
    }

    static public class Reply {
        private final ResponseEntity<String> entity;

        private Reply(ResponseEntity<String> entity) {
            this.entity = entity;
        }

        public HttpStatusCode getStatusCode() {
            return entity.getStatusCode();
        }

        public org.springframework.http.HttpHeaders getHeaders() {
            return entity.getHeaders();
        }

        public <RES> RES parseBody(Class<RES> type) {
            if (!entity.hasBody()) throw new RuntimeException("Reply does not have a body");

            try {
                var parser = new ObjectMapper();
                return parser.readValue(entity.getBody(), type);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse body as: " + type.getName(), e);
            }
        }
    }
}