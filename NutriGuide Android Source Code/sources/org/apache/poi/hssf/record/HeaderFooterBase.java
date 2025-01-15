package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public abstract class HeaderFooterBase extends StandardRecord {
    private boolean field_2_hasMultibyte;
    private String field_3_text;

    protected HeaderFooterBase(String text) {
        setText(text);
    }

    protected HeaderFooterBase(RecordInputStream in) {
        if (in.remaining() > 0) {
            int field_1_footer_len = in.readShort();
            boolean z = in.readByte() != 0;
            this.field_2_hasMultibyte = z;
            if (z) {
                this.field_3_text = in.readUnicodeLEString(field_1_footer_len);
            } else {
                this.field_3_text = in.readCompressedUnicode(field_1_footer_len);
            }
        } else {
            this.field_3_text = "";
        }
    }

    public final void setText(String text) {
        if (text != null) {
            this.field_2_hasMultibyte = StringUtil.hasMultibyte(text);
            this.field_3_text = text;
            if (getDataSize() > 8224) {
                throw new IllegalArgumentException("Header/Footer string too long (limit is 8224 bytes)");
            }
            return;
        }
        throw new IllegalArgumentException("text must not be null");
    }

    private int getTextLength() {
        return this.field_3_text.length();
    }

    public final String getText() {
        return this.field_3_text;
    }

    public final void serialize(LittleEndianOutput out) {
        if (getTextLength() > 0) {
            out.writeShort(getTextLength());
            out.writeByte(this.field_2_hasMultibyte ? 1 : 0);
            if (this.field_2_hasMultibyte) {
                StringUtil.putUnicodeLE(this.field_3_text, out);
            } else {
                StringUtil.putCompressedUnicode(this.field_3_text, out);
            }
        }
    }

    /* access modifiers changed from: protected */
    public final int getDataSize() {
        int i = 1;
        if (getTextLength() < 1) {
            return 0;
        }
        int textLength = getTextLength();
        if (this.field_2_hasMultibyte) {
            i = 2;
        }
        return (textLength * i) + 3;
    }
}
