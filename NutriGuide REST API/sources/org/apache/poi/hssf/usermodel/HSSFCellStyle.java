package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public final class HSSFCellStyle implements CellStyle {
    private ExtendedFormatRecord _format;
    private short _index;
    private InternalWorkbook _workbook;

    protected HSSFCellStyle(short index, ExtendedFormatRecord rec, HSSFWorkbook workbook) {
        this(index, rec, workbook.getWorkbook());
    }

    protected HSSFCellStyle(short index, ExtendedFormatRecord rec, InternalWorkbook workbook) {
        this._format = null;
        this._index = 0;
        this._workbook = null;
        this._workbook = workbook;
        this._index = index;
        this._format = rec;
    }

    public short getIndex() {
        return this._index;
    }

    public HSSFCellStyle getParentStyle() {
        if (this._format.getParentIndex() == 0) {
            return null;
        }
        return new HSSFCellStyle(this._format.getParentIndex(), this._workbook.getExFormatAt(this._format.getParentIndex()), this._workbook);
    }

    public void setDataFormat(short fmt) {
        this._format.setFormatIndex(fmt);
    }

    public short getDataFormat() {
        return this._format.getFormatIndex();
    }

    public String getDataFormatString() {
        return getDataFormatString(this._workbook);
    }

    public String getDataFormatString(Workbook workbook) {
        return getDataFormat() == -1 ? "General" : new HSSFDataFormat(((HSSFWorkbook) workbook).getWorkbook()).getFormat(getDataFormat());
    }

    public String getDataFormatString(InternalWorkbook workbook) {
        return new HSSFDataFormat(workbook).getFormat(getDataFormat());
    }

    public void setFont(Font font) {
        setFont((HSSFFont) font);
    }

    public void setFont(HSSFFont font) {
        this._format.setIndentNotParentFont(true);
        this._format.setFontIndex(font.getIndex());
    }

    public short getFontIndex() {
        return this._format.getFontIndex();
    }

    public HSSFFont getFont(Workbook parentWorkbook) {
        return ((HSSFWorkbook) parentWorkbook).getFontAt(getFontIndex());
    }

    public void setHidden(boolean hidden) {
        this._format.setIndentNotParentCellOptions(true);
        this._format.setHidden(hidden);
    }

    public boolean getHidden() {
        return this._format.isHidden();
    }

    public void setLocked(boolean locked) {
        this._format.setIndentNotParentCellOptions(true);
        this._format.setLocked(locked);
    }

    public boolean getLocked() {
        return this._format.isLocked();
    }

    public void setAlignment(short align) {
        this._format.setIndentNotParentAlignment(true);
        this._format.setAlignment(align);
    }

    public short getAlignment() {
        return this._format.getAlignment();
    }

    public void setWrapText(boolean wrapped) {
        this._format.setIndentNotParentAlignment(true);
        this._format.setWrapText(wrapped);
    }

    public boolean getWrapText() {
        return this._format.getWrapText();
    }

    public void setVerticalAlignment(short align) {
        this._format.setVerticalAlignment(align);
    }

    public short getVerticalAlignment() {
        return this._format.getVerticalAlignment();
    }

    public void setRotation(short rotation) {
        if (rotation != 255) {
            if (rotation < 0 && rotation >= -90) {
                rotation = (short) (90 - rotation);
            } else if (rotation < -90 || rotation > 90) {
                throw new IllegalArgumentException("The rotation must be between -90 and 90 degrees, or 0xff");
            }
        }
        this._format.setRotation(rotation);
    }

    public short getRotation() {
        short rotation = this._format.getRotation();
        if (rotation != 255 && rotation > 90) {
            return (short) (90 - rotation);
        }
        return rotation;
    }

    public void setIndention(short indent) {
        this._format.setIndent(indent);
    }

    public short getIndention() {
        return this._format.getIndent();
    }

    public void setBorderLeft(short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderLeft(border);
    }

    public short getBorderLeft() {
        return this._format.getBorderLeft();
    }

    public void setBorderRight(short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderRight(border);
    }

    public short getBorderRight() {
        return this._format.getBorderRight();
    }

    public void setBorderTop(short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderTop(border);
    }

    public short getBorderTop() {
        return this._format.getBorderTop();
    }

    public void setBorderBottom(short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderBottom(border);
    }

    public short getBorderBottom() {
        return this._format.getBorderBottom();
    }

    public void setLeftBorderColor(short color) {
        this._format.setLeftBorderPaletteIdx(color);
    }

    public short getLeftBorderColor() {
        return this._format.getLeftBorderPaletteIdx();
    }

    public void setRightBorderColor(short color) {
        this._format.setRightBorderPaletteIdx(color);
    }

    public short getRightBorderColor() {
        return this._format.getRightBorderPaletteIdx();
    }

    public void setTopBorderColor(short color) {
        this._format.setTopBorderPaletteIdx(color);
    }

    public short getTopBorderColor() {
        return this._format.getTopBorderPaletteIdx();
    }

    public void setBottomBorderColor(short color) {
        this._format.setBottomBorderPaletteIdx(color);
    }

    public short getBottomBorderColor() {
        return this._format.getBottomBorderPaletteIdx();
    }

    public void setFillPattern(short fp) {
        this._format.setAdtlFillPattern(fp);
    }

    public short getFillPattern() {
        return this._format.getAdtlFillPattern();
    }

    private void checkDefaultBackgroundFills() {
        if (this._format.getFillForeground() == 64) {
            if (this._format.getFillBackground() != 65) {
                setFillBackgroundColor(65);
            }
        } else if (this._format.getFillBackground() == 65 && this._format.getFillForeground() != 64) {
            setFillBackgroundColor(64);
        }
    }

    public void setFillBackgroundColor(short bg) {
        this._format.setFillBackground(bg);
        checkDefaultBackgroundFills();
    }

    public short getFillBackgroundColor() {
        short result = this._format.getFillBackground();
        if (result == 65) {
            return 64;
        }
        return result;
    }

    public HSSFColor getFillBackgroundColorColor() {
        return new HSSFPalette(this._workbook.getCustomPalette()).getColor(getFillBackgroundColor());
    }

    public void setFillForegroundColor(short bg) {
        this._format.setFillForeground(bg);
        checkDefaultBackgroundFills();
    }

    public short getFillForegroundColor() {
        return this._format.getFillForeground();
    }

    public HSSFColor getFillForegroundColorColor() {
        return new HSSFPalette(this._workbook.getCustomPalette()).getColor(getFillForegroundColor());
    }

    public String getUserStyleName() {
        StyleRecord sr = this._workbook.getStyleRecord(this._index);
        if (sr != null && !sr.isBuiltin()) {
            return sr.getName();
        }
        return null;
    }

    public void setUserStyleName(String styleName) {
        StyleRecord sr = this._workbook.getStyleRecord(this._index);
        if (sr == null) {
            sr = this._workbook.createStyleRecord(this._index);
        }
        if (!sr.isBuiltin() || this._index > 20) {
            sr.setName(styleName);
            return;
        }
        throw new IllegalArgumentException("Unable to set user specified style names for built in styles!");
    }

    public void verifyBelongsToWorkbook(HSSFWorkbook wb) {
        if (wb.getWorkbook() != this._workbook) {
            throw new IllegalArgumentException("This Style does not belong to the supplied Workbook. Are you trying to assign a style from one workbook to the cell of a differnt workbook?");
        }
    }

    public void cloneStyleFrom(CellStyle source) {
        if (source instanceof HSSFCellStyle) {
            cloneStyleFrom((HSSFCellStyle) source);
            return;
        }
        throw new IllegalArgumentException("Can only clone from one HSSFCellStyle to another, not between HSSFCellStyle and XSSFCellStyle");
    }

    public void cloneStyleFrom(HSSFCellStyle source) {
        this._format.cloneStyleFrom(source._format);
        InternalWorkbook internalWorkbook = this._workbook;
        if (internalWorkbook != source._workbook) {
            setDataFormat((short) internalWorkbook.createFormat(source.getDataFormatString()));
            FontRecord fr = this._workbook.createNewFont();
            fr.cloneStyleFrom(source._workbook.getFontRecordAt(source.getFontIndex()));
            setFont(new HSSFFont((short) this._workbook.getFontIndex(fr), fr));
        }
    }

    public int hashCode() {
        int i = 1 * 31;
        ExtendedFormatRecord extendedFormatRecord = this._format;
        return ((i + (extendedFormatRecord == null ? 0 : extendedFormatRecord.hashCode())) * 31) + this._index;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof HSSFCellStyle)) {
            return false;
        }
        HSSFCellStyle other = (HSSFCellStyle) obj;
        ExtendedFormatRecord extendedFormatRecord = this._format;
        if (extendedFormatRecord == null) {
            if (other._format != null) {
                return false;
            }
        } else if (!extendedFormatRecord.equals(other._format)) {
            return false;
        }
        if (this._index != other._index) {
            return false;
        }
        return true;
    }
}
