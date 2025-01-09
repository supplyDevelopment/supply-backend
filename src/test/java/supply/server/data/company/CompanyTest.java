package supply.server.data.company;

import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.RpResource;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.user.CreateUser;
import supply.server.data.user.RpUser;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.Unit;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;
import supply.server.data.warehouse.CreateWarehouse;
import supply.server.data.warehouse.RpWarehouse;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompanyTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    @Test
    void getProjectIdsTest() throws SQLException {
        RpProject rpProject = new RpProject(dataSource);
        List<UUID> projects = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            projects.add(rpProject.add("name" + i, "description" + i, getCompanyId()).orElseThrow().id());
        }
        RpCompany rpCompany = new RpCompany(dataSource);
        Company company = rpCompany.get(getCompanyId()).orElseThrow();

        for (int i = 0; i < 2; i++) {
            PaginatedList<UUID> projectIds = company.projectIds(new Pagination(5, i));
            assertEquals(10, projectIds.total());
            assertEquals(5, projectIds.items().size());
            for (UUID id: projectIds.items()) {
                assertTrue(projects.contains(id));
                projects.remove(id);
            }
        }
    }

    @Test
    void getWarehouseIdsTest() throws SQLException {
        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
        List<UUID> warehouses = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            warehouses.add(rpWarehouse.add(new CreateWarehouse(
                    "name" + i,
                    new Address("address" + i),
                    0L,
                    0L,
                    List.of(getUserId()),
                    getCompanyId()
            )).orElseThrow().id());
        }
        RpCompany rpCompany = new RpCompany(dataSource);
        Company company = rpCompany.get(getCompanyId()).orElseThrow();

        for (int i = 0; i < 2; i++) {
            PaginatedList<UUID> warehouseIds = company.warehouseIds(new Pagination(5, i));
            assertEquals(10, warehouseIds.total());
            assertEquals(5, warehouseIds.items().size());
            for (UUID id: warehouseIds.items()) {
                assertTrue(warehouses.contains(id));
                warehouses.remove(id);
            }
        }
    }

    @Test
    void getUserIdsTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);
        List<UUID> users = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            users.add(rpUser.add(new CreateUser(
                    new UserName("testFirstName", "testSecondName", "testLastName"),
                    new Email("email" + i + "@email.com"),
                    new Phone("+7900000000" + i),
                    "password",
                    getCompanyId(),
                    List.of(UserPermission.ADMIN)
            )).orElseThrow().id());
        }
        users.add(getUserId());
        RpCompany rpCompany = new RpCompany(dataSource);
        Company company = rpCompany.get(getCompanyId()).orElseThrow();

        for (int i = 0; i < 2; i++) {
            PaginatedList<UUID> userIds = company.userIds(new Pagination(5, i));
            assertEquals(10, userIds.total());
            assertEquals(5, userIds.items().size());
            for (UUID id: userIds.items()) {
                assertTrue(users.contains(id));
                users.remove(id);
            }
        }
    }

    @Test
    void getResourceIdsTest() throws SQLException, MalformedURLException {
        RpResource rpResource = new RpResource(dataSource);
        List<UUID> resources = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            resources.add(rpResource.add(new CreateResource(
                    List.of(new URL("http://test" + i + ".com")),
                    "name" + i,
                    i,
                    Unit.KG,
                    ResourceType.PRODUCT,
                    getUserId(),
                    getWarehouseId(),
                    getProjectId(),
                    ResourceStatus.ACTIVE,
                    "description" + i
            )).orElseThrow().id());
        }

        RpCompany rpCompany = new RpCompany(dataSource);
        Company company = rpCompany.get(getCompanyId()).orElseThrow();

        for (int i = 0; i < 2; i++) {
            PaginatedList<UUID> resourceIds = company.resourceIds(new Pagination(5, i));
            assertEquals(10, resourceIds.total());
            assertEquals(5, resourceIds.items().size());
            for (UUID id: resourceIds.items()) {
                assertTrue(resources.contains(id));
                resources.remove(id);
            }
        }
    }

}
