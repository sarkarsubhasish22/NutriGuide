package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public class LittleEndian implements LittleEndianConsts {
    private LittleEndian() {
    }

    public static short getShort(byte[] data, int offset) {
        return (short) (((data[offset + 1] & 255) << 8) + ((data[offset] & 255) << 0));
    }

    public static int getUShort(byte[] data, int offset) {
        return ((data[offset + 1] & 255) << 8) + ((data[offset] & 255) << 0);
    }

    public static short getShort(byte[] data) {
        return getShort(data, 0);
    }

    public static int getUShort(byte[] data) {
        return getUShort(data, 0);
    }

    public static int getInt(byte[] data, int offset) {
        int i = offset;
        int i2 = i + 1;
        int i3 = i2 + 1;
        int i4 = i3 + 1;
        int i5 = i4 + 1;
        return ((data[i4] & 255) << 24) + ((data[i3] & 255) << 16) + ((data[i2] & 255) << 8) + ((data[i] & 255) << 0);
    }

    public static int getInt(byte[] data) {
        return getInt(data, 0);
    }

    public static long getUInt(byte[] data, int offset) {
        return -1 & ((long) getInt(data, offset));
    }

    public static long getUInt(byte[] data) {
        return getUInt(data, 0);
    }

    public static long getLong(byte[] data, int offset) {
        long result = 0;
        for (int j = (offset + 8) - 1; j >= offset; j--) {
            result = (result << 8) | ((long) (data[j] & 255));
        }
        return result;
    }

    public static double getDouble(byte[] data, int offset) {
        return Double.longBitsToDouble(getLong(data, offset));
    }

    public static void putShort(byte[] data, int offset, short value) {
        int i = offset;
        int i2 = i + 1;
        data[i] = (byte) ((value >>> 0) & 255);
        int i3 = i2 + 1;
        data[i2] = (byte) ((value >>> 8) & 255);
    }

    public static void putByte(byte[] data, int offset, int value) {
        data[offset] = (byte) value;
    }

    public static void putUShort(byte[] data, int offset, int value) {
        int i = offset;
        int i2 = i + 1;
        data[i] = (byte) ((value >>> 0) & 255);
        int i3 = i2 + 1;
        data[i2] = (byte) ((value >>> 8) & 255);
    }

    public static void putShort(byte[] data, short value) {
        putShort(data, 0, value);
    }

    public static void putInt(byte[] data, int offset, int value) {
        int i = offset;
        int i2 = i + 1;
        data[i] = (byte) ((value >>> 0) & 255);
        int i3 = i2 + 1;
        data[i2] = (byte) ((value >>> 8) & 255);
        int i4 = i3 + 1;
        data[i3] = (byte) ((value >>> 16) & 255);
        int i5 = i4 + 1;
        data[i4] = (byte) ((value >>> 24) & 255);
    }

    public static void putInt(byte[] data, int value) {
        putInt(data, 0, value);
    }

    public static void putLong(byte[] data, int offset, long value) {
        int limit = offset + 8;
        long v = value;
        for (int j = offset; j < limit; j++) {
            data[j] = (byte) ((int) (255 & v));
            v >>= 8;
        }
    }

    public static void putDouble(byte[] data, int offset, double value) {
        putLong(data, offset, Double.doubleToLongBits(value));
    }

    public static final class BufferUnderrunException extends IOException {
        BufferUnderrunException() {
            super("buffer underrun");
        }
    }

    public static short readShort(InputStream stream) throws IOException, BufferUnderrunException {
        return (short) readUShort(stream);
    }

    public static int readUShort(InputStream stream) throws IOException, BufferUnderrunException {
        int ch1 = stream.read();
        int ch2 = stream.read();
        if ((ch1 | ch2) >= 0) {
            return (ch2 << 8) + (ch1 << 0);
        }
        throw new BufferUnderrunException();
    }

    public static int readInt(InputStream stream) throws IOException, BufferUnderrunException {
        int ch1 = stream.read();
        int ch2 = stream.read();
        int ch3 = stream.read();
        int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) >= 0) {
            return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
        }
        throw new BufferUnderrunException();
    }

    public static long readLong(InputStream stream) throws IOException, BufferUnderrunException {
        int ch1 = stream.read();
        int ch2 = stream.read();
        int ch3 = stream.read();
        int ch4 = stream.read();
        int ch5 = stream.read();
        int ch6 = stream.read();
        int ch7 = stream.read();
        int ch8 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) >= 0) {
            return (((long) ch8) << 56) + (((long) ch7) << 48) + (((long) ch6) << 40) + (((long) ch5) << 32) + (((long) ch4) << 24) + ((long) (ch3 << 16)) + ((long) (ch2 << 8)) + ((long) (ch1 << 0));
        }
        throw new BufferUnderrunException();
    }

    public static int ubyteToInt(byte b) {
        return b & 255;
    }

    public static int getUnsignedByte(byte[] data, int offset) {
        return data[offset] & 255;
    }

    public static byte[] getByteArray(byte[] data, int offset, int size) {
        byte[] copy = new byte[size];
        System.arraycopy(data, offset, copy, 0, size);
        return copy;
    }
}
