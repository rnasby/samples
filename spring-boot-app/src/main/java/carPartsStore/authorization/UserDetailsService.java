package carPartsStore.authorization;

import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsService {
    private final List<UserDetailsImpl> users = List.of(
        new UserDetailsImpl("fred", "pebbles", UserRole.USER),
        new UserDetailsImpl("mary", "password123", UserRole.USER),
        new UserDetailsImpl("joe", "password456", UserRole.USER, UserRole.ADMIN)
    );

    UserDetails getByLogin(String username) {
        String tryUsername = (username == null ? null : username.trim().isEmpty() ? null : username.trim());
        if (StringUtils.isBlank(tryUsername)) throw new UsernameNotFoundException("Username cannot be empty");
        var user = users.stream().filter(tryUserDetailsImpl -> tryUserDetailsImpl.getLogin().equals(tryUsername)).findFirst().orElse(null);

        if (user == null) throw new UsernameNotFoundException("User not found: " + username);

        return user;
    }
}