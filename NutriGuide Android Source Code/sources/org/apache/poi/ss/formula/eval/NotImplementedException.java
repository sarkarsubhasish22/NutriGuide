package org.apache.poi.ss.formula.eval;

public final class NotImplementedException extends RuntimeException {
    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, NotImplementedException cause) {
        super(message, cause);
    }
}
