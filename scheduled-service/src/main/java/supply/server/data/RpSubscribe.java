package supply.server.data;

import com.jcabi.jdbc.JdbcSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RpSubscribe {

    public final DataSource dataSource;

    public List<Subscribe> getAll() throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession
                .sql("""
                        SELECT
                            cs.company AS company_id,
                            cs.expires_at,
                            c.contact_emails
                        FROM company_subscribe cs
                        JOIN company c ON cs.company = c.id
                    """)
                .select((rset, stmt) -> {
                    List<Subscribe> subscribes = new ArrayList<>();

                    while (rset.next()) {
                        subscribes.add(new Subscribe(
                            UUID.fromString(rset.getString("company_id")),
                            Arrays.stream(rset.getString("contact_emails").split(",")).toList(),
                            rset.getDate("expires_at").toLocalDate()
                        ));
                    }

                    return subscribes;
                });
    }

}
