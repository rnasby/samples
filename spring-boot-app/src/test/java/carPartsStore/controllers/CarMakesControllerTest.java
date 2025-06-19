package carPartsStore.controllers;

import carPartsStore.dto.CarMakeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarMakesControllerTest {
    @Autowired
    Common common;

    // TODO: Add tests showing that login is required to change makes.

    @Test
    @DirtiesContext
    void testCreateCarMake() {
        common.loginFred();
        var voidReply = common.addCarMake("Ford");
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carMakeReply = common.call(location.getFirst(), HttpMethod.GET, null, CarMakeDTO.class);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        common.assertFordMake(carMake);
    }

    @Test
    @DirtiesContext
    void testGetCarMake() {
        common.setup();
        common.logout();

        var carMakeReply = common.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(carMakeReply.hasBody()).isTrue();

        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        common.assertFordMake(carMake);

        assertThat(carMake.getCarModels().size()).isEqualTo(1);
        common.assertMustangModel(carMake.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarMakeList() {
        common.setup();

        var reply = common.call(CarMakesController.ROOT, HttpMethod.GET, null, CarMakeDTO[].class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();
        CarMakeDTO[] makes = reply.getBody();

        common.assertFordMake(makes[0]);

        assertThat(makes[1].getId()).isEqualTo(2L);
        assertThat(makes[1].getName()).isEqualTo("Chevy");
        assertThat(makes[1].getCarModels()).isNull();
    }

    @Test
    @DirtiesContext
    void testUpdateCarMake() {
        common.setup();
        var dto = CarMakeDTO.builder().name("Bogus2").build();
        var carMakeReply = common.call(CarMakesController.ROOT + "/1", HttpMethod.PUT, dto, Void.class);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = common.getCarMake(1L).getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
    }

    @Test
    @DirtiesContext
    void testDeleteCarMake() {
        common.setup();
        var voidReply = common.call(CarMakesController.ROOT + "/1", HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carMakeReply = common.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void testDeleteCarMakeDeletesChildren() {
        common.setup();
        var modelReply = common.getCarModel(1L);
        assertThat(modelReply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var voidReply = common.call(CarMakesController.ROOT + "/1", HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carMakeReply = common.getCarMake(1L);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        modelReply = common.getCarModel(1L);
        assertThat(modelReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}