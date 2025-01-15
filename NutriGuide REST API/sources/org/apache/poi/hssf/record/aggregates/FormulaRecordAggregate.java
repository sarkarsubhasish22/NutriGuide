package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.record.formula.ExpPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.util.CellRangeAddress;

public final class FormulaRecordAggregate extends RecordAggregate implements CellValueRecordInterface {
    private final FormulaRecord _formulaRecord;
    private SharedFormulaRecord _sharedFormulaRecord;
    private SharedValueManager _sharedValueManager;
    private StringRecord _stringRecord;

    public FormulaRecordAggregate(FormulaRecord formulaRec, StringRecord stringRec, SharedValueManager svm) {
        if (svm != null) {
            if (!formulaRec.hasCachedResultString()) {
                this._stringRecord = null;
            } else if (stringRec != null) {
                this._stringRecord = stringRec;
            } else {
                throw new RecordFormatException("Formula record flag is set but String record was not found");
            }
            this._formulaRecord = formulaRec;
            this._sharedValueManager = svm;
            if (formulaRec.isSharedFormula()) {
                CellReference firstCell = formulaRec.getFormula().getExpReference();
                if (firstCell == null) {
                    handleMissingSharedFormulaRecord(formulaRec);
                } else {
                    this._sharedFormulaRecord = svm.linkSharedFormulaRecord(firstCell, this);
                }
            }
        } else {
            throw new IllegalArgumentException("sfm must not be null");
        }
    }

    private static void handleMissingSharedFormulaRecord(FormulaRecord formula) {
        if (!(formula.getParsedExpression()[0] instanceof ExpPtg)) {
            formula.setSharedFormula(false);
            return;
        }
        throw new RecordFormatException("SharedFormulaRecord not found for FormulaRecord with (isSharedFormula=true)");
    }

    public FormulaRecord getFormulaRecord() {
        return this._formulaRecord;
    }

    public StringRecord getStringRecord() {
        return this._stringRecord;
    }

    public short getXFIndex() {
        return this._formulaRecord.getXFIndex();
    }

    public void setXFIndex(short xf) {
        this._formulaRecord.setXFIndex(xf);
    }

    public void setColumn(short col) {
        this._formulaRecord.setColumn(col);
    }

    public void setRow(int row) {
        this._formulaRecord.setRow(row);
    }

    public short getColumn() {
        return this._formulaRecord.getColumn();
    }

    public int getRow() {
        return this._formulaRecord.getRow();
    }

    public String toString() {
        return this._formulaRecord.toString();
    }

    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        StringRecord stringRecord;
        rv.visitRecord(this._formulaRecord);
        Record sharedFormulaRecord = this._sharedValueManager.getRecordForFirstCell(this);
        if (sharedFormulaRecord != null) {
            rv.visitRecord(sharedFormulaRecord);
        }
        if (this._formulaRecord.hasCachedResultString() && (stringRecord = this._stringRecord) != null) {
            rv.visitRecord(stringRecord);
        }
    }

    public String getStringValue() {
        StringRecord stringRecord = this._stringRecord;
        if (stringRecord == null) {
            return null;
        }
        return stringRecord.getString();
    }

    public void setCachedStringResult(String value) {
        if (this._stringRecord == null) {
            this._stringRecord = new StringRecord();
        }
        this._stringRecord.setString(value);
        if (value.length() < 1) {
            this._formulaRecord.setCachedResultTypeEmptyString();
        } else {
            this._formulaRecord.setCachedResultTypeString();
        }
    }

    public void setCachedBooleanResult(boolean value) {
        this._stringRecord = null;
        this._formulaRecord.setCachedResultBoolean(value);
    }

    public void setCachedErrorResult(int errorCode) {
        this._stringRecord = null;
        this._formulaRecord.setCachedResultErrorCode(errorCode);
    }

    public void setCachedDoubleResult(double value) {
        this._stringRecord = null;
        this._formulaRecord.setValue(value);
    }

    public Ptg[] getFormulaTokens() {
        SharedFormulaRecord sharedFormulaRecord = this._sharedFormulaRecord;
        if (sharedFormulaRecord != null) {
            return sharedFormulaRecord.getFormulaTokens(this._formulaRecord);
        }
        CellReference expRef = this._formulaRecord.getFormula().getExpReference();
        if (expRef != null) {
            return this._sharedValueManager.getArrayRecord(expRef.getRow(), expRef.getCol()).getFormulaTokens();
        }
        return this._formulaRecord.getParsedExpression();
    }

    public void setParsedExpression(Ptg[] ptgs) {
        notifyFormulaChanging();
        this._formulaRecord.setParsedExpression(ptgs);
    }

    public void unlinkSharedFormula() {
        SharedFormulaRecord sfr = this._sharedFormulaRecord;
        if (sfr != null) {
            this._formulaRecord.setParsedExpression(sfr.getFormulaTokens(this._formulaRecord));
            this._formulaRecord.setSharedFormula(false);
            this._sharedFormulaRecord = null;
            return;
        }
        throw new IllegalStateException("Formula not linked to shared formula");
    }

    public void notifyFormulaChanging() {
        SharedFormulaRecord sharedFormulaRecord = this._sharedFormulaRecord;
        if (sharedFormulaRecord != null) {
            this._sharedValueManager.unlink(sharedFormulaRecord);
        }
    }

    public boolean isPartOfArrayFormula() {
        if (this._sharedFormulaRecord != null) {
            return false;
        }
        CellReference expRef = this._formulaRecord.getFormula().getExpReference();
        if ((expRef == null ? null : this._sharedValueManager.getArrayRecord(expRef.getRow(), expRef.getCol())) != null) {
            return true;
        }
        return false;
    }

    public CellRangeAddress getArrayFormulaRange() {
        if (this._sharedFormulaRecord == null) {
            CellReference expRef = this._formulaRecord.getFormula().getExpReference();
            if (expRef != null) {
                ArrayRecord arec = this._sharedValueManager.getArrayRecord(expRef.getRow(), expRef.getCol());
                if (arec != null) {
                    CellRangeAddress8Bit a = arec.getRange();
                    return new CellRangeAddress(a.getFirstRow(), a.getLastRow(), a.getFirstColumn(), a.getLastColumn());
                }
                throw new IllegalStateException("ArrayRecord was not found for the locator " + expRef.formatAsString());
            }
            throw new IllegalStateException("not an array formula cell.");
        }
        throw new IllegalStateException("not an array formula cell.");
    }

    public void setArrayFormula(CellRangeAddress r, Ptg[] ptgs) {
        this._sharedValueManager.addArrayRecord(new ArrayRecord(Formula.create(ptgs), new CellRangeAddress8Bit(r.getFirstRow(), r.getLastRow(), r.getFirstColumn(), r.getLastColumn())));
    }

    public CellRangeAddress removeArrayFormula(int rowIndex, int columnIndex) {
        CellRangeAddress8Bit a = this._sharedValueManager.removeArrayFormula(rowIndex, columnIndex);
        this._formulaRecord.setParsedExpression((Ptg[]) null);
        return new CellRangeAddress(a.getFirstRow(), a.getLastRow(), a.getFirstColumn(), a.getLastColumn());
    }
}
