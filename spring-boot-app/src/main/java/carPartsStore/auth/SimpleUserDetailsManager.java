package carPartsStore.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SimpleUserDetailsManager extends InMemoryUserDetailsManager {
    static private UserDetails newUserDetails(String username, String password, String[] roles) {
        return User.withUsername(username).password(password).roles(roles).build();
    }

    static private UserDetails newUserDetails(String username, String password, UserRole... roles) {
        var roleStrs = Arrays.stream(roles).map(Enum::name).toArray(String[]::new);
        return newUserDetails(username, password, roleStrs);
    }

    SimpleUserDetailsManager() {
        super(
            newUserDetails("fred", "{noop}pebbles", UserRole.USER, UserRole.ADMIN),
            newUserDetails("barney", "{noop}bambam", UserRole.USER)
        );
    }
}