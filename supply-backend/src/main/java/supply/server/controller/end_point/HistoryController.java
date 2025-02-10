package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.data.PaginatedList;
import supply.server.data.resource.history.ResourceHistory;
import supply.server.service.dataService.SearchService;

@RestController
@RequestMapping("/history")
@AllArgsConstructor
@Tag(name = "History controller" , description = "Fetch service for histories")
public class HistoryController {

    private final SearchService searchService;

    @GetMapping("/get")
    public ResponseEntity<?> getHistory(
            @RequestParam @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<ResourceHistory> resources = searchService.getResourceHistory(paginationRequest.toPagination());

        return ResponseEntity.ok(resources);
    }


}
