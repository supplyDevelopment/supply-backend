package supply.server.data;

import supply.server.data.company.RpCompany;
import supply.server.data.project.RpProject;
import supply.server.data.resource.RpResource;
import supply.server.data.user.RpUser;
import supply.server.data.warehouse.RpWarehouse;

import javax.sql.DataSource;

public record PgRepository(
        RpCompany rpCompany,
        RpUser rpUser,
        RpWarehouse rpWarehouse,
        RpProject rpProject,
        RpResource rpResource
) {

    private static PgRepository INSTANCE;

    public static PgRepository instance(DataSource dataSource) {
        if (INSTANCE == null) {
            INSTANCE = new PgRepository(dataSource);
        }
        return INSTANCE;
    }

    private PgRepository(DataSource dataSource) {
        this(
                new RpCompany(dataSource),
                new RpUser(dataSource),
                new RpWarehouse(dataSource),
                new RpProject(dataSource),
                new RpResource(dataSource)
        );
    }
}
