package carPartsStore.controllers;

import carPartsStore.db.CarMake;
import carPartsStore.dto.CarMakeDTO;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarMakesControllerTest {
    @Autowired
    Common common;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void returnExistingMake() {
        var reply = common.getCarMake(0L);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);

        var make = reply.getBody();
        assertThat(make).isNotNull();
        assertThat(make.getId()).isEqualTo(0);
        assertThat(make.getName()).isEqualTo("Toyota");
    }

    @Test
    void notReturnUnknownId() {
        var reply = common.getCarMake(1000L);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listValues() {
        var reply = restTemplate.getForEntity(CarModelsController.ROOT, CarMake[].class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reply.getBody()).isNotNull();
        CarMake[] makes = reply.getBody();
        assertThat(makes.length).isEqualTo(5);
    }

    // TODO: Add tests showing that login is required to change makes.

    void test_create_car_make() {
        common.loginFred();
        var voidReply = common.addCarMake("Ford");
        var location = voidReply.getHeaders().get("location");
        assertThat(location).isNotNull();

        var carMakeReply = restTemplate.getForEntity(location.getFirst(), CarMakeDTO.class);
        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(carMakeReply.hasBody()).isTrue();
        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        common.assertFordMake(carMakeReply.getBody());
    }

    void test_get_car_make() {
        common.setup();
        common.logout();

        var carMakeReply = common.getCarMake(1L);

        assertThat(carMakeReply.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(carMakeReply.hasBody()).isTrue();
        var carMake = carMakeReply.getBody();
        assertThat(carMake).isNotNull();
        var make = carMakeReply.getBody();
        common.assertFordMake(make);

        assertThat(make.getCarModels().size()).isEqualTo(1);
        common.assertMustangModel(make.getCarModels().iterator().next());
    }

    void test_get_car_make_list() {
        common.setup();
        common.logout();

        var reply = restTemplate.getForEntity(CarModelsController.ROOT, String.class);
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.OK);
        var json = JsonPath.parse(reply.getBody());
        List<CarMakeDTO> makes = json.read("$");
        common.assertFordMake(makes.get(0));

        var make = makes.get(1);
        assertThat(make.getId()).isEqualTo(2);
        assertThat(make.getName()).isEqualTo("Chevy");
        assertThat(make.getCarModels().size()).isEqualTo(0);
    }

//    void test_update_car_make() {
//        common.setup();
//        var make = new CarMake("Bogus2");
//        var reply = restTemplate.postForObject(Constants.CAR_MAKES_PATH + "/1", make,  ResponseEntity<Void>class);
//        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//
//        reply = test_client.put(f '{API}/1', json = {"name":"Bogus2"});
//        assert reply.status_code == HTTPStatus.NO_CONTENT;
//
//        reply = test_client.get(f '{API}/1');
//        make = json.loads(reply.data.decode());
//        assert make["id"] == 1;
//        assert make["name"] == "Bogus2";
//    }
//
//    void test_delete_car_make() {
//        common.setup(test_client);
//
//        reply = test_client.delete(f '{API}/1');
//        assert reply.status_code == HTTPStatus.NO_CONTENT;
//
//        reply = test_client.get(f '{API}/1');
//        assert reply.status_code == HTTPStatus.NOT_FOUND;
//    }
//
//    void test_delete_car_make_deletes_children() {
//        common.setup(test_client);
//
//        reply = test_client.get(f '{common.CAR_MODELS_API}/1');
//        assert reply.status_code == HTTPStatus.OK;
//
//        reply = test_client.delete(f '{API}/1');
//        assert reply.status_code == HTTPStatus.NO_CONTENT;
//
//        reply = test_client.get(f '{API}/1');
//        assert reply.status_code == HTTPStatus.NOT_FOUND;
//
//        reply = test_client.get(f '{common.CAR_MODELS_API}/1')
//        assert reply.status_code == HTTPStatus.NOT_FOUND;
//    }
}