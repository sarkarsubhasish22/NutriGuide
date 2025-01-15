package org.apache.poi.ddf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public class EscherBlipWMFRecord extends EscherBlipRecord {
    private static final int HEADER_SIZE = 8;
    public static final String RECORD_DESCRIPTION = "msofbtBlip";
    private byte field_10_compressionFlag;
    private byte field_11_filter;
    private byte[] field_12_data;
    private byte[] field_1_secondaryUID;
    private int field_2_cacheOfSize;
    private int field_3_boundaryTop;
    private int field_4_boundaryLeft;
    private int field_5_boundaryWidth;
    private int field_6_boundaryHeight;
    private int field_7_width;
    private int field_8_height;
    private int field_9_cacheOfSavedSize;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        int pos = offset + 8;
        byte[] bArr = new byte[16];
        this.field_1_secondaryUID = bArr;
        System.arraycopy(data, pos + 0, bArr, 0, 16);
        int size = 0 + 16;
        this.field_2_cacheOfSize = LittleEndian.getInt(data, pos + size);
        int size2 = size + 4;
        this.field_3_boundaryTop = LittleEndian.getInt(data, pos + size2);
        int size3 = size2 + 4;
        this.field_4_boundaryLeft = LittleEndian.getInt(data, pos + size3);
        int size4 = size3 + 4;
        this.field_5_boundaryWidth = LittleEndian.getInt(data, pos + size4);
        int size5 = size4 + 4;
        this.field_6_boundaryHeight = LittleEndian.getInt(data, pos + size5);
        int size6 = size5 + 4;
        this.field_7_width = LittleEndian.getInt(data, pos + size6);
        int size7 = size6 + 4;
        this.field_8_height = LittleEndian.getInt(data, pos + size7);
        int size8 = size7 + 4;
        this.field_9_cacheOfSavedSize = LittleEndian.getInt(data, pos + size8);
        int size9 = size8 + 4;
        this.field_10_compressionFlag = data[pos + size9];
        int size10 = size9 + 1;
        this.field_11_filter = data[pos + size10];
        int size11 = size10 + 1;
        int bytesRemaining = bytesAfterHeader - size11;
        byte[] bArr2 = new byte[bytesRemaining];
        this.field_12_data = bArr2;
        System.arraycopy(data, pos + size11, bArr2, 0, bytesRemaining);
        return size11 + bytesRemaining + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        LittleEndian.putInt(data, offset + 4, this.field_12_data.length + 36);
        int pos = offset + 8;
        System.arraycopy(this.field_1_secondaryUID, 0, data, pos, 16);
        int pos2 = pos + 16;
        LittleEndian.putInt(data, pos2, this.field_2_cacheOfSize);
        int pos3 = pos2 + 4;
        LittleEndian.putInt(data, pos3, this.field_3_boundaryTop);
        int pos4 = pos3 + 4;
        LittleEndian.putInt(data, pos4, this.field_4_boundaryLeft);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, this.field_5_boundaryWidth);
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_6_boundaryHeight);
        int pos7 = pos6 + 4;
        LittleEndian.putInt(data, pos7, this.field_7_width);
        int pos8 = pos7 + 4;
        LittleEndian.putInt(data, pos8, this.field_8_height);
        int pos9 = pos8 + 4;
        LittleEndian.putInt(data, pos9, this.field_9_cacheOfSavedSize);
        int pos10 = pos9 + 4;
        int pos11 = pos10 + 1;
        data[pos10] = this.field_10_compressionFlag;
        int pos12 = pos11 + 1;
        data[pos11] = this.field_11_filter;
        byte[] bArr = this.field_12_data;
        System.arraycopy(bArr, 0, data, pos12, bArr.length);
        int pos13 = pos12 + this.field_12_data.length;
        listener.afterRecordSerialize(pos13, getRecordId(), pos13 - offset, this);
        return pos13 - offset;
    }

    public int getRecordSize() {
        return this.field_12_data.length + 58;
    }

    public String getRecordName() {
        return "Blip";
    }

    public byte[] getSecondaryUID() {
        return this.field_1_secondaryUID;
    }

    public void setSecondaryUID(byte[] field_1_secondaryUID2) {
        this.field_1_secondaryUID = field_1_secondaryUID2;
    }

    public int getCacheOfSize() {
        return this.field_2_cacheOfSize;
    }

    public void setCacheOfSize(int field_2_cacheOfSize2) {
        this.field_2_cacheOfSize = field_2_cacheOfSize2;
    }

    public int getBoundaryTop() {
        return this.field_3_boundaryTop;
    }

    public void setBoundaryTop(int field_3_boundaryTop2) {
        this.field_3_boundaryTop = field_3_boundaryTop2;
    }

    public int getBoundaryLeft() {
        return this.field_4_boundaryLeft;
    }

    public void setBoundaryLeft(int field_4_boundaryLeft2) {
        this.field_4_boundaryLeft = field_4_boundaryLeft2;
    }

    public int getBoundaryWidth() {
        return this.field_5_boundaryWidth;
    }

    public void setBoundaryWidth(int field_5_boundaryWidth2) {
        this.field_5_boundaryWidth = field_5_boundaryWidth2;
    }

    public int getBoundaryHeight() {
        return this.field_6_boundaryHeight;
    }

    public void setBoundaryHeight(int field_6_boundaryHeight2) {
        this.field_6_boundaryHeight = field_6_boundaryHeight2;
    }

    public int getWidth() {
        return this.field_7_width;
    }

    public void setWidth(int width) {
        this.field_7_width = width;
    }

    public int getHeight() {
        return this.field_8_height;
    }

    public void setHeight(int height) {
        this.field_8_height = height;
    }

    public int getCacheOfSavedSize() {
        return this.field_9_cacheOfSavedSize;
    }

    public void setCacheOfSavedSize(int field_9_cacheOfSavedSize2) {
        this.field_9_cacheOfSavedSize = field_9_cacheOfSavedSize2;
    }

    public byte getCompressionFlag() {
        return this.field_10_compressionFlag;
    }

    public void setCompressionFlag(byte field_10_compressionFlag2) {
        this.field_10_compressionFlag = field_10_compressionFlag2;
    }

    public byte getFilter() {
        return this.field_11_filter;
    }

    public void setFilter(byte field_11_filter2) {
        this.field_11_filter = field_11_filter2;
    }

    public byte[] getData() {
        return this.field_12_data;
    }

    public void setData(byte[] field_12_data2) {
        this.field_12_data = field_12_data2;
    }

    public String toString() {
        String extraData;
        String nl = System.getProperty("line.separator");
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            HexDump.dump(this.field_12_data, 0, (OutputStream) b, 0);
            extraData = b.toString();
        } catch (Exception e) {
            extraData = e.toString();
        }
        return getClass().getName() + ":" + nl + "  RecordId: 0x" + HexDump.toHex(getRecordId()) + nl + "  Options: 0x" + HexDump.toHex(getOptions()) + nl + "  Secondary UID: " + HexDump.toHex(this.field_1_secondaryUID) + nl + "  CacheOfSize: " + this.field_2_cacheOfSize + nl + "  BoundaryTop: " + this.field_3_boundaryTop + nl + "  BoundaryLeft: " + this.field_4_boundaryLeft + nl + "  BoundaryWidth: " + this.field_5_boundaryWidth + nl + "  BoundaryHeight: " + this.field_6_boundaryHeight + nl + "  X: " + this.field_7_width + nl + "  Y: " + this.field_8_height + nl + "  CacheOfSavedSize: " + this.field_9_cacheOfSavedSize + nl + "  CompressionFlag: " + this.field_10_compressionFlag + nl + "  Filter: " + this.field_11_filter + nl + "  Data:" + nl + extraData;
    }

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out);
        int i = 0;
        while (i < data.length) {
            try {
                deflaterOutputStream.write(data[i]);
                i++;
            } catch (IOException e) {
                throw new RecordFormatException(e.toString());
            }
        }
        return out.toByteArray();
    }

    public static byte[] decompress(byte[] data, int pos, int length) {
        IOException e;
        byte[] compressedData = new byte[length];
        int i = 0;
        System.arraycopy(data, pos + 50, compressedData, 0, length);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(compressedData));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            try {
                int read = inflaterInputStream.read();
                int c = read;
                if (read == -1) {
                    return out.toByteArray();
                }
                try {
                    out.write(c);
                    i = c;
                } catch (IOException e2) {
                    e = e2;
                    throw new RecordFormatException(e.toString());
                }
            } catch (IOException e3) {
                IOException iOException = e3;
                int i2 = i;
                e = iOException;
                throw new RecordFormatException(e.toString());
            }
        }
    }
}
