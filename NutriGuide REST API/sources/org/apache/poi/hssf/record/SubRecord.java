package org.apache.poi.hssf.record;

import java.io.ByteArrayOutputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianOutputStream;

public abstract class SubRecord {
    public abstract Object clone();

    /* access modifiers changed from: protected */
    public abstract int getDataSize();

    public abstract void serialize(LittleEndianOutput littleEndianOutput);

    protected SubRecord() {
    }

    public static SubRecord createSubRecord(LittleEndianInput in, int cmoOt) {
        int sid = in.readUShort();
        int secondUShort = in.readUShort();
        if (sid == 0) {
            return new EndSubRecord(in, secondUShort);
        }
        if (sid == 6) {
            return new GroupMarkerSubRecord(in, secondUShort);
        }
        if (sid == 9) {
            return new EmbeddedObjectRefSubRecord(in, secondUShort);
        }
        if (sid == 19) {
            return new LbsDataSubRecord(in, secondUShort, cmoOt);
        }
        if (sid == 21) {
            return new CommonObjectDataSubRecord(in, secondUShort);
        }
        if (sid != 12) {
            return sid != 13 ? new UnknownSubRecord(in, sid, secondUShort) : new NoteStructureSubRecord(in, secondUShort);
        }
        return new FtCblsSubRecord(in, secondUShort);
    }

    public byte[] serialize() {
        int size = getDataSize() + 4;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        serialize(new LittleEndianOutputStream(baos));
        if (baos.size() == size) {
            return baos.toByteArray();
        }
        throw new RuntimeException("write size mismatch");
    }

    public boolean isTerminating() {
        return false;
    }

    private static final class UnknownSubRecord extends SubRecord {
        private final byte[] _data;
        private final int _sid;

        public UnknownSubRecord(LittleEndianInput in, int sid, int size) {
            this._sid = sid;
            byte[] buf = new byte[size];
            in.readFully(buf);
            this._data = buf;
        }

        /* access modifiers changed from: protected */
        public int getDataSize() {
            return this._data.length;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._sid);
            out.writeShort(this._data.length);
            out.write(this._data);
        }

        public Object clone() {
            return this;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append("sid=");
            sb.append(HexDump.shortToHex(this._sid));
            sb.append(" size=");
            sb.append(this._data.length);
            sb.append(" : ");
            sb.append(HexDump.toHex(this._data));
            sb.append("]\n");
            return sb.toString();
        }
    }
}
