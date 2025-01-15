package org.apache.poi.util;

public final class LittleEndianByteArrayInputStream implements LittleEndianInput {
    private final byte[] _buf;
    private final int _endIndex;
    private int _readIndex;

    public LittleEndianByteArrayInputStream(byte[] buf, int startOffset, int maxReadLen) {
        this._buf = buf;
        this._readIndex = startOffset;
        this._endIndex = startOffset + maxReadLen;
    }

    public LittleEndianByteArrayInputStream(byte[] buf, int startOffset) {
        this(buf, startOffset, buf.length - startOffset);
    }

    public LittleEndianByteArrayInputStream(byte[] buf) {
        this(buf, 0, buf.length);
    }

    public int available() {
        return this._endIndex - this._readIndex;
    }

    private void checkPosition(int i) {
        if (i > this._endIndex - this._readIndex) {
            throw new RuntimeException("Buffer overrun");
        }
    }

    public int getReadIndex() {
        return this._readIndex;
    }

    public byte readByte() {
        checkPosition(1);
        byte[] bArr = this._buf;
        int i = this._readIndex;
        this._readIndex = i + 1;
        return bArr[i];
    }

    public int readInt() {
        checkPosition(4);
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        int i3 = i2 + 1;
        int i4 = i3 + 1;
        this._readIndex = i4 + 1;
        return ((bArr[i4] & 255) << 24) + ((bArr[i3] & 255) << 16) + ((bArr[i2] & 255) << 8) + ((bArr[i] & 255) << 0);
    }

    public long readLong() {
        checkPosition(8);
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

    public short readShort() {
        return (short) readUShort();
    }

    public int readUByte() {
        checkPosition(1);
        byte[] bArr = this._buf;
        int i = this._readIndex;
        this._readIndex = i + 1;
        return bArr[i] & 255;
    }

    public int readUShort() {
        checkPosition(2);
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        this._readIndex = i2 + 1;
        return ((bArr[i2] & 255) << 8) + ((bArr[i] & 255) << 0);
    }

    public void readFully(byte[] buf, int off, int len) {
        checkPosition(len);
        System.arraycopy(this._buf, this._readIndex, buf, off, len);
        this._readIndex += len;
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }
}
