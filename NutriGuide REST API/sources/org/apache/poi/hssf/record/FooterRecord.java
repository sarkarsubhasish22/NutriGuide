package org.apache.poi.hssf.record;

public final class FooterRecord extends HeaderFooterBase {
    public static final short sid = 21;

    public FooterRecord(String text) {
        super(text);
    }

    public FooterRecord(RecordInputStream in) {
        super(in);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FOOTER]\n");
        buffer.append("    .footer = ");
        buffer.append(getText());
        buffer.append("\n");
        buffer.append("[/FOOTER]\n");
        return buffer.toString();
    }

    public short getSid() {
        return 21;
    }

    public Object clone() {
        return new FooterRecord(getText());
    }
}
