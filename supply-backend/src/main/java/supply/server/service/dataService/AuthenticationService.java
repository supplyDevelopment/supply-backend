package supply.server.service.dataService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import supply.server.configuration.security.JwtUtils;
import supply.server.data.utils.Email;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public Optional<String> authenticate(Email email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email.getEmail(),
                        password
                )
        );
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return Optional.of(jwtUtils.generateJwtToken(authentication));
        }
        return Optional.empty();
    }

    public void setAuthenticationCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie("token", jwt);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(86400);

        response.addCookie(cookie);
    }

}
