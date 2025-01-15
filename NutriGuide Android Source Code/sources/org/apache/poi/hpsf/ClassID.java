package org.apache.poi.hpsf;

import org.apache.poi.util.HexDump;

public class ClassID {
    public static final int LENGTH = 16;
    protected byte[] bytes;

    public ClassID(byte[] src, int offset) {
        read(src, offset);
    }

    public ClassID() {
        this.bytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            this.bytes[i] = 0;
        }
    }

    public int length() {
        return 16;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes2) {
        int i = 0;
        while (true) {
            byte[] bArr = this.bytes;
            if (i < bArr.length) {
                bArr[i] = bytes2[i];
                i++;
            } else {
                return;
            }
        }
    }

    public byte[] read(byte[] src, int offset) {
        byte[] bArr = new byte[16];
        this.bytes = bArr;
        bArr[0] = src[offset + 3];
        bArr[1] = src[offset + 2];
        bArr[2] = src[offset + 1];
        bArr[3] = src[offset + 0];
        bArr[4] = src[offset + 5];
        bArr[5] = src[offset + 4];
        bArr[6] = src[offset + 7];
        bArr[7] = src[offset + 6];
        for (int i = 8; i < 16; i++) {
            this.bytes[i] = src[i + offset];
        }
        return this.bytes;
    }

    public void write(byte[] dst, int offset) throws ArrayStoreException {
        if (dst.length >= 16) {
            byte[] bArr = this.bytes;
            dst[offset + 0] = bArr[3];
            dst[offset + 1] = bArr[2];
            dst[offset + 2] = bArr[1];
            dst[offset + 3] = bArr[0];
            dst[offset + 4] = bArr[5];
            dst[offset + 5] = bArr[4];
            dst[offset + 6] = bArr[7];
            dst[offset + 7] = bArr[6];
            for (int i = 8; i < 16; i++) {
                dst[i + offset] = this.bytes[i];
            }
            return;
        }
        throw new ArrayStoreException("Destination byte[] must have room for at least 16 bytes, but has a length of only " + dst.length + ".");
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ClassID)) {
            return false;
        }
        ClassID cid = (ClassID) o;
        if (this.bytes.length != cid.bytes.length) {
            return false;
        }
        int i = 0;
        while (true) {
            byte[] bArr = this.bytes;
            if (i >= bArr.length) {
                return true;
            }
            if (bArr[i] != cid.bytes[i]) {
                return false;
            }
            i++;
        }
    }

    public int hashCode() {
        return new String(this.bytes).hashCode();
    }

    public String toString() {
        StringBuffer sbClassId = new StringBuffer(38);
        sbClassId.append('{');
        for (int i = 0; i < 16; i++) {
            sbClassId.append(HexDump.toHex(this.bytes[i]));
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                sbClassId.append('-');
            }
        }
        sbClassId.append('}');
        return sbClassId.toString();
    }
}
