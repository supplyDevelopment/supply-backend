package supply.server.configuration;

import org.springframework.security.core.parameters.P;
import supply.server.data.company.Company;
import supply.server.data.company.RpCompany;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.resource.Resource;
import supply.server.data.resource.RpResource;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DataCreator extends DataGenerator {

    private final List<Company> storedCompanies = new ArrayList<>();
    private final List<Project> storedProjects = new ArrayList<>();
    private final List<Warehouse> storedWarehouses = new ArrayList<>();
    private final List<User> storedUsers = new ArrayList<>();
    private final List<Resource> storedResources = new ArrayList<>();

    protected List<Company> getCompanies(int count, boolean unique) throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource());
        List<Company> companies = new ArrayList<>();

        int storedCount;
        if (!unique) {
            storedCount = storedCompanies.size();
            for (int i = 0; i < storedCount; i++) {
                companies.add(storedCompanies.get(i));
            }
        } else {
            storedCount = 0;
        }

        for (int i = 0; i < count - storedCount; i++) {
            Company company = rpCompany.add(generateCompany()).orElseThrow();
            companies.add(company);
            storedCompanies.add(company);
        }

        return companies;
    }

    protected Company getCompany(boolean unique) throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource());
        if (storedCompanies.isEmpty() || unique) {
            Company company = rpCompany.add(generateCompany()).orElseThrow();
            storedCompanies.add(company);
            return company;
        }
        return storedCompanies.get(0);
    }

    protected List<Project> getProjects(int count, boolean unique) throws SQLException {
        RpProject rpProject = new RpProject(dataSource());
        List<Project> projects = new ArrayList<>();

        int storedCount;
        if (!unique) {
            storedCount = storedProjects.size();
            for (int i = 0; i < storedCount; i++) {
                projects.add(storedProjects.get(i));
            }
        } else {
            storedCount = 0;
        }

        for (int i = 0; i < count - storedCount; i++) {
            Project project = rpProject.add(
                    generateString(10),
                    generateString(10),
                    getCompany(unique).id()
            ).orElseThrow();
            projects.add(project);
            storedProjects.add(project);
        }
        return projects;
    }

    protected Project getProject(boolean unique) throws SQLException {
        RpProject rpProject = new RpProject(dataSource());
        if (storedProjects.isEmpty() || unique) {
            Project project = rpProject.add(
                    generateString(10),
                    generateString(10),
                    getCompany(unique).id()
            ).orElseThrow();
            storedProjects.add(project);
            return project;
        }
        return storedProjects.get(0);
    }

    protected List<User> getUsers(int count, boolean unique) throws SQLException {
        RpUser rpUser = new RpUser(dataSource());
        List<User> users = new ArrayList<>();

        int storedCount;
        if (!unique) {
            storedCount = storedUsers.size();
            for (int i = 0; i < storedCount; i++) {
                users.add(storedUsers.get(i));
            }
        } else {
            storedCount = 0;
        }

        for (int i = 0; i < count - storedCount; i++) {
            User user = rpUser.add(generateUser(), getCompany(unique).id()).orElseThrow();
            users.add(user);
            storedUsers.add(user);
        }
        return users;
    }

    protected User getUser(boolean unique) throws SQLException {
        RpUser rpUser = new RpUser(dataSource());
        if (storedUsers.isEmpty() || unique) {
            User user = rpUser.add(generateUser(), getCompany(unique).id()).orElseThrow();
            storedUsers.add(user);
            return user;
        }
        return storedUsers.get(0);
    }

    protected List<Warehouse> getWarehouses(int count, boolean unique) throws SQLException {
        RpWarehouse rpWarehouse = new RpWarehouse(dataSource());
        List<Warehouse> warehouses = new ArrayList<>();

        int storedCount;
        if (!unique) {
            storedCount = storedWarehouses.size();
            for (int i = 0; i < storedCount; i++) {
                warehouses.add(storedWarehouses.get(i));
            }
        } else {
            storedCount = 0;
        }

        for (int i = 0; i < count - storedCount; i++) {
            Warehouse warehouse = rpWarehouse.add(
                    generateWarehouse(getUsers(2, unique).stream().map(User::id).toList()),
                    getCompany(unique).id()
            ).orElseThrow();
            warehouses.add(warehouse);
            storedWarehouses.add(warehouse);
        }
        return warehouses;
    }

    protected Warehouse getWarehouse(boolean unique) throws SQLException {
        RpWarehouse rpWarehouse = new RpWarehouse(dataSource());
        if (storedWarehouses.isEmpty() || unique) {
            Warehouse warehouse = rpWarehouse.add(
                    generateWarehouse(getUsers(2, unique).stream().map(User::id).toList()),
                    getCompany(unique).id()
            ).orElseThrow();
            storedWarehouses.add(warehouse);
            return warehouse;
        }
        return storedWarehouses.get(0);
    }

    protected List<Resource> getResources(int count, boolean unique) throws SQLException {
        RpResource rpResource = new RpResource(dataSource());
        List<Resource> resources = new ArrayList<>();

        int storedCount;
        if (!unique) {
            storedCount = storedResources.size();
            for (int i = 0; i < storedCount; i++) {
                resources.add(storedResources.get(i));
            }
        } else {
            storedCount = 0;
        }

        for (int i = 0; i < count - storedCount; i++) {
            Resource resource = rpResource.add(
                    generateResource(
                            getUser(unique).id(),
                            getWarehouse(unique).id(),
                            getProject(unique).id()
                    )
            ).orElseThrow();
            resources.add(resource);
            storedResources.add(resource);
        }
        return resources;
    }

    protected Resource getResource(boolean unique) throws SQLException {
        RpResource rpResource = new RpResource(dataSource());
        if (storedResources.isEmpty() || unique) {
            Resource resource = rpResource.add(
                    generateResource(
                            getUser(unique).id(),
                            getWarehouse(unique).id(),
                            getProject(unique).id()
                    )
            ).orElseThrow();
            storedResources.add(resource);
            return resource;
        }
        return storedResources.get(0);
    }

}
