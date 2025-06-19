package carPartsStore.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Stream;

public class UserDetailsImpl implements UserDetails {
    private final String username, encryptedPassword;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String encryptedPassword, UserRole... roles) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;

        authorities = Stream.of(roles).map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return encryptedPassword;
    }
}