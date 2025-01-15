package org.apache.poi.ss.format;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.format.CellFormatPart;

public class CellElapsedFormatter extends CellFormatter {
    private static final double HOUR__FACTOR = 0.041666666666666664d;
    private static final double MIN__FACTOR = 6.944444444444444E-4d;
    /* access modifiers changed from: private */
    public static final Pattern PERCENTS = Pattern.compile("%");
    private static final double SEC__FACTOR = 1.1574074074074073E-5d;
    private final String printfFmt;
    private final List<TimeSpec> specs;
    /* access modifiers changed from: private */
    public TimeSpec topmost;

    private static class TimeSpec {
        final double factor;
        final int len;
        double modBy = 0.0d;
        final int pos;
        final char type;

        public TimeSpec(char type2, int pos2, int len2, double factor2) {
            this.type = type2;
            this.pos = pos2;
            this.len = len2;
            this.factor = factor2;
        }

        public long valueFor(double elapsed) {
            double val;
            double d = this.modBy;
            if (d == 0.0d) {
                val = elapsed / this.factor;
            } else {
                val = (elapsed / this.factor) % d;
            }
            if (this.type == '0') {
                return Math.round(val);
            }
            return (long) val;
        }
    }

    private class ElapsedPartHandler implements CellFormatPart.PartHandler {
        private ElapsedPartHandler() {
        }

        public String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc) {
            int pos = desc.length();
            char firstCh = part.charAt(0);
            if (firstCh == 10) {
                return "%n";
            }
            if (firstCh == '\"') {
                part = part.substring(1, part.length() - 1);
            } else if (firstCh != '*') {
                if (firstCh != '0') {
                    if (firstCh == '_') {
                        return null;
                    }
                    if (!(firstCh == 'h' || firstCh == 'm' || firstCh == 's')) {
                        if (firstCh != '[') {
                            if (firstCh == '\\') {
                                part = part.substring(1);
                            }
                        } else if (part.length() >= 3) {
                            if (CellElapsedFormatter.this.topmost == null) {
                                String part2 = part.toLowerCase();
                                int specLen = part2.length() - 2;
                                CellElapsedFormatter cellElapsedFormatter = CellElapsedFormatter.this;
                                TimeSpec unused = cellElapsedFormatter.topmost = cellElapsedFormatter.assignSpec(part2.charAt(1), pos, specLen);
                                return part2.substring(1, specLen + 1);
                            }
                            throw new IllegalArgumentException("Duplicate '[' times in format");
                        }
                    }
                }
                String part3 = part.toLowerCase();
                TimeSpec unused2 = CellElapsedFormatter.this.assignSpec(part3.charAt(0), pos, part3.length());
                return part3;
            } else if (part.length() > 1) {
                part = CellFormatPart.expandChar(part);
            }
            return CellElapsedFormatter.PERCENTS.matcher(part).replaceAll("%%");
        }
    }

    public CellElapsedFormatter(String pattern) {
        super(pattern);
        ArrayList arrayList = new ArrayList();
        this.specs = arrayList;
        StringBuffer desc = CellFormatPart.parseFormat(pattern, CellFormatType.ELAPSED, new ElapsedPartHandler());
        ListIterator<TimeSpec> it = arrayList.listIterator(arrayList.size());
        while (it.hasPrevious()) {
            TimeSpec spec = it.previous();
            int i = spec.pos;
            int i2 = spec.pos + spec.len;
            desc.replace(i, i2, "%0" + spec.len + "d");
            if (spec.type != this.topmost.type) {
                spec.modBy = modFor(spec.type, spec.len);
            }
        }
        this.printfFmt = desc.toString();
    }

    /* access modifiers changed from: private */
    public TimeSpec assignSpec(char type, int pos, int len) {
        TimeSpec spec = new TimeSpec(type, pos, len, factorFor(type, len));
        this.specs.add(spec);
        return spec;
    }

    private static double factorFor(char type, int len) {
        if (type == '0') {
            return SEC__FACTOR / Math.pow(10.0d, (double) len);
        }
        if (type == 'h') {
            return HOUR__FACTOR;
        }
        if (type == 'm') {
            return MIN__FACTOR;
        }
        if (type == 's') {
            return SEC__FACTOR;
        }
        throw new IllegalArgumentException("Uknown elapsed time spec: " + type);
    }

    private static double modFor(char type, int len) {
        if (type == '0') {
            return Math.pow(10.0d, (double) len);
        }
        if (type == 'h') {
            return 24.0d;
        }
        if (type == 'm' || type == 's') {
            return 60.0d;
        }
        throw new IllegalArgumentException("Uknown elapsed time spec: " + type);
    }

    public void formatValue(StringBuffer toAppendTo, Object value) {
        double elapsed = ((Number) value).doubleValue();
        if (elapsed < 0.0d) {
            toAppendTo.append('-');
            elapsed = -elapsed;
        }
        Object[] parts = new Long[this.specs.size()];
        for (int i = 0; i < this.specs.size(); i++) {
            parts[i] = Long.valueOf(this.specs.get(i).valueFor(elapsed));
        }
        new Formatter(toAppendTo).format(this.printfFmt, parts);
    }

    public void simpleValue(StringBuffer toAppendTo, Object value) {
        formatValue(toAppendTo, value);
    }
}
