package org.apache.poi.hssf.record.formula;

import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class NumberPtg extends ScalarConstantPtg {
    public static final int SIZE = 9;
    public static final byte sid = 31;
    private final double field_1_value;

    public NumberPtg(LittleEndianInput in) {
        this(in.readDouble());
    }

    public NumberPtg(String value) {
        this(Double.parseDouble(value));
    }

    public NumberPtg(double value) {
        this.field_1_value = value;
    }

    public double getValue() {
        return this.field_1_value;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(getPtgClass() + sid);
        out.writeDouble(getValue());
    }

    public int getSize() {
        return 9;
    }

    public String toFormulaString() {
        return NumberToTextConverter.toText(this.field_1_value);
    }
}
