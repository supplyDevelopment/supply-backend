package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateWarehouseRequest;
import supply.server.controller.entity.request.PaginationRequest;
import supply.server.controller.entity.response.WarehousePartialInfoResponse;
import supply.server.controller.entity.response.WarehouseResponse;
import supply.server.data.PaginatedList;
import supply.server.data.warehouse.Warehouse;
import supply.server.service.dataService.CreationService;
import supply.server.service.dataService.FetchService;
import supply.server.service.dataService.SearchService;

import java.util.UUID;

@RestController
@RequestMapping("/warehouse")
@AllArgsConstructor
@Tag(name = "Warehouse controller" , description = "Operations with warehouse")
public class WarehouseController {

    private final CreationService creationService;
    private final SearchService searchService;
    private final FetchService fetchService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addWarehouse(@RequestBody @Valid @NotNull CreateWarehouseRequest createResource) {
        creationService.createWarehouse(createResource.toCreateWarehouse());

        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/warehouses")
    public ResponseEntity<PaginatedList<WarehousePartialInfoResponse>> getWarehouses(
            @RequestParam @NotNull String prefix,
            @Valid @NotNull PaginationRequest paginationRequest
    ) {
        PaginatedList<Warehouse> warehouses = searchService.getWarehouses(prefix, paginationRequest.toPagination());
        return ResponseEntity.ok(
                new PaginatedList<>(
                        warehouses.total(),
                        warehouses.items().stream().map(WarehousePartialInfoResponse::new).toList()
                )
        );
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouse(@PathVariable String id) {
        Warehouse warehouse = fetchService.getWarehouse(UUID.fromString(id));

        return ResponseEntity.ok(new WarehouseResponse(warehouse));
    }

}
