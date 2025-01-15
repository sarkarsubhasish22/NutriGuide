package org.apache.poi.util;

import java.io.PrintStream;

public class SystemOutLogger extends POILogger {
    private String _cat;

    public void initialize(String cat) {
        this._cat = cat;
    }

    public void log(int level, Object obj1) {
        log(level, obj1, (Throwable) null);
    }

    public void log(int level, Object obj1, Throwable exception) {
        if (check(level)) {
            PrintStream printStream = System.out;
            printStream.println("[" + this._cat + "] " + obj1);
            if (exception != null) {
                exception.printStackTrace(System.out);
            }
        }
    }

    public boolean check(int level) {
        int currentLevel;
        try {
            currentLevel = Integer.parseInt(System.getProperty("poi.log.level", WARN + ""));
        } catch (SecurityException e) {
            currentLevel = POILogger.DEBUG;
        }
        if (level >= currentLevel) {
            return true;
        }
        return false;
    }
}
