package org.apache.poi.ss.format;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import org.apache.poi.ss.usermodel.Cell;

public class CellFormat {
    private static final CellFormatPart DEFAULT_TEXT_FORMAT = new CellFormatPart("@");
    public static final CellFormat GENERAL_FORMAT = new CellFormat("General") {
        public CellFormatResult apply(Object value) {
            String text;
            if (value == null) {
                text = "";
            } else if (value instanceof Number) {
                text = CellNumberFormatter.SIMPLE_NUMBER.format(value);
            } else {
                text = value.toString();
            }
            return new CellFormatResult(true, text, (Color) null);
        }
    };
    private static final Pattern ONE_PART = Pattern.compile(CellFormatPart.FORMAT_PAT.pattern() + "(;|$)", 6);
    private static final Map<String, CellFormat> formatCache = new WeakHashMap();
    private final String format;
    private final CellFormatPart negNumFmt;
    private final CellFormatPart posNumFmt;
    private final CellFormatPart textFmt;
    private final CellFormatPart zeroNumFmt;

    public static CellFormat getInstance(String format2) {
        Map<String, CellFormat> map = formatCache;
        CellFormat fmt = map.get(format2);
        if (fmt == null) {
            if (format2.equals("General")) {
                fmt = GENERAL_FORMAT;
            } else {
                fmt = new CellFormat(format2);
            }
            map.put(format2, fmt);
        }
        return fmt;
    }

    private CellFormat(String format2) {
        this.format = format2;
        Matcher m = ONE_PART.matcher(format2);
        List<CellFormatPart> parts = new ArrayList<>();
        while (m.find()) {
            try {
                String valueDesc = m.group();
                parts.add(new CellFormatPart(valueDesc.endsWith(";") ? valueDesc.substring(0, valueDesc.length() - 1) : valueDesc));
            } catch (RuntimeException e) {
                Logger logger = CellFormatter.logger;
                Level level = Level.WARNING;
                logger.log(level, "Invalid format: " + CellFormatter.quote(m.group()), e);
                parts.add((Object) null);
            }
        }
        int size = parts.size();
        if (size == 1) {
            CellFormatPart cellFormatPart = parts.get(0);
            this.negNumFmt = cellFormatPart;
            this.zeroNumFmt = cellFormatPart;
            this.posNumFmt = cellFormatPart;
            this.textFmt = DEFAULT_TEXT_FORMAT;
        } else if (size == 2) {
            CellFormatPart cellFormatPart2 = parts.get(0);
            this.zeroNumFmt = cellFormatPart2;
            this.posNumFmt = cellFormatPart2;
            this.negNumFmt = parts.get(1);
            this.textFmt = DEFAULT_TEXT_FORMAT;
        } else if (size != 3) {
            this.posNumFmt = parts.get(0);
            this.zeroNumFmt = parts.get(1);
            this.negNumFmt = parts.get(2);
            this.textFmt = parts.get(3);
        } else {
            this.posNumFmt = parts.get(0);
            this.zeroNumFmt = parts.get(1);
            this.negNumFmt = parts.get(2);
            this.textFmt = DEFAULT_TEXT_FORMAT;
        }
    }

    public CellFormatResult apply(Object value) {
        if (!(value instanceof Number)) {
            return this.textFmt.apply(value);
        }
        double val = ((Number) value).doubleValue();
        if (val > 0.0d) {
            return this.posNumFmt.apply(value);
        }
        if (val < 0.0d) {
            return this.negNumFmt.apply(Double.valueOf(-val));
        }
        return this.zeroNumFmt.apply(value);
    }

    public CellFormatResult apply(Cell c) {
        int ultimateType = ultimateType(c);
        if (ultimateType == 0) {
            return apply((Object) Double.valueOf(c.getNumericCellValue()));
        }
        if (ultimateType == 1) {
            return apply((Object) c.getStringCellValue());
        }
        if (ultimateType != 3) {
            return ultimateType != 4 ? apply((Object) "?") : apply((Object) c.getStringCellValue());
        }
        return apply((Object) "");
    }

    public CellFormatResult apply(JLabel label, Object value) {
        CellFormatResult result = apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }

    public CellFormatResult apply(JLabel label, Cell c) {
        int ultimateType = ultimateType(c);
        if (ultimateType == 0) {
            return apply(label, (Object) Double.valueOf(c.getNumericCellValue()));
        }
        if (ultimateType == 1) {
            return apply(label, (Object) c.getStringCellValue());
        }
        if (ultimateType != 3) {
            return ultimateType != 4 ? apply(label, (Object) "?") : apply(label, (Object) c.getStringCellValue());
        }
        return apply(label, (Object) "");
    }

    public static int ultimateType(Cell cell) {
        int type = cell.getCellType();
        if (type == 2) {
            return cell.getCachedFormulaResultType();
        }
        return type;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CellFormat) {
            return this.format.equals(((CellFormat) obj).format);
        }
        return false;
    }

    public int hashCode() {
        return this.format.hashCode();
    }
}
