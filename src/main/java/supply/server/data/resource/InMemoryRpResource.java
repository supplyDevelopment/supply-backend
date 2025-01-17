package supply.server.data.resource;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import supply.server.data.project.Project;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class InMemoryRpResource {

    private final int MAX_CACHE_SIZE = 1000;

    private final Map<UUID, Pair<UUID, Resource>> storage;

    public InMemoryRpResource() {
        this.storage = new LinkedHashMap<>(MAX_CACHE_SIZE, 1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, Pair<UUID, Resource>> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public Optional<Resource> add(Resource resource, UUID companyId) throws SQLException {
        storage.put(resource.id(), Pair.of(companyId, resource));
        return Optional.of(resource);
    }

    public Optional<Resource> get(UUID id, UUID companyId) throws SQLException {
        if (storage.containsKey(id)) {
            Pair<UUID, Resource> resource = storage.get(id);
            if (resource.getFirst().equals(companyId)) {
                return Optional.of(resource.getSecond());
            }
        }
        return Optional.empty();
    }

    public Optional<Resource> edit(UUID id, Resource resource, UUID companyId) throws SQLException {
        storage.put(id, Pair.of(companyId, resource));
        return Optional.of(resource);
    }

}
