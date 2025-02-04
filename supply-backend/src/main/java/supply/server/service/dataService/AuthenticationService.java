package supply.server.service.dataService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import supply.server.configuration.security.JwtUtils;
import supply.server.data.user.userDetails.UserEntityDetailsService;
import supply.server.data.utils.Email;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserEntityDetailsService entityDetailsService;

    public Optional<String> authenticate(Email email, String password) {
        UserDetails userDetails = entityDetailsService.loadUserByUsername(email.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        password,
                        userDetails.getAuthorities()
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
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(86400);

        response.addCookie(cookie);
    }

    public void resetAuthenticationCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    Cookie deleteCookie = new Cookie("token", null);
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
                    deleteCookie.setSecure(true);
                    deleteCookie.setHttpOnly(true);
                    response.addCookie(deleteCookie);
                    break;
                }
            }
        }
    }

}
