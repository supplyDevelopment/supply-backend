package supply.server.data;

import org.springframework.stereotype.Service;
import supply.server.data.company.RpCompany;
import supply.server.data.project.RpProject;
import supply.server.data.resource.RpResource;
import supply.server.data.user.RpUser;
import supply.server.data.warehouse.RpWarehouse;

import javax.sql.DataSource;

@Service
public record PgRepository(
        RpCompany rpCompany,
        RpUser rpUser,
        RpWarehouse rpWarehouse,
        RpProject rpProject,
        RpResource rpResource
) {
}
