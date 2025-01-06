package supply.server.data.item.product;

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
public class RpProduct {

    public final DataSource dataSource;

    public Optional<Product> add(Product product) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID productId = jdbcSession
                .sql("""
                        INSERT INTO product (name, description, count, status, expiration_date)
                        VALUES (?, ?, ?, ?::INVENTORY_ITEM_STATUS, ?)
                        """)
                .set(product.name())
                .set(product.description())
                .set(product.count())
                .set(product.status().name())
                .set(product.expirationDate())
                .insert(new SingleOutcome<>(UUID.class));

        return Optional.of(new Product(
                productId,
                product.name(),
                product.description(),
                product.count(),
                product.status(),
                product.expirationDate())
        );
    }

    public Optional<Product> get(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT * FROM product
                        WHERE id = ?
                        """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactProductFromResultSet(rset);
                    }
                    return Optional.empty();
                });
    }

    private static Optional<Product> compactProductFromResultSet(ResultSet rset) throws SQLException {
        return Optional.of(new Product(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                rset.getString("description"),
                rset.getInt("count"),
                ItemStatus.valueOf(rset.getString("status")),
                rset.getDate("expiration_date").toLocalDate()
        ));
    }
}
