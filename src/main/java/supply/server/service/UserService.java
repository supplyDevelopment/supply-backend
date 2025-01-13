package supply.server.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.supplier.Supplier;
import supply.server.data.user.User;
import supply.server.data.user.userDetails.UserEntityDetails;
import supply.server.data.warehouse.Warehouse;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final RepositoryService repository;

    private User user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        UUID id = ((UserEntityDetails) authentication.getPrincipal()).getId();
        return repository.getUser().get(id);
    }

    // TODO: add checks for permissions
    // TODO: add validation for parameters and implement base methods
    public PaginatedList<Project> projects(String prefix, Pagination pagination) {
        return repository.getProject().getAll(prefix, user().companyId(), pagination);
    }

    public PaginatedList<Warehouse> warehouses(String prefix, Pagination pagination) {
        return repository.getWarehouse().getAll(prefix, user().companyId(), pagination);
    }

    public PaginatedList<User> users(String prefix, Pagination pagination) {
        return repository.getUser().getAll(prefix, user().companyId(), pagination);
    }

    public PaginatedList<Resource> resources(String prefix, Pagination pagination) {
        return repository.getResource().getAll(prefix, user().companyId(), pagination);
    }

    public List<Supplier> suppliers() {
        throw new NotImplementedException();
    }

}
