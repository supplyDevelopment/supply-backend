package supply.server.data;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import supply.server.data.company.Company;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.user.User;
import supply.server.data.warehouse.Warehouse;

import java.util.UUID;

@Service
public record InMemoryRepository(
        Redis<Company> inMemoryRpCompany,
        Redis<User> inMemoryRpUser,
        Redis<Warehouse> inMemoryRpWarehouse,
        Redis<Project> inMemoryRpProject,
        Redis<Pair<UUID, Resource>> inMemoryRpResource
) {
    @Autowired
    public InMemoryRepository(RedisTemplate<String, Object> redisTemplate) {
        this(
                new Redis<>(redisTemplate, "company:"),
                new Redis<>(redisTemplate, "user:"),
                new Redis<>(redisTemplate, "warehouse:"),
                new Redis<>(redisTemplate, "project:"),
                new Redis<>(redisTemplate, "resource:")
        );
    }
}
