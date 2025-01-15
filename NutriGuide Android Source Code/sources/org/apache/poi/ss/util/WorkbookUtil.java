package org.apache.poi.ss.util;

public class WorkbookUtil {
    public static final String createSafeSheetName(String nameProposal) {
        if (nameProposal == null) {
            return "null";
        }
        if (nameProposal.length() < 1) {
            return "empty";
        }
        int length = Math.min(31, nameProposal.length());
        StringBuilder result = new StringBuilder(nameProposal.substring(0, length));
        for (int i = 0; i < length; i++) {
            char ch = result.charAt(i);
            if (ch != '\'') {
                if (!(ch == '*' || ch == '/' || ch == '?')) {
                    switch (ch) {
                        case '[':
                        case '\\':
                        case ']':
                            break;
                    }
                }
                result.setCharAt(i, ' ');
            } else if (i == 0 || i == length - 1) {
                result.setCharAt(i, ' ');
            }
        }
        return result.toString();
    }

    public static void validateSheetName(String sheetName) {
        if (sheetName != null) {
            int len = sheetName.length();
            if (len >= 1) {
                int i = 0;
                while (i < len) {
                    char ch = sheetName.charAt(i);
                    if (!(ch == '*' || ch == '/' || ch == ':' || ch == '?')) {
                        switch (ch) {
                            case '[':
                            case '\\':
                            case ']':
                                break;
                            default:
                                i++;
                        }
                    }
                    throw new IllegalArgumentException("Invalid char (" + ch + ") found at index (" + i + ") in sheet name '" + sheetName + "'");
                }
                if (sheetName.charAt(0) == '\'' || sheetName.charAt(len - 1) == '\'') {
                    throw new IllegalArgumentException("Invalid sheet name '" + sheetName + "'. Sheet names must not begin or end with (').");
                }
                return;
            }
            throw new IllegalArgumentException("sheetName must not be empty string");
        }
        throw new IllegalArgumentException("sheetName must not be null");
    }

    public static void validateSheetState(int state) {
        if (state != 0 && state != 1 && state != 2) {
            throw new IllegalArgumentException("Ivalid sheet state : " + state + "\n" + "Sheet state must beone of the Workbook.SHEET_STATE_* constants");
        }
    }
}
