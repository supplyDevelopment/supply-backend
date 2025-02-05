package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.resource.history.CreateHistory;
import supply.server.data.resource.history.ResourceHistory;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import javax.crypto.spec.OAEPParameterSpec;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
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

        connection.close();
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
                        return Optional.of(compactFromRset(rset));
                    }
                    return Optional.empty();
                });
    }

    public Optional<Resource> get(CreateResource createResource, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();

        Array imagesArray = connection.createArrayOf(
                "VARCHAR",
                createResource.images().stream().map(URL::toString).toArray(String[]::new)
        );

        String SQL = """
                    WITH params AS (
                        SELECT ?::VARCHAR[] AS images
                    )
                    SELECT r.id, r.images, r.name, r.count, r.unit, r.type, r.projectId,
                           r.status, r.description, r.warehouseId, r.created_at, r.updated_at,
                           ru.user_id AS user_id
                    FROM resource r
                    LEFT JOIN resource_users ru ON r.id = ru.resource_id
                    JOIN company_warehouses cw ON r.warehouseId = cw.warehouse
                    JOIN params p ON true
                    WHERE cw.company = ?
                      AND r.images = p.images
                      AND r.name = ?
                      AND r.count = ?
                      AND r.unit = ?::UNIT
                      AND r.type = ?::RESOURCE_TYPE
                      AND r.projectId = ?
                      AND r.status = ?::INVENTORY_ITEM_STATUS
                      AND r.description = ?
                      AND r.warehouseId = ?
                      AND ru.user_id = ?
                """;

        Optional<Resource> result = jdbcSession
                .sql(SQL)
                .set(imagesArray)
                .set(companyId)
                .set(createResource.name())
                .set(createResource.count())
                .set(createResource.unit().toString())
                .set(createResource.type().toString())
                .set(createResource.projectId())
                .set(createResource.status().toString())
                .set(createResource.description())
                .set(createResource.warehouseId())
                .set(createResource.userId())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(compactFromRset(rset));
                    }
                    return Optional.empty();
                });

        connection.close();
        return result;
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

                        resources.add(compactFromRset(rset));
                    }
                    return new PaginatedList<>(total, resources);
                });
    }

    public Optional<Resource> edit(UUID resourceId, UUID companyId, long count) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        jdbcSession
                .sql("""
                    UPDATE resource
                    SET count = ?
                    WHERE id = ? and warehouseId in
                    (SELECT warehouse FROM company_warehouses WHERE company = ?)
                    """)
                .set(count)
                .set(resourceId)
                .set(companyId)
                .update(Outcome.VOID);

        return get(resourceId, companyId);
    }

    public void delete(UUID resourceId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        jdbcSession
                .sql("""
                    DELETE FROM resource_users
                    WHERE resource_id = ?
                    """)
                .set(resourceId)
                .update(Outcome.VOID);

        jdbcSession
                .sql("""
                    DELETE FROM resource
                    WHERE id = ? AND warehouseId in
                    (SELECT warehouse FROM company_warehouses WHERE company = ?)
                    """)
                .set(resourceId)
                .set(companyId)
                .update(Outcome.VOID);

        jdbcSession
                .sql("""
                    UPDATE resource_history
                    SET prev_id = null
                    WHERE prev_id = ?
                    """)
                .set(resourceId)
                .update(Outcome.VOID);

        jdbcSession
                .sql("""                    
                    UPDATE resource_history
                    SET goal_id = null
                    WHERE goal_id = ?;
                    """)
                .set(resourceId)
                .update(Outcome.VOID);
    }

    private Resource compactFromRset(ResultSet rset) throws SQLException {
        Array dbImagesArray = rset.getArray("images");
        List<URL> images = null;
        if (dbImagesArray != null) {
            images = Arrays.stream((String[]) dbImagesArray.getArray())
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
                rset.getObject("warehouseId", UUID.class),
                rset.getObject("user_id", UUID.class),
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate()
        );
    }

}
