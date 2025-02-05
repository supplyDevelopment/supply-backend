package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateProjectRequest;
import supply.server.controller.entity.request.PaginationRequest;
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

    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody @Valid @NotNull CreateProjectRequest createProject) {
        creationService.createProject(createProject.name(), createProject.description());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjects(
            @RequestParam @NotNull String prefix,
            @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<Project> projects = searchService.getProjects(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<?> getProject(@PathVariable String id) {
        Project project = fetchService.getProject(UUID.fromString(id));

        return ResponseEntity.ok(project);
    }

}
