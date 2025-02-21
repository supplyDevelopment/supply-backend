package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateProjectRequest;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.controller.entity.response.ProjectResponse;
import supply.server.data.PaginatedList;
import supply.server.data.project.Project;
import supply.server.service.dataService.CreationService;
import supply.server.service.dataService.FetchService;
import supply.server.service.dataService.SearchService;

import java.util.UUID;

@RestController
@RequestMapping("/project")
@AllArgsConstructor
@Tag(name = "Project controller" , description = "Operations with project")
public class ProjectController {

    private final CreationService creationService;
    private final SearchService searchService;
    private final FetchService fetchService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody @Valid @NotNull CreateProjectRequest createProject) {
        creationService.createProject(createProject.name(), createProject.description());
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/projects")
    public ResponseEntity<PaginatedList<ProjectResponse>> getProjects(
            @RequestParam @NotNull String prefix,
            @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<Project> projects = searchService.getProjects(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(
                new PaginatedList<>(
                        projects.total(),
                        projects.items().stream().map(ProjectResponse::new).toList()
                )
        );
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String id) {
        Project project = fetchService.getProject(UUID.fromString(id));

        return ResponseEntity.ok(new ProjectResponse(project));
    }

}
