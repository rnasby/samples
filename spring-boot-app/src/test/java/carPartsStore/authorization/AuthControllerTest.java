package carPartsStore.authorization;

import carPartsStore.AppTests;
import carPartsStore.auth.AuthController;
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
    static private final String LOGOUT_PATH = AuthController.ROOT + AuthController.LOGOUT;
    static private final String REFRESH_PATH = AuthController.ROOT + AuthController.REFRESH;

    @Autowired
    AppTests appTests;

    @Test
    @DirtiesContext
    void testUserLogin() {
        appTests.loginFred();
    }

    @Test
    @DirtiesContext
    void testInvalidUserLogin() {
        var reply = appTests.login("stone", "pebbles");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testInvalidUserPasswordLogin() {
        var reply = appTests.login("fred", "i dont know");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testLogout() {
        var tokens = appTests.loginFred();
        appTests.logout();

        var strReply = appTests.rest.newPost(LOGOUT_PATH, String.class).withToken(tokens.accessToken()).call();
        assertThat(strReply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testLogoutWithoutJwt() {
        var reply = appTests.rest.newPost(LOGOUT_PATH, String.class).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    void testRefreshToken() {
        var oldTokens = appTests.loginFred();
        var newTokens = appTests.refreshTokenOk();

        assertThat(newTokens.accessToken()).isNotNull();
        assertThat(newTokens.refreshToken()).isNotNull();

        appTests.refreshTokenOk();
    }

    @Test
    @DirtiesContext
    void testDisabledTokens() throws Exception {
        var oldTokens = appTests.loginFred();

        Thread.sleep(1000L);
        var newTokens = appTests.refreshTokenOk();
        assertThat(newTokens.refreshToken()).isNotEqualTo(oldTokens.refreshToken());

        var strReply = appTests.rest.newPost(REFRESH_PATH, String.class).withToken(oldTokens.refreshToken()).call();
        assertThat(strReply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testCannotUseRegularTokenForRefresh() {
        var loginTokens = appTests.loginFred();
        var reply = appTests.rest.newPost(REFRESH_PATH, Void.class).withToken(loginTokens.accessToken()).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}