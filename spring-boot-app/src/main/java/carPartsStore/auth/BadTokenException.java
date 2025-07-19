package carPartsStore.auth;

import java.io.Serial;

public class BadTokenException extends  RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadTokenException(String message) {
        super(message);
    }

    public BadTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadTokenException(Throwable cause) {
        super(cause);
    }
}
