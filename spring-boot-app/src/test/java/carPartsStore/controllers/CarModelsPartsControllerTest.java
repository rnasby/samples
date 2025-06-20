package carPartsStore.controllers;

import carPartsStore.Common;
import carPartsStore.dto.CarPartDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarModelsPartsControllerTest {
    @Autowired
    Common common;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DirtiesContext
    void testAddModelCarPart() {
//        common.loginFred();
        common.addCarMakes();
        common.addCarModels();
        common.addCarParts();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1") + "/1";
        var reply = restTemplate.exchange(url, HttpMethod.POST, null, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void testGetModelParts() {
        common.setup();
//        common.logout();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1");
        var reply = restTemplate.getForEntity(url, CarPartDTO[].class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var carParts = reply.getBody();
        assertThat(carParts).hasSize(2);

        assertThat(carParts[0].getId()).isEqualTo(1L);
        assertThat(carParts[0].getName()).isEqualTo("Alternator");
        assertThat(carParts[0].getPrice()).isEqualTo(500.50);
    }

    @Test
    @DirtiesContext
    void testDeleteModelPart() {
        common.setup();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1") + "/1";
        var voidReply = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        url = CarModelsPartsController.ROOT.replace("{modelId}", "1");
        var carPartsReply = restTemplate.getForEntity(url, CarPartDTO[].class);
        assertThat(carPartsReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var carParts = carPartsReply.getBody();
        assertThat(carParts).hasSize(1);
        assertThat(carParts[0].getId()).isEqualTo(2L);
        assertThat(carParts[0].getName()).isEqualTo("Motor");
    }
}