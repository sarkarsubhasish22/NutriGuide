package org.apache.poi.hpsf;

public class HPSFException extends Exception {
    private Throwable reason;

    public HPSFException() {
    }

    public HPSFException(String msg) {
        super(msg);
    }

    public HPSFException(Throwable reason2) {
        this.reason = reason2;
    }

    public HPSFException(String msg, Throwable reason2) {
        super(msg);
        this.reason = reason2;
    }

    public Throwable getReason() {
        return this.reason;
    }
}
