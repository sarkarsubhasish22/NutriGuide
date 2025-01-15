package org.apache.poi.ss.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class CellUtil {
    public static final String ALIGNMENT = "alignment";
    public static final String BORDER_BOTTOM = "borderBottom";
    public static final String BORDER_LEFT = "borderLeft";
    public static final String BORDER_RIGHT = "borderRight";
    public static final String BORDER_TOP = "borderTop";
    public static final String BOTTOM_BORDER_COLOR = "bottomBorderColor";
    public static final String DATA_FORMAT = "dataFormat";
    public static final String FILL_BACKGROUND_COLOR = "fillBackgroundColor";
    public static final String FILL_FOREGROUND_COLOR = "fillForegroundColor";
    public static final String FILL_PATTERN = "fillPattern";
    public static final String FONT = "font";
    public static final String HIDDEN = "hidden";
    public static final String INDENTION = "indention";
    public static final String LEFT_BORDER_COLOR = "leftBorderColor";
    public static final String LOCKED = "locked";
    public static final String RIGHT_BORDER_COLOR = "rightBorderColor";
    public static final String ROTATION = "rotation";
    public static final String TOP_BORDER_COLOR = "topBorderColor";
    public static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    public static final String WRAP_TEXT = "wrapText";
    private static UnicodeMapping[] unicodeMappings = {um("alpha", "α"), um("beta", "β"), um("gamma", "γ"), um("delta", "δ"), um("epsilon", "ε"), um("zeta", "ζ"), um("eta", "η"), um("theta", "θ"), um("iota", "ι"), um("kappa", "κ"), um("lambda", "λ"), um("mu", "μ"), um("nu", "ν"), um("xi", "ξ"), um("omicron", "ο")};

    private static final class UnicodeMapping {
        public final String entityName;
        public final String resolvedValue;

        public UnicodeMapping(String pEntityName, String pResolvedValue) {
            this.entityName = "&" + pEntityName + ";";
            this.resolvedValue = pResolvedValue;
        }
    }

    private CellUtil() {
    }

    public static Row getRow(int rowIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return sheet.createRow(rowIndex);
        }
        return row;
    }

    public static Cell getCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return row.createCell(columnIndex);
        }
        return cell;
    }

    public static Cell createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = getCell(row, column);
        cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static Cell createCell(Row row, int column, String value) {
        return createCell(row, column, value, (CellStyle) null);
    }

    public static void setAlignment(Cell cell, Workbook workbook, short align) {
        setCellStyleProperty(cell, workbook, ALIGNMENT, Short.valueOf(align));
    }

    public static void setFont(Cell cell, Workbook workbook, Font font) {
        setCellStyleProperty(cell, workbook, FONT, Short.valueOf(font.getIndex()));
    }

    public static void setCellStyleProperty(Cell cell, Workbook workbook, String propertyName, Object propertyValue) {
        CellStyle newStyle = null;
        Map<String, Object> values = getFormatProperties(cell.getCellStyle());
        values.put(propertyName, propertyValue);
        short numberCellStyles = workbook.getNumCellStyles();
        short i = 0;
        while (true) {
            if (i >= numberCellStyles) {
                break;
            }
            CellStyle wbStyle = workbook.getCellStyleAt(i);
            if (getFormatProperties(wbStyle).equals(values)) {
                newStyle = wbStyle;
                break;
            }
            i = (short) (i + 1);
        }
        if (newStyle == null) {
            newStyle = workbook.createCellStyle();
            setFormatProperties(newStyle, workbook, values);
        }
        cell.setCellStyle(newStyle);
    }

    private static Map<String, Object> getFormatProperties(CellStyle style) {
        Map<String, Object> properties = new HashMap<>();
        putShort(properties, ALIGNMENT, style.getAlignment());
        putShort(properties, BORDER_BOTTOM, style.getBorderBottom());
        putShort(properties, BORDER_LEFT, style.getBorderLeft());
        putShort(properties, BORDER_RIGHT, style.getBorderRight());
        putShort(properties, BORDER_TOP, style.getBorderTop());
        putShort(properties, BOTTOM_BORDER_COLOR, style.getBottomBorderColor());
        putShort(properties, DATA_FORMAT, style.getDataFormat());
        putShort(properties, FILL_BACKGROUND_COLOR, style.getFillBackgroundColor());
        putShort(properties, FILL_FOREGROUND_COLOR, style.getFillForegroundColor());
        putShort(properties, FILL_PATTERN, style.getFillPattern());
        putShort(properties, FONT, style.getFontIndex());
        putBoolean(properties, HIDDEN, style.getHidden());
        putShort(properties, INDENTION, style.getIndention());
        putShort(properties, LEFT_BORDER_COLOR, style.getLeftBorderColor());
        putBoolean(properties, LOCKED, style.getLocked());
        putShort(properties, RIGHT_BORDER_COLOR, style.getRightBorderColor());
        putShort(properties, ROTATION, style.getRotation());
        putShort(properties, TOP_BORDER_COLOR, style.getTopBorderColor());
        putShort(properties, VERTICAL_ALIGNMENT, style.getVerticalAlignment());
        putBoolean(properties, WRAP_TEXT, style.getWrapText());
        return properties;
    }

    private static void setFormatProperties(CellStyle style, Workbook workbook, Map<String, Object> properties) {
        style.setAlignment(getShort(properties, ALIGNMENT));
        style.setBorderBottom(getShort(properties, BORDER_BOTTOM));
        style.setBorderLeft(getShort(properties, BORDER_LEFT));
        style.setBorderRight(getShort(properties, BORDER_RIGHT));
        style.setBorderTop(getShort(properties, BORDER_TOP));
        style.setBottomBorderColor(getShort(properties, BOTTOM_BORDER_COLOR));
        style.setDataFormat(getShort(properties, DATA_FORMAT));
        style.setFillBackgroundColor(getShort(properties, FILL_BACKGROUND_COLOR));
        style.setFillForegroundColor(getShort(properties, FILL_FOREGROUND_COLOR));
        style.setFillPattern(getShort(properties, FILL_PATTERN));
        style.setFont(workbook.getFontAt(getShort(properties, FONT)));
        style.setHidden(getBoolean(properties, HIDDEN));
        style.setIndention(getShort(properties, INDENTION));
        style.setLeftBorderColor(getShort(properties, LEFT_BORDER_COLOR));
        style.setLocked(getBoolean(properties, LOCKED));
        style.setRightBorderColor(getShort(properties, RIGHT_BORDER_COLOR));
        style.setRotation(getShort(properties, ROTATION));
        style.setTopBorderColor(getShort(properties, TOP_BORDER_COLOR));
        style.setVerticalAlignment(getShort(properties, VERTICAL_ALIGNMENT));
        style.setWrapText(getBoolean(properties, WRAP_TEXT));
    }

    private static short getShort(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Short) {
            return ((Short) value).shortValue();
        }
        return 0;
    }

    private static boolean getBoolean(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }

    private static void putShort(Map<String, Object> properties, String name, short value) {
        properties.put(name, Short.valueOf(value));
    }

    private static void putBoolean(Map<String, Object> properties, String name, boolean value) {
        properties.put(name, Boolean.valueOf(value));
    }

    public static Cell translateUnicodeValues(Cell cell) {
        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        String lowerCaseStr = s.toLowerCase();
        int i = 0;
        while (true) {
            UnicodeMapping[] unicodeMappingArr = unicodeMappings;
            if (i >= unicodeMappingArr.length) {
                break;
            }
            UnicodeMapping entry = unicodeMappingArr[i];
            String key = entry.entityName;
            if (lowerCaseStr.indexOf(key) != -1) {
                s = s.replaceAll(key, entry.resolvedValue);
                foundUnicode = true;
            }
            i++;
        }
        if (foundUnicode) {
            cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(s));
        }
        return cell;
    }

    private static UnicodeMapping um(String entityName, String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }
}
