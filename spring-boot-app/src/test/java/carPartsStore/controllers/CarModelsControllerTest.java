package carPartsStore.controllers;

import carPartsStore.Testing;
import carPartsStore.dto.CarModelDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarModelsControllerTest {
    @Autowired
    Testing testing;

    @Test
    @DirtiesContext
    void testCreateCarModel() {
        testing.loginFred();
        testing.addCarMakes();

        var voidReply = testing.addCarModel("Mustang", 1L, 1979, 6700.00);
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carModelReply = testing.rest.newGet(location.getFirst()).withAccessToken().call();
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carModel = carModelReply.parseBody(CarModelDTO.class);
        testing.assertMustangModel(carModel);
    }

    @Test
    @DirtiesContext
    void testGetCarModel() {
        testing.setup();
        var carModelReply = testing.getCarModel(1L);
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var carModel = carModelReply.parseBody(CarModelDTO.class);
        testing.assertMustangModel(carModel);
        testing.assertFordMake(carModel.getCarMake());
        assertThat(carModel.getCarParts().size()).isEqualTo(2L);
        testing.assertAlternatorPart(carModel.getCarParts().getFirst());
    }

    @Test
    @DirtiesContext
    void testGetCarModelList() {
        testing.setup();
        var reply = testing.rest.newGet(CarModelsController.ROOT).withAccessToken().call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);

        CarModelDTO[] models = reply.parseBody(CarModelDTO[].class);
        testing.assertMustangModel(models[0]);

        assertThat(models[1].getId()).isEqualTo(2L);
        assertThat(models[1].getName()).isEqualTo("Corvette");
        assertThat(models[1].getCarMakeId()).isEqualTo(2);
        assertThat(models[1].getYear()).isEqualTo(1981);
        assertThat(models[1].getPrice()).isEqualTo(15000.00);
    }

    @Test
    @DirtiesContext
    void testUpdateCarModel() {
        testing.setup();

        var dto = CarModelDTO.builder().name("Bogus2").carMakeId(2L).year(1910).price(1.75).build();;
        var carModelReply = testing.rest.newPut(CarModelsController.ROOT + "/1").withRequest(dto).withAccessToken()
                .call();
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        dto = testing.getCarModel(1L).parseBody(CarModelDTO.class);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Bogus2");
        assertThat(dto.getCarMakeId()).isEqualTo(2);
        assertThat(dto.getYear()).isEqualTo(1910);
        assertThat(dto.getPrice()).isEqualTo(1.75);
    }

    @Test
    @DirtiesContext
    void testDeleteCarModel() {
        testing.setup();

        var voidReply = testing.rest.newDelete(CarModelsController.ROOT + "/1").withAccessToken().call();
        assertThat(voidReply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var carModelReply = testing.getCarModel(1L);
        assertThat(carModelReply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}