package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class ValueRangeRecord extends StandardRecord {
    private static final BitField automaticCategoryCrossing = BitFieldFactory.getInstance(16);
    private static final BitField automaticMajor = BitFieldFactory.getInstance(4);
    private static final BitField automaticMaximum = BitFieldFactory.getInstance(2);
    private static final BitField automaticMinimum = BitFieldFactory.getInstance(1);
    private static final BitField automaticMinor = BitFieldFactory.getInstance(8);
    private static final BitField crossCategoryAxisAtMaximum = BitFieldFactory.getInstance(128);
    private static final BitField logarithmicScale = BitFieldFactory.getInstance(32);
    private static final BitField reserved = BitFieldFactory.getInstance(256);
    public static final short sid = 4127;
    private static final BitField valuesInReverse = BitFieldFactory.getInstance(64);
    private double field_1_minimumAxisValue;
    private double field_2_maximumAxisValue;
    private double field_3_majorIncrement;
    private double field_4_minorIncrement;
    private double field_5_categoryAxisCross;
    private short field_6_options;

    public ValueRangeRecord() {
    }

    public ValueRangeRecord(RecordInputStream in) {
        this.field_1_minimumAxisValue = in.readDouble();
        this.field_2_maximumAxisValue = in.readDouble();
        this.field_3_majorIncrement = in.readDouble();
        this.field_4_minorIncrement = in.readDouble();
        this.field_5_categoryAxisCross = in.readDouble();
        this.field_6_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[VALUERANGE]\n");
        buffer.append("    .minimumAxisValue     = ");
        buffer.append(" (");
        buffer.append(getMinimumAxisValue());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .maximumAxisValue     = ");
        buffer.append(" (");
        buffer.append(getMaximumAxisValue());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorIncrement       = ");
        buffer.append(" (");
        buffer.append(getMajorIncrement());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorIncrement       = ");
        buffer.append(" (");
        buffer.append(getMinorIncrement());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .categoryAxisCross    = ");
        buffer.append(" (");
        buffer.append(getCategoryAxisCross());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .automaticMinimum         = ");
        buffer.append(isAutomaticMinimum());
        buffer.append(10);
        buffer.append("         .automaticMaximum         = ");
        buffer.append(isAutomaticMaximum());
        buffer.append(10);
        buffer.append("         .automaticMajor           = ");
        buffer.append(isAutomaticMajor());
        buffer.append(10);
        buffer.append("         .automaticMinor           = ");
        buffer.append(isAutomaticMinor());
        buffer.append(10);
        buffer.append("         .automaticCategoryCrossing     = ");
        buffer.append(isAutomaticCategoryCrossing());
        buffer.append(10);
        buffer.append("         .logarithmicScale         = ");
        buffer.append(isLogarithmicScale());
        buffer.append(10);
        buffer.append("         .valuesInReverse          = ");
        buffer.append(isValuesInReverse());
        buffer.append(10);
        buffer.append("         .crossCategoryAxisAtMaximum     = ");
        buffer.append(isCrossCategoryAxisAtMaximum());
        buffer.append(10);
        buffer.append("         .reserved                 = ");
        buffer.append(isReserved());
        buffer.append(10);
        buffer.append("[/VALUERANGE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeDouble(this.field_1_minimumAxisValue);
        out.writeDouble(this.field_2_maximumAxisValue);
        out.writeDouble(this.field_3_majorIncrement);
        out.writeDouble(this.field_4_minorIncrement);
        out.writeDouble(this.field_5_categoryAxisCross);
        out.writeShort(this.field_6_options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 42;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        ValueRangeRecord rec = new ValueRangeRecord();
        rec.field_1_minimumAxisValue = this.field_1_minimumAxisValue;
        rec.field_2_maximumAxisValue = this.field_2_maximumAxisValue;
        rec.field_3_majorIncrement = this.field_3_majorIncrement;
        rec.field_4_minorIncrement = this.field_4_minorIncrement;
        rec.field_5_categoryAxisCross = this.field_5_categoryAxisCross;
        rec.field_6_options = this.field_6_options;
        return rec;
    }

    public double getMinimumAxisValue() {
        return this.field_1_minimumAxisValue;
    }

    public void setMinimumAxisValue(double field_1_minimumAxisValue2) {
        this.field_1_minimumAxisValue = field_1_minimumAxisValue2;
    }

    public double getMaximumAxisValue() {
        return this.field_2_maximumAxisValue;
    }

    public void setMaximumAxisValue(double field_2_maximumAxisValue2) {
        this.field_2_maximumAxisValue = field_2_maximumAxisValue2;
    }

    public double getMajorIncrement() {
        return this.field_3_majorIncrement;
    }

    public void setMajorIncrement(double field_3_majorIncrement2) {
        this.field_3_majorIncrement = field_3_majorIncrement2;
    }

    public double getMinorIncrement() {
        return this.field_4_minorIncrement;
    }

    public void setMinorIncrement(double field_4_minorIncrement2) {
        this.field_4_minorIncrement = field_4_minorIncrement2;
    }

    public double getCategoryAxisCross() {
        return this.field_5_categoryAxisCross;
    }

    public void setCategoryAxisCross(double field_5_categoryAxisCross2) {
        this.field_5_categoryAxisCross = field_5_categoryAxisCross2;
    }

    public short getOptions() {
        return this.field_6_options;
    }

    public void setOptions(short field_6_options2) {
        this.field_6_options = field_6_options2;
    }

    public void setAutomaticMinimum(boolean value) {
        this.field_6_options = automaticMinimum.setShortBoolean(this.field_6_options, value);
    }

    public boolean isAutomaticMinimum() {
        return automaticMinimum.isSet(this.field_6_options);
    }

    public void setAutomaticMaximum(boolean value) {
        this.field_6_options = automaticMaximum.setShortBoolean(this.field_6_options, value);
    }

    public boolean isAutomaticMaximum() {
        return automaticMaximum.isSet(this.field_6_options);
    }

    public void setAutomaticMajor(boolean value) {
        this.field_6_options = automaticMajor.setShortBoolean(this.field_6_options, value);
    }

    public boolean isAutomaticMajor() {
        return automaticMajor.isSet(this.field_6_options);
    }

    public void setAutomaticMinor(boolean value) {
        this.field_6_options = automaticMinor.setShortBoolean(this.field_6_options, value);
    }

    public boolean isAutomaticMinor() {
        return automaticMinor.isSet(this.field_6_options);
    }

    public void setAutomaticCategoryCrossing(boolean value) {
        this.field_6_options = automaticCategoryCrossing.setShortBoolean(this.field_6_options, value);
    }

    public boolean isAutomaticCategoryCrossing() {
        return automaticCategoryCrossing.isSet(this.field_6_options);
    }

    public void setLogarithmicScale(boolean value) {
        this.field_6_options = logarithmicScale.setShortBoolean(this.field_6_options, value);
    }

    public boolean isLogarithmicScale() {
        return logarithmicScale.isSet(this.field_6_options);
    }

    public void setValuesInReverse(boolean value) {
        this.field_6_options = valuesInReverse.setShortBoolean(this.field_6_options, value);
    }

    public boolean isValuesInReverse() {
        return valuesInReverse.isSet(this.field_6_options);
    }

    public void setCrossCategoryAxisAtMaximum(boolean value) {
        this.field_6_options = crossCategoryAxisAtMaximum.setShortBoolean(this.field_6_options, value);
    }

    public boolean isCrossCategoryAxisAtMaximum() {
        return crossCategoryAxisAtMaximum.isSet(this.field_6_options);
    }

    public void setReserved(boolean value) {
        this.field_6_options = reserved.setShortBoolean(this.field_6_options, value);
    }

    public boolean isReserved() {
        return reserved.isSet(this.field_6_options);
    }
}
