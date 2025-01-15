package org.apache.poi.hssf.record;

import java.io.PrintStream;
import org.apache.poi.hssf.record.common.FeatFormulaErr2;
import org.apache.poi.hssf.record.common.FeatProtection;
import org.apache.poi.hssf.record.common.FeatSmartTag;
import org.apache.poi.hssf.record.common.FtrHeader;
import org.apache.poi.hssf.record.common.SharedFeature;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.LittleEndianOutput;

public final class FeatRecord extends StandardRecord {
    public static final short sid = 2152;
    private long cbFeatData;
    private CellRangeAddress[] cellRefs;
    private FtrHeader futureHeader;
    private int isf_sharedFeatureType;
    private byte reserved1;
    private long reserved2;
    private int reserved3;
    private SharedFeature sharedFeature;

    public FeatRecord() {
        FtrHeader ftrHeader = new FtrHeader();
        this.futureHeader = ftrHeader;
        ftrHeader.setRecordType(sid);
    }

    public short getSid() {
        return sid;
    }

    public FeatRecord(RecordInputStream in) {
        this.futureHeader = new FtrHeader(in);
        this.isf_sharedFeatureType = in.readShort();
        this.reserved1 = in.readByte();
        this.reserved2 = (long) in.readInt();
        int cref = in.readUShort();
        this.cbFeatData = (long) in.readInt();
        this.reserved3 = in.readShort();
        this.cellRefs = new CellRangeAddress[cref];
        int i = 0;
        while (true) {
            CellRangeAddress[] cellRangeAddressArr = this.cellRefs;
            if (i >= cellRangeAddressArr.length) {
                break;
            }
            cellRangeAddressArr[i] = new CellRangeAddress(in);
            i++;
        }
        int i2 = this.isf_sharedFeatureType;
        if (i2 == 2) {
            this.sharedFeature = new FeatProtection(in);
        } else if (i2 == 3) {
            this.sharedFeature = new FeatFormulaErr2(in);
        } else if (i2 != 4) {
            PrintStream printStream = System.err;
            printStream.println("Unknown Shared Feature " + this.isf_sharedFeatureType + " found!");
        } else {
            this.sharedFeature = new FeatSmartTag(in);
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SHARED FEATURE]\n");
        buffer.append("[/SHARED FEATURE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        this.futureHeader.serialize(out);
        out.writeShort(this.isf_sharedFeatureType);
        out.writeByte(this.reserved1);
        out.writeInt((int) this.reserved2);
        out.writeShort(this.cellRefs.length);
        out.writeInt((int) this.cbFeatData);
        out.writeShort(this.reserved3);
        int i = 0;
        while (true) {
            CellRangeAddress[] cellRangeAddressArr = this.cellRefs;
            if (i < cellRangeAddressArr.length) {
                cellRangeAddressArr[i].serialize(out);
                i++;
            } else {
                this.sharedFeature.serialize(out);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return (this.cellRefs.length * 8) + 27 + this.sharedFeature.getDataSize();
    }

    public int getIsf_sharedFeatureType() {
        return this.isf_sharedFeatureType;
    }

    public long getCbFeatData() {
        return this.cbFeatData;
    }

    public void setCbFeatData(long cbFeatData2) {
        this.cbFeatData = cbFeatData2;
    }

    public CellRangeAddress[] getCellRefs() {
        return this.cellRefs;
    }

    public void setCellRefs(CellRangeAddress[] cellRefs2) {
        this.cellRefs = cellRefs2;
    }

    public SharedFeature getSharedFeature() {
        return this.sharedFeature;
    }

    public void setSharedFeature(SharedFeature feature) {
        this.sharedFeature = feature;
        if (feature instanceof FeatProtection) {
            this.isf_sharedFeatureType = 2;
        }
        if (feature instanceof FeatFormulaErr2) {
            this.isf_sharedFeatureType = 3;
        }
        if (feature instanceof FeatSmartTag) {
            this.isf_sharedFeatureType = 4;
        }
        if (this.isf_sharedFeatureType == 3) {
            this.cbFeatData = (long) feature.getDataSize();
        } else {
            this.cbFeatData = 0;
        }
    }

    public Object clone() {
        return cloneViaReserialise();
    }
}
