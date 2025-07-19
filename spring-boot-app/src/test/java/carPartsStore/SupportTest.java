package carPartsStore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SupportTest {
    static private final String ACTUATOR_PATH = "/actuator";
    static private final String SWAGGER_API_PATH = "/v3/api-docs";
    static private final String SWAGGER_UI_PATH = "/swagger-ui/index.html";

    @Autowired
    Testing testing;

    @Test
    @DirtiesContext
    void testSwaggerUIAccessible() {
        var reply = testing.rest.newGet(SWAGGER_UI_PATH).withBasicAuth("fred", "pebbles").call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void testSwaggerAPIAccessible() {
        var reply = testing.rest.newGet(SWAGGER_API_PATH).withBasicAuth("fred", "pebbles").call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void testSwaggerUIProtected() {
        var reply = testing.rest.newGet(SWAGGER_UI_PATH).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testSwaggerAPIProtected() {
        var reply = testing.rest.newGet(SWAGGER_API_PATH).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testActuatorAccessible() {
        var reply = testing.rest.newGet(ACTUATOR_PATH).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}