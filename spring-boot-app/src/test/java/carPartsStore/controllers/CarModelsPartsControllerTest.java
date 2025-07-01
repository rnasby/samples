package carPartsStore.controllers;

import carPartsStore.AppTests;
import carPartsStore.dto.CarPartDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarModelsPartsControllerTest {
    @Autowired
    AppTests appTests;

    @Test
    @DirtiesContext
    void testAddModelCarPart() {
        appTests.loginFred();
        appTests.addCarMakes();
        appTests.addCarModels();
        appTests.addCarParts();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1") + "/1";
        var reply = appTests.rest.newPost(url, Void.class).withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void testGetModelParts() {
        appTests.setup();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1");
        var reply = appTests.rest.newGet(url, CarPartDTO[].class).withAuth().call();
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
        appTests.setup();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1") + "/1";
        var voidReply = appTests.rest.newDelete(url).withAuth().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        url = CarModelsPartsController.ROOT.replace("{modelId}", "1");
        var carPartsReply = appTests.rest.newGet(url, CarPartDTO[].class).withAuth().call();
        assertThat(carPartsReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var carParts = carPartsReply.getBody();
        assertThat(carParts).hasSize(1);
        assertThat(carParts[0].getId()).isEqualTo(2L);
        assertThat(carParts[0].getName()).isEqualTo("Motor");
    }
}