package carPartsStore.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = AuthController.ROOT, headers = "Authorization")
@RequiredArgsConstructor
@Tag(name = AuthController.NAME, description = AuthController.NAME + " management API")
public class AuthController {
    static public final String NAME = "auth";
    static public final String ROOT = "/" + NAME;

    static public final String LOGIN = "/login";
    static public final String LOGOUT = "/logout";
    static public final String REFRESH = "/refresh";

    static private final String BEARER_PREFIX = "Bearer ";

    private final JWTService jwtService;

    @PostMapping(LOGIN)
    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Login to application with basic auth",
               description = "Pass basic-auth username and password. Returns JWT tokens for the authenticated user")
    public ResponseEntity<TokenDTO> login(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.newAccessToken(authentication);
            String refreshToken = jwtService.newRefreshToken(authentication);
            return ResponseEntity.ok(new TokenDTO(accessToken, refreshToken));
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    private String getTokenFromAuthorizationHeader(String authHeader) {
        boolean isValidValue = (authHeader != null && authHeader.startsWith(BEARER_PREFIX));
        if (!isValidValue) throw new BadTokenException("Invalid Authorization header");

        return authHeader.substring(BEARER_PREFIX.length());
    }

    @PostMapping(REFRESH)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get new access and refresh JWT tokens")
    public ResponseEntity<TokenDTO> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.assertValidToken(getTokenFromAuthorizationHeader(authHeader));
        if (!jwtService.isRefreshToken(token)) throw new BadTokenException("Invalid refresh token");

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String newAccessToken = jwtService.newAccessToken(auth);
        String newRefreshToken = jwtService.newRefreshToken(auth);

        // Depending on how fast the refresh is called, the new one may be the same as the old one.
        if (!newRefreshToken.equals(token)) jwtService.blockToken(token);

        return ResponseEntity.ok(new TokenDTO(newAccessToken, newRefreshToken));
    }

    @PostMapping(LOGOUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Logout of application", description = "This will block the JWT access token that was passed")
    public void logout(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.assertValidToken(getTokenFromAuthorizationHeader(authHeader));
        jwtService.blockToken(token);

        // TODO: Add logic to find and block refresh token?
    }
}