package carPartsStore.controllers;

import carPartsStore.Testing;
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
    Testing testing;

    @Test
    @DirtiesContext
    void testAddModelCarPart() {
        testing.loginFred();
        testing.addCarMakes();
        testing.addCarModels();
        testing.addCarParts();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1") + "/1";
        var reply = testing.newPost(url, Void.class).withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void testGetModelParts() {
        testing.setup();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1");
        var reply = testing.newGet(url, CarPartDTO[].class).withAuth().call();
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
        testing.setup();

        String url = CarModelsPartsController.ROOT.replace("{modelId}", "1") + "/1";
        var voidReply = testing.newDelete(url).withAuth().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        url = CarModelsPartsController.ROOT.replace("{modelId}", "1");
        var carPartsReply = testing.newGet(url, CarPartDTO[].class).withAuth().call();
        assertThat(carPartsReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var carParts = carPartsReply.getBody();
        assertThat(carParts).hasSize(1);
        assertThat(carParts[0].getId()).isEqualTo(2L);
        assertThat(carParts[0].getName()).isEqualTo("Motor");
    }
}