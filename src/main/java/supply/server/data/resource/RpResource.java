package supply.server.data.resource;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.Pagination;
import supply.server.data.resource.types.ResourceStatus;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpResource {

    public final DataSource dataSource;

    public Resource add(CreateResource createResource) {
        throw new NotImplementedException();
    }

    public Resource get(UUID resourceId) {
        throw new NotImplementedException();
    }

    public List<Resource> getAllByName(String prefix, ResourceFilters filters,  Pagination pagination) {
        throw new NotImplementedException();
    }

    public List changeLogs(UUID resourceId, Pagination pagination) {
        throw new NotImplementedException();
    }

    public List moveLogs(UUID resourceId, Pagination pagination) {
        throw new NotImplementedException();
    }

    public Resource moveToUser(UUID resourceId, UUID userId) {
        throw new NotImplementedException();
    }

    public Resource moveToWarehouse(UUID resourceId, UUID warehouseId) {
        throw new NotImplementedException();
    }

    public Resource applyMove(UUID resourceId, UUID userId) {
        throw new NotImplementedException();
    }

    public Resource edit(
            UUID resourceId,
            Optional<String> name,
            Optional<Integer> count,
            Optional<UUID> projectId,
            Optional<ResourceStatus> status,
            Optional<String> description
    ) {
        throw new NotImplementedException();
    }

}
