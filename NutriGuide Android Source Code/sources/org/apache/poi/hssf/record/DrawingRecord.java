package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class DrawingRecord extends StandardRecord {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final short sid = 236;
    private byte[] contd;
    private byte[] recordData;

    public DrawingRecord() {
        this.recordData = EMPTY_BYTE_ARRAY;
    }

    public DrawingRecord(RecordInputStream in) {
        this.recordData = in.readRemainder();
    }

    public void processContinueRecord(byte[] record) {
        this.contd = record;
    }

    public void serialize(LittleEndianOutput out) {
        out.write(this.recordData);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return this.recordData.length;
    }

    public short getSid() {
        return 236;
    }

    public byte[] getData() {
        byte[] bArr = this.contd;
        if (bArr == null) {
            return this.recordData;
        }
        byte[] bArr2 = this.recordData;
        byte[] newBuffer = new byte[(bArr2.length + bArr.length)];
        System.arraycopy(bArr2, 0, newBuffer, 0, bArr2.length);
        byte[] bArr3 = this.contd;
        System.arraycopy(bArr3, 0, newBuffer, this.recordData.length, bArr3.length);
        return newBuffer;
    }

    public void setData(byte[] thedata) {
        if (thedata != null) {
            this.recordData = thedata;
            return;
        }
        throw new IllegalArgumentException("data must not be null");
    }

    public Object clone() {
        DrawingRecord rec = new DrawingRecord();
        rec.recordData = (byte[]) this.recordData.clone();
        byte[] bArr = this.contd;
        if (bArr != null) {
            rec.contd = (byte[]) bArr.clone();
        }
        return rec;
    }
}
