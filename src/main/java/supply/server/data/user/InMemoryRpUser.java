package supply.server.data.user;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class InMemoryRpUser {

    private final int MAX_CACHE_SIZE = 1000;

    private final Map<UUID, User> storage;

    private final Map<String, UUID> emailStorage;

    public InMemoryRpUser() {
        this.storage = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<UUID, User> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
        this.emailStorage = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, UUID> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public Optional<User> add(User user) {
        storage.put(user.id(), user);
        emailStorage.put(user.email().getEmail(), user.id());
        return Optional.of(user);
    }

    public Optional<User> get(UUID id) {
        return storage.containsKey(id) ?
                Optional.of(storage.get(id))
                : Optional.empty();
    }

    public Optional<User> get(String email) {
        Optional<UUID> id = emailStorage.containsKey(email) ?
                Optional.of(emailStorage.get(email))
                : Optional.empty();
        return id.flatMap(this::get);
    }

}
