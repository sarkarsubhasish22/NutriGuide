package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class SheetPropertiesRecord extends StandardRecord {
    public static final byte EMPTY_INTERPOLATED = 2;
    public static final byte EMPTY_NOT_PLOTTED = 0;
    public static final byte EMPTY_ZERO = 1;
    private static final BitField autoPlotArea = BitFieldFactory.getInstance(16);
    private static final BitField chartTypeManuallyFormatted = BitFieldFactory.getInstance(1);
    private static final BitField defaultPlotDimensions = BitFieldFactory.getInstance(8);
    private static final BitField doNotSizeWithWindow = BitFieldFactory.getInstance(4);
    private static final BitField plotVisibleOnly = BitFieldFactory.getInstance(2);
    public static final short sid = 4164;
    private int field_1_flags;
    private int field_2_empty;

    public SheetPropertiesRecord() {
    }

    public SheetPropertiesRecord(RecordInputStream in) {
        this.field_1_flags = in.readUShort();
        this.field_2_empty = in.readUShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SHTPROPS]\n");
        buffer.append("    .flags                = ");
        buffer.append(HexDump.shortToHex(this.field_1_flags));
        buffer.append(10);
        buffer.append("         .chartTypeManuallyFormatted= ");
        buffer.append(isChartTypeManuallyFormatted());
        buffer.append(10);
        buffer.append("         .plotVisibleOnly           = ");
        buffer.append(isPlotVisibleOnly());
        buffer.append(10);
        buffer.append("         .doNotSizeWithWindow       = ");
        buffer.append(isDoNotSizeWithWindow());
        buffer.append(10);
        buffer.append("         .defaultPlotDimensions     = ");
        buffer.append(isDefaultPlotDimensions());
        buffer.append(10);
        buffer.append("         .autoPlotArea              = ");
        buffer.append(isAutoPlotArea());
        buffer.append(10);
        buffer.append("    .empty                = ");
        buffer.append(HexDump.shortToHex(this.field_2_empty));
        buffer.append(10);
        buffer.append("[/SHTPROPS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_flags);
        out.writeShort(this.field_2_empty);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SheetPropertiesRecord rec = new SheetPropertiesRecord();
        rec.field_1_flags = this.field_1_flags;
        rec.field_2_empty = this.field_2_empty;
        return rec;
    }

    public int getFlags() {
        return this.field_1_flags;
    }

    public int getEmpty() {
        return this.field_2_empty;
    }

    public void setEmpty(byte empty) {
        this.field_2_empty = empty;
    }

    public void setChartTypeManuallyFormatted(boolean value) {
        this.field_1_flags = chartTypeManuallyFormatted.setBoolean(this.field_1_flags, value);
    }

    public boolean isChartTypeManuallyFormatted() {
        return chartTypeManuallyFormatted.isSet(this.field_1_flags);
    }

    public void setPlotVisibleOnly(boolean value) {
        this.field_1_flags = plotVisibleOnly.setBoolean(this.field_1_flags, value);
    }

    public boolean isPlotVisibleOnly() {
        return plotVisibleOnly.isSet(this.field_1_flags);
    }

    public void setDoNotSizeWithWindow(boolean value) {
        this.field_1_flags = doNotSizeWithWindow.setBoolean(this.field_1_flags, value);
    }

    public boolean isDoNotSizeWithWindow() {
        return doNotSizeWithWindow.isSet(this.field_1_flags);
    }

    public void setDefaultPlotDimensions(boolean value) {
        this.field_1_flags = defaultPlotDimensions.setBoolean(this.field_1_flags, value);
    }

    public boolean isDefaultPlotDimensions() {
        return defaultPlotDimensions.isSet(this.field_1_flags);
    }

    public void setAutoPlotArea(boolean value) {
        this.field_1_flags = autoPlotArea.setBoolean(this.field_1_flags, value);
    }

    public boolean isAutoPlotArea() {
        return autoPlotArea.isSet(this.field_1_flags);
    }
}
