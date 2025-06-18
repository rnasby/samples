package carPartsStore.authorization;

import carPartsStore.controllers.Common;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {
    @Autowired
    Common common;

    @Test
    @DirtiesContext
    void testUserLogin() {
        common.loginFred();
    }

    @Test
    @DirtiesContext
    void testInvalidUserLogin() {
        var reply = common.login("stone", "pebbles");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(reply.getBody()).isEqualTo("Invalid user id");
    }

    @Test
    @DirtiesContext
    void testInvalidUserPasswordLogin() {
        var reply = common.login("fred", "i dont know");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(reply.getBody()).isEqualTo("Invalid password");
    }

    @Test
    @DirtiesContext
    void testLogout() {
        common.loginFred();
        common.logout();
    }

    @Test
    @DirtiesContext
    void testLogoutWithoutJwt() {
        var reply = common.logout();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(reply.getBody()).isEqualTo("Request does not contain an access token.");
    }

    @Test
    @DirtiesContext
    void testRefreshToken() {
        var token = common.loginFred();


//        token_str1 = reply.json["access_token"]
//        refresh_token_str = reply.json["refresh_token"]
//
//        reply = common.refresh_token_ok(test_client, refresh_token_str)
//        token_str2 = reply.json["access_token"]
//
//        assert token_str1 != token_str2

        common.logout();
    }
}