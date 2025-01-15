package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class NoteRecord extends StandardRecord {
    private static final Byte DEFAULT_PADDING = (byte) 0;
    public static final NoteRecord[] EMPTY_ARRAY = new NoteRecord[0];
    public static final short NOTE_HIDDEN = 0;
    public static final short NOTE_VISIBLE = 2;
    public static final short sid = 28;
    private int field_1_row;
    private int field_2_col;
    private short field_3_flags;
    private int field_4_shapeid;
    private boolean field_5_hasMultibyte;
    private String field_6_author;
    private Byte field_7_padding;

    public NoteRecord() {
        this.field_6_author = "";
        this.field_3_flags = 0;
        this.field_7_padding = DEFAULT_PADDING;
    }

    public short getSid() {
        return 28;
    }

    public NoteRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readShort();
        this.field_3_flags = in.readShort();
        this.field_4_shapeid = in.readUShort();
        int length = in.readShort();
        boolean z = in.readByte() != 0;
        this.field_5_hasMultibyte = z;
        if (z) {
            this.field_6_author = StringUtil.readUnicodeLE(in, length);
        } else {
            this.field_6_author = StringUtil.readCompressedUnicode(in, length);
        }
        if (in.available() == 1) {
            this.field_7_padding = Byte.valueOf(in.readByte());
        }
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_row);
        out.writeShort(this.field_2_col);
        out.writeShort(this.field_3_flags);
        out.writeShort(this.field_4_shapeid);
        out.writeShort(this.field_6_author.length());
        out.writeByte(this.field_5_hasMultibyte ? 1 : 0);
        if (this.field_5_hasMultibyte) {
            StringUtil.putUnicodeLE(this.field_6_author, out);
        } else {
            StringUtil.putCompressedUnicode(this.field_6_author, out);
        }
        Byte b = this.field_7_padding;
        if (b != null) {
            out.writeByte(b.intValue());
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        int i = 1;
        int length = (this.field_6_author.length() * (this.field_5_hasMultibyte ? 2 : 1)) + 11;
        if (this.field_7_padding == null) {
            i = 0;
        }
        return length + i;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[NOTE]\n");
        buffer.append("    .row    = ");
        buffer.append(this.field_1_row);
        buffer.append("\n");
        buffer.append("    .col    = ");
        buffer.append(this.field_2_col);
        buffer.append("\n");
        buffer.append("    .flags  = ");
        buffer.append(this.field_3_flags);
        buffer.append("\n");
        buffer.append("    .shapeid= ");
        buffer.append(this.field_4_shapeid);
        buffer.append("\n");
        buffer.append("    .author = ");
        buffer.append(this.field_6_author);
        buffer.append("\n");
        buffer.append("[/NOTE]\n");
        return buffer.toString();
    }

    public int getRow() {
        return this.field_1_row;
    }

    public void setRow(int row) {
        this.field_1_row = row;
    }

    public int getColumn() {
        return this.field_2_col;
    }

    public void setColumn(int col) {
        this.field_2_col = col;
    }

    public short getFlags() {
        return this.field_3_flags;
    }

    public void setFlags(short flags) {
        this.field_3_flags = flags;
    }

    /* access modifiers changed from: protected */
    public boolean authorIsMultibyte() {
        return this.field_5_hasMultibyte;
    }

    public int getShapeId() {
        return this.field_4_shapeid;
    }

    public void setShapeId(int id) {
        this.field_4_shapeid = id;
    }

    public String getAuthor() {
        return this.field_6_author;
    }

    public void setAuthor(String author) {
        this.field_6_author = author;
        this.field_5_hasMultibyte = StringUtil.hasMultibyte(author);
    }

    public Object clone() {
        NoteRecord rec = new NoteRecord();
        rec.field_1_row = this.field_1_row;
        rec.field_2_col = this.field_2_col;
        rec.field_3_flags = this.field_3_flags;
        rec.field_4_shapeid = this.field_4_shapeid;
        rec.field_6_author = this.field_6_author;
        return rec;
    }
}
