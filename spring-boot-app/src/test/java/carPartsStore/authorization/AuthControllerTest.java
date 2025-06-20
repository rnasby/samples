package carPartsStore.authorization;

import carPartsStore.Common;
import carPartsStore.auth.AuthController;
import carPartsStore.auth.TokenDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
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
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void testInvalidUserPasswordLogin() {
        var reply = common.login("fred", "i dont know");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void testRefreshToken() {
        var loginTokens = common.loginFred();
        var refreshTokens = common.refreshTokenOk();

        common.logout();
        var reply = common.newCall(AuthController.REFRESH, HttpMethod.POST, TokenDTO.class).withToken(
                loginTokens.accessToken()).call();
    }
}