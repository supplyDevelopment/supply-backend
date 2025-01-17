package supply.server.service.dataService;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.supplier.Supplier;
import supply.server.data.user.User;
import supply.server.data.warehouse.Warehouse;

import java.util.List;

@Service
public class SearchService extends UserService {

    public SearchService(RepositoryService repository) {
        super(repository);
    }

    public PaginatedList<Project> getProjects(String prefix, Pagination pagination) {
        return repository.getProject().getAll(prefix, user().companyId(), pagination);
    }

    public PaginatedList<Warehouse> getWarehouses(String prefix, Pagination pagination) {
        return repository.getWarehouse().getAll(prefix, user().companyId(), pagination);
    }

    public PaginatedList<User> getUsers(String prefix, Pagination pagination) {
        return repository.getUser().getAll(prefix, user().companyId(), pagination);
    }

    public PaginatedList<Resource> getResources(String prefix, Pagination pagination) {
        return repository.getResource().getAll(prefix, user().companyId(), pagination);
    }

    public List<Supplier> getSuppliers() {
        throw new NotImplementedException();
    }

}
