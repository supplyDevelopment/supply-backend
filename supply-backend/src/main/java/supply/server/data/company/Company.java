package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.supplier.Supplier;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Company (
        UUID id,
        String name,
        List<Email> contactEmails,
        List<Phone> contactPhones,
        Bil bil_address,
        Tax tax,
        List<Address> addresses,
        CompanyStatus status,
        LocalDate createdAt,
        LocalDate updatedAt
) implements Serializable {
}
