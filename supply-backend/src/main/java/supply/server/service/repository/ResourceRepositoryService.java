package supply.server.service.repository;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.Redis;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.RpResource;
import supply.server.data.resource.types.ResourceStatus;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ResourceRepositoryService {

    private final RpResource rpResource;

    private final Redis<Pair<UUID, Resource>> inMemoryRpResource;

    public Resource add(CreateResource createResource, UUID companyId) {
        Resource resource;
        try {
            Optional<Resource> resourceOpt = rpResource.add(createResource);

            if (resourceOpt.isPresent()) {
                resource = resourceOpt.get();
                inMemoryRpResource.set(resource.id(), Pair.of(companyId, resource));
            } else {
                throw new DbException("Failed to add resource");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

    public Resource get(UUID resourceId, UUID companyId) {
        Resource resource;
        try {
            Optional<Pair<UUID, Resource>> resourcePairOpt = inMemoryRpResource.get(resourceId);

            if (resourcePairOpt.isEmpty()) {
                Optional<Resource> resourceOpt;
                resourceOpt = rpResource.get(resourceId, companyId);
                if (resourceOpt.isPresent()) {
                    resource = resourceOpt.get();
                    inMemoryRpResource.set(resource.id(), Pair.of(companyId, resource));
                } else {
                    throw new DataNotFound("Resource with id " + resourceId + " not found");
                }
            } else {
                if (!resourcePairOpt.get().getKey().equals(companyId)) {
                    throw new DataNotFound("Resource with id " + resourceId + " not found");
                }
                resource = resourcePairOpt.get().getValue();
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

    public Resource edit(
             UUID resourceId,
             UUID companyId,
             Optional<String> name,
             Optional<Integer> count,
             Optional<UUID> projectId,
             Optional<ResourceStatus> status,
             Optional<String> description
    ) {
        Resource resource;
        try {
            Optional<Resource> resourceOpt = rpResource.edit(
                    resourceId,
                    companyId,
                    name,
                    count,
                    projectId,
                    status,
                    description
            );

            if (resourceOpt.isPresent()) {
                resource = resourceOpt.get();
                inMemoryRpResource.set(resource.id(), Pair.of(companyId, resource));
            } else {
                throw new DbException("Failed to edit resource with id " + resourceId);
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

    public PaginatedList<Resource> getAll(String prefix, UUID companyId, Pagination pagination) {
        PaginatedList<Resource> resources;
        try {
            resources = rpResource.getAll(prefix, companyId, pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resources;
    }

}
