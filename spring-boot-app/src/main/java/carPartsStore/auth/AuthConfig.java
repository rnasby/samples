package carPartsStore.auth;

import carPartsStore.authorization.AuthController;
import carPartsStore.controllers.CarMakesController;
import carPartsStore.controllers.CarModelsController;
import carPartsStore.controllers.CarPartsController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class AuthConfig {
    private final JWTFilter jwtFilter;
    private final UserDetailsServiceImpl detailsService;

    public AuthConfig(JWTFilter jwtFilter, UserDetailsServiceImpl detailsService) {
        this.jwtFilter = jwtFilter;
        this.detailsService = detailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String adminRole = UserRole.ADMIN.name();
        String authMatcher = AuthController.ROOT + "/**";
        var appMatchers = Stream.of(CarMakesController.ROOT, CarModelsController.ROOT, CarPartsController.ROOT)
                .map(p -> p + "/**").toList();

        http
                // Disable CSRF (not needed for stateless JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session (required for JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before Spring Security's default filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.POST, authMatcher).permitAll();

                    appMatchers.forEach(matcher -> {
                        auth.requestMatchers(HttpMethod.GET, matcher).permitAll();
                        auth.requestMatchers(HttpMethod.POST, matcher).permitAll();//.hasAnyRole(adminRole);
                        auth.requestMatchers(HttpMethod.PUT, matcher).permitAll();//.hasAnyRole(adminRole);
                        auth.requestMatchers(HttpMethod.DELETE, matcher).permitAll();//.hasRole(adminRole);
                    });

                    auth.anyRequest().authenticated();
                });


        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(detailsService);
        provider.setPasswordEncoder(detailsService.getPasswordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}