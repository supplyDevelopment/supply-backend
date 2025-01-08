package supply.server.data;

import supply.server.data.company.RpCompany;
import supply.server.data.project.RpProject;
import supply.server.data.resource.RpResource;
import supply.server.data.user.RpUser;
import supply.server.data.warehouse.RpWarehouse;

import javax.sql.DataSource;

public record Repository(
        RpCompany rpCompany,
        RpUser rpUser,
        RpWarehouse rpWarehouse,
        RpProject rpProject,
        RpResource rpResource
) {
    public Repository(DataSource dataSource) {
        this(
                new RpCompany(dataSource),
                new RpUser(dataSource),
                new RpWarehouse(dataSource),
                new RpProject(dataSource),
                new RpResource(dataSource)
        );
    }
}
