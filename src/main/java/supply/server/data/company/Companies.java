package supply.server.data.company;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface Companies {
    Optional<Company> add(CreateCompany createCompany) throws SQLException;

    Optional<Company> get(UUID companyId) throws SQLException;
}
