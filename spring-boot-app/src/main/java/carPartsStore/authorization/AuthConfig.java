package carPartsStore.authorization;

import carPartsStore.controllers.CarMakesController;
import carPartsStore.controllers.CarModelsController;
import carPartsStore.controllers.CarPartsController;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class AuthConfig {
    private final SecurityFilter securityFilter;

    public AuthConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        String adminRole = UserRole.ADMIN.name();
        String authMatcher = AuthController.ROOT + "/**";
        List<String> appMatchers = Stream.of(CarMakesController.ROOT, CarModelsController.ROOT, CarPartsController.ROOT)
                .map(p -> p + "/**").toList();

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.POST, authMatcher).permitAll();

                    appMatchers.forEach(matcher -> {
                        authorize.requestMatchers(HttpMethod.GET, matcher).permitAll();
                        authorize.requestMatchers(HttpMethod.POST, matcher).hasAnyRole(adminRole);
                        authorize.requestMatchers(HttpMethod.PUT, matcher).hasAnyRole(adminRole);
                        authorize.requestMatchers(HttpMethod.DELETE, matcher).hasRole(adminRole);
                    });
                    authorize.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}