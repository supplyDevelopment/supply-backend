package supply.server.service.repository;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ProjectRepositoryService {

    private final RpProject rpProject;

    public Project add(String name, String description, UUID companyId) {
        Project project;
        try {
            Optional<Project> projectOpt = rpProject.add(name, description, companyId);

            if (projectOpt.isPresent()) {
                project = projectOpt.get();
            } else {
                throw new DbException("Failed to add project");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return project;
    }

    public Project get(UUID projectId, UUID companyId) {
        Project project;
        try {
            Optional<Project> projectOpt = rpProject.get(projectId, companyId);

            if (projectOpt.isPresent()) {
                project = projectOpt.get();
            } else {
                throw new DataNotFound("Project with id " + projectId + " not found");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return project;
    }

    public PaginatedList<Project> getAll(String prefix, UUID companyId, Pagination pagination) {
        PaginatedList<Project> projects;
        try {
            projects = rpProject.getAll(prefix, companyId, pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return projects;
    }

}
