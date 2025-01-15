package org.apache.poi.ss.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.record.formula.SheetNameFormatter;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;

public class CellReference {
    private static final char ABSOLUTE_REFERENCE_MARKER = '$';
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("\\$?([A-Za-z]+)\\$?([0-9]+)");
    private static final Pattern COLUMN_REF_PATTERN = Pattern.compile("\\$?([A-Za-z]+)");
    private static final Pattern NAMED_RANGE_NAME_PATTERN = Pattern.compile("[_A-Za-z][_.A-Za-z0-9]*");
    private static final Pattern ROW_REF_PATTERN = Pattern.compile("\\$?([0-9]+)");
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char SPECIAL_NAME_DELIMITER = '\'';
    private final int _colIndex;
    private final boolean _isColAbs;
    private final boolean _isRowAbs;
    private final int _rowIndex;
    private final String _sheetName;

    public enum NameType {
        CELL,
        NAMED_RANGE,
        COLUMN,
        ROW,
        BAD_CELL_OR_NAMED_RANGE
    }

    public CellReference(String cellRef) {
        String[] parts = separateRefParts(cellRef);
        boolean z = false;
        this._sheetName = parts[0];
        String colRef = parts[1];
        if (colRef.length() >= 1) {
            boolean z2 = colRef.charAt(0) == '$';
            this._isColAbs = z2;
            this._colIndex = convertColStringToIndex(z2 ? colRef.substring(1) : colRef);
            String rowRef = parts[2];
            if (rowRef.length() >= 1) {
                z = rowRef.charAt(0) == '$' ? true : z;
                this._isRowAbs = z;
                this._rowIndex = Integer.parseInt(z ? rowRef.substring(1) : rowRef) - 1;
                return;
            }
            throw new IllegalArgumentException("Invalid Formula cell reference: '" + cellRef + "'");
        }
        throw new IllegalArgumentException("Invalid Formula cell reference: '" + cellRef + "'");
    }

    public CellReference(int pRow, int pCol) {
        this(pRow, pCol, false, false);
    }

    public CellReference(int pRow, short pCol) {
        this(pRow, 65535 & pCol, false, false);
    }

    public CellReference(Cell cell) {
        this(cell.getRowIndex(), cell.getColumnIndex(), false, false);
    }

    public CellReference(int pRow, int pCol, boolean pAbsRow, boolean pAbsCol) {
        this((String) null, pRow, pCol, pAbsRow, pAbsCol);
    }

    public CellReference(String pSheetName, int pRow, int pCol, boolean pAbsRow, boolean pAbsCol) {
        if (pRow < -1) {
            throw new IllegalArgumentException("row index may not be negative");
        } else if (pCol >= -1) {
            this._sheetName = pSheetName;
            this._rowIndex = pRow;
            this._colIndex = pCol;
            this._isRowAbs = pAbsRow;
            this._isColAbs = pAbsCol;
        } else {
            throw new IllegalArgumentException("column index may not be negative");
        }
    }

    public int getRow() {
        return this._rowIndex;
    }

    public short getCol() {
        return (short) this._colIndex;
    }

    public boolean isRowAbsolute() {
        return this._isRowAbs;
    }

    public boolean isColAbsolute() {
        return this._isColAbs;
    }

    public String getSheetName() {
        return this._sheetName;
    }

    public static boolean isPartAbsolute(String part) {
        return part.charAt(0) == '$';
    }

    public static int convertColStringToIndex(String ref) {
        int pos = 0;
        int retval = 0;
        int k = ref.length() - 1;
        while (true) {
            if (k < 0) {
                break;
            }
            char thechar = ref.charAt(k);
            if (thechar != '$') {
                retval += (Character.getNumericValue(thechar) - 9) * ((int) Math.pow(26.0d, (double) pos));
                pos++;
                k--;
            } else if (k != 0) {
                throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
            }
        }
        return retval - 1;
    }

