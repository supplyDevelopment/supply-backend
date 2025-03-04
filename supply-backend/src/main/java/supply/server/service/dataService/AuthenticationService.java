package supply.server.service.dataService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserEntityDetailsService entityDetailsService;

    @Value("${cookie.domain}")
    private String cookieDomain;

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
        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .sameSite("None")
                .secure(true)
                .path("/")
                .domain(cookieDomain)
                .maxAge(86400)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void resetAuthenticationCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    ResponseCookie deleteCookie = ResponseCookie.from("token", null)
                            .sameSite("None")
                            .secure(true)
                            .path("/")
                            .domain(cookieDomain)
                            .maxAge(0)
                            .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                    break;
                }
            }
        }
    }

}
