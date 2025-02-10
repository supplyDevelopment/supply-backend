package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import supply.server.controller.entity.response.ResourcePartialInfoResponse;
import supply.server.controller.entity.response.ResourceResponse;
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addStack(@RequestBody @Valid @NotNull CreateResourceRequest createResource) {
        creationService.createResource(createResource.toCreateResource());

        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stacks")
    public ResponseEntity<PaginatedList<ResourcePartialInfoResponse>> getStacks(@RequestParam @Valid @NotNull String prefix, @Valid @NotNull PaginationRequest paginationRequest) {
        PaginatedList<Resource> resources = searchService.getResources(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(
                new PaginatedList<>(
                        resources.total(),
                        resources.items().stream().map(ResourcePartialInfoResponse::new).toList()
                )
        );
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stacks/{id}")
    public ResponseEntity<ResourceResponse> getStack(@PathVariable String id) {
        Resource resource = fetchService.getResource(UUID.fromString(id));
        return ResponseEntity.ok(new ResourceResponse(resource));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/expand")
    public ResponseEntity<?> expandStack(@RequestBody @Valid @NotNull ExpandResourceRequest expandResourceRequest) {
        updateService.expendResource(UUID.fromString(expandResourceRequest.id()), expandResourceRequest.quantity());
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/edit")
    public ResponseEntity<?> editStack(@RequestBody @Valid @NotNull EditResourceRequest editResourceRequest) {
        updateService.updateResource(UUID.fromString(editResourceRequest.id()), editResourceRequest.toEditResource());
        return ResponseEntity.ok().build();
    }

}
