package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.aggregates.CFRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.ConditionalFormattingTable;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;

public final class HSSFSheetConditionalFormatting {
    private final ConditionalFormattingTable _conditionalFormattingTable;
    private final HSSFSheet _sheet;

    HSSFSheetConditionalFormatting(HSSFSheet sheet) {
        this._sheet = sheet;
        this._conditionalFormattingTable = sheet.getSheet().getConditionalFormattingTable();
    }

    public HSSFConditionalFormattingRule createConditionalFormattingRule(byte comparisonOperation, String formula1, String formula2) {
        return new HSSFConditionalFormattingRule(this._sheet.getWorkbook(), CFRuleRecord.create(this._sheet, comparisonOperation, formula1, formula2));
    }

    public HSSFConditionalFormattingRule createConditionalFormattingRule(String formula) {
        return new HSSFConditionalFormattingRule(this._sheet.getWorkbook(), CFRuleRecord.create(this._sheet, formula));
    }

    public int addConditionalFormatting(HSSFConditionalFormatting cf) {
        return this._conditionalFormattingTable.add(cf.getCFRecordsAggregate().cloneCFAggregate());
    }

    public int addConditionalFormatting(Region[] regions, HSSFConditionalFormattingRule[] cfRules) {
        return addConditionalFormatting(Region.convertRegionsToCellRanges(regions), cfRules);
    }

    public int addConditionalFormatting(CellRangeAddress[] regions, HSSFConditionalFormattingRule[] cfRules) {
        if (regions != null) {
            for (CellRangeAddress range : regions) {
                range.validate(SpreadsheetVersion.EXCEL97);
            }
            if (cfRules == null) {
                throw new IllegalArgumentException("cfRules must not be null");
            } else if (cfRules.length == 0) {
                throw new IllegalArgumentException("cfRules must not be empty");
            } else if (cfRules.length <= 3) {
                CFRuleRecord[] rules = new CFRuleRecord[cfRules.length];
                for (int i = 0; i != cfRules.length; i++) {
                    rules[i] = cfRules[i].getCfRuleRecord();
                }
                return this._conditionalFormattingTable.add(new CFRecordsAggregate(regions, rules));
            } else {
                throw new IllegalArgumentException("Number of rules must not exceed 3");
            }
        } else {
            throw new IllegalArgumentException("regions must not be null");
        }
    }

    public int addConditionalFormatting(CellRangeAddress[] regions, HSSFConditionalFormattingRule rule1) {
        return addConditionalFormatting(regions, new HSSFConditionalFormattingRule[]{rule1});
    }

    public int addConditionalFormatting(CellRangeAddress[] regions, HSSFConditionalFormattingRule rule1, HSSFConditionalFormattingRule rule2) {
        return addConditionalFormatting(regions, new HSSFConditionalFormattingRule[]{rule1, rule2});
    }

    public HSSFConditionalFormatting getConditionalFormattingAt(int index) {
        CFRecordsAggregate cf = this._conditionalFormattingTable.get(index);
        if (cf == null) {
            return null;
        }
        return new HSSFConditionalFormatting(this._sheet.getWorkbook(), cf);
    }

    public int getNumConditionalFormattings() {
        return this._conditionalFormattingTable.size();
    }

    public void removeConditionalFormatting(int index) {
        this._conditionalFormattingTable.remove(index);
    }
}
