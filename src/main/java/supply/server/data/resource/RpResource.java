package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.Pagination;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpResource {

    public final DataSource dataSource;

    public Optional<Resource> add(CreateResource createResource) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();

        Array sqlUrls = connection.createArrayOf(
                "VARCHAR",
                createResource.images().stream().map(URL::toString).toArray()
        );

        UUID resourceId = jdbcSession
                .sql("""
                        INSERT INTO resource (
                        images,
                        name,
                        count,
                        unit,
                        type,
                        projectId,
                        status,
                        description,
                        created_at
                        )
                        VALUES (?, ?, ?, ?::UNIT, ?::RESOURCE_TYPE, ?, ?::INVENTORY_ITEM_STATUS, ?, ?)
                        """)
                .set(sqlUrls)
                .set(createResource.name())
                .set(createResource.count())
                .set(createResource.unit().toString())
                .set(createResource.type().toString())
                .set(createResource.projectId())
                .set(createResource.status().toString())
                .set(createResource.description())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        return Optional.of(new Resource(
                resourceId,
                createResource.images(),
                createResource.name(),
                createResource.count(),
                createResource.unit(),
                createResource.type(),
                createResource.projectId(),
                createResource.status(),
                createResource.description(),
                null, // TODO: implement connection with warehouse
                null,
                LocalDate.now(),
                LocalDate.now()
        ));
    }

    public Optional<Resource> get(UUID resourceId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, images, name, count, unit, type, projectId, status, description, created_at, updated_at FROM resource
                        WHERE id = ?
                        """)
                .set(resourceId)
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

                        return Optional.of(new Resource(
                                rset.getObject("id", UUID.class),
                                images,
                                rset.getString("name"),
                                rset.getInt("count"),
                                Unit.valueOf(rset.getString("unit")),
                                ResourceType.valueOf(rset.getString("type")),
                                rset.getObject("projectId", UUID.class),
                                ResourceStatus.valueOf(rset.getString("status")),
                                rset.getString("description"),
                                null, // TODO: implement connection with warehouse
                                null,
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }
                    return Optional.empty();
                });
    }

    public List<Resource> getAllByName(String prefix, ResourceFilters filters,  Pagination pagination) {
        throw new NotImplementedException();
    }

    public List changeLogs(UUID resourceId, Pagination pagination) {
        throw new NotImplementedException();
    }

    public List moveLogs(UUID resourceId, Pagination pagination) {
        throw new NotImplementedException();
    }

    public Resource moveToUser(UUID resourceId, UUID userId) {
        throw new NotImplementedException();
    }

    public Resource moveToWarehouse(UUID resourceId, UUID warehouseId) {
        throw new NotImplementedException();
    }

    public Resource applyMove(UUID resourceId, UUID userId) {
        throw new NotImplementedException();
    }

    public Resource edit(
            UUID resourceId,
            Optional<String> name,
            Optional<Integer> count,
            Optional<UUID> projectId,
            Optional<ResourceStatus> status,
            Optional<String> description
    ) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        jdbcSession
                .sql("""
                        UPDATE resource SET
                        name = coalesce(?, name),
                        count = coalesce(?, count),
                        projectId = coalesce(?, projectId),
                        status = coalesce(?::INVENTORY_ITEM_STATUS, status),
                        description = coalesce(?, description)
                        WHERE id = ?
                        """)
                .set(name.orElse(null))
                .set(count.orElse(null))
                .set(projectId.orElse(null))
                .set(status.map(ResourceStatus::toString).orElse(null))
                .set(description.orElse(null))
                .set(resourceId)
                .update(Outcome.VOID);

        // TODO: create change log
        return get(resourceId).orElseThrow();
    }

}
