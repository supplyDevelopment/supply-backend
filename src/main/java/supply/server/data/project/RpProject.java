package supply.server.data.project;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RpProject {

    private final DataSource dataSource;

    public Optional<Project> add(String name, String description, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID projectId = jdbcSession
                .sql("""
                        INSERT INTO project (name, description, created_at)
                        VALUES (?, ?, ?)
                        RETURNING id
                        """)
                .set(name)
                .set(description)
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        jdbcSession
                .sql("""
                        INSERT INTO company_projects
                        (project, company)
                        VALUES (?, ?)
                        """)
                .set(projectId)
                .set(companyId)
                .insert(Outcome.VOID);

        return Optional.of(new Project(
                projectId,
                name,
                description,
                companyId,
                LocalDate.now(),
                LocalDate.now()
        ));
    }

    public Optional<Project> get(UUID projectId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                    SELECT p.id, p.name, p.description, p.created_at, p.updated_at,
                           cp.company AS company_id
                    FROM project p
                    LEFT JOIN company_projects cp ON p.id = cp.project
                    WHERE p.id = ? AND cp.company = ?
                    """)
                .set(projectId)
                .set(companyId)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(new Project(
                                rset.getObject("id", UUID.class),
                                rset.getString("name"),
                                rset.getString("description"),
                                rset.getObject("company_id", UUID.class),
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }
                    return Optional.empty();
                });
    }

    public PaginatedList<Project> getAll(String prefix, UUID companyId, Pagination pagination) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession
                .sql("""
                        SELECT
                            p.id, p.name, p.description, p.created_at, p.updated_at,
                            COUNT(*) OVER() AS total_count
                        FROM project p
                        INNER JOIN company_projects cp ON p.id = cp.project
                        WHERE cp.company = ?
                          AND p.name ILIKE ?
                        LIMIT ?
                        OFFSET ?
                    """)
                .set(companyId)
                .set(prefix + "%")
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<Project> projects = new ArrayList<>();
                    long total = 0;
                    while (rset.next()) {
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                        projects.add(new Project(
                                rset.getObject("id", UUID.class),
                                rset.getString("name"),
                                rset.getString("description"),
                                companyId,
                                rset.getTimestamp("created_at").toLocalDateTime().toLocalDate(),
                                rset.getTimestamp("updated_at").toLocalDateTime().toLocalDate()
                        ));
                    }
                    return new PaginatedList<>(total, projects);
                });
    }

}
