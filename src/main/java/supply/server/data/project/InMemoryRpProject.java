package supply.server.data.project;

import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class InMemoryRpProject {

    private final int MAX_CACHE_SIZE = 500;

    private final Map<UUID, Project> storage;

    public InMemoryRpProject() {
        this.storage = new LinkedHashMap<>(MAX_CACHE_SIZE, 1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, Project> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public Optional<Project> add(Project project) throws SQLException {
        storage.put(project.id(), project);
        return Optional.of(project);
    }

    public Optional<Project> get(UUID id, UUID companyId) throws SQLException {
        if (storage.containsKey(id)) {
            Project project = storage.get(id);
            if (project.companyId().equals(companyId)) {
                return Optional.of(project);
            }
        }
        return Optional.empty();
    }

}
