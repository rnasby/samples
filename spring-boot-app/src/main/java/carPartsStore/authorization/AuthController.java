package carPartsStore.authorization;

import carPartsStore.dto.LoginDTO;
import carPartsStore.dto.TokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthController.ROOT)
public class AuthController {
    static public final String NAME = "auth";
    static public final String ROOT = "/" + NAME;

    static private final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO data) {
        var authToken = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var authUser = authenticationManager.authenticate(authToken);
        var accessToken = tokenService.buildAccessToken((User)authUser.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(accessToken));
    }

    private String getTokenFromAuthorizationHeader(String authHeader) {
        boolean isValidValue = (authHeader != null && authHeader.startsWith(BEARER_PREFIX));
        if (!isValidValue) throw new IllegalArgumentException("Invalid Authorization header");

        return authHeader.substring(BEARER_PREFIX.length());
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = getTokenFromAuthorizationHeader(authHeader);
        String user = tokenService.validateToken(token);

        tokenService.blockToken(token);
        String newAccessToken = tokenService.buildRefreshedAccessToken(new User(user, "", null));
        return ResponseEntity.ok(new TokenDTO(newAccessToken));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestHeader("Authorization") String authHeader) {
        String token = getTokenFromAuthorizationHeader(authHeader);
        String user = tokenService.validateToken(token);

        tokenService.blockToken(token);
        logger.info("Successfully logged out: {}", user);
    }
}
