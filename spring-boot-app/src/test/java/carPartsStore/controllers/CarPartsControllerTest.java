package carPartsStore.controllers;

import carPartsStore.Common;
import carPartsStore.dto.CarPartDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarPartsControllerTest {
    // TODO: Add tests showing that login is required to change parts.

    @Autowired
    Common common;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DirtiesContext
    void testCreateCarPart() {
//        common.loginFred();

        var voidReply = common.addCarPart("Alternator", 500.50);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carPartReply = restTemplate.getForEntity(location.getFirst(), CarPartDTO.class);
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carPart = carPartReply.getBody();
        assertThat(carPart).isNotNull();
        common.assertAlternatorPart(carPart);
    }

    @Test
    @DirtiesContext
    void testGetCarPart() {
        common.setup();
//        common.logout();

        var carPartReply = common.getCarPart(1L);
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carPart = carPartReply.getBody();
        assertThat(carPart).isNotNull();
        common.assertAlternatorPart(carPart);

        assertThat(carPart.getCarModels().size()).isEqualTo(2L);
        common.assertMustangModel(carPart.getCarModels().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarPartList() {
        common.setup();
//        common.logout();

        var reply = restTemplate.getForEntity(CarPartsController.ROOT, CarPartDTO[].class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();

        CarPartDTO[] parts = reply.getBody();
        common.assertAlternatorPart(parts[0]);
        assertThat(parts[0].getCarModels()).isNull();

        assertThat(parts[1].getId()).isEqualTo(2L);
        assertThat(parts[1].getName()).isEqualTo("Motor");
        assertThat(parts[1].getPrice()).isEqualTo(9500.50);
    }

    @Test
    @DirtiesContext
    void testUpdateCarPart() {
        common.setup();

        var dto = CarPartDTO.builder().name("Bogus2").price(0.75).build();
        var request = new HttpEntity<>(dto);

        var carPartReply = restTemplate.exchange(CarPartsController.ROOT + "/1", HttpMethod.PUT, request, Void.class);
        assertThat(carPartReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = common.getCarPart(1L).getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
        assertThat(dto.getPrice()).isEqualTo(0.75);
    }

    @Test
    @DirtiesContext
    void testDeleteCarPart() {
        common.setup();

        var voidReply = restTemplate.exchange(CarPartsController.ROOT + "/1", HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carPartsReply = common.getCarPart(1L);
        assertThat(carPartsReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}