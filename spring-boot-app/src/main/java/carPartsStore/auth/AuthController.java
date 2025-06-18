package carPartsStore.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthController.ROOT)
@RequiredArgsConstructor
public class AuthController {
    static public final String NAME = "auth";
    static public final String ROOT = "/" + NAME;

    static public final String GENERATE_TOKEN = ROOT + "/generateToken";
    static public final String LOGOUT = ROOT + "/logout";
    static public final String REFRESH = ROOT + "/refresh";

    private final JWTService jwtService;
    private final UserDetailsServiceImpl service;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    // Removed the role checks here as they are already managed in SecurityConfig

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthDTO authDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authDTO.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}