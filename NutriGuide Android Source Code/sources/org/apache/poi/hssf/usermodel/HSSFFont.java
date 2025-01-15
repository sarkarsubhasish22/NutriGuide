package org.apache.poi.hssf.usermodel;

import androidx.core.view.InputDeviceCompat;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.ss.usermodel.Font;

public final class HSSFFont implements Font {
    public static final String FONT_ARIAL = "Arial";
    private FontRecord font;
    private short index;

    protected HSSFFont(short index2, FontRecord rec) {
        this.font = rec;
        this.index = index2;
    }

    public void setFontName(String name) {
        this.font.setFontName(name);
    }

    public String getFontName() {
        return this.font.getFontName();
    }

    public short getIndex() {
        return this.index;
    }

    public void setFontHeight(short height) {
        this.font.setFontHeight(height);
    }

    public void setFontHeightInPoints(short height) {
        this.font.setFontHeight((short) (height * 20));
    }

    public short getFontHeight() {
        return this.font.getFontHeight();
    }

    public short getFontHeightInPoints() {
        return (short) (this.font.getFontHeight() / 20);
    }

    public void setItalic(boolean italic) {
        this.font.setItalic(italic);
    }

    public boolean getItalic() {
        return this.font.isItalic();
    }

    public void setStrikeout(boolean strikeout) {
        this.font.setStrikeout(strikeout);
    }

    public boolean getStrikeout() {
        return this.font.isStruckout();
    }

    public void setColor(short color) {
        this.font.setColorPaletteIndex(color);
    }

    public short getColor() {
        return this.font.getColorPaletteIndex();
    }

    public void setBoldweight(short boldweight) {
        this.font.setBoldWeight(boldweight);
    }

    public short getBoldweight() {
        return this.font.getBoldWeight();
    }

    public void setTypeOffset(short offset) {
        this.font.setSuperSubScript(offset);
    }

    public short getTypeOffset() {
        return this.font.getSuperSubScript();
    }

    public void setUnderline(byte underline) {
        this.font.setUnderline(underline);
    }

    public byte getUnderline() {
        return this.font.getUnderline();
    }

    public int getCharSet() {
        byte charset = this.font.getCharset();
        if (charset >= 0) {
            return charset;
        }
        return charset + 256;
    }

    public void setCharSet(int charset) {
        byte cs = (byte) charset;
        if (charset > 127) {
            cs = (byte) (charset + InputDeviceCompat.SOURCE_ANY);
        }
        setCharSet(cs);
    }

    public void setCharSet(byte charset) {
        this.font.setCharset(charset);
    }

    public String toString() {
        return "org.apache.poi.hssf.usermodel.HSSFFont{" + this.font + "}";
    }

    public int hashCode() {
        int i = 1 * 31;
        FontRecord fontRecord = this.font;
        return ((i + (fontRecord == null ? 0 : fontRecord.hashCode())) * 31) + this.index;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof HSSFFont)) {
            return false;
        }
        HSSFFont other = (HSSFFont) obj;
        FontRecord fontRecord = this.font;
        if (fontRecord == null) {
            if (other.font != null) {
                return false;
            }
        } else if (!fontRecord.equals(other.font)) {
            return false;
        }
        if (this.index != other.index) {
            return false;
        }
        return true;
    }
}
