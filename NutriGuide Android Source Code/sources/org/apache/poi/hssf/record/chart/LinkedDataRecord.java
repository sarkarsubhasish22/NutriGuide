package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class LinkedDataRecord extends StandardRecord {
    public static final byte LINK_TYPE_CATEGORIES = 2;
    public static final byte LINK_TYPE_TITLE_OR_TEXT = 0;
    public static final byte LINK_TYPE_VALUES = 1;
    public static final byte REFERENCE_TYPE_DEFAULT_CATEGORIES = 0;
    public static final byte REFERENCE_TYPE_DIRECT = 1;
    public static final byte REFERENCE_TYPE_ERROR_REPORTED = 4;
    public static final byte REFERENCE_TYPE_NOT_USED = 3;
    public static final byte REFERENCE_TYPE_WORKSHEET = 2;
    private static final BitField customNumberFormat = BitFieldFactory.getInstance(1);
    public static final short sid = 4177;
    private byte field_1_linkType;
    private byte field_2_referenceType;
    private short field_3_options;
    private short field_4_indexNumberFmtRecord;
    private Formula field_5_formulaOfLink;

    public LinkedDataRecord() {
    }

    public LinkedDataRecord(RecordInputStream in) {
        this.field_1_linkType = in.readByte();
        this.field_2_referenceType = in.readByte();
        this.field_3_options = in.readShort();
        this.field_4_indexNumberFmtRecord = in.readShort();
        this.field_5_formulaOfLink = Formula.read(in.readUShort(), in);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AI]\n");
        buffer.append("    .linkType             = ");
        buffer.append(HexDump.byteToHex(getLinkType()));
        buffer.append(10);
        buffer.append("    .referenceType        = ");
        buffer.append(HexDump.byteToHex(getReferenceType()));
        buffer.append(10);
        buffer.append("    .options              = ");
        buffer.append(HexDump.shortToHex(getOptions()));
        buffer.append(10);
        buffer.append("    .customNumberFormat   = ");
        buffer.append(isCustomNumberFormat());
        buffer.append(10);
        buffer.append("    .indexNumberFmtRecord = ");
        buffer.append(HexDump.shortToHex(getIndexNumberFmtRecord()));
        buffer.append(10);
        buffer.append("    .formulaOfLink        = ");
        buffer.append(10);
        Ptg[] ptgs = this.field_5_formulaOfLink.getTokens();
        for (Ptg ptg : ptgs) {
            buffer.append(ptg.toString());
            buffer.append(ptg.getRVAType());
            buffer.append(10);
        }
        buffer.append("[/AI]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeByte(this.field_1_linkType);
        out.writeByte(this.field_2_referenceType);
        out.writeShort(this.field_3_options);
        out.writeShort(this.field_4_indexNumberFmtRecord);
        this.field_5_formulaOfLink.serialize(out);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.field_5_formulaOfLink.getEncodedSize() + 6;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        LinkedDataRecord rec = new LinkedDataRecord();
        rec.field_1_linkType = this.field_1_linkType;
        rec.field_2_referenceType = this.field_2_referenceType;
        rec.field_3_options = this.field_3_options;
        rec.field_4_indexNumberFmtRecord = this.field_4_indexNumberFmtRecord;
        rec.field_5_formulaOfLink = this.field_5_formulaOfLink.copy();
        return rec;
    }

    public byte getLinkType() {
        return this.field_1_linkType;
    }

    public void setLinkType(byte field_1_linkType2) {
        this.field_1_linkType = field_1_linkType2;
    }

    public byte getReferenceType() {
        return this.field_2_referenceType;
    }

    public void setReferenceType(byte field_2_referenceType2) {
        this.field_2_referenceType = field_2_referenceType2;
    }

    public short getOptions() {
        return this.field_3_options;
    }

    public void setOptions(short field_3_options2) {
        this.field_3_options = field_3_options2;
    }

    public short getIndexNumberFmtRecord() {
        return this.field_4_indexNumberFmtRecord;
    }

    public void setIndexNumberFmtRecord(short field_4_indexNumberFmtRecord2) {
        this.field_4_indexNumberFmtRecord = field_4_indexNumberFmtRecord2;
    }

    public Ptg[] getFormulaOfLink() {
        return this.field_5_formulaOfLink.getTokens();
    }

    public void setFormulaOfLink(Ptg[] ptgs) {
        this.field_5_formulaOfLink = Formula.create(ptgs);
    }

    public void setCustomNumberFormat(boolean value) {
        this.field_3_options = customNumberFormat.setShortBoolean(this.field_3_options, value);
    }

    public boolean isCustomNumberFormat() {
        return customNumberFormat.isSet(this.field_3_options);
    }
}
