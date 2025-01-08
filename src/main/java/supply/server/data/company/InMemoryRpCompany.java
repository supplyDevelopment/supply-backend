package supply.server.data.company;

import java.sql.SQLException;
import java.util.*;

public class InMemoryRpCompany {

    private final int MAX_CACHE_SIZE = 1000;

    private final Map<UUID, Company> storage;

    public InMemoryRpCompany() {
        this.storage = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
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
