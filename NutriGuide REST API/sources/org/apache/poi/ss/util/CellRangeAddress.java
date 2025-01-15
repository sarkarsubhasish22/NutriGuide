package org.apache.poi.ss.util;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;

public class CellRangeAddress extends CellRangeAddressBase {
    public static final int ENCODED_SIZE = 8;

    public CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }

    public int serialize(int offset, byte[] data) {
        serialize(new LittleEndianByteArrayOutputStream(data, offset, 8));
        return 8;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getFirstRow());
        out.writeShort(getLastRow());
        out.writeShort(getFirstColumn());
        out.writeShort(getLastColumn());
    }

    public CellRangeAddress(RecordInputStream in) {
        super(readUShortAndCheck(in), in.readUShort(), in.readUShort(), in.readUShort());
    }

    private static int readUShortAndCheck(RecordInputStream in) {
        if (in.remaining() >= 8) {
            return in.readUShort();
        }
        throw new RuntimeException("Ran out of data reading CellRangeAddress");
    }

    public CellRangeAddress copy() {
        return new CellRangeAddress(getFirstRow(), getLastRow(), getFirstColumn(), getLastColumn());
    }

    public static int getEncodedSize(int numberOfItems) {
        return numberOfItems * 8;
    }

    public String formatAsString() {
        StringBuffer sb = new StringBuffer();
        CellReference cellRefFrom = new CellReference(getFirstRow(), getFirstColumn());
        CellReference cellRefTo = new CellReference(getLastRow(), getLastColumn());
        sb.append(cellRefFrom.formatAsString());
        if (!cellRefFrom.equals(cellRefTo)) {
            sb.append(':');
            sb.append(cellRefTo.formatAsString());
        }
        return sb.toString();
    }

    public static CellRangeAddress valueOf(String ref) {
        CellReference b;
        CellReference a;
        int sep = ref.indexOf(":");
        if (sep == -1) {
            a = new CellReference(ref);
            b = a;
        } else {
            a = new CellReference(ref.substring(0, sep));
            b = new CellReference(ref.substring(sep + 1));
        }
        return new CellRangeAddress(a.getRow(), b.getRow(), a.getCol(), b.getCol());
    }
}
