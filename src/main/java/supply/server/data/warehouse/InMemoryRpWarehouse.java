package supply.server.data.warehouse;

import org.springframework.stereotype.Component;
import supply.server.data.user.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class InMemoryRpWarehouse {

    private final int MAX_CACHE_SIZE = 500;

    private final Map<UUID, Warehouse> storage;

    public InMemoryRpWarehouse() {
        this.storage = new LinkedHashMap<>(MAX_CACHE_SIZE, 1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, Warehouse> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public Optional<Warehouse> add(Warehouse warehouse) {
        storage.put(warehouse.id(), warehouse);
        return Optional.of(warehouse);
    }

    public Optional<Warehouse> get(UUID id, UUID companyId) {
        if (storage.containsKey(id)) {
            Warehouse warehouse = storage.get(id);
            if (warehouse.companyId().equals(companyId)) {
                return Optional.of(warehouse);
            }
        }
        return Optional.empty();
    }

}
