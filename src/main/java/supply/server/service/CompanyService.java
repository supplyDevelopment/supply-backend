package supply.server.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.PgRepository;
import supply.server.data.company.Company;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.supplier.Supplier;
import supply.server.data.user.User;
import supply.server.data.warehouse.Warehouse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class CompanyService {

    private final Company company;
    private final RepositoryService repository;

    public PaginatedList<Project> projects(Pagination pagination) {
        PaginatedList<UUID> projectIds;
        try {
            projectIds = company.projectIds(pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        List<Project> projects = new ArrayList<>();
        for (UUID id: projectIds.items()) {
            Project project = repository.getProject().get(id);
            projects.add(project);
        }

        return new PaginatedList<>(projectIds.total(), projects);
    }

    public PaginatedList<Warehouse> warehouses(Pagination pagination) {
        PaginatedList<UUID> warehouseIds;
        try {
            warehouseIds = company.warehouseIds(pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        List<Warehouse> warehouses = new ArrayList<>();
        for (UUID id: warehouseIds.items()) {
            Warehouse warehouse = repository.getWarehouse().get(id);
            warehouses.add(warehouse);
        }

        return new PaginatedList<>(warehouseIds.total(), warehouses);
    }

    public PaginatedList<User> users(Pagination pagination) {
        PaginatedList<UUID> userIds;
        try {
            userIds = company.userIds(pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        List<User> users = new ArrayList<>();
        for (UUID id: userIds.items()) {
            User user = repository.getUser().get(id);
            users.add(user);
        }

        return new PaginatedList<>(userIds.total(), users);
    }

    public PaginatedList<Resource> resources(Pagination pagination) {
        PaginatedList<UUID> resourceIds;

        try {
            resourceIds = company.resourceIds(pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        List<Resource> resources = new ArrayList<>();
        for (UUID id: resourceIds.items()) {
            Resource resource = repository.getResource().get(id);
            resources.add(resource);
        }

        return new PaginatedList<>(resourceIds.total(), resources);
    }

    public List<Supplier> suppliers() {
        throw new NotImplementedException();
    }

}
