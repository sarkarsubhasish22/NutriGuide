package org.apache.poi.hssf.record;

import java.util.Iterator;
import org.apache.poi.hssf.record.PageBreakRecord;

public final class HorizontalPageBreakRecord extends PageBreakRecord {
    public static final short sid = 27;

    public HorizontalPageBreakRecord() {
    }

    public HorizontalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    public short getSid() {
        return 27;
    }

    public Object clone() {
        PageBreakRecord result = new HorizontalPageBreakRecord();
        Iterator iterator = getBreaksIterator();
        while (iterator.hasNext()) {
            PageBreakRecord.Break original = iterator.next();
            result.addBreak(original.main, original.subFrom, original.subTo);
        }
        return result;
    }
}
