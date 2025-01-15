package org.apache.poi.hssf.eventusermodel;

public class HSSFUserException extends Exception {
    private Throwable reason;

    public HSSFUserException() {
    }

    public HSSFUserException(String msg) {
        super(msg);
    }

    public HSSFUserException(Throwable reason2) {
        this.reason = reason2;
    }

    public HSSFUserException(String msg, Throwable reason2) {
        super(msg);
        this.reason = reason2;
    }

    public Throwable getReason() {
        return this.reason;
    }
}
