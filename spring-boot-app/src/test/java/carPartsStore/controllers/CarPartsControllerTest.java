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
public class CarPartsControllerTest {
    // TODO: Add tests showing that login is required to change parts.

    @Autowired
    Testing testing;

    @Test
    @DirtiesContext
    void testCreateCarPart() {
        testing.loginFred();

        var voidReply = testing.addCarPart("Alternator", 500.50);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carPartReply = testing.rest.newGet(location.getFirst()).withAccessToken().call();
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carPart = carPartReply.parseBody(CarPartDTO.class);
        testing.assertAlternatorPart(carPart);
    }

    @Test
    @DirtiesContext
    void testGetCarPart() {
        testing.setup();

        var carPartReply = testing.getCarPart(1L);
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carPart = carPartReply.parseBody(CarPartDTO.class);
        testing.assertAlternatorPart(carPart);

        assertThat(carPart.getCarModels().size()).isEqualTo(2L);
        testing.assertMustangModel(carPart.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarPartList() {
        testing.setup();

        var reply = testing.rest.newGet(CarPartsController.ROOT).withAccessToken().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);

        CarPartDTO[] parts = reply.parseBody(CarPartDTO[].class);
        testing.assertAlternatorPart(parts[0]);
        assertThat(parts[0].getCarModels()).isNull();

        assertThat(parts[1].getId()).isEqualTo(2L);
        assertThat(parts[1].getName()).isEqualTo("Motor");
        assertThat(parts[1].getPrice()).isEqualTo(9500.50);
    }

    @Test
    @DirtiesContext
    void testUpdateCarPart() {
        testing.setup();

        var dto = CarPartDTO.builder().name("Bogus2").price(0.75).build();
        var carPartReply = testing.rest.newPut(CarPartsController.ROOT + "/1").withRequest(dto).withAccessToken().call();
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = testing.getCarPart(1L).parseBody(CarPartDTO.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
        assertThat(dto.getPrice()).isEqualTo(0.75);
    }

    @Test
    @DirtiesContext
    void testDeleteCarPart() {
        testing.setup();

        var voidReply = testing.rest.newDelete(CarPartsController.ROOT + "/1").withAccessToken().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carPartsReply = testing.getCarPart(1L);
        assertThat(carPartsReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}