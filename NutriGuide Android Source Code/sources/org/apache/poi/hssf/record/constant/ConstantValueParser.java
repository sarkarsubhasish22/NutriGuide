package org.apache.poi.hssf.record.constant;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class ConstantValueParser {
    private static final Object EMPTY_REPRESENTATION = null;
    private static final int FALSE_ENCODING = 0;
    private static final int TRUE_ENCODING = 1;
    private static final int TYPE_BOOLEAN = 4;
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_ERROR_CODE = 16;
    private static final int TYPE_NUMBER = 1;
    private static final int TYPE_STRING = 2;

    private ConstantValueParser() {
    }

    public static Object[] parse(LittleEndianInput in, int nValues) {
        Object[] result = new Object[nValues];
        for (int i = 0; i < result.length; i++) {
            result[i] = readAConstantValue(in);
        }
        return result;
    }

    private static Object readAConstantValue(LittleEndianInput in) {
        byte grbit = in.readByte();
        if (grbit == 0) {
            in.readLong();
            return EMPTY_REPRESENTATION;
        } else if (grbit == 1) {
            return new Double(in.readDouble());
        } else {
            if (grbit == 2) {
                return StringUtil.readUnicodeString(in);
            }
            if (grbit == 4) {
                return readBoolean(in);
            }
            if (grbit == 16) {
                int errCode = in.readUShort();
                in.readUShort();
                in.readInt();
                return ErrorConstant.valueOf(errCode);
            }
            throw new RuntimeException("Unknown grbit value (" + grbit + ")");
        }
    }

    private static Object readBoolean(LittleEndianInput in) {
        byte val = (byte) ((int) in.readLong());
        if (val == 0) {
            return Boolean.FALSE;
        }
        if (val == 1) {
            return Boolean.TRUE;
        }
        throw new RuntimeException("unexpected boolean encoding (" + val + ")");
    }

    public static int getEncodedSize(Object[] values) {
        int result = values.length * 1;
        for (Object encodedSize : values) {
            result += getEncodedSize(encodedSize);
        }
        return result;
    }

    private static int getEncodedSize(Object object) {
        Class cls;
        if (object == EMPTY_REPRESENTATION || (cls = object.getClass()) == Boolean.class || cls == Double.class || cls == ErrorConstant.class) {
            return 8;
        }
        return StringUtil.getEncodedSize((String) object);
    }

    public static void encode(LittleEndianOutput out, Object[] values) {
        for (Object encodeSingleValue : values) {
            encodeSingleValue(out, encodeSingleValue);
        }
    }

    private static void encodeSingleValue(LittleEndianOutput out, Object value) {
        long longVal = 0;
        if (value == EMPTY_REPRESENTATION) {
            out.writeByte(0);
            out.writeLong(0);
        } else if (value instanceof Boolean) {
            out.writeByte(4);
            if (((Boolean) value).booleanValue()) {
                longVal = 1;
            }
            out.writeLong(longVal);
        } else if (value instanceof Double) {
            out.writeByte(1);
            out.writeDouble(((Double) value).doubleValue());
        } else if (value instanceof String) {
            out.writeByte(2);
            StringUtil.writeUnicodeString(out, (String) value);
        } else if (value instanceof ErrorConstant) {
            out.writeByte(16);
            out.writeLong((long) ((ErrorConstant) value).getErrorCode());
        } else {
            throw new IllegalStateException("Unexpected value type (" + value.getClass().getName() + "'");
        }
    }
}
