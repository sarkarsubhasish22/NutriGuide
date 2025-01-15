package org.apache.poi.poifs.storage;

public final class DataInputBlock {
    private final byte[] _buf;
    private int _maxIndex;
    private int _readIndex;

    DataInputBlock(byte[] data, int startOffset) {
        this._buf = data;
        this._readIndex = startOffset;
        this._maxIndex = data.length;
    }

    public int available() {
        return this._maxIndex - this._readIndex;
    }

    public int readUByte() {
        byte[] bArr = this._buf;
        int i = this._readIndex;
        this._readIndex = i + 1;
        return bArr[i] & 255;
    }

    public int readUShortLE() {
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        this._readIndex = i2 + 1;
        return ((bArr[i2] & 255) << 8) + ((bArr[i] & 255) << 0);
    }

    public int readUShortLE(DataInputBlock prevBlock) {
        byte[] bArr = prevBlock._buf;
        int i = bArr.length - 1;
        int i2 = i + 1;
        byte[] bArr2 = this._buf;
        int i3 = this._readIndex;
        this._readIndex = i3 + 1;
        return ((bArr2[i3] & 255) << 8) + ((bArr[i] & 255) << 0);
    }

    public int readIntLE() {
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        int i3 = i2 + 1;
        int i4 = i3 + 1;
        this._readIndex = i4 + 1;
        return ((bArr[i4] & 255) << 24) + ((bArr[i3] & 255) << 16) + ((bArr[i2] & 255) << 8) + ((bArr[i] & 255) << 0);
    }

    public int readIntLE(DataInputBlock prevBlock, int prevBlockAvailable) {
        byte[] buf = new byte[4];
        readSpanning(prevBlock, prevBlockAvailable, buf);
        return ((buf[3] & 255) << 24) + ((buf[2] & 255) << 16) + ((buf[1] & 255) << 8) + ((buf[0] & 255) << 0);
    }

    public long readLongLE() {
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        int i3 = i2 + 1;
        int i4 = i3 + 1;
        int i5 = i4 + 1;
        int i6 = i5 + 1;
        int i7 = i6 + 1;
        int i8 = i7 + 1;
        this._readIndex = i8 + 1;
        return (((long) (bArr[i8] & 255)) << 56) + (((long) (bArr[i7] & 255)) << 48) + (((long) (bArr[i6] & 255)) << 40) + (((long) (bArr[i5] & 255)) << 32) + (((long) (bArr[i4] & 255)) << 24) + ((long) ((bArr[i3] & 255) << 16)) + ((long) ((bArr[i2] & 255) << 8)) + ((long) ((bArr[i] & 255) << 0));
    }

    public long readLongLE(DataInputBlock prevBlock, int prevBlockAvailable) {
        byte[] buf = new byte[8];
        readSpanning(prevBlock, prevBlockAvailable, buf);
        return (((long) (buf[7] & 255)) << 56) + (((long) (buf[6] & 255)) << 48) + (((long) (buf[5] & 255)) << 40) + (((long) (buf[4] & 255)) << 32) + (((long) (buf[3] & 255)) << 24) + ((long) ((buf[2] & 255) << 16)) + ((long) ((buf[1] & 255) << 8)) + ((long) ((buf[0] & 255) << 0));
    }

    private void readSpanning(DataInputBlock prevBlock, int prevBlockAvailable, byte[] buf) {
        System.arraycopy(prevBlock._buf, prevBlock._readIndex, buf, 0, prevBlockAvailable);
        int secondReadLen = buf.length - prevBlockAvailable;
        System.arraycopy(this._buf, 0, buf, prevBlockAvailable, secondReadLen);
        this._readIndex = secondReadLen;
    }

    public void readFully(byte[] buf, int off, int len) {
        System.arraycopy(this._buf, this._readIndex, buf, off, len);
        this._readIndex += len;
    }
}
