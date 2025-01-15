package org.apache.poi.hssf.record;

public final class UnicodeString extends org.apache.poi.hssf.record.common.UnicodeString {
    public UnicodeString(RecordInputStream in) {
        super(in);
    }

    public UnicodeString(String str) {
        super(str);
    }
}
