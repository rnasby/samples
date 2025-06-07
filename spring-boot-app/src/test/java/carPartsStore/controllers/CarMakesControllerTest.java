package carPartsStore.controllers;

import carPartsStore.dto.CarMakeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarMakesControllerTest {
    @Autowired
    Common common;

    @Autowired
    TestRestTemplate restTemplate;

    // TODO: Add tests showing that login is required to change makes.

    @Test
    @DirtiesContext
    void testCreateCarMake() {
//        common.loginFred();
        var voidReply = common.addCarMake("Ford");
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carMakeReply = restTemplate.getForEntity(location.getFirst(), CarMakeDTO.class);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void testGetCarMake() {
        common.setup();
//        common.logout();

        var carMakeReply = common.getCarMake(0L);

        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(carMakeReply.hasBody()).isTrue();
        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        var make = carMakeReply.getBody();
        common.assertFordMake(make);

        assertThat(make.getCarModels().size()).isEqualTo(1);
        common.assertMustangModel(make.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarMakeList() {
        common.setup();

        var reply = restTemplate.getForEntity(CarMakesController.ROOT, CarMakeDTO[].class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();
        CarMakeDTO[] makes = reply.getBody();

        common.assertFordMake(makes[0]);

        var make = makes[1];
        assertThat(make.getId()).isEqualTo(1L);
        assertThat(make.getName()).isEqualTo("Chevy");
        assertThat(make.getCarModels()).isNull();
    }

    @Test
    @DirtiesContext
    void testUpdateCarMake() {
        common.setup();
        var make = CarMakeDTO.builder().name("Bogus2").build();
        var request = new HttpEntity<>(make);

        var reply = restTemplate.exchange(CarMakesController.ROOT + "/0", HttpMethod.PUT, request, Void.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        make = common.getCarMake(0L).getBody();
        assertThat(make).isNotNull();
        assertThat(make.getId()).isEqualTo(0L);
        assertThat(make.getName()).isEqualTo("Bogus2");
    }

    @Test
    @DirtiesContext
    void testDeleteCarMake() {
        common.setup();
        var voidReply = restTemplate.exchange(CarMakesController.ROOT + "/0", HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carGetCarMakeReply = common.getCarMake(0L);
        assertThat(carGetCarMakeReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void testDeleteCarMakeDeletesChildren() {
        common.setup();
        var modelEntity = common.getCarModel(0L);
        assertThat(modelEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        var voidReply = restTemplate.exchange(CarMakesController.ROOT + "/0", HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var makeEntity = common.getCarMake(0L);
        assertThat(makeEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        modelEntity = common.getCarModel(0L);
        assertThat(modelEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}