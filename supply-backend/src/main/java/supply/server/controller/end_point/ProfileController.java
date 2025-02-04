package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.EmailRequest;
import supply.server.controller.entity.response.UserInfoResponse;
import supply.server.controller.entity.response.UserProfileResponse;
import supply.server.data.company.Company;
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


    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        User user = fetchService.getUser();
        Company company = fetchService.getCompany();

        return ResponseEntity.ok(new UserProfileResponse(user, company));
    }

    @GetMapping("/user_info")
    public ResponseEntity<?> getUserInfo() {
        User user = fetchService.getUser();
        Company company = fetchService.getCompany();

        return ResponseEntity.ok(new UserInfoResponse(user, company));
    }

    @PostMapping("/company/update")
    public ResponseEntity<?> updateCompany(
            @RequestBody @Valid @NotNull @NotEmpty List<EmailRequest> emails
    ) {
        updateService.updateCompany(emails.stream().map(EmailRequest::toEmail).toList());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody @Valid @NotNull EmailRequest email) {
        updateService.updateUser(email.toEmail());
        return ResponseEntity.ok().build();
    }

}
