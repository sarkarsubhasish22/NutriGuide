package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.aggregates.CFRecordsAggregate;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;

public final class HSSFConditionalFormatting {
    private final HSSFWorkbook _workbook;
    private final CFRecordsAggregate cfAggregate;

    HSSFConditionalFormatting(HSSFWorkbook workbook, CFRecordsAggregate cfAggregate2) {
        if (workbook == null) {
            throw new IllegalArgumentException("workbook must not be null");
        } else if (cfAggregate2 != null) {
            this._workbook = workbook;
            this.cfAggregate = cfAggregate2;
        } else {
            throw new IllegalArgumentException("cfAggregate must not be null");
        }
    }

    /* access modifiers changed from: package-private */
    public CFRecordsAggregate getCFRecordsAggregate() {
        return this.cfAggregate;
    }

    public Region[] getFormattingRegions() {
        return Region.convertCellRangesToRegions(getFormattingRanges());
    }

    public CellRangeAddress[] getFormattingRanges() {
        return this.cfAggregate.getHeader().getCellRanges();
    }

    public void setRule(int idx, HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.setRule(idx, cfRule.getCfRuleRecord());
    }

    public void addRule(HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.addRule(cfRule.getCfRuleRecord());
    }

    public HSSFConditionalFormattingRule getRule(int idx) {
        return new HSSFConditionalFormattingRule(this._workbook, this.cfAggregate.getRule(idx));
    }

    public int getNumberOfRules() {
        return this.cfAggregate.getNumberOfRules();
    }

    public String toString() {
        return this.cfAggregate.toString();
    }
}
