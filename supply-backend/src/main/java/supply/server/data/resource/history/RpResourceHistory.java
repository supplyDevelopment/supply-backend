package supply.server.data.resource.history;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.resource.types.ResourceStatus;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class RpResourceHistory {

    public final DataSource dataSource;

    public Optional<ResourceHistory> add(CreateHistory createHistory, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        UUID historyOperationId = jdbcSession
                .sql("""
                     INSERT INTO resource_history (
                         company_id,
                         prev_id,
                         goal_id,
                         quantity,
                         created_at,
                         name,
                         count,
                         projectId,
                         status,
                         description,
                         warehouseId,
                         userId,
                         goal_name,
                         goal_count,
                         goal_projectId,
                         goal_status,
                         goal_description,
                         goal_warehouseId,
                         goal_userId
                     )
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::INVENTORY_ITEM_STATUS, ?, ?, ?, ?, ?, ?, ?::INVENTORY_ITEM_STATUS, ?, ?, ?)
                 """)
                .set(companyId)
                .set(createHistory.prev_id())
                .set(createHistory.goal_id())
                .set(createHistory.quantity())
                .set(LocalDate.now())
                .set(createHistory.prev_name())
                .set(createHistory.prev_count())
                .set(createHistory.prev_projectId())
                .set(Objects.isNull(createHistory.prev_status()) ? null : createHistory.prev_status().toString())
                .set(createHistory.prev_description())
                .set(createHistory.prev_warehouseId())
                .set(createHistory.prev_userId())
                .set(createHistory.goal_name())
                .set(createHistory.goal_count())
                .set(createHistory.goal_projectId())
                .set(Objects.isNull(createHistory.goal_status()) ? null : createHistory.goal_status().toString())
                .set(createHistory.goal_description())
                .set(createHistory.goal_warehouseId())
                .set(createHistory.goal_userId())
                .insert(new SingleOutcome<>(UUID.class));


        return Optional.of(new ResourceHistory(
                historyOperationId,
                createHistory.quantity(),
                createHistory.prev_id(),
                createHistory.goal_id(),
                createHistory.prev_name(),
                createHistory.goal_name(),
                createHistory.prev_status(),
                createHistory.goal_status(),
                createHistory.prev_count(),
                createHistory.goal_count(),
                createHistory.prev_projectId(),
                createHistory.goal_projectId(),
                createHistory.prev_description(),
                createHistory.goal_description(),
                createHistory.prev_warehouseId(),
                createHistory.goal_warehouseId(),
                createHistory.prev_userId(),
                createHistory.goal_userId(),
                LocalDate.now()
        ));
    }

    public PaginatedList<ResourceHistory> get(UUID resourceId, UUID companyId, Pagination pagination) throws SQLException {
        String SQL = """
            WITH history_table AS (
                SELECT *
                FROM resource_history
                WHERE (prev_id = ? OR goal_id = ?)
                  AND company_id = ?
            )
            SELECT *,
                   (SELECT COUNT(*) FROM history_table) AS total_count
            FROM history_table
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
            """;

        return new JdbcSession(dataSource)
                .sql(SQL)
                .set(resourceId)
                .set(resourceId)
                .set(companyId)
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<ResourceHistory> histories = new ArrayList<>();
                    long total = 0;

                    while (rset.next()) {
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }

                        String prev_id = rset.getString("prev_id");
                        String goal_id = rset.getString("goal_id");
                        String projectId = rset.getString("projectId");
                        String goal_projectId = rset.getString("goal_projectId");
                        String warehouseId = rset.getString("warehouseId");
                        String goal_warehouseId = rset.getString("goal_warehouseId");
                        String userId = rset.getString("userId");
                        String goal_userId = rset.getString("goal_userId");
                        String goal_status = rset.getString("goal_status");
                        String prev_status = rset.getString("status");
                        String count = rset.getString("count");
                        String goal_count = rset.getString("goal_count");

                        histories.add(new ResourceHistory(
                                rset.getObject("id", UUID.class),
                                rset.getInt("quantity"),
                                Objects.isNull(prev_id) ? null : UUID.fromString(prev_id),
                                Objects.isNull(goal_id) ? null : UUID.fromString(goal_id),
                                rset.getString("name"),
                                rset.getString("goal_name"),
                                Objects.isNull(prev_status) ? null : ResourceStatus.valueOf(prev_status),
                                Objects.isNull(goal_status) ? null : ResourceStatus.valueOf(goal_status),
                                Objects.isNull(count) ? null : Integer.parseInt(count),
                                Objects.isNull(goal_count) ? null : Integer.parseInt(goal_count),
                                Objects.isNull(projectId) ? null : UUID.fromString(projectId),
                                Objects.isNull(goal_projectId) ? null : UUID.fromString(goal_projectId),
                                rset.getString("description"),
                                rset.getString("goal_description"),
                                Objects.isNull(warehouseId) ? null : UUID.fromString(warehouseId),
                                Objects.isNull(goal_warehouseId) ? null : UUID.fromString(goal_warehouseId),
                                Objects.isNull(userId) ? null : UUID.fromString(userId),
                                Objects.isNull(goal_userId) ? null : UUID.fromString(goal_userId),
                                rset.getDate("created_at").toLocalDate()
                        ));
                    }
                    return new PaginatedList<>(total, histories);
                });
    }

    public Optional<ResourceHistory> get(UUID historyId, UUID companyId) throws SQLException {
        String SQL = """
            SELECT *
            FROM resource_history
                WHERE id = ?
                  AND company_id = ?
            """;

        return new JdbcSession(dataSource)
                .sql(SQL)
                .set(historyId)
                .set(companyId)
                .select((rset, stmt) -> {
                    if (!rset.next()) {

                        String prev_id = rset.getString("prev_id");
                        String goal_id = rset.getString("goal_id");
                        String projectId = rset.getString("projectId");
                        String goal_projectId = rset.getString("goal_projectId");
                        String warehouseId = rset.getString("warehouseId");
                        String goal_warehouseId = rset.getString("goal_warehouseId");
                        String userId = rset.getString("userId");
                        String goal_userId = rset.getString("goal_userId");
                        String goal_status = rset.getString("goal_status");
                        String prev_status = rset.getString("status");
                        String count = rset.getString("count");
                        String goal_count = rset.getString("goal_count");

                        return Optional.of(new ResourceHistory(
                                rset.getObject("id", UUID.class),
                                rset.getInt("quantity"),
                                Objects.isNull(prev_id) ? null : UUID.fromString(prev_id),
                                Objects.isNull(goal_id) ? null : UUID.fromString(goal_id),
                                rset.getString("name"),
                                rset.getString("goal_name"),
                                Objects.isNull(prev_status) ? null : ResourceStatus.valueOf(prev_status),
                                Objects.isNull(goal_status) ? null : ResourceStatus.valueOf(goal_status),
                                Objects.isNull(count) ? null : Integer.parseInt(count),
                                Objects.isNull(goal_count) ? null : Integer.parseInt(goal_count),
                                Objects.isNull(projectId) ? null : UUID.fromString(projectId),
                                Objects.isNull(goal_projectId) ? null : UUID.fromString(goal_projectId),
                                rset.getString("description"),
                                rset.getString("goal_description"),
                                Objects.isNull(warehouseId) ? null : UUID.fromString(warehouseId),
                                Objects.isNull(goal_warehouseId) ? null : UUID.fromString(goal_warehouseId),
                                Objects.isNull(userId) ? null : UUID.fromString(userId),
                                Objects.isNull(goal_userId) ? null : UUID.fromString(goal_userId),
                                rset.getDate("created_at").toLocalDate()
                        ));
                    }
                    return Optional.empty();
                });
    }

    public PaginatedList<ResourceHistory> get(UUID companyId, Pagination pagination) throws SQLException {
        String SQL = """
            WITH history_table AS (
                SELECT *
                FROM resource_history
                WHERE company_id = ?
            )
            SELECT *,
                   (SELECT COUNT(*) FROM history_table) AS total_count
            FROM history_table
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
            """;

        return new JdbcSession(dataSource)
                .sql(SQL)
                .set(companyId)
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<ResourceHistory> histories = new ArrayList<>();
                    long total = 0;

                    while (rset.next()) {
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }

                        String prev_id = rset.getString("prev_id");
                        String goal_id = rset.getString("goal_id");
                        String projectId = rset.getString("projectId");
                        String goal_projectId = rset.getString("goal_projectId");
                        String warehouseId = rset.getString("warehouseId");
                        String goal_warehouseId = rset.getString("goal_warehouseId");
                        String userId = rset.getString("userId");
                        String goal_userId = rset.getString("goal_userId");
                        String goal_status = rset.getString("goal_status");
                        String prev_status = rset.getString("status");
                        String count = rset.getString("count");
                        String goal_count = rset.getString("goal_count");

                        histories.add(new ResourceHistory(
                                rset.getObject("id", UUID.class),
                                rset.getInt("quantity"),
                                Objects.isNull(prev_id) ? null : UUID.fromString(prev_id),
                                Objects.isNull(goal_id) ? null : UUID.fromString(goal_id),
                                rset.getString("name"),
                                rset.getString("goal_name"),
                                Objects.isNull(prev_status) ? null : ResourceStatus.valueOf(prev_status),
                                Objects.isNull(goal_status) ? null : ResourceStatus.valueOf(goal_status),
                                Objects.isNull(count) ? null : Integer.parseInt(count),
                                Objects.isNull(goal_count) ? null : Integer.parseInt(goal_count),
                                Objects.isNull(projectId) ? null : UUID.fromString(projectId),
                                Objects.isNull(goal_projectId) ? null : UUID.fromString(goal_projectId),
                                rset.getString("description"),
                                rset.getString("goal_description"),
                                Objects.isNull(warehouseId) ? null : UUID.fromString(warehouseId),
                                Objects.isNull(goal_warehouseId) ? null : UUID.fromString(goal_warehouseId),
                                Objects.isNull(userId) ? null : UUID.fromString(userId),
                                Objects.isNull(goal_userId) ? null : UUID.fromString(goal_userId),
                                rset.getDate("created_at").toLocalDate()
                        ));
                    }
                    return new PaginatedList<>(total, histories);
                });
    }

}
