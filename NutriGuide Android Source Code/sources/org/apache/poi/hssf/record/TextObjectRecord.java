package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.cont.ContinuableRecord;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.hssf.record.formula.OperandPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;

public final class TextObjectRecord extends ContinuableRecord {
    private static final int FORMAT_RUN_ENCODED_SIZE = 8;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_CENTERED = 2;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_JUSTIFIED = 4;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_LEFT_ALIGNED = 1;
    public static final short HORIZONTAL_TEXT_ALIGNMENT_RIGHT_ALIGNED = 3;
    private static final BitField HorizontalTextAlignment = BitFieldFactory.getInstance(14);
    public static final short TEXT_ORIENTATION_NONE = 0;
    public static final short TEXT_ORIENTATION_ROT_LEFT = 3;
    public static final short TEXT_ORIENTATION_ROT_RIGHT = 2;
    public static final short TEXT_ORIENTATION_TOP_TO_BOTTOM = 1;
    public static final short VERTICAL_TEXT_ALIGNMENT_BOTTOM = 3;
    public static final short VERTICAL_TEXT_ALIGNMENT_CENTER = 2;
    public static final short VERTICAL_TEXT_ALIGNMENT_JUSTIFY = 4;
    public static final short VERTICAL_TEXT_ALIGNMENT_TOP = 1;
    private static final BitField VerticalTextAlignment = BitFieldFactory.getInstance(112);
    public static final short sid = 438;
    private static final BitField textLocked = BitFieldFactory.getInstance(512);
    private OperandPtg _linkRefPtg;
    private HSSFRichTextString _text;
    private Byte _unknownPostFormulaByte;
    private int _unknownPreFormulaInt;
    private int field_1_options;
    private int field_2_textOrientation;
    private int field_3_reserved4;
    private int field_4_reserved5;
    private int field_5_reserved6;
    private int field_8_reserved7;

    public TextObjectRecord() {
    }

    public TextObjectRecord(RecordInputStream in) {
        String text;
        this.field_1_options = in.readUShort();
        this.field_2_textOrientation = in.readUShort();
        this.field_3_reserved4 = in.readUShort();
        this.field_4_reserved5 = in.readUShort();
        this.field_5_reserved6 = in.readUShort();
        int field_6_textLength = in.readUShort();
        int field_7_formattingDataLength = in.readUShort();
        this.field_8_reserved7 = in.readInt();
        if (in.remaining() <= 0) {
            this._linkRefPtg = null;
        } else if (in.remaining() >= 11) {
            int formulaSize = in.readUShort();
            this._unknownPreFormulaInt = in.readInt();
            Ptg[] ptgs = Ptg.readTokens(formulaSize, in);
            if (ptgs.length == 1) {
                this._linkRefPtg = (OperandPtg) ptgs[0];
                if (in.remaining() > 0) {
                    this._unknownPostFormulaByte = Byte.valueOf(in.readByte());
                } else {
                    this._unknownPostFormulaByte = null;
                }
            } else {
                throw new RecordFormatException("Read " + ptgs.length + " tokens but expected exactly 1");
            }
        } else {
            throw new RecordFormatException("Not enough remaining data for a link formula");
        }
        if (in.remaining() <= 0) {
            if (field_6_textLength > 0) {
                text = readRawString(in, field_6_textLength);
            } else {
                text = "";
            }
            HSSFRichTextString hSSFRichTextString = new HSSFRichTextString(text);
            this._text = hSSFRichTextString;
            if (field_7_formattingDataLength > 0) {
                processFontRuns(in, hSSFRichTextString, field_7_formattingDataLength);
                return;
            }
            return;
        }
        throw new RecordFormatException("Unused " + in.remaining() + " bytes at end of record");
    }

    private static String readRawString(RecordInputStream in, int textLength) {
        if ((in.readByte() & 1) == 0) {
            return in.readCompressedUnicode(textLength);
        }
        return in.readUnicodeLEString(textLength);
    }

    private static void processFontRuns(RecordInputStream in, HSSFRichTextString str, int formattingRunDataLength) {
        if (formattingRunDataLength % 8 == 0) {
            int nRuns = formattingRunDataLength / 8;
            for (int i = 0; i < nRuns; i++) {
                short index = in.readShort();
                short iFont = in.readShort();
                in.readInt();
                str.applyFont((int) index, str.length(), iFont);
            }
            return;
        }
        throw new RecordFormatException("Bad format run data length " + formattingRunDataLength + ")");
    }

    public short getSid() {
        return sid;
    }

    private void serializeTXORecord(ContinuableRecordOutput out) {
        out.writeShort(this.field_1_options);
        out.writeShort(this.field_2_textOrientation);
        out.writeShort(this.field_3_reserved4);
        out.writeShort(this.field_4_reserved5);
        out.writeShort(this.field_5_reserved6);
        out.writeShort(this._text.length());
        out.writeShort(getFormattingDataLength());
        out.writeInt(this.field_8_reserved7);
        OperandPtg operandPtg = this._linkRefPtg;
        if (operandPtg != null) {
            out.writeShort(operandPtg.getSize());
            out.writeInt(this._unknownPreFormulaInt);
            this._linkRefPtg.write(out);
            Byte b = this._unknownPostFormulaByte;
            if (b != null) {
                out.writeByte(b.byteValue());
            }
        }
    }

