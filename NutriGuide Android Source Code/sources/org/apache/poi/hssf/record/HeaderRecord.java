package org.apache.poi.hssf.record;

public final class HeaderRecord extends HeaderFooterBase {
    public static final short sid = 20;

    public HeaderRecord(String text) {
        super(text);
    }

    public HeaderRecord(RecordInputStream in) {
        super(in);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[HEADER]\n");
        buffer.append("    .header = ");
        buffer.append(getText());
        buffer.append("\n");
        buffer.append("[/HEADER]\n");
        return buffer.toString();
    }

    public short getSid() {
        return 20;
    }

    public Object clone() {
        return new HeaderRecord(getText());
    }
}
