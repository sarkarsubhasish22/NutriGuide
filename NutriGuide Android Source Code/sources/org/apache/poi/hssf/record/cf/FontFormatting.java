package org.apache.poi.hssf.record.cf;

import androidx.appcompat.widget.ActivityChooserView;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;

public final class FontFormatting {
    public static final int FONT_CELL_HEIGHT_PRESERVED = -1;
    private static final short FONT_WEIGHT_BOLD = 700;
    private static final short FONT_WEIGHT_NORMAL = 400;
    private static final int OFFSET_ESCAPEMENT_TYPE = 74;
    private static final int OFFSET_ESCAPEMENT_TYPE_MODIFIED = 92;
    private static final int OFFSET_FONT_COLOR_INDEX = 80;
    private static final int OFFSET_FONT_FORMATING_END = 116;
    private static final int OFFSET_FONT_HEIGHT = 64;
    private static final int OFFSET_FONT_NAME = 0;
    private static final int OFFSET_FONT_OPTIONS = 68;
    private static final int OFFSET_FONT_WEIGHT = 72;
    private static final int OFFSET_FONT_WEIGHT_MODIFIED = 100;
    private static final int OFFSET_NOT_USED1 = 104;
    private static final int OFFSET_NOT_USED2 = 108;
    private static final int OFFSET_NOT_USED3 = 112;
    private static final int OFFSET_OPTION_FLAGS = 88;
    private static final int OFFSET_UNDERLINE_TYPE = 76;
    private static final int OFFSET_UNDERLINE_TYPE_MODIFIED = 96;
    private static final int RAW_DATA_SIZE = 118;
    public static final short SS_NONE = 0;
    public static final short SS_SUB = 2;
    public static final short SS_SUPER = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    private static final BitField cancellation = BitFieldFactory.getInstance(128);
    private static final BitField cancellationModified = BitFieldFactory.getInstance(128);
    private static final BitField outline = BitFieldFactory.getInstance(8);
    private static final BitField outlineModified = BitFieldFactory.getInstance(8);
    private static final BitField posture = BitFieldFactory.getInstance(2);
    private static final BitField shadow = BitFieldFactory.getInstance(16);
    private static final BitField shadowModified = BitFieldFactory.getInstance(16);
    private static final BitField styleModified = BitFieldFactory.getInstance(2);
    private byte[] _rawData;

    private FontFormatting(byte[] rawData) {
        this._rawData = rawData;
    }

