package supply.server.data.company;

import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class InMemoryRpCompany {

    private final int MAX_CACHE_SIZE = 200;

    private final Map<UUID, Company> storage;

    public InMemoryRpCompany() {
        this.storage = new LinkedHashMap<>(MAX_CACHE_SIZE, 1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, Company> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public Optional<Company> add(Company company) throws SQLException {
        storage.put(company.id(), company);
        return Optional.of(company);
    }

    public Optional<Company> get(UUID companyId) throws SQLException {
        return storage.containsKey(companyId) ?
                Optional.of(storage.get(companyId))
                : Optional.empty();
    }
}
