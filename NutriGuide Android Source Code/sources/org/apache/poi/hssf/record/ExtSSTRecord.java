package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class ExtSSTRecord extends StandardRecord {
    public static final int DEFAULT_BUCKET_SIZE = 8;
    public static final int MAX_BUCKETS = 128;
    public static final short sid = 255;
    private InfoSubRecord[] _sstInfos;
    private short _stringsPerBucket;

    private static final class InfoSubRecord {
        public static final int ENCODED_SIZE = 8;
        private int field_1_stream_pos;
        private int field_2_bucket_sst_offset;
        private short field_3_zero;

        public InfoSubRecord(int streamPos, int bucketSstOffset) {
            this.field_1_stream_pos = streamPos;
            this.field_2_bucket_sst_offset = bucketSstOffset;
        }

        public InfoSubRecord(RecordInputStream in) {
            this.field_1_stream_pos = in.readInt();
            this.field_2_bucket_sst_offset = in.readShort();
            this.field_3_zero = in.readShort();
        }

        public int getStreamPos() {
            return this.field_1_stream_pos;
        }

        public int getBucketSSTOffset() {
            return this.field_2_bucket_sst_offset;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeInt(this.field_1_stream_pos);
            out.writeShort(this.field_2_bucket_sst_offset);
            out.writeShort(this.field_3_zero);
        }
    }

    public ExtSSTRecord() {
        this._stringsPerBucket = 8;
        this._sstInfos = new InfoSubRecord[0];
    }

    public ExtSSTRecord(RecordInputStream in) {
        this._stringsPerBucket = in.readShort();
        this._sstInfos = new InfoSubRecord[(in.remaining() / 8)];
        int i = 0;
        while (true) {
            InfoSubRecord[] infoSubRecordArr = this._sstInfos;
            if (i < infoSubRecordArr.length) {
                infoSubRecordArr[i] = new InfoSubRecord(in);
                i++;
            } else {
                return;
            }
        }
    }

    public void setNumStringsPerBucket(short numStrings) {
        this._stringsPerBucket = numStrings;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[EXTSST]\n");
        buffer.append("    .dsst           = ");
        buffer.append(Integer.toHexString(this._stringsPerBucket));
        buffer.append("\n");
        buffer.append("    .numInfoRecords = ");
        buffer.append(this._sstInfos.length);
        buffer.append("\n");
        for (int k = 0; k < this._sstInfos.length; k++) {
            buffer.append("    .inforecord     = ");
            buffer.append(k);
            buffer.append("\n");
            buffer.append("    .streampos      = ");
            buffer.append(Integer.toHexString(this._sstInfos[k].getStreamPos()));
            buffer.append("\n");
            buffer.append("    .sstoffset      = ");
            buffer.append(Integer.toHexString(this._sstInfos[k].getBucketSSTOffset()));
            buffer.append("\n");
        }
        buffer.append("[/EXTSST]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._stringsPerBucket);
        int k = 0;
        while (true) {
            InfoSubRecord[] infoSubRecordArr = this._sstInfos;
            if (k < infoSubRecordArr.length) {
                infoSubRecordArr[k].serialize(out);
                k++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this._sstInfos.length * 8) + 2;
    }

    public static final int getNumberOfInfoRecsForStrings(int numStrings) {
        int infoRecs = numStrings / 8;
        if (numStrings % 8 != 0) {
            infoRecs++;
        }
        if (infoRecs > 128) {
            return 128;
        }
        return infoRecs;
    }

    public static final int getRecordSizeForStrings(int numStrings) {
        return (getNumberOfInfoRecsForStrings(numStrings) * 8) + 6;
    }

    public short getSid() {
        return 255;
    }

    public void setBucketOffsets(int[] bucketAbsoluteOffsets, int[] bucketRelativeOffsets) {
        this._sstInfos = new InfoSubRecord[bucketAbsoluteOffsets.length];
        for (int i = 0; i < bucketAbsoluteOffsets.length; i++) {
            this._sstInfos[i] = new InfoSubRecord(bucketAbsoluteOffsets[i], bucketRelativeOffsets[i]);
        }
    }
}
