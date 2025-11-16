package exception;

public class CRUDRepositoryException extends Exception {
    public CRUDRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
