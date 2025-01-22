package supply.server.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import supply.server.configuration.exception.RedisLockException;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Redis<T> {

    private final RedisTemplate<String, Object> redisTemplate;

    protected final String prefix;
    protected static final String LOCK = "lock:";
    protected static final long TTL_SECONDS = 3600;
    private static final long LOCK_TTL_SECONDS = 5;

    public Redis(RedisTemplate<String, Object> redisTemplate, String prefix) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
    }

    public Optional<T> set(UUID id, T value) {
        try {
            String lockValue = acquireLock(id);
            try {
                redisTemplate.opsForValue().set(getKey(id), value, TTL_SECONDS, TimeUnit.SECONDS);
                return Optional.of(value);
            } finally {
                releaseLock(getLockKey(id), lockValue);
            }
        } catch (RedisLockException e) {
            return Optional.empty();
        }
    }

    public Optional<T> get(UUID id) {
        Object value = redisTemplate.opsForValue().get(getKey(id));
        if (value == null) {
            return Optional.empty();
        }
        try {
            T result = (T) value;
            return Optional.of(result);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    private String acquireLock(UUID id) {
        String lockValue = getLockValue();
        if (Boolean.FALSE.equals(
                redisTemplate
                .opsForValue()
                .setIfAbsent(getLockKey(id), lockValue, LOCK_TTL_SECONDS, TimeUnit.SECONDS)
        )) {
           throw new RedisLockException("Could not acquire lock");
        }
        return lockValue;
    }

    private void releaseLock(String lockKey, String value) {
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        if (value.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }

    private String getKey(UUID id) {
        return prefix + id;
    }

    private String getLockKey(UUID id) {
        return LOCK + prefix + id;
    }

    private String getLockValue() {
        return UUID.randomUUID().toString();
    }
}
