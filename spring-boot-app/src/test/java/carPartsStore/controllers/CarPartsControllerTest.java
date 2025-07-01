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
public class CarPartsControllerTest {
    // TODO: Add tests showing that login is required to change parts.

    @Autowired
    AppTests appTests;

    @Test
    @DirtiesContext
    void testCreateCarPart() {
        appTests.loginFred();

        var voidReply = appTests.addCarPart("Alternator", 500.50);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carPartReply = appTests.rest.newGet(location.getFirst(), CarPartDTO.class).withAuth().call();
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carPart = carPartReply.getBody();
        assertThat(carPart).isNotNull();
        appTests.assertAlternatorPart(carPart);
    }

    @Test
    @DirtiesContext
    void testGetCarPart() {
        appTests.setup();

        var carPartReply = appTests.getCarPart(1L);
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carPart = carPartReply.getBody();
        assertThat(carPart).isNotNull();
        appTests.assertAlternatorPart(carPart);

        assertThat(carPart.getCarModels().size()).isEqualTo(2L);
        appTests.assertMustangModel(carPart.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarPartList() {
        appTests.setup();

        var reply = appTests.rest.newGet(CarPartsController.ROOT, CarPartDTO[].class).withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();

        CarPartDTO[] parts = reply.getBody();
        appTests.assertAlternatorPart(parts[0]);
        assertThat(parts[0].getCarModels()).isNull();

        assertThat(parts[1].getId()).isEqualTo(2L);
        assertThat(parts[1].getName()).isEqualTo("Motor");
        assertThat(parts[1].getPrice()).isEqualTo(9500.50);
    }

    @Test
    @DirtiesContext
    void testUpdateCarPart() {
        appTests.setup();

        var dto = CarPartDTO.builder().name("Bogus2").price(0.75).build();
        var carPartReply = appTests.rest.newPut(CarPartsController.ROOT + "/1").withRequest(dto).withAuth().call();
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = appTests.getCarPart(1L).getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
        assertThat(dto.getPrice()).isEqualTo(0.75);
    }

    @Test
    @DirtiesContext
    void testDeleteCarPart() {
        appTests.setup();

        var voidReply = appTests.rest.newDelete(CarPartsController.ROOT + "/1").withAuth().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carPartsReply = appTests.getCarPart(1L);
        assertThat(carPartsReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}