package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.EmailPasswordRequest;
import supply.server.controller.entity.request.EmailRequest;
import supply.server.controller.entity.response.UserFullInfoResponse;
import supply.server.controller.entity.response.UserPartialInfoResponse;
import supply.server.data.user.User;
import supply.server.service.dataService.FetchService;
import supply.server.service.dataService.UpdateService;

import java.util.List;

@RestController
@RequestMapping("/profile")
@AllArgsConstructor
@Tag(name = "Profile controller" , description = "Get and update profile")
public class ProfileController {

    private final FetchService fetchService;
    private final UpdateService updateService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/info")
    public ResponseEntity<UserFullInfoResponse> getInfo() {
        User user = fetchService.getUser();

        return ResponseEntity.ok(new UserFullInfoResponse(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user_info")
    public ResponseEntity<UserPartialInfoResponse> getUserInfo() {
        User user = fetchService.getUser();

        return ResponseEntity.ok(new UserPartialInfoResponse(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/company/update")
    public ResponseEntity<?> updateCompany(
            @RequestBody @Valid @NotNull @NotEmpty List<EmailRequest> emails
    ) {
        updateService.updateCompany(emails.stream().map(EmailRequest::toEmail).toList());
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody @Valid @NotNull EmailPasswordRequest emailPassword) {
        if (emailPassword.password() != null) {
            updateService.updateUser(emailPassword.password());
        }
        if (emailPassword.email() != null) {
            updateService.updateUser(emailPassword.toEmail());
        }
        return ResponseEntity.ok().build();
    }


}
