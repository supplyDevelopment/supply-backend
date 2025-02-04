package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateResourceRequest;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.data.PaginatedList;
import supply.server.data.resource.Resource;
import supply.server.service.dataService.CreationService;
import supply.server.service.dataService.SearchService;

@RestController
@RequestMapping("/stack")
@AllArgsConstructor
@Tag(name = "Stack controller" , description = "Operations with stack")
public class StackController {

    private final CreationService creationService;
    private final SearchService searchService;

    @PostMapping("/add")
    public ResponseEntity<?> addStack(@RequestBody CreateResourceRequest createResource) {
        creationService.createResource(createResource.toCreateResource());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stacks")
    public ResponseEntity<?> getStacks(@RequestParam String prefix, @RequestParam PaginationRequest paginationRequest) {
        PaginatedList<Resource> resources = searchService.getResources(prefix, paginationRequest.toPagination());

        return ResponseEntity.ok(resources);
    }

}
