package org.apache.poi.hssf.record;

import java.util.Iterator;
import org.apache.poi.hssf.record.PageBreakRecord;

public final class VerticalPageBreakRecord extends PageBreakRecord {
    public static final short sid = 26;

    public VerticalPageBreakRecord() {
    }

    public VerticalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    public short getSid() {
        return 26;
    }

    public Object clone() {
        PageBreakRecord result = new VerticalPageBreakRecord();
        Iterator iterator = getBreaksIterator();
        while (iterator.hasNext()) {
            PageBreakRecord.Break original = iterator.next();
            result.addBreak(original.main, original.subFrom, original.subTo);
        }
        return result;
    }
}
