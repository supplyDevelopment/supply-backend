package supply.server.data;

import supply.server.data.company.InMemoryRpCompany;
import supply.server.data.user.InMemoryRpUser;

public record InMemoryRepository(
        InMemoryRpCompany inMemoryRpCompany,
        InMemoryRpUser inMemoryRpUser
) {

    public static InMemoryRepository INSTANCE;

    public static InMemoryRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InMemoryRepository();
        }
        return INSTANCE;
    }

    private InMemoryRepository() {
        this(new InMemoryRpCompany(), new InMemoryRpUser());
    }
}
