package org.apache.poi.ss.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class RegionUtil {
    private RegionUtil() {
    }

    private static final class CellPropertySetter {
        private final String _propertyName;
        private final Short _propertyValue;
        private final Workbook _workbook;

        public CellPropertySetter(Workbook workbook, String propertyName, int value) {
            this._workbook = workbook;
            this._propertyName = propertyName;
            this._propertyValue = Short.valueOf((short) value);
        }

        public void setProperty(Row row, int column) {
            CellUtil.setCellStyleProperty(CellUtil.getCell(row, column), this._workbook, this._propertyName, this._propertyValue);
        }
    }

    public static void setBorderLeft(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getFirstColumn();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_LEFT, border);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    public static void setLeftBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getFirstColumn();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.LEFT_BORDER_COLOR, color);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    public static void setBorderRight(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getLastColumn();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_RIGHT, border);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    public static void setRightBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int rowStart = region.getFirstRow();
        int rowEnd = region.getLastRow();
        int column = region.getLastColumn();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.RIGHT_BORDER_COLOR, color);
        for (int i = rowStart; i <= rowEnd; i++) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }

    public static void setBorderBottom(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getLastRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_BOTTOM, border);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }

    public static void setBottomBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getLastRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BOTTOM_BORDER_COLOR, color);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }

    public static void setBorderTop(int border, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getFirstRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_TOP, border);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }

    public static void setTopBorderColor(int color, CellRangeAddress region, Sheet sheet, Workbook workbook) {
        int colStart = region.getFirstColumn();
        int colEnd = region.getLastColumn();
        int rowIndex = region.getFirstRow();
        CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.TOP_BORDER_COLOR, color);
        Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; i++) {
            cps.setProperty(row, i);
        }
    }
}