    public static NameType classifyCellReference(String str, SpreadsheetVersion ssVersion) {
        int len = str.length();
        if (len >= 1) {
            char firstChar = str.charAt(0);
            if (firstChar != '$' && firstChar != '.' && firstChar != '_' && !Character.isLetter(firstChar) && !Character.isDigit(firstChar)) {
                throw new IllegalArgumentException("Invalid first char (" + firstChar + ") of cell reference or named range.  Letter expected");
            } else if (!Character.isDigit(str.charAt(len - 1))) {
                return validateNamedRangeName(str, ssVersion);
            } else {
                Matcher cellRefPatternMatcher = CELL_REF_PATTERN.matcher(str);
                if (!cellRefPatternMatcher.matches()) {
                    return validateNamedRangeName(str, ssVersion);
                }
                if (cellReferenceIsWithinRange(cellRefPatternMatcher.group(1), cellRefPatternMatcher.group(2), ssVersion)) {
                    return NameType.CELL;
                }
                if (str.indexOf(36) >= 0) {
                    return NameType.BAD_CELL_OR_NAMED_RANGE;
                }
                return NameType.NAMED_RANGE;
            }
        } else {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    private static NameType validateNamedRangeName(String str, SpreadsheetVersion ssVersion) {
        Matcher colMatcher = COLUMN_REF_PATTERN.matcher(str);
        if (colMatcher.matches() && isColumnWithnRange(colMatcher.group(1), ssVersion)) {
            return NameType.COLUMN;
        }
        Matcher rowMatcher = ROW_REF_PATTERN.matcher(str);
        if (rowMatcher.matches() && isRowWithnRange(rowMatcher.group(1), ssVersion)) {
            return NameType.ROW;
        }
        if (!NAMED_RANGE_NAME_PATTERN.matcher(str).matches()) {
            return NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return NameType.NAMED_RANGE;
    }

    public static boolean cellReferenceIsWithinRange(String colStr, String rowStr, SpreadsheetVersion ssVersion) {
        if (!isColumnWithnRange(colStr, ssVersion)) {
            return false;
        }
        return isRowWithnRange(rowStr, ssVersion);
    }

    public static boolean isColumnWithnRange(String colStr, SpreadsheetVersion ssVersion) {
        String lastCol = ssVersion.getLastColumnName();
        int lastColLength = lastCol.length();
        int numberOfLetters = colStr.length();
        if (numberOfLetters > lastColLength) {
            return false;
        }
        if (numberOfLetters != lastColLength || colStr.toUpperCase().compareTo(lastCol) <= 0) {
            return true;
        }
        return false;
    }

    public static boolean isRowWithnRange(String rowStr, SpreadsheetVersion ssVersion) {
        int rowNum = Integer.parseInt(rowStr);
        if (rowNum < 0) {
            throw new IllegalStateException("Invalid rowStr '" + rowStr + "'.");
        } else if (rowNum != 0 && rowNum <= ssVersion.getMaxRows()) {
            return true;
        } else {
            return false;
        }
    }

    private static String[] separateRefParts(String reference) {
        int plingPos = reference.lastIndexOf(33);
        String sheetName = parseSheetName(reference, plingPos);
        int start = plingPos + 1;
        int length = reference.length();
        int loc = start;
        if (reference.charAt(loc) == '$') {
            loc++;
        }
        while (loc < length) {
            char ch = reference.charAt(loc);
            if (Character.isDigit(ch) || ch == '$') {
                break;
            }
            loc++;
        }
        return new String[]{sheetName, reference.substring(start, loc), reference.substring(loc)};
    }

    private static String parseSheetName(String reference, int indexOfSheetNameDelimiter) {
        if (indexOfSheetNameDelimiter < 0) {
            return null;
        }
        if (!(reference.charAt(0) == '\'')) {
            return reference.substring(0, indexOfSheetNameDelimiter);
        }
        int lastQuotePos = indexOfSheetNameDelimiter - 1;
        if (reference.charAt(lastQuotePos) == '\'') {
            StringBuffer sb = new StringBuffer(indexOfSheetNameDelimiter);
            int i = 1;
            while (i < lastQuotePos) {
                char ch = reference.charAt(i);
                if (ch != '\'') {
                    sb.append(ch);
                } else if (i >= lastQuotePos || reference.charAt(i + 1) != '\'') {
                    throw new RuntimeException("Bad sheet name quote escaping: (" + reference + ")");
                } else {
                    i++;
                    sb.append(ch);
                }
                i++;
            }
            return sb.toString();
        }
        throw new RuntimeException("Mismatched quotes: (" + reference + ")");
    }

    public static String convertNumToColString(int col) {
        String colRef = "";
        int colRemain = col + 1;
        while (colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }
            colRemain = (colRemain - thisPart) / 26;
            colRef = ((char) (thisPart + 64)) + colRef;
        }
        return colRef;
    }

    public String formatAsString() {
        StringBuffer sb = new StringBuffer(32);
        String str = this._sheetName;
        if (str != null) {
            SheetNameFormatter.appendFormat(sb, str);
            sb.append(SHEET_NAME_DELIMITER);
        }
        appendCellReference(sb);
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }

    public String[] getCellRefParts() {
        return new String[]{this._sheetName, Integer.toString(this._rowIndex + 1), convertNumToColString(this._colIndex)};
    }

    /* access modifiers changed from: package-private */
    public void appendCellReference(StringBuffer sb) {
        if (this._isColAbs) {
            sb.append(ABSOLUTE_REFERENCE_MARKER);
        }
        sb.append(convertNumToColString(this._colIndex));
        if (this._isRowAbs) {
            sb.append(ABSOLUTE_REFERENCE_MARKER);
        }
        sb.append(this._rowIndex + 1);
    }

    public boolean equals(Object o) {
        boolean z;
        if (!(o instanceof CellReference)) {
            return false;
        }
        CellReference cr = (CellReference) o;
        if (this._rowIndex == cr._rowIndex && this._colIndex == cr._colIndex && this._isRowAbs == (z = cr._isColAbs) && this._isColAbs == z) {
            return true;
        }
        return false;
    }
}
