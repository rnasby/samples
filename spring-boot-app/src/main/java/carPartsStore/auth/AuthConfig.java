package carPartsStore.auth;

import carPartsStore.ApplicationTraits;
import carPartsStore.controllers.CarMakesController;
import carPartsStore.controllers.CarModelsController;
import carPartsStore.controllers.CarPartsController;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class AuthConfig {
    @Bean
    public KeyPair jwtKeyPair(ApplicationTraits traits) {
        return traits.getSecurity().getJwt().getRSAKeyPair();
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        var privateKey = jwtKeyPair.getPrivate();
        var publicKey = (RSAPublicKey)jwtKeyPair.getPublic();
        var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair) {
        var publicKey = (RSAPublicKey)jwtKeyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String authMatcher = AuthController.ROOT + "/**";
        var appMatchers = Stream.of(CarMakesController.ROOT, CarModelsController.ROOT, CarPartsController.ROOT)
                .map(p -> p + "/**").toList();

        http
            // Disable CSRF (not needed for stateless JWT)
            .csrf(AbstractHttpConfigurer::disable)

            .httpBasic(Customizer.withDefaults())
            .oauth2ResourceServer(auth -> auth.jwt(Customizer.withDefaults()))

            // Stateless session (required for JWT)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configure endpoint authorization
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.POST, authMatcher).permitAll();

                appMatchers.forEach(matcher -> {
                    auth.requestMatchers(HttpMethod.GET, matcher).hasAnyAuthority(
                            UserRole.USER.scope(),
                            UserRole.ADMIN.scope());
                    auth.requestMatchers(HttpMethod.POST, matcher).hasAuthority(
                            UserRole.ADMIN.scope());
                    auth.requestMatchers(HttpMethod.PUT, matcher).hasAuthority(
                            UserRole.ADMIN.scope());
                    auth.requestMatchers(HttpMethod.DELETE, matcher).hasAuthority(
                            UserRole.ADMIN.scope());
                });

                auth.anyRequest().authenticated();
            });

        return http.build();
    }
}