package supply.server.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import supply.server.data.InMemoryRepository;
import supply.server.data.PgRepository;
import supply.server.service.repository.*;

@Service
public class RepositoryService {

    private final PgRepository pgRepository;
    private final InMemoryRepository inMemoryRepository;

    @Getter
    private final CompanyRepositoryService company;
    @Getter
    private final UserRepositoryService user;
    @Getter
    private final ProjectRepositoryService project;
    @Getter
    private final WarehouseRepositoryService warehouse;
    @Getter
    private final ResourceRepositoryService resource;

    public RepositoryService(PgRepository pgRepository, InMemoryRepository inMemoryRepository) {
        this.pgRepository = pgRepository;
        this.inMemoryRepository = inMemoryRepository;

        company = new CompanyRepositoryService(
                pgRepository.rpCompany(),
                inMemoryRepository.inMemoryRpCompany()
        );
        user = new UserRepositoryService(
                pgRepository.rpUser(),
                inMemoryRepository.inMemoryRpUser()
        );
        project = new ProjectRepositoryService(
                pgRepository.rpProject()
        );
        warehouse = new WarehouseRepositoryService(
                pgRepository.rpWarehouse()
        );
        resource = new ResourceRepositoryService(
                pgRepository.rpResource()
        );
    }
}
