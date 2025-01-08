package supply.server.service;

import lombok.AllArgsConstructor;
import supply.server.data.InMemoryRepository;
import supply.server.data.PgRepository;
import supply.server.service.repository.CompanyRepositoryService;
import supply.server.service.repository.UserRepositoryService;

public class RepositoryService {

    private final PgRepository pgRepository;
    private final InMemoryRepository inMemoryRepository;

    private final CompanyRepositoryService companyRepositoryService;
    private final UserRepositoryService userRepositoryService;

    public RepositoryService(PgRepository pgRepository, InMemoryRepository inMemoryRepository) {
        this.pgRepository = pgRepository;
        this.inMemoryRepository = inMemoryRepository;

        companyRepositoryService = new CompanyRepositoryService(
                pgRepository.rpCompany(),
                inMemoryRepository.inMemoryRpCompany()
        );
        userRepositoryService = new UserRepositoryService(
                pgRepository.rpUser(),
                inMemoryRepository.inMemoryRpUser()
        );
    }


}
