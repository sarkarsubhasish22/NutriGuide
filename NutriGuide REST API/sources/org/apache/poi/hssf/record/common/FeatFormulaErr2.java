package org.apache.poi.hssf.record.common;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;

public final class FeatFormulaErr2 implements SharedFeature {
    static BitField checkCalculationErrors = BitFieldFactory.getInstance(1);
    static BitField checkDateTimeFormats = BitFieldFactory.getInstance(32);
    static BitField checkEmptyCellRef = BitFieldFactory.getInstance(2);
    static BitField checkInconsistentFormulas = BitFieldFactory.getInstance(16);
    static BitField checkInconsistentRanges = BitFieldFactory.getInstance(8);
    static BitField checkNumbersAsText = BitFieldFactory.getInstance(4);
    static BitField checkUnprotectedFormulas = BitFieldFactory.getInstance(64);
    static BitField performDataValidation = BitFieldFactory.getInstance(128);
    private int errorCheck;

    public FeatFormulaErr2() {
    }

    public FeatFormulaErr2(RecordInputStream in) {
        this.errorCheck = in.readInt();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" [FEATURE FORMULA ERRORS]\n");
        buffer.append("  checkCalculationErrors    = ");
        buffer.append("  checkEmptyCellRef         = ");
        buffer.append("  checkNumbersAsText        = ");
        buffer.append("  checkInconsistentRanges   = ");
        buffer.append("  checkInconsistentFormulas = ");
        buffer.append("  checkDateTimeFormats      = ");
        buffer.append("  checkUnprotectedFormulas  = ");
        buffer.append("  performDataValidation     = ");
        buffer.append(" [/FEATURE FORMULA ERRORS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.errorCheck);
    }

    public int getDataSize() {
        return 4;
    }

    public int _getRawErrorCheckValue() {
        return this.errorCheck;
    }

    public boolean getCheckCalculationErrors() {
        return checkCalculationErrors.isSet(this.errorCheck);
    }

    public void setCheckCalculationErrors(boolean checkCalculationErrors2) {
        checkCalculationErrors.setBoolean(this.errorCheck, checkCalculationErrors2);
    }

    public boolean getCheckEmptyCellRef() {
        return checkEmptyCellRef.isSet(this.errorCheck);
    }

    public void setCheckEmptyCellRef(boolean checkEmptyCellRef2) {
        checkEmptyCellRef.setBoolean(this.errorCheck, checkEmptyCellRef2);
    }

    public boolean getCheckNumbersAsText() {
        return checkNumbersAsText.isSet(this.errorCheck);
    }

    public void setCheckNumbersAsText(boolean checkNumbersAsText2) {
        checkNumbersAsText.setBoolean(this.errorCheck, checkNumbersAsText2);
    }

    public boolean getCheckInconsistentRanges() {
        return checkInconsistentRanges.isSet(this.errorCheck);
    }

    public void setCheckInconsistentRanges(boolean checkInconsistentRanges2) {
        checkInconsistentRanges.setBoolean(this.errorCheck, checkInconsistentRanges2);
    }

    public boolean getCheckInconsistentFormulas() {
        return checkInconsistentFormulas.isSet(this.errorCheck);
    }

    public void setCheckInconsistentFormulas(boolean checkInconsistentFormulas2) {
        checkInconsistentFormulas.setBoolean(this.errorCheck, checkInconsistentFormulas2);
    }

    public boolean getCheckDateTimeFormats() {
        return checkDateTimeFormats.isSet(this.errorCheck);
    }

    public void setCheckDateTimeFormats(boolean checkDateTimeFormats2) {
        checkDateTimeFormats.setBoolean(this.errorCheck, checkDateTimeFormats2);
    }

    public boolean getCheckUnprotectedFormulas() {
        return checkUnprotectedFormulas.isSet(this.errorCheck);
    }

    public void setCheckUnprotectedFormulas(boolean checkUnprotectedFormulas2) {
        checkUnprotectedFormulas.setBoolean(this.errorCheck, checkUnprotectedFormulas2);
    }

    public boolean getPerformDataValidation() {
        return performDataValidation.isSet(this.errorCheck);
    }

    public void setPerformDataValidation(boolean performDataValidation2) {
        performDataValidation.setBoolean(this.errorCheck, performDataValidation2);
    }
}
