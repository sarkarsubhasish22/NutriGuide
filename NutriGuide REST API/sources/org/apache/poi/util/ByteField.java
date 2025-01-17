package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import org.apache.poi.util.LittleEndian;

public class ByteField implements FixedField {
    private static final byte _default_value = 0;
    private final int _offset;
    private byte _value;

    public ByteField(int offset) throws ArrayIndexOutOfBoundsException {
        this(offset, (byte) 0);
    }

    public ByteField(int offset, byte value) throws ArrayIndexOutOfBoundsException {
        if (offset >= 0) {
            this._offset = offset;
            set(value);
            return;
        }
        throw new ArrayIndexOutOfBoundsException("offset cannot be negative");
    }

    public ByteField(int offset, byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }

    public ByteField(int offset, byte value, byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset, value);
        writeToBytes(data);
    }

    public byte get() {
        return this._value;
    }

    public void set(byte value) {
        this._value = value;
    }

    public void set(byte value, byte[] data) throws ArrayIndexOutOfBoundsException {
        set(value);
        writeToBytes(data);
    }

    public void readFromBytes(byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = data[this._offset];
    }

    public void readFromStream(InputStream stream) throws IOException, LittleEndian.BufferUnderrunException {
        int ib = stream.read();
        if (ib >= 0) {
            this._value = (byte) ib;
            return;
        }
        throw new BufferUnderflowException();
    }

    public void writeToBytes(byte[] data) throws ArrayIndexOutOfBoundsException {
        data[this._offset] = this._value;
    }

    public String toString() {
        return String.valueOf(this._value);
    }
}
