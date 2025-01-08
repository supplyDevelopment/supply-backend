package supply.server.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.Repository;
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
    private final Repository repository;

    public PaginatedList<Project> projects(Pagination pagination) throws SQLException {
        PaginatedList<UUID> projectIds = company.projectIds(pagination);

        List<Project> projects = new ArrayList<>();
        for (UUID id: projectIds.items()) {
            Optional<Project> project = repository.rpProject().get(id);
            project.ifPresent(projects::add);
        }

        return new PaginatedList<>(projectIds.total(), projects);
    }

    public PaginatedList<Warehouse> warehouses(Pagination pagination) throws SQLException {
        PaginatedList<UUID> warehouseIds = company.warehouseIds(pagination);

        List<Warehouse> warehouses = new ArrayList<>();
        for (UUID id: warehouseIds.items()) {
            Optional<Warehouse> warehouse = repository.rpWarehouse().getById(id);
            warehouse.ifPresent(warehouses::add);
        }

        return new PaginatedList<>(warehouseIds.total(), warehouses);
    }

    public PaginatedList<User> users(Pagination pagination) throws SQLException {
        PaginatedList<UUID> userIds = company.userIds(pagination);

        List<User> users = new ArrayList<>();
        for (UUID id: userIds.items()) {
            Optional<User> user = repository.rpUser().get(id);
            user.ifPresent(users::add);
        }

        return new PaginatedList<>(userIds.total(), users);
    }

    public PaginatedList<Resource> resources(Pagination pagination) throws SQLException {
        PaginatedList<UUID> resourceIds = company.resourceIds(pagination);

        List<Resource> resources = new ArrayList<>();
        for (UUID id: resourceIds.items()) {
            Optional<Resource> resource = repository.rpResource().get(id);
            resource.ifPresent(resources::add);
        }

        return new PaginatedList<>(resourceIds.total(), resources);
    }

    public List<Supplier> suppliers() {
        throw new NotImplementedException();
    }

}
