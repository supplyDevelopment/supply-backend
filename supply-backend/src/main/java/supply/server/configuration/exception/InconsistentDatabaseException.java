package supply.server.configuration.exception;

public class InconsistentDatabaseException extends RuntimeException {
    public InconsistentDatabaseException(String message) {
        super(message);
    }
}
