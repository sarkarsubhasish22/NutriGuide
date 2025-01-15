package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

public final class AxisOptionsRecord extends StandardRecord {
    private static final BitField defaultBase = BitFieldFactory.getInstance(32);
    private static final BitField defaultCross = BitFieldFactory.getInstance(64);
    private static final BitField defaultDateSettings = BitFieldFactory.getInstance(128);
    private static final BitField defaultMajor = BitFieldFactory.getInstance(4);
    private static final BitField defaultMaximum = BitFieldFactory.getInstance(2);
    private static final BitField defaultMinimum = BitFieldFactory.getInstance(1);
    private static final BitField defaultMinorUnit = BitFieldFactory.getInstance(8);
    private static final BitField isDate = BitFieldFactory.getInstance(16);
    public static final short sid = 4194;
    private short field_1_minimumCategory;
    private short field_2_maximumCategory;
    private short field_3_majorUnitValue;
    private short field_4_majorUnit;
    private short field_5_minorUnitValue;
    private short field_6_minorUnit;
    private short field_7_baseUnit;
    private short field_8_crossingPoint;
    private short field_9_options;

    public AxisOptionsRecord() {
    }

    public AxisOptionsRecord(RecordInputStream in) {
        this.field_1_minimumCategory = in.readShort();
        this.field_2_maximumCategory = in.readShort();
        this.field_3_majorUnitValue = in.readShort();
        this.field_4_majorUnit = in.readShort();
        this.field_5_minorUnitValue = in.readShort();
        this.field_6_minorUnit = in.readShort();
        this.field_7_baseUnit = in.readShort();
        this.field_8_crossingPoint = in.readShort();
        this.field_9_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AXCEXT]\n");
        buffer.append("    .minimumCategory      = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMinimumCategory()));
        buffer.append(" (");
        buffer.append(getMinimumCategory());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .maximumCategory      = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMaximumCategory()));
        buffer.append(" (");
        buffer.append(getMaximumCategory());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorUnitValue       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMajorUnitValue()));
        buffer.append(" (");
        buffer.append(getMajorUnitValue());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .majorUnit            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMajorUnit()));
        buffer.append(" (");
        buffer.append(getMajorUnit());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorUnitValue       = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMinorUnitValue()));
        buffer.append(" (");
        buffer.append(getMinorUnitValue());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .minorUnit            = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getMinorUnit()));
        buffer.append(" (");
        buffer.append(getMinorUnit());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .baseUnit             = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getBaseUnit()));
        buffer.append(" (");
        buffer.append(getBaseUnit());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .crossingPoint        = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getCrossingPoint()));
        buffer.append(" (");
        buffer.append(getCrossingPoint());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ");
        buffer.append("0x");
        buffer.append(HexDump.toHex(getOptions()));
        buffer.append(" (");
        buffer.append(getOptions());
        buffer.append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .defaultMinimum           = ");
        buffer.append(isDefaultMinimum());
        buffer.append(10);
        buffer.append("         .defaultMaximum           = ");
        buffer.append(isDefaultMaximum());
        buffer.append(10);
        buffer.append("         .defaultMajor             = ");
        buffer.append(isDefaultMajor());
        buffer.append(10);
        buffer.append("         .defaultMinorUnit         = ");
        buffer.append(isDefaultMinorUnit());
        buffer.append(10);
        buffer.append("         .isDate                   = ");
        buffer.append(isIsDate());
        buffer.append(10);
        buffer.append("         .defaultBase              = ");
        buffer.append(isDefaultBase());
        buffer.append(10);
        buffer.append("         .defaultCross             = ");
        buffer.append(isDefaultCross());
        buffer.append(10);
        buffer.append("         .defaultDateSettings      = ");
        buffer.append(isDefaultDateSettings());
        buffer.append(10);
        buffer.append("[/AXCEXT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_minimumCategory);
        out.writeShort(this.field_2_maximumCategory);
        out.writeShort(this.field_3_majorUnitValue);
        out.writeShort(this.field_4_majorUnit);
        out.writeShort(this.field_5_minorUnitValue);
        out.writeShort(this.field_6_minorUnit);
        out.writeShort(this.field_7_baseUnit);
        out.writeShort(this.field_8_crossingPoint);
        out.writeShort(this.field_9_options);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        return 18;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        AxisOptionsRecord rec = new AxisOptionsRecord();
        rec.field_1_minimumCategory = this.field_1_minimumCategory;
        rec.field_2_maximumCategory = this.field_2_maximumCategory;
        rec.field_3_majorUnitValue = this.field_3_majorUnitValue;
        rec.field_4_majorUnit = this.field_4_majorUnit;
        rec.field_5_minorUnitValue = this.field_5_minorUnitValue;
        rec.field_6_minorUnit = this.field_6_minorUnit;
        rec.field_7_baseUnit = this.field_7_baseUnit;
        rec.field_8_crossingPoint = this.field_8_crossingPoint;
        rec.field_9_options = this.field_9_options;
        return rec;
    }

    public short getMinimumCategory() {
        return this.field_1_minimumCategory;
    }

    public void setMinimumCategory(short field_1_minimumCategory2) {
        this.field_1_minimumCategory = field_1_minimumCategory2;
    }

    public short getMaximumCategory() {
        return this.field_2_maximumCategory;
    }

    public void setMaximumCategory(short field_2_maximumCategory2) {
        this.field_2_maximumCategory = field_2_maximumCategory2;
    }

    public short getMajorUnitValue() {
        return this.field_3_majorUnitValue;
    }

    public void setMajorUnitValue(short field_3_majorUnitValue2) {
        this.field_3_majorUnitValue = field_3_majorUnitValue2;
    }

    public short getMajorUnit() {
        return this.field_4_majorUnit;
    }

    public void setMajorUnit(short field_4_majorUnit2) {
        this.field_4_majorUnit = field_4_majorUnit2;
    }

    public short getMinorUnitValue() {
        return this.field_5_minorUnitValue;
    }

    public void setMinorUnitValue(short field_5_minorUnitValue2) {
        this.field_5_minorUnitValue = field_5_minorUnitValue2;
    }

    public short getMinorUnit() {
        return this.field_6_minorUnit;
    }

    public void setMinorUnit(short field_6_minorUnit2) {
        this.field_6_minorUnit = field_6_minorUnit2;
    }

    public short getBaseUnit() {
        return this.field_7_baseUnit;
    }

    public void setBaseUnit(short field_7_baseUnit2) {
        this.field_7_baseUnit = field_7_baseUnit2;
    }

    public short getCrossingPoint() {
        return this.field_8_crossingPoint;
    }

    public void setCrossingPoint(short field_8_crossingPoint2) {
        this.field_8_crossingPoint = field_8_crossingPoint2;
    }

    public short getOptions() {
        return this.field_9_options;
    }

    public void setOptions(short field_9_options2) {
        this.field_9_options = field_9_options2;
    }

    public void setDefaultMinimum(boolean value) {
        this.field_9_options = defaultMinimum.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultMinimum() {
        return defaultMinimum.isSet(this.field_9_options);
    }

    public void setDefaultMaximum(boolean value) {
        this.field_9_options = defaultMaximum.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultMaximum() {
        return defaultMaximum.isSet(this.field_9_options);
    }

    public void setDefaultMajor(boolean value) {
        this.field_9_options = defaultMajor.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultMajor() {
        return defaultMajor.isSet(this.field_9_options);
    }

    public void setDefaultMinorUnit(boolean value) {
        this.field_9_options = defaultMinorUnit.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultMinorUnit() {
        return defaultMinorUnit.isSet(this.field_9_options);
    }

    public void setIsDate(boolean value) {
        this.field_9_options = isDate.setShortBoolean(this.field_9_options, value);
    }

    public boolean isIsDate() {
        return isDate.isSet(this.field_9_options);
    }

    public void setDefaultBase(boolean value) {
        this.field_9_options = defaultBase.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultBase() {
        return defaultBase.isSet(this.field_9_options);
    }

    public void setDefaultCross(boolean value) {
        this.field_9_options = defaultCross.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultCross() {
        return defaultCross.isSet(this.field_9_options);
    }

    public void setDefaultDateSettings(boolean value) {
        this.field_9_options = defaultDateSettings.setShortBoolean(this.field_9_options, value);
    }

    public boolean isDefaultDateSettings() {
        return defaultDateSettings.isSet(this.field_9_options);
    }
}