    private void serializeTrailingRecords(ContinuableRecordOutput out) {
        out.writeContinue();
        out.writeStringData(this._text.getString());
        out.writeContinue();
        writeFormatData(out, this._text);
    }

    /* access modifiers changed from: protected */
    public void serialize(ContinuableRecordOutput out) {
        serializeTXORecord(out);
        if (this._text.getString().length() > 0) {
            serializeTrailingRecords(out);
        }
    }

    private int getFormattingDataLength() {
        if (this._text.length() < 1) {
            return 0;
        }
        return (this._text.numFormattingRuns() + 1) * 8;
    }

    private static void writeFormatData(ContinuableRecordOutput out, HSSFRichTextString str) {
        int nRuns = str.numFormattingRuns();
        for (int i = 0; i < nRuns; i++) {
            out.writeShort(str.getIndexOfFormattingRun(i));
            int fontIndex = str.getFontOfFormattingRun(i);
            out.writeShort(fontIndex == 0 ? 0 : fontIndex);
            out.writeInt(0);
        }
        out.writeShort(str.length());
        out.writeShort(0);
        out.writeInt(0);
    }

    public void setHorizontalTextAlignment(int value) {
        this.field_1_options = HorizontalTextAlignment.setValue(this.field_1_options, value);
    }

    public int getHorizontalTextAlignment() {
        return HorizontalTextAlignment.getValue(this.field_1_options);
    }

    public void setVerticalTextAlignment(int value) {
        this.field_1_options = VerticalTextAlignment.setValue(this.field_1_options, value);
    }

    public int getVerticalTextAlignment() {
        return VerticalTextAlignment.getValue(this.field_1_options);
    }

    public void setTextLocked(boolean value) {
        this.field_1_options = textLocked.setBoolean(this.field_1_options, value);
    }

    public boolean isTextLocked() {
        return textLocked.isSet(this.field_1_options);
    }

    public int getTextOrientation() {
        return this.field_2_textOrientation;
    }

    public void setTextOrientation(int textOrientation) {
        this.field_2_textOrientation = textOrientation;
    }

    public HSSFRichTextString getStr() {
        return this._text;
    }

    public void setStr(HSSFRichTextString str) {
        this._text = str;
    }

    public Ptg getLinkRefPtg() {
        return this._linkRefPtg;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[TXO]\n");
        sb.append("    .options        = ");
        sb.append(HexDump.shortToHex(this.field_1_options));
        sb.append("\n");
        sb.append("         .isHorizontal = ");
        sb.append(getHorizontalTextAlignment());
        sb.append(10);
        sb.append("         .isVertical   = ");
        sb.append(getVerticalTextAlignment());
        sb.append(10);
        sb.append("         .textLocked   = ");
        sb.append(isTextLocked());
        sb.append(10);
        sb.append("    .textOrientation= ");
        sb.append(HexDump.shortToHex(getTextOrientation()));
        sb.append("\n");
        sb.append("    .reserved4      = ");
        sb.append(HexDump.shortToHex(this.field_3_reserved4));
        sb.append("\n");
        sb.append("    .reserved5      = ");
        sb.append(HexDump.shortToHex(this.field_4_reserved5));
        sb.append("\n");
        sb.append("    .reserved6      = ");
        sb.append(HexDump.shortToHex(this.field_5_reserved6));
        sb.append("\n");
        sb.append("    .textLength     = ");
        sb.append(HexDump.shortToHex(this._text.length()));
        sb.append("\n");
        sb.append("    .reserved7      = ");
        sb.append(HexDump.intToHex(this.field_8_reserved7));
        sb.append("\n");
        sb.append("    .string = ");
        sb.append(this._text);
        sb.append(10);
        for (int i = 0; i < this._text.numFormattingRuns(); i++) {
            sb.append("    .textrun = ");
            sb.append(this._text.getFontOfFormattingRun(i));
            sb.append(10);
        }
        sb.append("[/TXO]\n");
        return sb.toString();
    }

    public Object clone() {
        TextObjectRecord rec = new TextObjectRecord();
        rec._text = this._text;
        rec.field_1_options = this.field_1_options;
        rec.field_2_textOrientation = this.field_2_textOrientation;
        rec.field_3_reserved4 = this.field_3_reserved4;
        rec.field_4_reserved5 = this.field_4_reserved5;
        rec.field_5_reserved6 = this.field_5_reserved6;
        rec.field_8_reserved7 = this.field_8_reserved7;
        rec._text = this._text;
        OperandPtg operandPtg = this._linkRefPtg;
        if (operandPtg != null) {
            rec._unknownPreFormulaInt = this._unknownPreFormulaInt;
            rec._linkRefPtg = operandPtg.copy();
            rec._unknownPostFormulaByte = this._unknownPostFormulaByte;
        }
        return rec;
    }
}
