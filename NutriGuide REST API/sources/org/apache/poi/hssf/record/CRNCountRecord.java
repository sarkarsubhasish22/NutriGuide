package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

public final class CRNCountRecord extends StandardRecord {
    private static final short DATA_SIZE = 4;
    public static final short sid = 89;
    private int field_1_number_crn_records;
    private int field_2_sheet_table_index;

    public CRNCountRecord() {
        throw new RuntimeException("incomplete code");
    }

    public int getNumberOfCRNs() {
        return this.field_1_number_crn_records;
    }

    public CRNCountRecord(RecordInputStream in) {
        short readShort = in.readShort();
        this.field_1_number_crn_records = readShort;
        if (readShort < 0) {
            this.field_1_number_crn_records = (short) (-readShort);
        }
        this.field_2_sheet_table_index = in.readShort();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [XCT");
        sb.append(" nCRNs=");
        sb.append(this.field_1_number_crn_records);
        sb.append(" sheetIx=");
        sb.append(this.field_2_sheet_table_index);
        sb.append("]");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort((short) this.field_1_number_crn_records);
        out.writeShort((short) this.field_2_sheet_table_index);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 4;
    }

    public short getSid() {
        return 89;
    }
}
