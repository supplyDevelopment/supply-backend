package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import supply.server.data.PaginatedList;
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
import java.util.*;

@Component
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
                        warehouseId,
                        created_at
                        )
                        VALUES (?, ?, ?, ?::UNIT, ?::RESOURCE_TYPE, ?, ?::INVENTORY_ITEM_STATUS, ?, ?, ?)
                        """)
                .set(sqlUrls)
                .set(createResource.name())
                .set(createResource.count())
                .set(createResource.unit().toString())
                .set(createResource.type().toString())
                .set(createResource.projectId())
                .set(createResource.status().toString())
                .set(createResource.description())
                .set(createResource.warehouseId())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        jdbcSession
                .sql("""
                        INSERT INTO resource_users
                        (resource_id, user_id)
                        VALUES (?, ?)
                        """)
                .set(resourceId)
                .set(createResource.userId())
                .insert(Outcome.VOID);

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
                createResource.warehouseId(),
                createResource.userId(),
                LocalDate.now(),
                LocalDate.now()
        ));
    }

    public Optional<Resource> get(UUID resourceId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                    SELECT r.id, r.images, r.name, r.count, r.unit, r.type, r.projectId,
                           r.status, r.description, r.warehouseId, r.created_at, r.updated_at,
                           ru.user_id AS user_id
                    FROM resource r
                    LEFT JOIN resource_users ru ON r.id = ru.resource_id
                    JOIN company_warehouses cw ON r.warehouseId = cw.warehouse
                    WHERE r.id = ? AND cw.company = ?
            """)
                .set(resourceId)
                .set(companyId)
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
                                rset.getObject("warehouseId", UUID.class),
                                rset.getObject("user_id", UUID.class),
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }
                    return Optional.empty();
                });
    }

    // TODO: implement filters
    public PaginatedList<Resource> getAll(String prefix, UUID companyId, Pagination pagination) throws SQLException {
        String SQLWith = """
                WITH params AS (SELECT ? AS lower_prefix),
                         resource_table AS (
                             SELECT
                                 r.id,
                                 r.images,
                                 r.name,
                                 r.count,
                                 r.unit,
                                 r.type,
                                 r.projectId,
                                 r.status,
                                 r.description,
                                 r.warehouseId,
                                 r.created_at,
                                 r.updated_at,
                                 ru.user_id AS user_id,
                                 cw.company AS company_id, -- Используем связь через warehouse и company
                                 CASE
                                     WHEN lower(r.name) LIKE concat((SELECT lower_prefix FROM params), '%') THEN 1
                                     WHEN lower(r.type::TEXT) LIKE concat((SELECT lower_prefix FROM params), '%') THEN 2
                                     WHEN lower(r.status::TEXT) LIKE concat((SELECT lower_prefix FROM params), '%') THEN 3
                                     ELSE 4
                                 END AS priority
                             FROM resource r
                             LEFT JOIN resource_users ru ON r.id = ru.resource_id
                             JOIN company_warehouses cw ON r.warehouseId = cw.warehouse  -- Замена связи на через warehouses
                             JOIN params ON true
                             WHERE cw.company = ? -- Фильтрация по company, через warehouse
                         )
                    SELECT *,
                           (SELECT COUNT(*) FROM resource_table WHERE priority <= 3) AS total_count
                    FROM resource_table
                    WHERE priority <= 3
                    ORDER BY priority ASC, created_at DESC
                    LIMIT ? OFFSET ?
            """;

        return new JdbcSession(dataSource)
                .sql(SQLWith)
                .set(prefix.toLowerCase() + "%")
                .set(companyId)
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<Resource> resources = new ArrayList<>();
                    long total = 0;

                    while (rset.next()) {
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }

                        List<URL> images = null;
                        Array imagesArray = rset.getArray("images");
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

                        resources.add(new Resource(
                                rset.getObject("id", UUID.class),
                                images,
                                rset.getString("name"),
                                rset.getInt("count"),
                                Unit.valueOf(rset.getString("unit")),
                                ResourceType.valueOf(rset.getString("type")),
                                rset.getObject("projectId", UUID.class),
                                ResourceStatus.valueOf(rset.getString("status")),
                                rset.getString("description"),
                                rset.getObject("warehouseId", UUID.class),
                                rset.getObject("user_id", UUID.class),
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }

                    return new PaginatedList<>(total, resources);
                });
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

    public Optional<Resource> edit(
            UUID resourceId,
            UUID companyId,
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
                    AND EXISTS (
                        SELECT 1
                        FROM company_warehouses cw
                        WHERE cw.company = ?
                        AND cw.warehouse = resource.warehouseId
                    )
                    """)
                .set(name.orElse(null))
                .set(count.orElse(null))
                .set(projectId.orElse(null))
                .set(status.map(ResourceStatus::toString).orElse(null))
                .set(description.orElse(null))
                .set(resourceId)
                .set(companyId)
                .update(Outcome.VOID);

        return get(resourceId, companyId);
    }


}
