package carPartsStore.authorization;

import carPartsStore.ApplicationTraits;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTCreationException;
//import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

//@Service
public class TokenService {
    private final Set<String> blockedTokens;
    private final String secretKey;
    private final int tokenTimeoutMin, refreshTokenTimeoutMin;

    public TokenService(ApplicationTraits traits) {
        blockedTokens = new HashSet<>();
        var jwtTraits = traits.getSecurity().getJwt();

        secretKey = jwtTraits.getSecretKey();
        tokenTimeoutMin = jwtTraits.getTokenTimeoutMin();
        refreshTokenTimeoutMin = jwtTraits.getRefreshTokenTimeoutMin();
    }

//    public String buildAccessToken(User user) {
//        try {
//            return buildAccessToken(user, getExpirationTime(tokenTimeoutMin), false, true);
//        } catch (JWTCreationException e) {
//            throw new JWTCreationException("Failed to generate token", e);
//        }
//    }
//
//    public String buildRefreshToken(User user) {
//        try {
//            return buildAccessToken(user, getExpirationTime(refreshTokenTimeoutMin), true, null);
//        } catch (JWTCreationException e) {
//            throw new JWTCreationException("Failed to generate token", e);
//        }
//    }
//
//    public String buildRefreshedAccessToken(User user) {
//        try {
//            return buildAccessToken(user, getExpirationTime(tokenTimeoutMin), false, false);
//        } catch (JWTCreationException e) {
//            throw new JWTCreationException("Failed to generate token", e);
//        }
//    }
//
//    private Instant getExpirationTime(int hoursUntilExpiration) {
//        return LocalDateTime.now().plusHours(hoursUntilExpiration).toInstant(ZoneOffset.of("-03:00"));
//    }
//
//    private String buildAccessToken(User user, Instant expirationDate, boolean isRefreshToken, Boolean isFresh) {
//        var username = user.getUsername();
//        var builder = JWT.create().withExpiresAt(expirationDate).withSubject(username).withClaim("username", username);
//
//        if (isRefreshToken) {
//            builder.withClaim("refresh", true);
//        } else {
//            builder.withClaim("fresh", isFresh);
//        }
//
//        return builder.sign(getAlgorithm());
//    }
//
//    private Algorithm getAlgorithm() {
//        return Algorithm.HMAC256(secretKey);
//    }
//
//    public String validateToken(String token) {
//        try {
//            if (blockedTokens.contains(token)) {
//                throw new JWTVerificationException("Token is blocked");
//            }
//
//            return JWT.require(getAlgorithm()).build().verify(token).getSubject();
//
//        } catch (JWTVerificationException e) {
//            throw new JWTVerificationException("Failed to validate token", e);
//        }
//    }

    public void blockToken(String token) {
        blockedTokens.add(token);
    }
}