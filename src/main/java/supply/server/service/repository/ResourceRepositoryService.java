package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.RpResource;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ResourceRepositoryService {

    private final RpResource rpResource;

    public Resource add(CreateResource createResource) {
        Resource resource;
        try {
            Optional<Resource> resourceOpt = rpResource.add(createResource);

            if (resourceOpt.isPresent()) {
                resource = resourceOpt.get();
            } else {
                throw new DbException("Failed to add resource");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

    public Resource get(UUID resourceId) {
        Resource resource;
        try {
            Optional<Resource> resourceOpt = rpResource.get(resourceId);

            if (resourceOpt.isPresent()) {
                resource = resourceOpt.get();
            } else {
                throw new DataNotFound("Resource with id " + resourceId + " not found");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

}
