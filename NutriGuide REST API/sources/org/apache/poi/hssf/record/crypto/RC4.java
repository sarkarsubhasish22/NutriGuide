package org.apache.poi.hssf.record.crypto;

import org.apache.poi.util.HexDump;

final class RC4 {
    private int _i;
    private int _j;
    private final byte[] _s = new byte[256];

    public RC4(byte[] key) {
        int key_length = key.length;
        for (int i = 0; i < 256; i++) {
            this._s[i] = (byte) i;
        }
        int j = 0;
        for (int i2 = 0; i2 < 256; i2++) {
            byte[] bArr = this._s;
            j = (key[i2 % key_length] + j + bArr[i2]) & 255;
            byte temp = bArr[i2];
            bArr[i2] = bArr[j];
            bArr[j] = temp;
        }
        this._i = 0;
        this._j = 0;
    }

    public byte output() {
        int i = (this._i + 1) & 255;
        this._i = i;
        int i2 = this._j;
        byte[] bArr = this._s;
        int i3 = (i2 + bArr[i]) & 255;
        this._j = i3;
        byte temp = bArr[i];
        bArr[i] = bArr[i3];
        bArr[i3] = temp;
        return bArr[(bArr[i] + bArr[i3]) & 255];
    }

    public void encrypt(byte[] in) {
        for (int i = 0; i < in.length; i++) {
            in[i] = (byte) (in[i] ^ output());
        }
    }

    public void encrypt(byte[] in, int offset, int len) {
        int end = offset + len;
        for (int i = offset; i < end; i++) {
            in[i] = (byte) (in[i] ^ output());
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append("i=");
        sb.append(this._i);
        sb.append(" j=");
        sb.append(this._j);
        sb.append("]");
        sb.append("\n");
        sb.append(HexDump.dump(this._s, 0, 0));
        return sb.toString();
    }
}
