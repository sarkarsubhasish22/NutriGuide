package org.apache.poi.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class HexDump {
    public static final String EOL = System.getProperty("line.separator");
    private static final char[] _hexcodes = "0123456789ABCDEF".toCharArray();
    private static final int[] _shifts = {60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0};

    private HexDump() {
    }

    public static void dump(byte[] data, long offset, OutputStream stream, int index, int length) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (data.length == 0) {
            stream.write(("No Data" + System.getProperty("line.separator")).getBytes());
            stream.flush();
        } else if (index < 0 || index >= data.length) {
            throw new ArrayIndexOutOfBoundsException("illegal index: " + index + " into array of length " + data.length);
        } else if (stream != null) {
            long display_offset = ((long) index) + offset;
            StringBuffer buffer = new StringBuffer(74);
            int data_length = Math.min(data.length, index + length);
            for (int j = index; j < data_length; j += 16) {
                int chars_read = data_length - j;
                if (chars_read > 16) {
                    chars_read = 16;
                }
                buffer.append(dump(display_offset));
                buffer.append(' ');
                for (int k = 0; k < 16; k++) {
                    if (k < chars_read) {
                        buffer.append(dump(data[k + j]));
                    } else {
                        buffer.append("  ");
                    }
                    buffer.append(' ');
                }
                for (int k2 = 0; k2 < chars_read; k2++) {
                    if (data[k2 + j] < 32 || data[k2 + j] >= Byte.MAX_VALUE) {
                        buffer.append('.');
                    } else {
                        buffer.append((char) data[k2 + j]);
                    }
                }
                buffer.append(EOL);
                stream.write(buffer.toString().getBytes());
                stream.flush();
                buffer.setLength(0);
                display_offset += (long) chars_read;
            }
        } else {
            throw new IllegalArgumentException("cannot write to nullstream");
        }
    }

    public static synchronized void dump(byte[] data, long offset, OutputStream stream, int index) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        synchronized (HexDump.class) {
            dump(data, offset, stream, index, data.length - index);
        }
    }

    public static String dump(byte[] data, long offset, int index) {
        if (index < 0 || index >= data.length) {
            throw new ArrayIndexOutOfBoundsException("illegal index: " + index + " into array of length " + data.length);
        }
        long display_offset = ((long) index) + offset;
        StringBuffer buffer = new StringBuffer(74);
        for (int j = index; j < data.length; j += 16) {
            int chars_read = data.length - j;
            if (chars_read > 16) {
                chars_read = 16;
            }
            buffer.append(dump(display_offset));
            buffer.append(' ');
            for (int k = 0; k < 16; k++) {
                if (k < chars_read) {
                    buffer.append(dump(data[k + j]));
                } else {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k2 = 0; k2 < chars_read; k2++) {
                if (data[k2 + j] < 32 || data[k2 + j] >= Byte.MAX_VALUE) {
                    buffer.append('.');
                } else {
                    buffer.append((char) data[k2 + j]);
                }
            }
            buffer.append(EOL);
            display_offset += (long) chars_read;
        }
        return buffer.toString();
    }

    private static String dump(long value) {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for (int j = 0; j < 8; j++) {
            char[] cArr = _hexcodes;
            int[] iArr = _shifts;
            buf.append(cArr[((int) (value >> iArr[(iArr.length + j) - 8])) & 15]);
        }
        return buf.toString();
    }

    private static String dump(byte value) {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for (int j = 0; j < 2; j++) {
            buf.append(_hexcodes[(value >> _shifts[j + 6]) & 15]);
        }
        return buf.toString();
    }

    public static String toHex(byte[] value) {
        StringBuffer retVal = new StringBuffer();
        retVal.append('[');
        for (int x = 0; x < value.length; x++) {
            if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        retVal.append(']');
        return retVal.toString();
    }

    public static String toHex(short[] value) {
        StringBuffer retVal = new StringBuffer();
        retVal.append('[');
        for (short hex : value) {
            retVal.append(toHex(hex));
            retVal.append(", ");
        }
        retVal.append(']');
        return retVal.toString();
    }

    public static String toHex(byte[] value, int bytesPerLine) {
        int digits = (int) Math.round((Math.log((double) value.length) / Math.log(10.0d)) + 0.5d);
        StringBuffer formatString = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            formatString.append('0');
        }
        formatString.append(": ");
        DecimalFormat format = new DecimalFormat(formatString.toString());
        StringBuffer retVal = new StringBuffer();
        retVal.append(format.format(0));
        int i2 = -1;
        for (int x = 0; x < value.length; x++) {
            i2++;
            if (i2 == bytesPerLine) {
                retVal.append(10);
                retVal.append(format.format((long) x));
                i2 = 0;
            }
            retVal.append(toHex(value[x]));
            retVal.append(", ");
        }
        return retVal.toString();
    }

    public static String toHex(short value) {
        return toHex((long) value, 4);
    }

    public static String toHex(byte value) {
        return toHex((long) value, 2);
    }

    public static String toHex(int value) {
        return toHex((long) value, 8);
    }

    public static String toHex(long value) {
        return toHex(value, 16);
    }

    private static String toHex(long value, int digits) {
        StringBuffer result = new StringBuffer(digits);
        for (int j = 0; j < digits; j++) {
            result.append(_hexcodes[(int) ((value >> _shifts[(16 - digits) + j]) & 15)]);
        }
        return result.toString();
    }

    public static void dump(InputStream in, PrintStream out, int start, int bytesToDump) throws IOException {
        int c;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if (bytesToDump != -1) {
            int c2 = bytesToDump;
            while (true) {
                int bytesRemaining = c2 - 1;
                if (c2 <= 0 || (c = in.read()) == -1) {
                    break;
                }
                buf.write(c);
                c2 = bytesRemaining;
            }
        } else {
            int c3 = in.read();
            while (c3 != -1) {
                buf.write(c3);
                c3 = in.read();
            }
        }
        byte[] data = buf.toByteArray();
        dump(data, 0, out, start, data.length);
    }

    private static char[] toHexChars(long pValue, int nBytes) {
        int charPos = (nBytes * 2) + 2;
        char[] result = new char[charPos];
        long value = pValue;
        do {
            charPos--;
            result[charPos] = _hexcodes[(int) (15 & value)];
            value >>>= 4;
        } while (charPos > 1);
        result[0] = '0';
        result[1] = 'x';
        return result;
    }

    public static char[] longToHex(long value) {
        return toHexChars(value, 8);
    }

    public static char[] intToHex(int value) {
        return toHexChars((long) value, 4);
    }

    public static char[] shortToHex(int value) {
        return toHexChars((long) value, 2);
    }

    public static char[] byteToHex(int value) {
        return toHexChars((long) value, 1);
    }

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        byte[] b = new byte[((int) file.length())];
        in.read(b);
        System.out.println(dump(b, 0, 0));
        in.close();
    }
}
