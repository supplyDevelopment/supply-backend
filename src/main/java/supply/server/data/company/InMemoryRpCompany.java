package supply.server.data.company;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class InMemoryRpCompany {

    private final HashMap<UUID, Company> storage;

    public InMemoryRpCompany() {
        this.storage = new HashMap<>();
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
