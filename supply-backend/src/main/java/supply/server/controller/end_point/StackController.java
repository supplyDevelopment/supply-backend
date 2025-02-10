package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateResourceRequest;
import supply.server.controller.entity.request.EditResourceRequest;
import supply.server.controller.entity.request.ExpandResourceRequest;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.data.PaginatedList;
import supply.server.data.resource.Resource;
import supply.server.service.dataService.CreationService;
import supply.server.service.dataService.FetchService;
import supply.server.service.dataService.SearchService;
import supply.server.service.dataService.UpdateService;

import java.util.UUID;

@RestController
@RequestMapping("/stack")
@AllArgsConstructor
@Tag(name = "Stack controller" , description = "Operations with stack")
public class StackController {

    private final CreationService creationService;
    private final SearchService searchService;
    private final UpdateService updateService;
    private final FetchService fetchService;

    @PostMapping("/add")
    public ResponseEntity<?> addStack(@RequestBody @Valid @NotNull CreateResourceRequest createResource) {
        creationService.createResource(createResource.toCreateResource());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stacks")
    public ResponseEntity<?> getStacks(@RequestParam @Valid @NotNull String prefix, @Valid @NotNull PaginationRequest paginationRequest) {
        PaginatedList<Resource> resources = searchService.getResources(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(resources);
    }

    @GetMapping("/stacks/{id}")
    public ResponseEntity<?> getStack(@PathVariable String id) {
        Resource resource = fetchService.getResource(UUID.fromString(id));
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/expand")
    public ResponseEntity<?> expandStack(@RequestBody @Valid @NotNull ExpandResourceRequest expandResourceRequest) {
        updateService.expendResource(UUID.fromString(expandResourceRequest.id()), expandResourceRequest.quantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editStack(@RequestBody @Valid @NotNull EditResourceRequest editResourceRequest) {
        updateService.updateResource(UUID.fromString(editResourceRequest.id()), editResourceRequest.toEditResource());
        return ResponseEntity.ok().build();
    }

}
