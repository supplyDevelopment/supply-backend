package supply.server.configuration.exception;

public class RedisLockException extends RuntimeException {
    public RedisLockException(String message) {
        super(message);
    }
}
