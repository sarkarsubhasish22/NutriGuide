package org.apache.poi.hssf.record.formula;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.SpreadsheetVersion;

public final class SheetNameFormatter {
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("([A-Za-z]+)([0-9]+)");
    private static final char DELIMITER = '\'';

    private SheetNameFormatter() {
    }

    public static String format(String rawSheetName) {
        StringBuffer sb = new StringBuffer(rawSheetName.length() + 2);
        appendFormat(sb, rawSheetName);
        return sb.toString();
    }

    public static void appendFormat(StringBuffer out, String rawSheetName) {
        if (needsDelimiting(rawSheetName)) {
            out.append(DELIMITER);
            appendAndEscape(out, rawSheetName);
            out.append(DELIMITER);
            return;
        }
        out.append(rawSheetName);
    }

    public static void appendFormat(StringBuffer out, String workbookName, String rawSheetName) {
        if (needsDelimiting(workbookName) || needsDelimiting(rawSheetName)) {
            out.append(DELIMITER);
            out.append('[');
            appendAndEscape(out, workbookName.replace('[', '(').replace(']', ')'));
            out.append(']');
            appendAndEscape(out, rawSheetName);
            out.append(DELIMITER);
            return;
        }
        out.append('[');
        out.append(workbookName);
        out.append(']');
        out.append(rawSheetName);
    }

    private static void appendAndEscape(StringBuffer sb, String rawSheetName) {
        int len = rawSheetName.length();
        for (int i = 0; i < len; i++) {
            char ch = rawSheetName.charAt(i);
            if (ch == '\'') {
                sb.append(DELIMITER);
            }
            sb.append(ch);
        }
    }

    private static boolean needsDelimiting(String rawSheetName) {
        int len = rawSheetName.length();
        if (len < 1) {
            throw new RuntimeException("Zero length string is an invalid sheet name");
        } else if (Character.isDigit(rawSheetName.charAt(0))) {
            return true;
        } else {
            for (int i = 0; i < len; i++) {
                if (isSpecialChar(rawSheetName.charAt(i))) {
                    return true;
                }
            }
            if ((!Character.isLetter(rawSheetName.charAt(0)) || !Character.isDigit(rawSheetName.charAt(len - 1)) || !nameLooksLikePlainCellReference(rawSheetName)) && !nameLooksLikeBooleanLiteral(rawSheetName)) {
                return false;
            }
            return true;
        }
    }

    private static boolean nameLooksLikeBooleanLiteral(String rawSheetName) {
        char charAt = rawSheetName.charAt(0);
        if (charAt != 'F') {
            if (charAt != 'T') {
                if (charAt != 'f') {
                    if (charAt != 't') {
                        return false;
                    }
                }
            }
            return "TRUE".equalsIgnoreCase(rawSheetName);
        }
        return "FALSE".equalsIgnoreCase(rawSheetName);
    }

    static boolean isSpecialChar(char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return false;
        }
        if (ch == 9 || ch == 10 || ch == 13) {
            throw new RuntimeException("Illegal character (0x" + Integer.toHexString(ch) + ") found in sheet name");
        } else if (ch == '.' || ch == '_') {
            return false;
        } else {
            return true;
        }
    }

    static boolean cellReferenceIsWithinRange(String lettersPrefix, String numbersSuffix) {
        return CellReference.cellReferenceIsWithinRange(lettersPrefix, numbersSuffix, SpreadsheetVersion.EXCEL97);
    }

    static boolean nameLooksLikePlainCellReference(String rawSheetName) {
        Matcher matcher = CELL_REF_PATTERN.matcher(rawSheetName);
        if (!matcher.matches()) {
            return false;
        }
        return cellReferenceIsWithinRange(matcher.group(1), matcher.group(2));
    }
}
