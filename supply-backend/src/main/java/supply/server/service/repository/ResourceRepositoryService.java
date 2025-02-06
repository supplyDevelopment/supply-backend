package supply.server.service.repository;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import supply.server.configuration.exception.DataNotFoundException;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.Redis;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.RpResource;

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
            Optional<Resource> existingResource = rpResource.get(createResource, companyId);
            Optional<Resource> resourceOpt;
            if (existingResource.isPresent()) {
                resourceOpt = rpResource.edit(existingResource.get().id(), companyId, existingResource.get().count() + createResource.count());

                if (resourceOpt.isPresent()) {
                    resource = resourceOpt.get();
                    inMemoryRpResource.set(resource.id(), Pair.of(companyId, resource));
                } else {
                    throw new DbException("Failed to update existing resource");
                }
            } else {
                resourceOpt = rpResource.add(createResource);

                if (resourceOpt.isPresent()) {
                    resource = resourceOpt.get();
                    inMemoryRpResource.set(resource.id(), Pair.of(companyId, resource));
                } else {
                    throw new DbException("Failed to add resource");
                }
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
                    throw new DataNotFoundException("Resource with id " + resourceId + " not found");
                }
            } else {
                if (!resourcePairOpt.get().getKey().equals(companyId)) {
                    throw new DataNotFoundException("Resource with id " + resourceId + " not found");
                }
                resource = resourcePairOpt.get().getValue();
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

    public Optional<Resource> get(CreateResource createResource, UUID companyId) {
        Optional<Resource> resource;
        try {
            resource = rpResource.get(createResource, companyId);
            resource.ifPresent(value -> inMemoryRpResource.set(value.id(), Pair.of(companyId, value)));
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

    public Resource edit(UUID id, UUID companyId, int count) {
        Resource resource;
        try {
            Optional<Resource> resourceOpt = rpResource.edit(id, companyId, count);

            if (resourceOpt.isPresent()) {
                resource = resourceOpt.get();
                inMemoryRpResource.set(resource.id(), Pair.of(companyId, resource));
            } else {
                throw new DataNotFoundException("Resource with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resource;
    }

    public void delete(UUID resourceId, UUID companyId) {
        try {
            rpResource.delete(resourceId, companyId);
            inMemoryRpResource.remove(resourceId);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

}
