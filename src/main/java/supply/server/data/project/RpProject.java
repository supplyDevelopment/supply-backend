package supply.server.data.project;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.Pagination;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class RpProject {

    private final DataSource dataSource;

    public Project add(String name, String description, UUID companyId) {
        throw new NotImplementedException();
    }

    public Project get(UUID projectId) {
        throw new NotImplementedException();
    }

    public List<Project> getAll(String prefix, Pagination pagination) {
        throw new NotImplementedException();
    }

}
