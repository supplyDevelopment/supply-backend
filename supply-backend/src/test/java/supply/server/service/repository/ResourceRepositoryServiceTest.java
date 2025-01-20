package supply.server.service.repository;

import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.configuration.exception.DataNotFound;
import supply.server.data.company.Company;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.InMemoryRpResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.RpResource;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceRepositoryServiceTest extends DataCreator {

    private final InMemoryRpResource inMemoryRpResource = new InMemoryRpResource();
    private final RpResource rpResource = new RpResource(dataSource);
    private final ResourceRepositoryService resourceService = new ResourceRepositoryService(rpResource, inMemoryRpResource);

    @Test
    void addAndGetTest() throws SQLException {
        Company company = getCompany(false);
        CreateResource createResource = generateResource(
                getUser(false).id(),
                getWarehouse(false).id(),
                getProject(false).id()
        );

        Resource resource = resourceService.add(createResource, company.id());
        checkEquality(createResource, resource);

        Resource actual1 = resourceService.get(resource.id(), company.id());
        checkEquality(resource, actual1);

        Resource actual2 = inMemoryRpResource.get(resource.id(), company.id()).orElseThrow();
        checkEquality(resource, actual2);

        Resource actual3 = rpResource.get(resource.id(), company.id()).orElseThrow();
        checkEquality(resource, actual3);
    }

    @Test
    void getRpTest() throws SQLException {
        Company company = getCompany(false);
        CreateResource createResource = generateResource(
                getUser(false).id(),
                getWarehouse(false).id(),
                getProject(false).id()
        );

        Resource resource = rpResource.add(createResource).orElseThrow();
        checkEquality(createResource, resource);

        assertTrue(inMemoryRpResource.get(resource.id(), company.id()).isEmpty());

        Resource actual = resourceService.get(resource.id(), company.id());
        checkEquality(resource, actual);

        assertTrue(inMemoryRpResource.get(resource.id(), company.id()).isPresent());

        assertThrows(DataNotFound.class, () -> resourceService.get(UUID.randomUUID(), company.id()));
    }

    private void checkEquality(CreateResource createResource, Resource resource) {
        assertEquals(createResource.name(), resource.name());
        assertEquals(createResource.count(), resource.count());
        assertEquals(createResource.unit(), resource.unit());
        assertEquals(createResource.type(), resource.type());
        assertEquals(createResource.projectId(), resource.projectId());
        assertEquals(createResource.status(), resource.status());
        assertEquals(createResource.description(), resource.description());
    }

    private void checkEquality(Resource expected, Resource actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.images().get(0), actual.images().get(0));
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.count(), actual.count());
        assertEquals(expected.unit(), actual.unit());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.projectId(), actual.projectId());
        assertEquals(expected.status(), actual.status());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

}
