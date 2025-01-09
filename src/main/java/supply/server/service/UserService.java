package supply.server.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.supplier.Supplier;
import supply.server.data.user.User;
import supply.server.data.warehouse.Warehouse;

import java.util.List;

@AllArgsConstructor
public class UserService {

    private final User user;
    private final RepositoryService repository;

    // TODO: add checks for permissions
    // TODO: add validation for parameters and implement base methods
    public PaginatedList<Project> projects(String prefix, Pagination pagination) {
        return repository.getProject().getAll(prefix, user.companyId(), pagination);
    }

    public PaginatedList<Warehouse> warehouses(String prefix, Pagination pagination) {
        return repository.getWarehouse().getAll(prefix, user.companyId(), pagination);
    }

    public PaginatedList<User> users(String prefix, Pagination pagination) {
        return repository.getUser().getAll(prefix, user.companyId(), pagination);
    }

    public PaginatedList<Resource> resources(String prefix, Pagination pagination) {
        return repository.getResource().getAll(prefix, user.companyId(), pagination);
    }

    public List<Supplier> suppliers() {
        throw new NotImplementedException();
    }

}
