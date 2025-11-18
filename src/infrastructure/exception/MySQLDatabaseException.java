package exception;

public class MySQLDatabaseException extends RuntimeException {
    public MySQLDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MySQLDatabaseException(String message) {
        super(message);
    }
}