    public FontFormatting() {
        this(new byte[118]);
        setFontHeight(-1);
        setItalic(false);
        setFontWieghtModified(false);
        setOutline(false);
        setShadow(false);
        setStrikeout(false);
        setEscapementType(0);
        setUnderlineType(0);
        setFontColorIndex(-1);
        setFontStyleModified(false);
        setFontOutlineModified(false);
        setFontShadowModified(false);
        setFontCancellationModified(false);
        setEscapementTypeModified(false);
        setUnderlineTypeModified(false);
        setShort(0, 0);
        setInt(104, 1);
        setInt(108, 0);
        setInt(112, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        setShort(116, 1);
    }

    public FontFormatting(RecordInputStream in) {
        this(new byte[118]);
        int i = 0;
        while (true) {
            byte[] bArr = this._rawData;
            if (i < bArr.length) {
                bArr[i] = in.readByte();
                i++;
            } else {
                return;
            }
        }
    }

    private short getShort(int offset) {
        return LittleEndian.getShort(this._rawData, offset);
    }

    private void setShort(int offset, int value) {
        LittleEndian.putShort(this._rawData, offset, (short) value);
    }

    private int getInt(int offset) {
        return LittleEndian.getInt(this._rawData, offset);
    }

    private void setInt(int offset, int value) {
        LittleEndian.putInt(this._rawData, offset, value);
    }

    public byte[] getRawRecord() {
        return this._rawData;
    }

    public void setFontHeight(int height) {
        setInt(64, height);
    }

    public int getFontHeight() {
        return getInt(64);
    }

    private void setFontOption(boolean option, BitField field) {
        setInt(68, field.setBoolean(getInt(68), option));
    }

    private boolean getFontOption(BitField field) {
        return field.isSet(getInt(68));
    }

    public void setItalic(boolean italic) {
        setFontOption(italic, posture);
    }

    public boolean isItalic() {
        return getFontOption(posture);
    }

    public void setOutline(boolean on) {
        setFontOption(on, outline);
    }

    public boolean isOutlineOn() {
        return getFontOption(outline);
    }

    public void setShadow(boolean on) {
        setFontOption(on, shadow);
    }

    public boolean isShadowOn() {
        return getFontOption(shadow);
    }

    public void setStrikeout(boolean strike) {
        setFontOption(strike, cancellation);
    }

    public boolean isStruckout() {
        return getFontOption(cancellation);
    }

    private void setFontWeight(short pbw) {
        short bw = pbw;
        if (bw < 100) {
            bw = 100;
        }
        if (bw > 1000) {
            bw = 1000;
        }
        setShort(72, bw);
    }

    public void setBold(boolean bold) {
        setFontWeight(bold ? (short) 700 : 400);
    }

    public short getFontWeight() {
        return getShort(72);
    }

    public boolean isBold() {
        return getFontWeight() == 700;
    }

    public short getEscapementType() {
        return getShort(74);
    }

    public void setEscapementType(short escapementType) {
        setShort(74, escapementType);
    }

    public short getUnderlineType() {
        return getShort(76);
    }

    public void setUnderlineType(short underlineType) {
        setShort(76, underlineType);
    }

    public short getFontColorIndex() {
        return (short) getInt(80);
    }

    public void setFontColorIndex(short fci) {
        setInt(80, fci);
    }

    private boolean getOptionFlag(BitField field) {
        return field.getValue(getInt(88)) == 0;
    }

    private void setOptionFlag(boolean modified, BitField field) {
        setInt(88, field.setValue(getInt(88), (int) (!modified)));
    }

    public boolean isFontStyleModified() {
        return getOptionFlag(styleModified);
    }

    public void setFontStyleModified(boolean modified) {
        setOptionFlag(modified, styleModified);
    }

    public boolean isFontOutlineModified() {
        return getOptionFlag(outlineModified);
    }

    public void setFontOutlineModified(boolean modified) {
        setOptionFlag(modified, outlineModified);
    }

    public boolean isFontShadowModified() {
        return getOptionFlag(shadowModified);
    }

    public void setFontShadowModified(boolean modified) {
        setOptionFlag(modified, shadowModified);
    }

    public void setFontCancellationModified(boolean modified) {
        setOptionFlag(modified, cancellationModified);
    }

    public boolean isFontCancellationModified() {
        return getOptionFlag(cancellationModified);
    }

    public void setEscapementTypeModified(boolean modified) {
        setInt(92, (int) (!modified));
    }

    public boolean isEscapementTypeModified() {
        return getInt(92) == 0;
    }

    public void setUnderlineTypeModified(boolean modified) {
        setInt(96, (int) (!modified));
    }

    public boolean isUnderlineTypeModified() {
        return getInt(96) == 0;
    }

    public void setFontWieghtModified(boolean modified) {
        setInt(100, (int) (!modified));
    }

    public boolean isFontWeightModified() {
        return getInt(100) == 0;
    }

    public String toString() {
        String str;
        StringBuffer buffer = new StringBuffer();
        buffer.append("\t[Font Formatting]\n");
        buffer.append("\t.font height = ");
        buffer.append(getFontHeight());
        buffer.append(" twips\n");
        if (isFontStyleModified()) {
            buffer.append("\t.font posture = ");
            buffer.append(isItalic() ? "Italic" : "Normal");
            buffer.append("\n");
        } else {
            buffer.append("\t.font posture = ]not modified]");
            buffer.append("\n");
        }
        if (isFontOutlineModified()) {
            buffer.append("\t.font outline = ");
            buffer.append(isOutlineOn());
            buffer.append("\n");
        } else {
            buffer.append("\t.font outline is not modified\n");
        }
        if (isFontShadowModified()) {
            buffer.append("\t.font shadow = ");
            buffer.append(isShadowOn());
            buffer.append("\n");
        } else {
            buffer.append("\t.font shadow is not modified\n");
        }
        if (isFontCancellationModified()) {
            buffer.append("\t.font strikeout = ");
            buffer.append(isStruckout());
            buffer.append("\n");
        } else {
            buffer.append("\t.font strikeout is not modified\n");
        }
        if (isFontStyleModified()) {
            buffer.append("\t.font weight = ");
            buffer.append(getFontWeight());
            if (getFontWeight() == 400) {
                str = "(Normal)";
            } else if (getFontWeight() == 700) {
                str = "(Bold)";
            } else {
                str = "0x" + Integer.toHexString(getFontWeight());
            }
            buffer.append(str);
            buffer.append("\n");
        } else {
            buffer.append("\t.font weight = ]not modified]");
            buffer.append("\n");
        }
        if (isEscapementTypeModified()) {
            buffer.append("\t.escapement type = ");
            buffer.append(getEscapementType());
            buffer.append("\n");
        } else {
            buffer.append("\t.escapement type is not modified\n");
        }
        if (isUnderlineTypeModified()) {
            buffer.append("\t.underline type = ");
            buffer.append(getUnderlineType());
            buffer.append("\n");
        } else {
            buffer.append("\t.underline type is not modified\n");
        }
        buffer.append("\t.color index = ");
        buffer.append("0x" + Integer.toHexString(getFontColorIndex()).toUpperCase());
        buffer.append("\n");
        buffer.append("\t[/Font Formatting]\n");
        return buffer.toString();
    }

    public Object clone() {
        return new FontFormatting((byte[]) this._rawData.clone());
    }
}
