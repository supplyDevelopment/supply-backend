package supply.server.service.repository;

import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.configuration.exception.DataNotFoundException;
import supply.server.data.Redis;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectRepositoryServiceTest extends DataCreator {

    private final Redis<Project> inMemoryRpProject = new Redis<>(redisTemplate, "project:");
    private final RpProject rpProject = new RpProject(dataSource);

    private final ProjectRepositoryService projectService = new ProjectRepositoryService(rpProject, inMemoryRpProject);

    @Test
    void addAndGetTest() throws SQLException {
        String name = "name";
        String description = "description";
        UUID companyId = getCompany(false).id();

        Project project = projectService.add(name, description, companyId);
        assertEquals(name, project.name());
        assertEquals(description, project.description());
        assertEquals(companyId, project.companyId());

        Project actual1 = projectService.get(project.id(), companyId);
        assertEquals(project.id(), actual1.id());
        assertEquals(project.name(), actual1.name());
        assertEquals(project.description(), actual1.description());
        assertEquals(project.companyId(), actual1.companyId());

        Project actual2 = inMemoryRpProject.get(project.id()).orElseThrow();
        assertEquals(project.id(), actual2.id());
        assertEquals(project.name(), actual2.name());
        assertEquals(project.description(), actual2.description());
        assertEquals(project.companyId(), actual2.companyId());

        Project actual3 = rpProject.get(project.id(), companyId).orElseThrow();
        assertEquals(project.id(), actual3.id());
        assertEquals(project.name(), actual3.name());
        assertEquals(project.description(), actual3.description());
        assertEquals(project.companyId(), actual3.companyId());
    }

    @Test
    void getRpTest() throws SQLException {
        String name = "name";
        String description = "description";
        UUID companyId = getCompany(false).id();

        Project project = rpProject.add(name, description, companyId).orElseThrow();
        assertEquals(name, project.name());
        assertEquals(description, project.description());
        assertEquals(companyId, project.companyId());

        assertTrue(inMemoryRpProject.get(project.id()).isEmpty());

        Project actual = projectService.get(project.id(), companyId);
        assertEquals(project.id(), actual.id());
        assertEquals(project.name(), actual.name());
        assertEquals(project.description(), actual.description());
        assertEquals(project.companyId(), actual.companyId());

        assertTrue(inMemoryRpProject.get(project.id()).isPresent());

        assertThrows(DataNotFoundException.class, () -> projectService.get(UUID.randomUUID(), companyId));
    }

}
