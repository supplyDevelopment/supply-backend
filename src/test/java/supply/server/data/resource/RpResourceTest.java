package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import supply.server.configuration.DBConnection;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class RpResourceTest extends DBConnection {

    @Autowired
    private DataSource dataSource;

    @Test
    void addTest() throws SQLException, MalformedURLException {
        RpProject rpProject = new RpProject(dataSource);
        Project project = rpProject.add("testName", "testDescription", UUID.randomUUID());

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = new CreateResource(
                List.of(new URL("http://test.com")),
                "testName",
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                UUID.randomUUID(),
                UUID.randomUUID(),
                project.id(),
                ResourceStatus.ACTIVE,
                "testDescription"
                );

        Resource resource = rpResource.add(createResource).orElseThrow();

        assertEquals(createResource.name(), resource.name());
        assertEquals(createResource.count(), resource.count());
        assertEquals(createResource.unit(), resource.unit());
        assertEquals(createResource.type(), resource.type());
        assertEquals(createResource.projectId(), resource.projectId());
        assertEquals(createResource.status(), resource.status());
        assertEquals(createResource.description(), resource.description());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Resource insertedResource = jdbcSession.sql("SELECT * FROM resource WHERE id = ?")
                .set(resource.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        Array imagesArray = rset.getArray("images");
                        List<URL> images = null;

                        if (imagesArray != null) {
                            images = Arrays.stream((String[]) imagesArray.getArray())
                                    .map(url -> {
                                        try {
                                            return new URL(url);
                                        } catch (MalformedURLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .toList();
                        }

                        return new Resource(
                                rset.getObject("id", UUID.class),
                                images,
                                rset.getString("name"),
                                rset.getInt("count"),
                                Unit.valueOf(rset.getString("unit")),
                                ResourceType.valueOf(rset.getString("type")),
                                rset.getObject("projectId", UUID.class),
                                ResourceStatus.valueOf(rset.getString("status")),
                                rset.getString("description"),
                                null,
                                null,
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        );
                    }
                    return null;
                });

        assertEquals(insertedResource.id(), resource.id());
        assertEquals(insertedResource.images().get(0), resource.images().get(0));
        assertEquals(insertedResource.name(), resource.name());
        assertEquals(insertedResource.count(), resource.count());
        assertEquals(insertedResource.unit(), resource.unit());
        assertEquals(insertedResource.type(), resource.type());
        assertEquals(insertedResource.projectId(), resource.projectId());
        assertEquals(insertedResource.status(), resource.status());
        assertEquals(insertedResource.description(), resource.description());
    }

    @Test
    void getTest() throws SQLException, MalformedURLException {
        RpProject rpProject = new RpProject(dataSource);
        Project project = rpProject.add("testName", "testDescription", UUID.randomUUID());

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = new CreateResource(
                List.of(new URL("http://test.com")),
                "testName",
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                UUID.randomUUID(),
                UUID.randomUUID(),
                project.id(),
                ResourceStatus.ACTIVE,
                "testDescription"
        );

        Resource expected = rpResource.add(createResource).orElseThrow();
        Resource actual = rpResource.get(expected.id()).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.images().get(0), actual.images().get(0));
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.count(), actual.count());
        assertEquals(expected.unit(), actual.unit());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.projectId(), actual.projectId());
        assertEquals(expected.status(), actual.status());
        assertEquals(expected.description(), actual.description());
    }

}
