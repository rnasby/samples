package carPartsStore.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(AuthController.ROOT)
@RequiredArgsConstructor
public class AuthController {
    static public final String NAME = "auth";
    static public final String ROOT = "/" + NAME;

    static public final String LOGIN = "/login";
    static public final String LOGOUT = "/logout";
    static public final String REFRESH = "/refresh";

    static private final String BEARER_PREFIX = "Bearer ";

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping(LOGIN)
    public ResponseEntity<TokenDTO> login(@RequestBody AuthDTO dto) {
        var username = dto.getUsername();
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,
                dto.getPassword()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(username);
            String refreshToken = jwtService.generateRefreshToken(username);
            return ResponseEntity.ok(TokenDTO.builder().accessToken(token).refreshToken(refreshToken).build());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    private String getTokenFromAuthorizationHeader(String authHeader) {
        boolean isValidValue = (authHeader != null && authHeader.startsWith(BEARER_PREFIX));
        if (!isValidValue) throw new IllegalArgumentException("Invalid Authorization header");

        return authHeader.substring(BEARER_PREFIX.length());
    }

    @PostMapping(REFRESH)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> refresh(@RequestHeader("Authorization") String authHeader, Principal principal) {
        String token = jwtService.assertValidToken(getTokenFromAuthorizationHeader(authHeader));

        jwtService.blockToken(token);
        String newAccessToken = jwtService.generateRefreshToken(principal.getName());
        return ResponseEntity.ok(new TokenDTO(newAccessToken, null));
    }

    @PostMapping(LOGOUT)
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.assertValidToken(getTokenFromAuthorizationHeader(authHeader));
        jwtService.blockToken(token);
    }
}