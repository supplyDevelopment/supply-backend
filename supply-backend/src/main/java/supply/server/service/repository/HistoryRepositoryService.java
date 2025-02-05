package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.resource.history.CreateHistory;
import supply.server.data.resource.history.ResourceHistory;
import supply.server.data.resource.history.RpResourceHistory;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class HistoryRepositoryService {

    private final RpResourceHistory rpResourceHistory;

    public ResourceHistory add(CreateHistory createHistory, UUID companyId) {
        ResourceHistory resourceHistory;
        try {
            Optional<ResourceHistory> resourceHistoryOpt = rpResourceHistory.add(createHistory, companyId);
            if (resourceHistoryOpt.isPresent()) {
                resourceHistory = resourceHistoryOpt.get();
            } else {
                throw new DbException("Failed to add resource history");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resourceHistory;
    }

    public PaginatedList<ResourceHistory> get(UUID resourceId, UUID companyId, Pagination pagination) {
        PaginatedList<ResourceHistory> resourceHistories;
        try {
            resourceHistories = rpResourceHistory.get(resourceId, companyId, pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resourceHistories;
    }

    public PaginatedList<ResourceHistory> get(UUID companyId, Pagination pagination) {
        PaginatedList<ResourceHistory> resourceHistories;
        try {
            resourceHistories = rpResourceHistory.get(companyId, pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return resourceHistories;
    }

}
