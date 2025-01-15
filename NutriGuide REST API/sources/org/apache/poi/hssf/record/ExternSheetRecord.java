package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.LittleEndianOutput;

public class ExternSheetRecord extends StandardRecord {
    public static final short sid = 23;
    private List<RefSubRecord> _list = new ArrayList();

    private static final class RefSubRecord {
        public static final int ENCODED_SIZE = 6;
        private int _extBookIndex;
        private int _firstSheetIndex;
        private int _lastSheetIndex;

        public RefSubRecord(int extBookIndex, int firstSheetIndex, int lastSheetIndex) {
            this._extBookIndex = extBookIndex;
            this._firstSheetIndex = firstSheetIndex;
            this._lastSheetIndex = lastSheetIndex;
        }

        public RefSubRecord(RecordInputStream in) {
            this(in.readShort(), in.readShort(), in.readShort());
        }

        public int getExtBookIndex() {
            return this._extBookIndex;
        }

        public int getFirstSheetIndex() {
            return this._firstSheetIndex;
        }

        public int getLastSheetIndex() {
            return this._lastSheetIndex;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("extBook=");
            buffer.append(this._extBookIndex);
            buffer.append(" firstSheet=");
            buffer.append(this._firstSheetIndex);
            buffer.append(" lastSheet=");
            buffer.append(this._lastSheetIndex);
            return buffer.toString();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._extBookIndex);
            out.writeShort(this._firstSheetIndex);
            out.writeShort(this._lastSheetIndex);
        }
    }

    public ExternSheetRecord() {
    }

    public ExternSheetRecord(RecordInputStream in) {
        int nItems = in.readShort();
        for (int i = 0; i < nItems; i++) {
            this._list.add(new RefSubRecord(in));
        }
    }

    public int getNumOfRefs() {
        return this._list.size();
    }

    public void addREFRecord(RefSubRecord rec) {
        this._list.add(rec);
    }

    public int getNumOfREFRecords() {
        return this._list.size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        int nItems = this._list.size();
        sb.append("[EXTERNSHEET]\n");
        sb.append("   numOfRefs     = ");
        sb.append(nItems);
        sb.append("\n");
        for (int i = 0; i < nItems; i++) {
            sb.append("refrec         #");
            sb.append(i);
            sb.append(": ");
            sb.append(getRef(i).toString());
            sb.append(10);
        }
        sb.append("[/EXTERNSHEET]\n");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this._list.size() * 6) + 2;
    }

    public void serialize(LittleEndianOutput out) {
        int nItems = this._list.size();
        out.writeShort(nItems);
        for (int i = 0; i < nItems; i++) {
            getRef(i).serialize(out);
        }
    }

    private RefSubRecord getRef(int i) {
        return this._list.get(i);
    }

    public short getSid() {
        return 23;
    }

    public int getExtbookIndexFromRefIndex(int refIndex) {
        return getRef(refIndex).getExtBookIndex();
    }

    public int findRefIndexFromExtBookIndex(int extBookIndex) {
        int nItems = this._list.size();
        for (int i = 0; i < nItems; i++) {
            if (getRef(i).getExtBookIndex() == extBookIndex) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstSheetIndexFromRefIndex(int extRefIndex) {
        return getRef(extRefIndex).getFirstSheetIndex();
    }

    public int addRef(int extBookIndex, int firstSheetIndex, int lastSheetIndex) {
        this._list.add(new RefSubRecord(extBookIndex, firstSheetIndex, lastSheetIndex));
        return this._list.size() - 1;
    }

    public int getRefIxForSheet(int externalBookIndex, int sheetIndex) {
        int nItems = this._list.size();
        for (int i = 0; i < nItems; i++) {
            RefSubRecord ref = getRef(i);
            if (ref.getExtBookIndex() == externalBookIndex && ref.getFirstSheetIndex() == sheetIndex && ref.getLastSheetIndex() == sheetIndex) {
                return i;
            }
        }
        return -1;
    }

    public static ExternSheetRecord combine(ExternSheetRecord[] esrs) {
        ExternSheetRecord result = new ExternSheetRecord();
        for (ExternSheetRecord esr : esrs) {
            int nRefs = esr.getNumOfREFRecords();
            for (int j = 0; j < nRefs; j++) {
                result.addREFRecord(esr.getRef(j));
            }
        }
        return result;
    }
}
