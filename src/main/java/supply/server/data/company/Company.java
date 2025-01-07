package supply.server.data.company;

import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.supplier.Supplier;
import supply.server.data.user.User;
import supply.server.data.utils.Address;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.data.warehouse.Warehouse;

import javax.sql.DataSource;
import java.time.LocalDate;
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
        LocalDate updatedAt,
        DataSource dataSource
) {

    public List<Project> projects() {
        throw new NotImplementedException();
    }

    public List<Warehouse> warehouses() {
        throw new NotImplementedException();
    }

    public List<User> users() {
        throw new NotImplementedException();
    }

    public List<Resource> resources() {
        throw new NotImplementedException();
    }

    public List<Supplier> suppliers() {
        throw new NotImplementedException();
    }

}
