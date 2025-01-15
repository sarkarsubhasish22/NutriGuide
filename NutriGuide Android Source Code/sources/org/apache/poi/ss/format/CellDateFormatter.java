package org.apache.poi.ss.format;

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import org.apache.poi.ss.format.CellFormatPart;

public class CellDateFormatter extends CellFormatter {
    private static final Date EXCEL_EPOCH_DATE;
    private static final long EXCEL_EPOCH_TIME;
    private static final CellFormatter SIMPLE_DATE = new CellDateFormatter("mm/d/y");
    /* access modifiers changed from: private */
    public boolean amPmUpper;
    private final DateFormat dateFmt;
    /* access modifiers changed from: private */
    public String sFmt;
    /* access modifiers changed from: private */
    public boolean showAmPm;
    /* access modifiers changed from: private */
    public boolean showM;

    static {
        Calendar c = Calendar.getInstance();
        c.set(1904, 0, 1, 0, 0, 0);
        EXCEL_EPOCH_DATE = c.getTime();
        EXCEL_EPOCH_TIME = c.getTimeInMillis();
    }

    private class DatePartHandler implements CellFormatPart.PartHandler {
        private int hLen;
        private int hStart;
        private int mLen;
        private int mStart;

        private DatePartHandler() {
            this.mStart = -1;
            this.hStart = -1;
        }

        public String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc) {
            int pos = desc.length();
            boolean z = false;
            switch (part.charAt(0)) {
                case '0':
                    this.mStart = -1;
                    int sLen = part.length();
                    CellDateFormatter cellDateFormatter = CellDateFormatter.this;
                    String unused = cellDateFormatter.sFmt = "%0" + (sLen + 2) + "." + sLen + "f";
                    return part.replace('0', 'S');
                case 'A':
                case 'P':
                case 'a':
                case 'p':
                    if (part.length() <= 1) {
                        return null;
                    }
                    this.mStart = -1;
                    boolean unused2 = CellDateFormatter.this.showAmPm = true;
                    boolean unused3 = CellDateFormatter.this.showM = Character.toLowerCase(part.charAt(1)) == 'm';
                    CellDateFormatter cellDateFormatter2 = CellDateFormatter.this;
                    if (cellDateFormatter2.showM || Character.isUpperCase(part.charAt(0))) {
                        z = true;
                    }
                    boolean unused4 = cellDateFormatter2.amPmUpper = z;
                    return "a";
                case 'D':
                case 'd':
                    this.mStart = -1;
                    if (part.length() <= 2) {
                        return part.toLowerCase();
                    }
                    return part.toLowerCase().replace('d', 'E');
                case 'H':
                case 'h':
                    this.mStart = -1;
                    this.hStart = pos;
                    this.hLen = part.length();
                    return part.toLowerCase();
                case 'M':
                case 'm':
                    this.mStart = pos;
                    this.mLen = part.length();
                    return part.toUpperCase();
                case 'S':
                case 's':
                    if (this.mStart >= 0) {
                        for (int i = 0; i < this.mLen; i++) {
                            desc.setCharAt(this.mStart + i, 'm');
                        }
                        this.mStart = -1;
                    }
                    return part.toLowerCase();
                case 'Y':
                case 'y':
                    this.mStart = -1;
                    if (part.length() == 3) {
                        part = "yyyy";
                    }
                    return part.toLowerCase();
                default:
                    return null;
            }
        }

        public void finish(StringBuffer toAppendTo) {
            if (this.hStart >= 0 && !CellDateFormatter.this.showAmPm) {
                for (int i = 0; i < this.hLen; i++) {
                    toAppendTo.setCharAt(this.hStart + i, 'H');
                }
            }
        }
    }

    public CellDateFormatter(String format) {
        super(format);
        DatePartHandler partHandler = new DatePartHandler();
        StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.DATE, partHandler);
        partHandler.finish(descBuf);
        this.dateFmt = new SimpleDateFormat(descBuf.toString());
    }

    public void formatValue(StringBuffer toAppendTo, Object value) {
        Object value2;
        Object value3;
        boolean doneMillis;
        StringBuffer stringBuffer = toAppendTo;
        if (value == null) {
            value2 = Double.valueOf(0.0d);
        } else {
            value2 = value;
        }
        if (value2 instanceof Number) {
            double v = ((Number) value2).doubleValue();
            if (v == 0.0d) {
                value2 = EXCEL_EPOCH_DATE;
            } else {
                double d = (double) EXCEL_EPOCH_TIME;
                Double.isNaN(d);
                value2 = new Date((long) (d + v));
            }
        }
        AttributedCharacterIterator it = this.dateFmt.formatToCharacterIterator(value2);
        boolean doneAm = false;
        boolean doneMillis2 = false;
        it.first();
        char ch = it.first();
        while (ch != 65535) {
            if (it.getAttribute(DateFormat.Field.MILLISECOND) == null) {
                value3 = value2;
                doneMillis = doneMillis2;
                if (it.getAttribute(DateFormat.Field.AM_PM) == null) {
                    stringBuffer.append(ch);
                } else if (!doneAm) {
                    if (this.showAmPm) {
                        if (this.amPmUpper) {
                            stringBuffer.append(Character.toUpperCase(ch));
                            if (this.showM) {
                                stringBuffer.append('M');
                            }
                        } else {
                            stringBuffer.append(Character.toLowerCase(ch));
                            if (this.showM) {
                                stringBuffer.append('m');
                            }
                        }
                    }
                    doneAm = true;
                    doneMillis2 = doneMillis;
                    ch = it.next();
                    value2 = value3;
                }
            } else if (!doneMillis2) {
                int pos = toAppendTo.length();
                Formatter formatter = new Formatter(stringBuffer);
                Locale locale = LOCALE;
                String str = this.sFmt;
                value3 = value2;
                boolean z = doneMillis2;
                double time = (double) (((Date) value2).getTime() % 1000);
                Double.isNaN(time);
                formatter.format(locale, str, new Object[]{Double.valueOf(time / 1000.0d)});
                stringBuffer.delete(pos, pos + 2);
                doneMillis2 = true;
                ch = it.next();
                value2 = value3;
            } else {
                value3 = value2;
                doneMillis = doneMillis2;
            }
            doneMillis2 = doneMillis;
            ch = it.next();
            value2 = value3;
        }
    }

    public void simpleValue(StringBuffer toAppendTo, Object value) {
        SIMPLE_DATE.formatValue(toAppendTo, value);
    }
}
