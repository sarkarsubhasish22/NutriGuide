package org.apache.poi.ddf;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public final class EscherPictBlip extends EscherBlipRecord {
    private static final int HEADER_SIZE = 8;
    public static final short RECORD_ID_EMF = -4070;
    public static final short RECORD_ID_PICT = -4068;
    public static final short RECORD_ID_WMF = -4069;
    private static final POILogger log = POILogFactory.getLogger(EscherPictBlip.class);
    private byte[] field_1_UID;
    private int field_2_cb;
    private int field_3_rcBounds_x1;
    private int field_3_rcBounds_x2;
    private int field_3_rcBounds_y1;
    private int field_3_rcBounds_y2;
    private int field_4_ptSize_h;
    private int field_4_ptSize_w;
    private int field_5_cbSave;
    private byte field_6_fCompression;
    private byte field_7_fFilter;
    private byte[] raw_pictureData;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        int pos = offset + 8;
        byte[] bArr = new byte[16];
        this.field_1_UID = bArr;
        System.arraycopy(data, pos, bArr, 0, 16);
        int pos2 = pos + 16;
        this.field_2_cb = LittleEndian.getInt(data, pos2);
        int pos3 = pos2 + 4;
        this.field_3_rcBounds_x1 = LittleEndian.getInt(data, pos3);
        int pos4 = pos3 + 4;
        this.field_3_rcBounds_y1 = LittleEndian.getInt(data, pos4);
        int pos5 = pos4 + 4;
        this.field_3_rcBounds_x2 = LittleEndian.getInt(data, pos5);
        int pos6 = pos5 + 4;
        this.field_3_rcBounds_y2 = LittleEndian.getInt(data, pos6);
        int pos7 = pos6 + 4;
        this.field_4_ptSize_w = LittleEndian.getInt(data, pos7);
        int pos8 = pos7 + 4;
        this.field_4_ptSize_h = LittleEndian.getInt(data, pos8);
        int pos9 = pos8 + 4;
        int i = LittleEndian.getInt(data, pos9);
        this.field_5_cbSave = i;
        int pos10 = pos9 + 4;
        this.field_6_fCompression = data[pos10];
        int pos11 = pos10 + 1;
        this.field_7_fFilter = data[pos11];
        byte[] bArr2 = new byte[i];
        this.raw_pictureData = bArr2;
        System.arraycopy(data, pos11 + 1, bArr2, 0, i);
        if (this.field_6_fCompression == 0) {
            this.field_pictureData = inflatePictureData(this.raw_pictureData);
        } else {
            this.field_pictureData = this.raw_pictureData;
        }
        return bytesAfterHeader + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, getOptions());
        int pos2 = pos + 2;
        LittleEndian.putShort(data, pos2, getRecordId());
        LittleEndian.putInt(data, getRecordSize() - 8);
        int pos3 = pos2 + 2 + 4;
        System.arraycopy(this.field_1_UID, 0, data, pos3, 16);
        int pos4 = pos3 + 16;
        LittleEndian.putInt(data, pos4, this.field_2_cb);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, this.field_3_rcBounds_x1);
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_3_rcBounds_y1);
        int pos7 = pos6 + 4;
        LittleEndian.putInt(data, pos7, this.field_3_rcBounds_x2);
        int pos8 = pos7 + 4;
        LittleEndian.putInt(data, pos8, this.field_3_rcBounds_y2);
        int pos9 = pos8 + 4;
        LittleEndian.putInt(data, pos9, this.field_4_ptSize_w);
        int pos10 = pos9 + 4;
        LittleEndian.putInt(data, pos10, this.field_4_ptSize_h);
        int pos11 = pos10 + 4;
        LittleEndian.putInt(data, pos11, this.field_5_cbSave);
        int pos12 = pos11 + 4;
        data[pos12] = this.field_6_fCompression;
        int pos13 = pos12 + 1;
        data[pos13] = this.field_7_fFilter;
        byte[] bArr = this.raw_pictureData;
        System.arraycopy(bArr, 0, data, pos13 + 1, bArr.length);
        listener.afterRecordSerialize(getRecordSize() + offset, getRecordId(), getRecordSize(), this);
        return this.raw_pictureData.length + 25;
    }

    private static byte[] inflatePictureData(byte[] data) {
        try {
            InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            while (true) {
                int read = in.read(buf);
                int readBytes = read;
                if (read <= 0) {
                    return out.toByteArray();
                }
                out.write(buf, 0, readBytes);
            }
        } catch (IOException e) {
            log.log(POILogger.INFO, (Object) "Possibly corrupt compression or non-compressed data", (Throwable) e);
            return data;
        }
    }

    public int getRecordSize() {
        return this.raw_pictureData.length + 58;
    }

    public byte[] getUID() {
        return this.field_1_UID;
    }

    public void setUID(byte[] uid) {
        this.field_1_UID = uid;
    }

    public int getUncompressedSize() {
        return this.field_2_cb;
    }

    public void setUncompressedSize(int uncompressedSize) {
        this.field_2_cb = uncompressedSize;
    }

    public Rectangle getBounds() {
        int i = this.field_3_rcBounds_x1;
        int i2 = this.field_3_rcBounds_y1;
        return new Rectangle(i, i2, this.field_3_rcBounds_x2 - i, this.field_3_rcBounds_y2 - i2);
    }

    public void setBounds(Rectangle bounds) {
        this.field_3_rcBounds_x1 = bounds.x;
        this.field_3_rcBounds_y1 = bounds.y;
        this.field_3_rcBounds_x2 = bounds.x + bounds.width;
        this.field_3_rcBounds_y2 = bounds.y + bounds.height;
    }

    public Dimension getSizeEMU() {
        return new Dimension(this.field_4_ptSize_w, this.field_4_ptSize_h);
    }

    public void setSizeEMU(Dimension sizeEMU) {
        this.field_4_ptSize_w = sizeEMU.width;
        this.field_4_ptSize_h = sizeEMU.height;
    }

    public int getCompressedSize() {
        return this.field_5_cbSave;
    }

    public void setCompressedSize(int compressedSize) {
        this.field_5_cbSave = compressedSize;
    }

    public boolean isCompressed() {
        return this.field_6_fCompression == 0;
    }

    public void setCompressed(boolean compressed) {
        this.field_6_fCompression = compressed ? (byte) 0 : -2;
    }

    public String toString() {
        String extraData = HexDump.toHex(this.field_pictureData, 32);
        return getClass().getName() + ":" + 10 + "  RecordId: 0x" + HexDump.toHex(getRecordId()) + 10 + "  Options: 0x" + HexDump.toHex(getOptions()) + 10 + "  UID: 0x" + HexDump.toHex(this.field_1_UID) + 10 + "  Uncompressed Size: " + HexDump.toHex(this.field_2_cb) + 10 + "  Bounds: " + getBounds() + 10 + "  Size in EMU: " + getSizeEMU() + 10 + "  Compressed Size: " + HexDump.toHex(this.field_5_cbSave) + 10 + "  Compression: " + HexDump.toHex(this.field_6_fCompression) + 10 + "  Filter: " + HexDump.toHex(this.field_7_fFilter) + 10 + "  Extra Data:" + 10 + extraData;
    }
}
