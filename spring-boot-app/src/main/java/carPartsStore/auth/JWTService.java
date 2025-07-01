package carPartsStore.auth;

import carPartsStore.ApplicationTraits;
import carPartsStore.error.BadTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTService {
    // TODO: Change to use persistent storage in database.
    private final Set<String> blockedTokens;

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final int tokenTimeoutMin, refreshTokenTimeoutMin;

    JWTService(ApplicationTraits traits, JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;

        blockedTokens = new HashSet<>();
        var jwtTraits = traits.getSecurity().getJwt();

        tokenTimeoutMin = jwtTraits.getTokenTimeoutMin();
        refreshTokenTimeoutMin = jwtTraits.getRefreshTokenTimeoutMin();
    }

    public String newAccessToken(Authentication authentication) {
        return new TokenBuilder(authentication, tokenTimeoutMin).build(false);
    }

    public String newRefreshToken(Authentication authentication) {
        return new TokenBuilder(authentication, refreshTokenTimeoutMin).build(true);
    }

    private Jwt parseToken(String token) {
        return decoder.decode(token);
    }

    public boolean isRefreshToken(String token) {
        return parseToken(token).getClaimAsBoolean("refresh");
    }

    public Instant extractExpiration(String token) {
        return parseToken(token).getExpiresAt();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    public String assertValidToken(String token) {
        var isExpired = isTokenExpired(token);
        var isBlocked = blockedTokens.contains(token);
        if (isExpired || isBlocked) throw new BadTokenException("Invalid token");

        return token;
    }

    public void blockToken(String token) {
        blockedTokens.add(token);
    }

    private class TokenBuilder {
        final Instant now, expirationTime;
        final Authentication authentication;

        TokenBuilder(Authentication authentication, int expirationMin) {
            now = Instant.now();

            this.authentication = authentication;
            this.expirationTime = now.plus(expirationMin, ChronoUnit.MINUTES);
        }

        String build(boolean isRefreshToken) {
            var builder = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(expirationTime)
                    .subject(authentication.getName());

            builder.claim("refresh", isRefreshToken);
            if (!isRefreshToken) builder.claim("scope", authentication.getAuthorities().stream().map(
                    GrantedAuthority::getAuthority).collect(Collectors.joining(" ")));

            JwtClaimsSet claims = builder.build();
            return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }
    }
}