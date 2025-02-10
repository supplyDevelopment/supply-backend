package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import supply.server.controller.entity.response.ResourceHistoryResponse;
import supply.server.data.PaginatedList;
import supply.server.data.resource.history.ResourceHistory;
import supply.server.service.dataService.SearchService;

@RestController
@RequestMapping("/history")
@AllArgsConstructor
@Tag(name = "History controller" , description = "Fetch service for histories")
public class HistoryController {

    private final SearchService searchService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get")
    public ResponseEntity<PaginatedList<ResourceHistoryResponse>> getHistory(
            @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<ResourceHistory> resources = searchService.getResourceHistory(paginationRequest.toPagination());

        return ResponseEntity.ok(
                new PaginatedList<>(
                        resources.total(),
                        resources.items().stream().map(ResourceHistoryResponse::new).toList()
                )
        );
    }


}
