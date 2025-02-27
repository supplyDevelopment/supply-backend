package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateUserRequest;
import supply.server.controller.entity.request.EmailPasswordRequest;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.controller.entity.response.UserFullInfoResponse;
import supply.server.controller.entity.response.UserPartialInfoResponse;
import supply.server.data.PaginatedList;
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/employees")
    public ResponseEntity<PaginatedList<UserPartialInfoResponse>> getEmployees(
            @RequestParam @Valid @NotNull String prefix,
            @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<User> users = searchService.getUsers(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(
                new PaginatedList<>(
                        users.total(),
                        users.items().stream().map(user -> new UserPartialInfoResponse(user, fetchService.getCompany().subscriptionExpiresAt())).toList()
                )
        );
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/employees/{id}")
    public ResponseEntity<UserFullInfoResponse> getEmployee(@PathVariable String id) {
        User user = fetchService.getUser(UUID.fromString(id));

        return ResponseEntity.ok(new UserFullInfoResponse(user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody @Valid @NotNull CreateUserRequest createUser) {
        creationService.createUser(createUser.toCreateUser());
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody @Valid @NotNull EmailPasswordRequest emailPassword, @Valid @NotNull String id) {
        if (emailPassword.password() != null) {
            updateService.updateUser(emailPassword.password(), UUID.fromString(id));
        }
        if (emailPassword.email() != null) {
            updateService.updateUser(emailPassword.toEmail(), UUID.fromString(id));
        }
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/remove")
    public ResponseEntity<?> removeEmployee(@RequestBody @Valid @NotNull UUID id) {
        updateService.removeUser(id);
        return ResponseEntity.ok().build();
    }

}
