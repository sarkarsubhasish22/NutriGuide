package org.apache.poi.ss.format;

import java.util.Formatter;

public class CellGeneralFormatter extends CellFormatter {
    public CellGeneralFormatter() {
        super("General");
    }

    public void formatValue(StringBuffer toAppendTo, Object value) {
        String fmt;
        int removeFrom;
        if (value instanceof Number) {
            double val = ((Number) value).doubleValue();
            if (val == 0.0d) {
                toAppendTo.append('0');
                return;
            }
            double exp = Math.log10(Math.abs(val));
            boolean stripZeros = true;
            if (exp > 10.0d || exp < -9.0d) {
                fmt = "%1.5E";
            } else if (((double) ((long) val)) != val) {
                fmt = "%1.9f";
            } else {
                fmt = "%1.0f";
                stripZeros = false;
            }
            new Formatter(toAppendTo).format(LOCALE, fmt, new Object[]{value});
            if (stripZeros) {
                if (fmt.endsWith("E")) {
                    removeFrom = toAppendTo.lastIndexOf("E") - 1;
                } else {
                    removeFrom = toAppendTo.length() - 1;
                }
                while (toAppendTo.charAt(removeFrom) == '0') {
                    toAppendTo.deleteCharAt(removeFrom);
                    removeFrom--;
                }
                if (toAppendTo.charAt(removeFrom) == '.') {
                    int i = removeFrom - 1;
                    toAppendTo.deleteCharAt(removeFrom);
                    return;
                }
                return;
            }
            return;
        }
        toAppendTo.append(value.toString());
    }

    public void simpleValue(StringBuffer toAppendTo, Object value) {
        formatValue(toAppendTo, value);
    }
}
