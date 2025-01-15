package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.constant.ConstantValueParser;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class ExternalNameRecord extends StandardRecord {
    private static final int OPT_AUTOMATIC_LINK = 2;
    private static final int OPT_BUILTIN_NAME = 1;
    private static final int OPT_ICONIFIED_PICTURE_LINK = 32768;
    private static final int OPT_OLE_LINK = 16;
    private static final int OPT_PICTURE_LINK = 4;
    private static final int OPT_STD_DOCUMENT_NAME = 8;
    public static final short sid = 35;
    private Object[] _ddeValues;
    private int _nColumns;
    private int _nRows;
    private short field_1_option_flag;
    private short field_2_ixals;
    private short field_3_not_used;
    private String field_4_name;
    private Formula field_5_name_definition;

    public boolean isBuiltInName() {
        return (this.field_1_option_flag & 1) != 0;
    }

    public boolean isAutomaticLink() {
        return (this.field_1_option_flag & 2) != 0;
    }

    public boolean isPicureLink() {
        return (this.field_1_option_flag & 4) != 0;
    }

    public boolean isStdDocumentNameIdentifier() {
        return (this.field_1_option_flag & 8) != 0;
    }

    public boolean isOLELink() {
        return (this.field_1_option_flag & 16) != 0;
    }

    public boolean isIconifiedPictureLink() {
        return (this.field_1_option_flag & 32768) != 0;
    }

    public String getText() {
        return this.field_4_name;
    }

    public short getIx() {
        return this.field_2_ixals;
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        int result = 6 + (StringUtil.getEncodedSize(this.field_4_name) - 1);
        if (isOLELink() || isStdDocumentNameIdentifier()) {
            return result;
        }
        if (isAutomaticLink()) {
            return result + 3 + ConstantValueParser.getEncodedSize(this._ddeValues);
        }
        return result + this.field_5_name_definition.getEncodedSize();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_option_flag);
        out.writeShort(this.field_2_ixals);
        out.writeShort(this.field_3_not_used);
        out.writeByte(this.field_4_name.length());
        StringUtil.writeUnicodeStringFlagAndData(out, this.field_4_name);
        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                out.writeByte(this._nColumns - 1);
                out.writeShort(this._nRows - 1);
                ConstantValueParser.encode(out, this._ddeValues);
                return;
            }
            this.field_5_name_definition.serialize(out);
        }
    }

    public ExternalNameRecord(RecordInputStream in) {
        this.field_1_option_flag = in.readShort();
        this.field_2_ixals = in.readShort();
        this.field_3_not_used = in.readShort();
        this.field_4_name = StringUtil.readUnicodeString(in, in.readUByte());
        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                int nColumns = in.readUByte() + 1;
                int nRows = in.readShort() + 1;
                this._ddeValues = ConstantValueParser.parse(in, nRows * nColumns);
                this._nColumns = nColumns;
                this._nRows = nRows;
                return;
            }
            this.field_5_name_definition = Formula.read(in.readUShort(), in);
        }
    }

    public short getSid() {
        return 35;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[EXTERNALNAME]\n");
        sb.append("    .ix      = ");
        sb.append(this.field_2_ixals);
        sb.append("\n");
        sb.append("    .name    = ");
        sb.append(this.field_4_name);
        sb.append("\n");
        if (this.field_5_name_definition != null) {
            sb.append("    .formula = ");
            sb.append(this.field_5_name_definition);
            sb.append("\n");
        }
        sb.append("[/EXTERNALNAME]\n");
        return sb.toString();
    }
}
