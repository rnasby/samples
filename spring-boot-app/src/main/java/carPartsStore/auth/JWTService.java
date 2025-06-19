package carPartsStore.auth;

import carPartsStore.ApplicationTraits;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;

@Component
public class JWTService {
    private final Key signingKey;

    // TODO: Change to use persistent storage in database.
    private final Set<String> blockedTokens;

    private final int tokenTimeoutMin, refreshTokenTimeoutMin;

    JWTService(ApplicationTraits traits) {
        blockedTokens = new HashSet<>();
        var jwtTraits = traits.getSecurity().getJwt();

        var key = jwtTraits.getSecretKey();
        var bytes = key.getBytes(StandardCharsets.UTF_8);
        signingKey = Keys.hmacShaKeyFor(bytes);

        tokenTimeoutMin = jwtTraits.getTokenTimeoutMin();
        refreshTokenTimeoutMin = jwtTraits.getRefreshTokenTimeoutMin();
    }

    public String generateToken(String email) {
        return new TokenBuilder(email, tokenTimeoutMin, false).build();
    }

    public String generateRefreshToken(String email) {
        return new TokenBuilder(email, refreshTokenTimeoutMin, true).build();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
//        return Jwts.parser().decryptWith((SecretKey)getSignKey()).build().parseEncryptedClaims(token).getBody();

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String assertValidToken(String token) {
        return assertValidToken(token, (UserDetails)SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal());
    }

    public String assertValidToken(String token, UserDetails details) {
        String username = extractUsername(token);
        var isOk = (username.equals(details.getUsername()) && !isTokenExpired(token) && !blockedTokens.contains(
                token));

        if (!isOk) throw new IllegalArgumentException("Invalid token");

        return token;
    }

    public void blockToken(String token) {
        blockedTokens.add(token);
    }

    private class TokenBuilder {
        final Date now;
        final String email;
        final Date expirationTime;
        final boolean isRefreshToken;
        final Map<String, Object> claims = new HashMap<>();

        TokenBuilder(String email, int expirationMin, boolean isRefreshToken) {
            now = new Date();

            this.email = email;
            this.isRefreshToken = isRefreshToken;
            this.expirationTime = new Date(now.getTime() + (expirationMin * 60L * 1000L));
        }

        TokenBuilder addClaim(String key, Object value) {
            claims.put(key, value);
            return this;
        }

        String build() {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expirationTime)
                    .signWith(signingKey, SignatureAlgorithm.HS256)
                    .compact();
        }
    }
}