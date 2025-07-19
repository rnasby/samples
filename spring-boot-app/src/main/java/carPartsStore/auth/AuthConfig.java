package carPartsStore.auth;

import carPartsStore.ApplicationTraits;
import carPartsStore.controllers.CarMakesController;
import carPartsStore.controllers.CarModelsController;
import carPartsStore.controllers.CarPartsController;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
    @Order(1)
    public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .securityMatcher(AuthController.ROOT + AuthController.LOGIN, "/swagger-ui/**", "/v3/api-docs/**",
                    "/swagger-resources/**");

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        var appMatchers = Stream.of(CarMakesController.ROOT, CarModelsController.ROOT, CarPartsController.ROOT)
                .map(p -> p + "/**").toList();
        var authMatchers = Stream.of(AuthController.REFRESH, AuthController.LOGOUT).map(p -> AuthController.ROOT + p)
                .toList();
        var securityMatchers = Stream.concat(appMatchers.stream(), authMatchers.stream()).toArray(String[]::new);

        http
            .securityMatcher(securityMatchers)
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(auth -> auth.jwt(Customizer.withDefaults()))

            // Stateless session (required for JWT)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configure endpoint authorization
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.POST, authMatchers.toArray(new String[0])).permitAll();

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

    @Bean
    public OpenAPI openAPI() {
        var license = new License().name("TODO license name").url("TODO license URL");
        var bearerScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
        var basicScheme = new SecurityScheme().name("basicAuth").type(SecurityScheme.Type.HTTP).scheme("basic");
        var components = new Components()
                .addSecuritySchemes("basicAuth", basicScheme)
                .addSecuritySchemes("Bearer Authentication", bearerScheme);
        var contact = new Contact().name("Ron Nasby").email("RonNasbyTech.com").url("ronald.nasby@gmail.com");
        var info = new Info()
            .license(license)
            .title("Car Parts REST API")
            .description("TODO description")
            .version("1.0")
            .contact(contact);

        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(components).info(info);
    }
}