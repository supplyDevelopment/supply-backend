package supply.server.data;

import org.springframework.stereotype.Service;
import supply.server.data.company.InMemoryRpCompany;
import supply.server.data.user.InMemoryRpUser;

@Service
public record InMemoryRepository(
        InMemoryRpCompany inMemoryRpCompany,
        InMemoryRpUser inMemoryRpUser
) {
}
