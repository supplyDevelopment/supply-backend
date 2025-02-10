package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.EmailPasswordRequest;
import supply.server.controller.entity.request.EmailRequest;
import supply.server.service.dataService.AuthenticationService;
import supply.server.service.dataService.CompanyService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authorisation controller" , description = "Registration and login")
public class AuthorisationController {

    private final CompanyService companyService;
    private final AuthenticationService authenticationService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, cookie set"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register_company")
    public ResponseEntity<?> registerCompany(@RequestBody @Valid @NotNull EmailRequest email, HttpServletResponse response) {
        String jwt = companyService.createCompany(email.toEmail());
        authenticationService.setAuthenticationCookie(response, jwt);

        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, cookie set"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestBody @Valid @NotNull EmailPasswordRequest emailPassword, HttpServletResponse response) {
        Optional<String> jwtOptional = authenticationService.authenticate(emailPassword.toEmail(), emailPassword.password());
        if (jwtOptional.isPresent()) {
            authenticationService.setAuthenticationCookie(response, jwtOptional.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, cookie deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.resetAuthenticationCookie(request, response);
        return ResponseEntity.ok().build();
    }

}
