package carPartsStore.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private String login;
    private String password;
    private List<UserRole> roles;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String login, String password, UserRole... roles) {
        this.login = login;
        this.password = password;
        this.roles = List.of(roles);

        authorities = this.roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return login;
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
}