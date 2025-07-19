package carPartsStore.authorization;

import carPartsStore.Testing;
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
    Testing testing;

    @Test
    @DirtiesContext
    void testUserLogin() {
        testing.loginFred();
    }

    @Test
    @DirtiesContext
    void testInvalidUserLogin() {
        var reply = testing.login("stone", "pebbles");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testInvalidUserPasswordLogin() {
        var reply = testing.login("fred", "i dont know");
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testLogout() {
        var tokens = testing.loginFred();
        testing.logout();

        var strReply = testing.rest.newPost(LOGOUT_PATH).withToken(tokens.accessToken()).call();
        assertThat(strReply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testLogoutWithoutJwt() {
        var reply = testing.rest.newPost(LOGOUT_PATH).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void testRefreshToken() {
        testing.loginFred();
        var newTokens = testing.refreshTokenOk();

        assertThat(newTokens.accessToken()).isNotNull();
        assertThat(newTokens.refreshToken()).isNotNull();

        testing.refreshTokenOk();
    }

    @Test
    @DirtiesContext
    void testDisabledTokens() throws Exception {
        var oldTokens = testing.loginFred();

        Thread.sleep(1000L);
        var newTokens = testing.refreshTokenOk();
        assertThat(newTokens.refreshToken()).isNotEqualTo(oldTokens.refreshToken());

        var strReply = testing.rest.newPost(REFRESH_PATH).withToken(oldTokens.refreshToken()).call();
        assertThat(strReply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DirtiesContext
    void testCannotUseRegularTokenForRefresh() {
        var loginTokens = testing.loginFred();
        var reply = testing.rest.newPost(REFRESH_PATH).withToken(loginTokens.accessToken()).call();
        assertThat(reply.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}