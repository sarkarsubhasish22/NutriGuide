package org.apache.poi.hssf.record;

import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;

public final class WSBoolRecord extends StandardRecord {
    private static final BitField alternateexpression = BitFieldFactory.getInstance(64);
    private static final BitField alternateformula = BitFieldFactory.getInstance(128);
    private static final BitField applystyles = BitFieldFactory.getInstance(32);
    private static final BitField autobreaks = BitFieldFactory.getInstance(1);
    private static final BitField dialog = BitFieldFactory.getInstance(16);
    private static final BitField displayguts = BitFieldFactory.getInstance(6);
    private static final BitField fittopage = BitFieldFactory.getInstance(1);
    private static final BitField rowsumsbelow = BitFieldFactory.getInstance(64);
    private static final BitField rowsumsright = BitFieldFactory.getInstance(128);
    public static final short sid = 129;
    private byte field_1_wsbool;
    private byte field_2_wsbool;

    public WSBoolRecord() {
    }

    public WSBoolRecord(RecordInputStream in) {
        byte[] data = in.readRemainder();
        this.field_1_wsbool = data[1];
        this.field_2_wsbool = data[0];
    }

    public void setWSBool1(byte bool1) {
        this.field_1_wsbool = bool1;
    }

    public void setAutobreaks(boolean ab) {
        this.field_1_wsbool = autobreaks.setByteBoolean(this.field_1_wsbool, ab);
    }

    public void setDialog(boolean isDialog) {
        this.field_1_wsbool = dialog.setByteBoolean(this.field_1_wsbool, isDialog);
    }

    public void setRowSumsBelow(boolean below) {
        this.field_1_wsbool = rowsumsbelow.setByteBoolean(this.field_1_wsbool, below);
    }

    public void setRowSumsRight(boolean right) {
        this.field_1_wsbool = rowsumsright.setByteBoolean(this.field_1_wsbool, right);
    }

    public void setWSBool2(byte bool2) {
        this.field_2_wsbool = bool2;
    }

    public void setFitToPage(boolean fit2page) {
        this.field_2_wsbool = fittopage.setByteBoolean(this.field_2_wsbool, fit2page);
    }

    public void setDisplayGuts(boolean guts) {
        this.field_2_wsbool = displayguts.setByteBoolean(this.field_2_wsbool, guts);
    }

    public void setAlternateExpression(boolean altexp) {
        this.field_2_wsbool = alternateexpression.setByteBoolean(this.field_2_wsbool, altexp);
    }

    public void setAlternateFormula(boolean formula) {
        this.field_2_wsbool = alternateformula.setByteBoolean(this.field_2_wsbool, formula);
    }

    public byte getWSBool1() {
        return this.field_1_wsbool;
    }

    public boolean getAutobreaks() {
        return autobreaks.isSet(this.field_1_wsbool);
    }

    public boolean getDialog() {
        return dialog.isSet(this.field_1_wsbool);
    }

    public boolean getRowSumsBelow() {
        return rowsumsbelow.isSet(this.field_1_wsbool);
    }

    public boolean getRowSumsRight() {
        return rowsumsright.isSet(this.field_1_wsbool);
    }

    public byte getWSBool2() {
        return this.field_2_wsbool;
    }

    public boolean getFitToPage() {
        return fittopage.isSet(this.field_2_wsbool);
    }

    public boolean getDisplayGuts() {
        return displayguts.isSet(this.field_2_wsbool);
    }

    public boolean getAlternateExpression() {
        return alternateexpression.isSet(this.field_2_wsbool);
    }

    public boolean getAlternateFormula() {
        return alternateformula.isSet(this.field_2_wsbool);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[WSBOOL]\n");
        buffer.append("    .wsbool1        = ");
        buffer.append(Integer.toHexString(getWSBool1()));
        buffer.append("\n");
        buffer.append("        .autobreaks = ");
        buffer.append(getAutobreaks());
        buffer.append("\n");
        buffer.append("        .dialog     = ");
        buffer.append(getDialog());
        buffer.append("\n");
        buffer.append("        .rowsumsbelw= ");
        buffer.append(getRowSumsBelow());
        buffer.append("\n");
        buffer.append("        .rowsumsrigt= ");
        buffer.append(getRowSumsRight());
        buffer.append("\n");
        buffer.append("    .wsbool2        = ");
        buffer.append(Integer.toHexString(getWSBool2()));
        buffer.append("\n");
        buffer.append("        .fittopage  = ");
        buffer.append(getFitToPage());
        buffer.append("\n");
        buffer.append("        .displayguts= ");
        buffer.append(getDisplayGuts());
        buffer.append("\n");
        buffer.append("        .alternateex= ");
        buffer.append(getAlternateExpression());
        buffer.append("\n");
        buffer.append("        .alternatefo= ");
        buffer.append(getAlternateFormula());
        buffer.append("\n");
        buffer.append("[/WSBOOL]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeByte(getWSBool2());
        out.writeByte(getWSBool1());
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 2;
    }

    public short getSid() {
        return 129;
    }

    public Object clone() {
        WSBoolRecord rec = new WSBoolRecord();
        rec.field_1_wsbool = this.field_1_wsbool;
        rec.field_2_wsbool = this.field_2_wsbool;
        return rec;
    }
}
