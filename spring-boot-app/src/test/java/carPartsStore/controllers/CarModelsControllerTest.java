package carPartsStore.controllers;

import carPartsStore.Common;
import carPartsStore.dto.CarModelDTO;
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
public class CarModelsControllerTest {
    @Autowired
    Common common;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DirtiesContext
    void testCreateCarModel() {
        common.loginFred();
        common.addCarMakes();

        var voidReply = common.addCarModel("Mustang", 1L, 1979, 6700.00);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carModelReply = restTemplate.getForEntity(location.getFirst(), CarModelDTO.class);
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carModel = carModelReply.getBody();
        assertThat(carModel).isNotNull();
        common.assertMustangModel(carModelReply.getBody());
    }

    @Test
    @DirtiesContext
    void testGetCarModel() {
        common.setup();
        common.logout();

        var carModelReply = common.getCarModel(1L);
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carModel = carModelReply.getBody();
        assertThat(carModel).isNotNull();
        common.assertMustangModel(carModel);
        common.assertFordMake(carModel.getCarMake());
        assertThat(carModel.getCarParts().size()).isEqualTo(2L);
        common.assertAlternatorPart(carModel.getCarParts().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarModelList() {
        common.setup();
        common.logout();

        var reply = restTemplate.getForEntity(CarModelsController.ROOT, CarModelDTO[].class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();

        CarModelDTO[] models = reply.getBody();
        common.assertMustangModel(models[0]);

        assertThat(models[1].getId()).isEqualTo(2L);
        assertThat(models[1].getName()).isEqualTo("Corvette");
        assertThat(models[1].getCarMakeId()).isEqualTo(2);
        assertThat(models[1].getYear()).isEqualTo(1981);
        assertThat(models[1].getPrice()).isEqualTo(15000.00);
    }

    @Test
    @DirtiesContext
    void testUpdateCarModel() {
        common.setup();

        var dto = CarModelDTO.builder().name("Bogus2").carMakeId(2L).year(1910).price(1.75).build();
        var request = new HttpEntity<>(dto);

        var carModelReply = restTemplate.exchange(CarModelsController.ROOT + "/1", HttpMethod.PUT, request, Void.class);
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = common.getCarModel(1L).getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
        assertThat(dto.getCarMakeId()).isEqualTo(2);
        assertThat(dto.getYear()).isEqualTo(1910);
        assertThat(dto.getPrice()).isEqualTo(1.75);
    }

    @Test
    @DirtiesContext
    void testDeleteCarModel() {
        common.setup();

        var voidReply = restTemplate.exchange(CarModelsController.ROOT + "/1", HttpMethod.DELETE, null, Void.class);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carModelReply = common.getCarModel(1L);
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}