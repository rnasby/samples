package carPartsStore.auth;

import carPartsStore.ApplicationTraits;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
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

    public String newAccessToken(String email) {
        return new TokenBuilder(email, tokenTimeoutMin).build();
    }

    public String newRefreshToken(String email) {
        return new TokenBuilder(email, refreshTokenTimeoutMin).build();
    }

    public String extractSubject(String token) {
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
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();

        // TODO: With updated library use: return Jwts.parser().decryptWith((SecretKey)getSignKey()).build()
        //                                            .parseEncryptedClaims(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String assertValidToken(String token) {
        var isExpired = isTokenExpired(token);
        var isBlocked = blockedTokens.contains(token);
        if (isExpired || isBlocked) throw new IllegalArgumentException("Invalid token");

        return token;
    }

    public void blockToken(String token) {
        blockedTokens.add(token);
    }

    private class TokenBuilder {
        final Date now;
        final String subject;
        final Date expirationTime;
        final Map<String, Object> claims = new HashMap<>();

        TokenBuilder(String subject, int expirationMin) {
            now = new Date();

            this.subject = subject;
            this.expirationTime = new Date(now.getTime() + (expirationMin * 60L * 1000L));
        }

        TokenBuilder addClaim(String key, Object value) {
            claims.put(key, value);
            return this;
        }

        String build() {
            return Jwts.builder()
                    .setIssuedAt(now)
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(expirationTime)
                    .signWith(signingKey, SignatureAlgorithm.HS256)
                    .compact();
        }
    }
}