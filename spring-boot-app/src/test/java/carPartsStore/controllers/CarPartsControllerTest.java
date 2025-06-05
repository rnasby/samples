package carPartsStore.controllers;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarPartsControllerTest {
    static private final String API = "car-parts";

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void returnExistingMake() {
        var response = restTemplate.getForEntity(API + "/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var json = JsonPath.parse(response.getBody());
        assertThat((Number)json.read("$.id")).isEqualTo(1);
        assertThat((String)json.read("$.name")).isEqualTo("'Engine'");
        assertThat((Number)json.read("$.price")).isEqualTo(5000.00);
    }

    @Test
    void notReturnUnknownId() {
        var response = restTemplate.getForEntity(API + "/1000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }
}
