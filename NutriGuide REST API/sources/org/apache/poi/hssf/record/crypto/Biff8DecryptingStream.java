package org.apache.poi.hssf.record.crypto;

import java.io.InputStream;
import org.apache.poi.hssf.record.BiffHeaderInput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianInputStream;

public final class Biff8DecryptingStream implements BiffHeaderInput, LittleEndianInput {
    private final LittleEndianInput _le;
    private final Biff8RC4 _rc4;

    public Biff8DecryptingStream(InputStream in, int initialOffset, Biff8EncryptionKey key) {
        this._rc4 = new Biff8RC4(initialOffset, key);
        if (in instanceof LittleEndianInput) {
            this._le = (LittleEndianInput) in;
        } else {
            this._le = new LittleEndianInputStream(in);
        }
    }

    public int available() {
        return this._le.available();
    }

    public int readRecordSID() {
        int sid = this._le.readUShort();
        this._rc4.skipTwoBytes();
        this._rc4.startRecord(sid);
        return sid;
    }

    public int readDataSize() {
        int dataSize = this._le.readUShort();
        this._rc4.skipTwoBytes();
        return dataSize;
    }

    public double readDouble() {
        double result = Double.longBitsToDouble(readLong());
        if (!Double.isNaN(result)) {
            return result;
        }
        throw new RuntimeException("Did not expect to read NaN");
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) {
        this._le.readFully(buf, off, len);
        this._rc4.xor(buf, off, len);
    }

    public int readUByte() {
        return this._rc4.xorByte(this._le.readUByte());
    }

    public byte readByte() {
        return (byte) this._rc4.xorByte(this._le.readUByte());
    }

    public int readUShort() {
        return this._rc4.xorShort(this._le.readUShort());
    }

    public short readShort() {
        return (short) this._rc4.xorShort(this._le.readUShort());
    }

    public int readInt() {
        return this._rc4.xorInt(this._le.readInt());
    }

    public long readLong() {
        return this._rc4.xorLong(this._le.readLong());
    }
}
