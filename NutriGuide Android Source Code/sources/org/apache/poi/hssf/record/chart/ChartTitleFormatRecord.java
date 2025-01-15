package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.LittleEndianOutput;

public class ChartTitleFormatRecord extends StandardRecord {
    public static final short sid = 4176;
    private CTFormat[] _formats;

    private static final class CTFormat {
        public static final int ENCODED_SIZE = 4;
        private int _fontIndex;
        private int _offset;

        protected CTFormat(short offset, short fontIdx) {
            this._offset = offset;
            this._fontIndex = fontIdx;
        }

        public CTFormat(RecordInputStream in) {
            this._offset = in.readShort();
            this._fontIndex = in.readShort();
        }

        public int getOffset() {
            return this._offset;
        }

        public void setOffset(int newOff) {
            this._offset = newOff;
        }

        public int getFontIndex() {
            return this._fontIndex;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._offset);
            out.writeShort(this._fontIndex);
        }
    }

    public ChartTitleFormatRecord(RecordInputStream in) {
        int nRecs = in.readUShort();
        this._formats = new CTFormat[nRecs];
        for (int i = 0; i < nRecs; i++) {
            this._formats[i] = new CTFormat(in);
        }
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._formats.length);
        int i = 0;
        while (true) {
            CTFormat[] cTFormatArr = this._formats;
            if (i < cTFormatArr.length) {
                cTFormatArr[i].serialize(out);
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this._formats.length * 4) + 2;
    }

    public short getSid() {
        return sid;
    }

    public int getFormatCount() {
        return this._formats.length;
    }

    public void modifyFormatRun(short oldPos, short newLen) {
        int shift = 0;
        int i = 0;
        while (true) {
            CTFormat[] cTFormatArr = this._formats;
            if (i < cTFormatArr.length) {
                CTFormat ctf = cTFormatArr[i];
                if (shift != 0) {
                    ctf.setOffset(ctf.getOffset() + shift);
                } else if (oldPos == ctf.getOffset()) {
                    CTFormat[] cTFormatArr2 = this._formats;
                    if (i < cTFormatArr2.length - 1) {
                        shift = newLen - (cTFormatArr2[i + 1].getOffset() - ctf.getOffset());
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CHARTTITLEFORMAT]\n");
        buffer.append("    .format_runs       = ");
        buffer.append(this._formats.length);
        buffer.append("\n");
        int i = 0;
        while (true) {
            CTFormat[] cTFormatArr = this._formats;
            if (i < cTFormatArr.length) {
                CTFormat ctf = cTFormatArr[i];
                buffer.append("       .char_offset= ");
                buffer.append(ctf.getOffset());
                buffer.append(",.fontidx= ");
                buffer.append(ctf.getFontIndex());
                buffer.append("\n");
                i++;
            } else {
                buffer.append("[/CHARTTITLEFORMAT]\n");
                return buffer.toString();
            }
        }
    }
}
