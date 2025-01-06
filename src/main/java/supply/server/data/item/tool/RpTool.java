package supply.server.data.item.tool;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import supply.server.data.utils.item.ItemStatus;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpTool {

    public final DataSource dataSource;

    public Optional<Tool> add(Tool tool) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID toolId = jdbcSession
                .sql("""
                    INSERT INTO tool (name, description, status, serial_number)
                    VALUES (?, ?, ?::INVENTORY_ITEM_STATUS, ?)
                    """)
                .set(tool.name())
                .set(tool.description())
                .set(tool.status().name())
                .set(tool.serialNumber())
                .insert(new SingleOutcome<>(UUID.class));

        return Optional.of(new Tool(
                toolId,
                tool.name(),
                tool.description(),
                tool.status(),
                tool.serialNumber()
        ));
    }

    public Optional<Tool> get(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                    SELECT * FROM tool
                    WHERE id = ?
                    """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactToolFromResultSet(rset);
                    }
                    return Optional.empty();
                });
    }

    private static Optional<Tool> compactToolFromResultSet(ResultSet rset) throws SQLException {
        return Optional.of(new Tool(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                rset.getString("description"),
                ItemStatus.valueOf(rset.getString("status")),
                rset.getString("serial_number")
        ));
    }
    
}
