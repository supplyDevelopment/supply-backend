package supply.server.controller.entity.response;

import supply.server.data.project.Project;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public ProjectResponse(Project project) {
        this(
                project.id(),
                project.name(),
                project.description(),
                project.createdAt(),
                project.updatedAt()
        );
    }
}
