package org.apache.poi.ss.format;

import java.util.Locale;
import java.util.logging.Logger;

public abstract class CellFormatter {
    public static final Locale LOCALE = Locale.US;
    static final Logger logger = Logger.getLogger(CellFormatter.class.getName());
    protected final String format;

    public abstract void formatValue(StringBuffer stringBuffer, Object obj);

    public abstract void simpleValue(StringBuffer stringBuffer, Object obj);

    public CellFormatter(String format2) {
        this.format = format2;
    }

    public String format(Object value) {
        StringBuffer sb = new StringBuffer();
        formatValue(sb, value);
        return sb.toString();
    }

    public String simpleFormat(Object value) {
        StringBuffer sb = new StringBuffer();
        simpleValue(sb, value);
        return sb.toString();
    }

    static String quote(String str) {
        return '\"' + str + '\"';
    }
}
