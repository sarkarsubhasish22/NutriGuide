package org.apache.poi.hpsf;

import java.io.PrintStream;
import java.io.PrintWriter;

public class HPSFRuntimeException extends RuntimeException {
    private Throwable reason;

    public HPSFRuntimeException() {
    }

    public HPSFRuntimeException(String msg) {
        super(msg);
    }

    public HPSFRuntimeException(Throwable reason2) {
        this.reason = reason2;
    }

    public HPSFRuntimeException(String msg, Throwable reason2) {
        super(msg);
        this.reason = reason2;
    }

    public Throwable getReason() {
        return this.reason;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream p) {
        Throwable reason2 = getReason();
        super.printStackTrace(p);
        if (reason2 != null) {
            p.println("Caused by:");
            reason2.printStackTrace(p);
        }
    }

    public void printStackTrace(PrintWriter p) {
        Throwable reason2 = getReason();
        super.printStackTrace(p);
        if (reason2 != null) {
            p.println("Caused by:");
            reason2.printStackTrace(p);
        }
    }
}
