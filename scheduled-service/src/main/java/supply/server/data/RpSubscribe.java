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
                        SElECT id, contact_emails, expires_at
                        FROM company
                    """)
                .select((rset, stmt) -> {
                    List<Subscribe> subscribes = new ArrayList<>();

                    while (rset.next()) {
                        String emails = rset.getString("contact_emails");
                        emails = emails.substring(1, emails.length() - 1);
                        List<String> companyEmails = Arrays.stream(
                                emails.split(",")
                        ).filter(string -> !string.isEmpty()).toList();

                        subscribes.add(new Subscribe(
                            UUID.fromString(rset.getString("id")),
                            companyEmails,
                            rset.getDate("expires_at").toLocalDate()
                        ));
                    }

                    return subscribes;
                });
    }

}
