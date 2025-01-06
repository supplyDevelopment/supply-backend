package supply.server.data.project;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.Pagination;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpProject {

    private final DataSource dataSource;

    public Optional<Project> add(String name, String description, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID projectId = jdbcSession
                .sql("""
                        INSERT INTO project (name, description)
                        VALUES (?, ?)
                        RETURNING id
                        """)
                .set(name)
                .set(description)
                .insert(new SingleOutcome<>(UUID.class));

        // TODO: implement connection with company

        return Optional.of(new Project(
                projectId,
                name,
                description,
                companyId,
                LocalDate.now(),
                LocalDate.now()
        ));
    }

    public Optional<Project> get(UUID projectId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, name, description, created_at, updated_at FROM project
                        WHERE id = ?
                        """)
                .set(projectId)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(new Project(
                                rset.getObject("id", UUID.class),
                                rset.getString("name"),
                                rset.getString("description"),
                                null, // TODO: find company id from connection
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }
                    return Optional.empty();
                });
    }

    public List<Project> getAll(String prefix, Pagination pagination) {
        throw new NotImplementedException();
    }

}
