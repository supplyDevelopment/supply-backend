package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.supplier.Supplier;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Company (
        UUID id,
        String name,
        List<Email> contactEmails,
        List<Phone> contactPhones,
        Bil bil_address,
        Tax tax,
        List<Address> addresses,
        CompanyStatus status,
        LocalDate createdAt,
        LocalDate updatedAt,
        DataSource dataSource
) {

    public PaginatedList<UUID> projectIds(Pagination pagination) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession
                .sql("""
                        SELECT
                            cp.project,
                            COUNT(*) OVER() AS total_count
                        FROM company_projects cp
                        WHERE cp.company = ?
                        LIMIT ?
                        OFFSET ?
                        """)
                .set(id())
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<UUID> ids = new ArrayList<>();
                    long total = 0;
                    while (rset.next()) {
                        ids.add(rset.getObject("project", UUID.class));
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                    }
                    return new PaginatedList<>(total, ids);
                });
    }

    public PaginatedList<UUID> warehouseIds(Pagination pagination) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession
                .sql("""
                        SELECT
                            cw.warehouse,
                            COUNT(*) OVER() AS total_count
                        FROM company_warehouses cw
                        WHERE cw.company = ?
                        LIMIT ?
                        OFFSET ?
                        """)
                .set(id())
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<UUID> ids = new ArrayList<>();
                    long total = 0;
                    while (rset.next()) {
                        ids.add(rset.getObject("warehouse", UUID.class));
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                    }
                    return new PaginatedList<>(total, ids);
                });
    }

    public PaginatedList<UUID> userIds(Pagination pagination) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession
                .sql("""
                        SELECT
                            cu.user_id,
                            COUNT(*) OVER() AS total_count
                        FROM company_users cu
                        WHERE cu.company_id = ?
                        LIMIT ?
                        OFFSET ?
                        """)
                .set(id())
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<UUID> ids = new ArrayList<>();
                    long total = 0;
                    while (rset.next()) {
                        ids.add(rset.getObject("user_id", UUID.class));
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                    }
                    return new PaginatedList<>(total, ids);
                });
    }

    public PaginatedList<UUID> resourceIds(Pagination pagination) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);

        return jdbcSession
                .sql("""
                        SELECT
                            cr.resource,
                            COUNT(*) OVER() AS total_count
                        FROM company_resources cr
                        WHERE cr.company = ?
                        LIMIT ?
                        OFFSET ?
                        """)
                .set(id())
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<UUID> ids = new ArrayList<>();
                    long total = 0;
                    while (rset.next()) {
                        ids.add(rset.getObject("resource", UUID.class));
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                    }
                    return new PaginatedList<>(total, ids);
                });
    }

    public List<Supplier> supplierIds() {
        throw new NotImplementedException();
    }

}
