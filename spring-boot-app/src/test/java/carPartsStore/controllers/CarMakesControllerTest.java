package carPartsStore.controllers;

import carPartsStore.AppTests;
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
    AppTests appTests;

    // TODO: Add tests showing that login is required to change makes.

    @Test
    @DirtiesContext
    void testCreateCarMake() {
        appTests.loginFred();
        var voidReply = appTests.addCarMake("Ford");
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carMakeReply = appTests.rest.newGet(location.getFirst(), CarMakeDTO.class).withAuth().call();
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        appTests.assertFordMake(carMake);
    }

    @Test
    @DirtiesContext
    void testGetCarMake() {
        appTests.setup();
        var carMakeReply = appTests.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(carMakeReply.hasBody()).isTrue();

        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        appTests.assertFordMake(carMake);

        assertThat(carMake.getCarModels().size()).isEqualTo(1);
        appTests.assertMustangModel(carMake.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarMakeList() {
        appTests.setup();

        var reply = appTests.rest.newGet(CarMakesController.ROOT, CarMakeDTO[].class).withAuth().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();
        CarMakeDTO[] makes = reply.getBody();

        appTests.assertFordMake(makes[0]);

        assertThat(makes[1].getId()).isEqualTo(2L);
        assertThat(makes[1].getName()).isEqualTo("Chevy");
        assertThat(makes[1].getCarModels()).isNull();
    }

    @Test
    @DirtiesContext
    void testUpdateCarMake() {
        appTests.setup();
        var dto = CarMakeDTO.builder().name("Bogus2").build();
        var carMakeReply = appTests.rest.newPut(CarMakesController.ROOT + "/1").withRequest(dto).withAuth().call();
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = appTests.getCarMake(1L).getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
    }

    @Test
    @DirtiesContext
    void testDeleteCarMake() {
        appTests.setup();
        var voidReply = appTests.rest.newDelete(CarMakesController.ROOT + "/1").withAuth().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carMakeReply = appTests.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void testDeleteCarMakeDeletesChildren() {
        appTests.setup();
        var modelReply = appTests.getCarModel(1L);
        assertThat(modelReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var voidReply = appTests.rest.newDelete(CarMakesController.ROOT + "/1").withAuth().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carMakeReply = appTests.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        modelReply = appTests.getCarModel(1L);
        assertThat(modelReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}