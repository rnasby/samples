package carPartsStore.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BogusUserDetailsManager extends InMemoryUserDetailsManager {
    static private UserDetails newUserDetails(String username, String password, String[] roles) {
        return User.withUsername(username).password(password).authorities(roles).build();
    }

    static private UserDetails newUserDetails(String username, String password, AuthConfig.UserRole... roles) {
        var roleStrs = Arrays.stream(roles).map(Enum::name).toArray(String[]::new);
        return newUserDetails(username, password, roleStrs);
    }

    BogusUserDetailsManager() {
        super(
            newUserDetails("fred", "{noop}pebbles", AuthConfig.UserRole.USER, AuthConfig.UserRole.ADMIN),
            newUserDetails("barney", "{noop}bambam", AuthConfig.UserRole.USER)
        );
    }
}