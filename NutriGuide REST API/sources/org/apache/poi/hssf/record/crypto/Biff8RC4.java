package org.apache.poi.hssf.record.crypto;

final class Biff8RC4 {
    private static final int RC4_REKEYING_INTERVAL = 1024;
    private int _currentKeyIndex;
    private final Biff8EncryptionKey _key;
    private int _nextRC4BlockStart;
    private RC4 _rc4;
    private boolean _shouldSkipEncryptionOnCurrentRecord;
    private int _streamPos;

    public Biff8RC4(int initialOffset, Biff8EncryptionKey key) {
        if (initialOffset < 1024) {
            this._key = key;
            this._streamPos = 0;
            rekeyForNextBlock();
            this._streamPos = initialOffset;
            for (int i = initialOffset; i > 0; i--) {
                this._rc4.output();
            }
            this._shouldSkipEncryptionOnCurrentRecord = false;
            return;
        }
        throw new RuntimeException("initialOffset (" + initialOffset + ")>" + 1024 + " not supported yet");
    }

    private void rekeyForNextBlock() {
        int i = this._streamPos / 1024;
        this._currentKeyIndex = i;
        this._rc4 = this._key.createRC4(i);
        this._nextRC4BlockStart = (this._currentKeyIndex + 1) * 1024;
    }

    private int getNextRC4Byte() {
        if (this._streamPos >= this._nextRC4BlockStart) {
            rekeyForNextBlock();
        }
        byte mask = this._rc4.output();
        this._streamPos++;
        if (this._shouldSkipEncryptionOnCurrentRecord) {
            return 0;
        }
        return mask & 255;
    }

    public void startRecord(int currentSid) {
        this._shouldSkipEncryptionOnCurrentRecord = isNeverEncryptedRecord(currentSid);
    }

    private static boolean isNeverEncryptedRecord(int sid) {
        if (sid == 47 || sid == 225 || sid == 2057) {
            return true;
        }
        return false;
    }

    public void skipTwoBytes() {
        getNextRC4Byte();
        getNextRC4Byte();
    }

    public void xor(byte[] buf, int pOffset, int pLen) {
        int nLeftInBlock = this._nextRC4BlockStart - this._streamPos;
        if (pLen <= nLeftInBlock) {
            this._rc4.encrypt(buf, pOffset, pLen);
            this._streamPos += pLen;
            return;
        }
        int offset = pOffset;
        int len = pLen;
        if (len > nLeftInBlock) {
            if (nLeftInBlock > 0) {
                this._rc4.encrypt(buf, offset, nLeftInBlock);
                this._streamPos += nLeftInBlock;
                offset += nLeftInBlock;
                len -= nLeftInBlock;
            }
            rekeyForNextBlock();
        }
        while (len > 1024) {
            this._rc4.encrypt(buf, offset, 1024);
            this._streamPos += 1024;
            offset += 1024;
            len -= 1024;
            rekeyForNextBlock();
        }
        this._rc4.encrypt(buf, offset, len);
        this._streamPos += len;
    }

    public int xorByte(int rawVal) {
        return (byte) (rawVal ^ getNextRC4Byte());
    }

    public int xorShort(int rawVal) {
        return rawVal ^ ((getNextRC4Byte() << 8) + (getNextRC4Byte() << 0));
    }

    public int xorInt(int rawVal) {
        int b0 = getNextRC4Byte();
        int b1 = getNextRC4Byte();
        return rawVal ^ ((((getNextRC4Byte() << 24) + (getNextRC4Byte() << 16)) + (b1 << 8)) + (b0 << 0));
    }

    public long xorLong(long rawVal) {
        int b0 = getNextRC4Byte();
        int b1 = getNextRC4Byte();
        int b2 = getNextRC4Byte();
        int b3 = getNextRC4Byte();
        int b4 = getNextRC4Byte();
        int b5 = getNextRC4Byte();
        return rawVal ^ ((((((((((long) getNextRC4Byte()) << 56) + (((long) getNextRC4Byte()) << 48)) + (((long) b5) << 40)) + (((long) b4) << 32)) + (((long) b3) << 24)) + ((long) (b2 << 16))) + ((long) (b1 << 8))) + ((long) (b0 << 0)));
    }
}
