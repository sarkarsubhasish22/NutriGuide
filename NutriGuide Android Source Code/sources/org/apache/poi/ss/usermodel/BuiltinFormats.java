package org.apache.poi.ss.usermodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BuiltinFormats {
    public static final int FIRST_USER_DEFINED_FORMAT_INDEX = 164;
    private static final String[] _formats;

    static {
        List<String> m = new ArrayList<>();
        putFormat(m, 0, "General");
        putFormat(m, 1, "0");
        putFormat(m, 2, "0.00");
        putFormat(m, 3, "#,##0");
        putFormat(m, 4, "#,##0.00");
        putFormat(m, 5, "$#,##0_);($#,##0)");
        putFormat(m, 6, "$#,##0_);[Red]($#,##0)");
        putFormat(m, 7, "$#,##0.00_);($#,##0.00)");
        putFormat(m, 8, "$#,##0.00_);[Red]($#,##0.00)");
        putFormat(m, 9, "0%");
        putFormat(m, 10, "0.00%");
        putFormat(m, 11, "0.00E+00");
        putFormat(m, 12, "# ?/?");
        putFormat(m, 13, "# ??/??");
        putFormat(m, 14, "m/d/yy");
        putFormat(m, 15, "d-mmm-yy");
        putFormat(m, 16, "d-mmm");
        putFormat(m, 17, "mmm-yy");
        putFormat(m, 18, "h:mm AM/PM");
        putFormat(m, 19, "h:mm:ss AM/PM");
        putFormat(m, 20, "h:mm");
        putFormat(m, 21, "h:mm:ss");
        putFormat(m, 22, "m/d/yy h:mm");
        for (int i = 23; i <= 36; i++) {
            putFormat(m, i, "reserved-0x" + Integer.toHexString(i));
        }
        putFormat(m, 37, "#,##0_);(#,##0)");
        putFormat(m, 38, "#,##0_);[Red](#,##0)");
        putFormat(m, 39, "#,##0.00_);(#,##0.00)");
        putFormat(m, 40, "#,##0.00_);[Red](#,##0.00)");
        putFormat(m, 41, "_(*#,##0_);_(*(#,##0);_(* \"-\"_);_(@_)");
        putFormat(m, 42, "_($*#,##0_);_($*(#,##0);_($* \"-\"_);_(@_)");
        putFormat(m, 43, "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)");
        putFormat(m, 44, "_($*#,##0.00_);_($*(#,##0.00);_($*\"-\"??_);_(@_)");
        putFormat(m, 45, "mm:ss");
        putFormat(m, 46, "[h]:mm:ss");
        putFormat(m, 47, "mm:ss.0");
        putFormat(m, 48, "##0.0E+0");
        putFormat(m, 49, "@");
        String[] ss = new String[m.size()];
        m.toArray(ss);
        _formats = ss;
    }

    private static void putFormat(List<String> m, int index, String value) {
        if (m.size() == index) {
            m.add(value);
            return;
        }
        throw new IllegalStateException("index " + index + " is wrong");
    }

    public static Map<Integer, String> getBuiltinFormats() {
        Map<Integer, String> result = new LinkedHashMap<>();
        int i = 0;
        while (true) {
            String[] strArr = _formats;
            if (i >= strArr.length) {
                return result;
            }
            result.put(Integer.valueOf(i), strArr[i]);
            i++;
        }
    }

    public static String[] getAll() {
        return (String[]) _formats.clone();
    }

    public static String getBuiltinFormat(int index) {
        if (index < 0) {
            return null;
        }
        String[] strArr = _formats;
        if (index >= strArr.length) {
            return null;
        }
        return strArr[index];
    }

    public static int getBuiltinFormat(String pFmt) {
        String fmt;
        if (pFmt.equalsIgnoreCase("TEXT")) {
            fmt = "@";
        } else {
            fmt = pFmt;
        }
        int i = 0;
        while (true) {
            String[] strArr = _formats;
            if (i >= strArr.length) {
                return -1;
            }
            if (fmt.equals(strArr[i])) {
                return i;
            }
            i++;
        }
    }
}
