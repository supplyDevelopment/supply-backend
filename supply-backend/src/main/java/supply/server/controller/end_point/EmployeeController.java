package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateUserRequest;
import supply.server.controller.entity.request.EmailPasswordRequest;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.controller.entity.response.UserProfileResponse;
import supply.server.data.PaginatedList;
import supply.server.data.company.Company;
import supply.server.data.user.User;
import supply.server.service.dataService.CreationService;
import supply.server.service.dataService.FetchService;
import supply.server.service.dataService.SearchService;
import supply.server.service.dataService.UpdateService;

import java.util.UUID;

@RestController
@RequestMapping("/employee")
@AllArgsConstructor
@Tag(name = "Employee controller" , description = "Operations with employee")
public class EmployeeController {

    private final SearchService searchService;
    private final UpdateService updateService;
    private final CreationService creationService;
    private final FetchService fetchService;

    @GetMapping("/employees")
    public ResponseEntity<?> getEmployees(
            @RequestParam @Valid @NotNull String prefix,
            @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<User> users = searchService.getUsers(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable String id) {
        User user = fetchService.getUser(UUID.fromString(id));
        Company company = fetchService.getCompany();

        return ResponseEntity.ok(new UserProfileResponse(user, company));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody @Valid @NotNull CreateUserRequest createUser) {
        creationService.createUser(createUser.toCreateUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody @Valid @NotNull EmailPasswordRequest emailPassword) {
        if (emailPassword.password() != null) {
            updateService.updateUser(emailPassword.password());
        }
        if (emailPassword.email() != null) {
            updateService.updateUser(emailPassword.toEmail());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeEmployee(@RequestBody String id) {
        updateService.removeUser(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

}
