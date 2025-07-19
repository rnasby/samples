package carPartsStore.controllers;

import carPartsStore.Testing;
import carPartsStore.dto.CarMakeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpStatus;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarMakesControllerTest {
    @Autowired
    Testing testing;

    @Test
    @DirtiesContext
    void testLoginIsRequired() {
        var reply = testing.rest.newGet(CarMakesController.ROOT).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testBasicAuthDenied() {
        testing.loginFred();
        var reply = testing.rest.newGet(CarMakesController.ROOT).withBasicAuth("fred", "pebbles").call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void testCreateCarMake() {
        testing.loginFred();
        var voidReply = testing.addCarMake("Ford");
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carMakeReply = testing.rest.newGet(location.getFirst()).withAccessToken().call();
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carMake = carMakeReply.parseBody(CarMakeDTO.class);
        testing.assertFordMake(carMake);
    }

    @Test
    @DirtiesContext
    void testGetCarMake() {
        testing.setup();
        var carMakeReply = testing.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var carMake = carMakeReply.parseBody(CarMakeDTO.class);
        testing.assertFordMake(carMake);

        assertThat(carMake.getCarModels().size()).isEqualTo(1);
        testing.assertMustangModel(carMake.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarMakeList() {
        testing.setup();

        var reply = testing.rest.newGet(CarMakesController.ROOT).withAccessToken().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        CarMakeDTO[] makes = reply.parseBody(CarMakeDTO[].class);

        testing.assertFordMake(makes[0]);

        assertThat(makes[1].getId()).isEqualTo(2L);
        assertThat(makes[1].getName()).isEqualTo("Chevy");
        assertThat(makes[1].getCarModels()).isNull();
    }

    @Test
    @DirtiesContext
    void testUpdateCarMake() {
        testing.setup();
        var dto = CarMakeDTO.builder().name("Bogus2").build();
        var reply = testing.rest.newPut(CarMakesController.ROOT + "/1").withRequest(dto).withAccessToken().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = testing.getCarMake(1L).parseBody(CarMakeDTO.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
    }

    @Test
    @DirtiesContext
    void testDeleteCarMake() {
        testing.setup();
        var voidReply = testing.rest.newDelete(CarMakesController.ROOT + "/1").withAccessToken().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carMakeReply = testing.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void testDeleteCarMakeDeletesChildren() {
        testing.setup();
        var modelReply = testing.getCarModel(1L);
        assertThat(modelReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var voidReply = testing.rest.newDelete(CarMakesController.ROOT + "/1").withAccessToken().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carMakeReply = testing.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        modelReply = testing.getCarModel(1L);
        assertThat(modelReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}