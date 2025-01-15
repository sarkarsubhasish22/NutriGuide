package org.apache.poi.hssf.record;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.poi.hssf.record.crypto.Biff8DecryptingStream;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianInputStream;

public final class RecordInputStream implements LittleEndianInput {
    private static final int DATA_LEN_NEEDS_TO_BE_READ = -1;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int INVALID_SID_VALUE = -1;
    public static final short MAX_RECORD_DATA_SIZE = 8224;
    private final BiffHeaderInput _bhi;
    private int _currentDataLength;
    private int _currentDataOffset;
    private int _currentSid;
    private final LittleEndianInput _dataInput;
    private int _nextSid;

    public static final class LeftoverDataException extends RuntimeException {
        public LeftoverDataException(int sid, int remainingByteCount) {
            super("Initialisation of record 0x" + Integer.toHexString(sid).toUpperCase() + " left " + remainingByteCount + " bytes remaining still to be read.");
        }
    }

    private static final class SimpleHeaderInput implements BiffHeaderInput {
        private final LittleEndianInput _lei;

        public SimpleHeaderInput(InputStream in) {
            this._lei = RecordInputStream.getLEI(in);
        }

        public int available() {
            return this._lei.available();
        }

        public int readDataSize() {
            return this._lei.readUShort();
        }

        public int readRecordSID() {
            return this._lei.readUShort();
        }
    }

    public RecordInputStream(InputStream in) throws RecordFormatException {
        this(in, (Biff8EncryptionKey) null, 0);
    }

    public RecordInputStream(InputStream in, Biff8EncryptionKey key, int initialOffset) throws RecordFormatException {
        if (key == null) {
            this._dataInput = getLEI(in);
            this._bhi = new SimpleHeaderInput(in);
        } else {
            Biff8DecryptingStream bds = new Biff8DecryptingStream(in, initialOffset, key);
            this._bhi = bds;
            this._dataInput = bds;
        }
        this._nextSid = readNextSid();
    }

    static LittleEndianInput getLEI(InputStream is) {
        if (is instanceof LittleEndianInput) {
            return (LittleEndianInput) is;
        }
        return new LittleEndianInputStream(is);
    }

    public int available() {
        return remaining();
    }

    public int read(byte[] b, int off, int len) {
        int limit = Math.min(len, remaining());
        if (limit == 0) {
            return 0;
        }
        readFully(b, off, limit);
        return limit;
    }

    public short getSid() {
        return (short) this._currentSid;
    }

    public boolean hasNextRecord() throws LeftoverDataException {
        int i = this._currentDataLength;
        if (i == -1 || i == this._currentDataOffset) {
            if (i != -1) {
                this._nextSid = readNextSid();
            }
            return this._nextSid != -1;
        }
        throw new LeftoverDataException(this._currentSid, remaining());
    }

    private int readNextSid() {
        if (this._bhi.available() < 4) {
            return -1;
        }
        int result = this._bhi.readRecordSID();
        if (result != -1) {
            this._currentDataLength = -1;
            return result;
        }
        throw new RecordFormatException("Found invalid sid (" + result + ")");
    }

    public void nextRecord() throws RecordFormatException {
        int i = this._nextSid;
        if (i == -1) {
            throw new IllegalStateException("EOF - next record not available");
        } else if (this._currentDataLength == -1) {
            this._currentSid = i;
            this._currentDataOffset = 0;
            int readDataSize = this._bhi.readDataSize();
            this._currentDataLength = readDataSize;
            if (readDataSize > 8224) {
                throw new RecordFormatException("The content of an excel record cannot exceed 8224 bytes");
            }
        } else {
            throw new IllegalStateException("Cannot call nextRecord() without checking hasNextRecord() first");
        }
    }

    private void checkRecordPosition(int requiredByteCount) {
        int nAvailable = remaining();
        if (nAvailable < requiredByteCount) {
            if (nAvailable != 0 || !isContinueNext()) {
                throw new RecordFormatException("Not enough data (" + nAvailable + ") to read requested (" + requiredByteCount + ") bytes");
            }
            nextRecord();
        }
    }

    public byte readByte() {
        checkRecordPosition(1);
        this._currentDataOffset++;
        return this._dataInput.readByte();
    }

    public short readShort() {
        checkRecordPosition(2);
        this._currentDataOffset += 2;
        return this._dataInput.readShort();
    }

    public int readInt() {
        checkRecordPosition(4);
        this._currentDataOffset += 4;
        return this._dataInput.readInt();
    }

    public long readLong() {
        checkRecordPosition(8);
        this._currentDataOffset += 8;
        return this._dataInput.readLong();
    }

    public int readUByte() {
        return readByte() & 255;
    }

    public int readUShort() {
        checkRecordPosition(2);
        this._currentDataOffset += 2;
        return this._dataInput.readUShort();
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
        checkRecordPosition(len);
        this._dataInput.readFully(buf, off, len);
        this._currentDataOffset += len;
    }

    public String readString() {
        return readStringCommon(readUShort(), readByte() == 0);
    }

    public String readUnicodeLEString(int requestedLength) {
        return readStringCommon(requestedLength, false);
    }

    public String readCompressedUnicode(int requestedLength) {
        return readStringCommon(requestedLength, true);
    }

    private String readStringCommon(int requestedLength, boolean pIsCompressedEncoding) {
        int i;
        int i2;
        if (requestedLength < 0 || requestedLength > 1048576) {
            throw new IllegalArgumentException("Bad requested string length (" + requestedLength + ")");
        }
        char[] buf = new char[requestedLength];
        boolean isCompressedEncoding = pIsCompressedEncoding;
        int curLen = 0;
        while (true) {
            int availableChars = remaining();
            if (!isCompressedEncoding) {
                availableChars /= 2;
            }
            if (requestedLength - curLen <= availableChars) {
                while (curLen < requestedLength) {
                    if (isCompressedEncoding) {
                        i = readUByte();
                    } else {
                        i = readShort();
                    }
                    buf[curLen] = (char) i;
                    curLen++;
                }
                return new String(buf);
            }
            while (availableChars > 0) {
                if (isCompressedEncoding) {
                    i2 = readUByte();
                } else {
                    i2 = readShort();
                }
                buf[curLen] = (char) i2;
                curLen++;
                availableChars--;
            }
            if (!isContinueNext()) {
                throw new RecordFormatException("Expected to find a ContinueRecord in order to read remaining " + (requestedLength - curLen) + " of " + requestedLength + " chars");
            } else if (remaining() == 0) {
                nextRecord();
                isCompressedEncoding = readByte() == 0;
            } else {
                throw new RecordFormatException("Odd number of bytes(" + remaining() + ") left behind");
            }
        }
    }

    public byte[] readRemainder() {
        int size = remaining();
        if (size == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        readFully(result);
        return result;
    }

    public byte[] readAllContinuedRemainder() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(16448);
        while (true) {
            byte[] b = readRemainder();
            out.write(b, 0, b.length);
            if (!isContinueNext()) {
                return out.toByteArray();
            }
            nextRecord();
        }
    }

    public int remaining() {
        int i = this._currentDataLength;
        if (i == -1) {
            return 0;
        }
        return i - this._currentDataOffset;
    }

    private boolean isContinueNext() {
        int i = this._currentDataLength;
        if (i != -1 && this._currentDataOffset != i) {
            throw new IllegalStateException("Should never be called before end of current record");
        } else if (hasNextRecord() && this._nextSid == 60) {
            return true;
        } else {
            return false;
        }
    }

    public int getNextSid() {
        return this._nextSid;
    }
}
