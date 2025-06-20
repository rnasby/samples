package carPartsStore.auth;

import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final List<UserDetailsImpl> users;
    private final PasswordEncoder passwordEncoder;

    UserDetailsServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();

        users = List.of(
            new UserDetailsImpl("fred", passwordEncoder.encode("pebbles"), UserRole.USER, UserRole.ADMIN),
            new UserDetailsImpl("barney", passwordEncoder.encode("bambam"), UserRole.USER)
        );
    }

    PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        String tryUsername = (username == null ? null : username.trim().isEmpty() ? null : username.trim());
        if (StringUtils.isBlank(tryUsername)) throw new UsernameNotFoundException("Username cannot be empty");
        var user = users.stream().filter(tryUser -> tryUser.getUsername().equals(tryUsername)).findFirst().orElse(null);

        if (user == null) throw new UsernameNotFoundException("User not found: " + username);

        return user;
    }
}