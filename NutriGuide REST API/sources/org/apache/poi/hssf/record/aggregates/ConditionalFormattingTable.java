package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.CFHeaderRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.record.formula.FormulaShifter;

public final class ConditionalFormattingTable extends RecordAggregate {
    private final List _cfHeaders;

    public ConditionalFormattingTable() {
        this._cfHeaders = new ArrayList();
    }

    public ConditionalFormattingTable(RecordStream rs) {
        List temp = new ArrayList();
        while (rs.peekNextClass() == CFHeaderRecord.class) {
            temp.add(CFRecordsAggregate.createCFAggregate(rs));
        }
        this._cfHeaders = temp;
    }

    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        for (int i = 0; i < this._cfHeaders.size(); i++) {
            ((CFRecordsAggregate) this._cfHeaders.get(i)).visitContainedRecords(rv);
        }
    }

    public int add(CFRecordsAggregate cfAggregate) {
        this._cfHeaders.add(cfAggregate);
        return this._cfHeaders.size() - 1;
    }

    public int size() {
        return this._cfHeaders.size();
    }

    public CFRecordsAggregate get(int index) {
        checkIndex(index);
        return (CFRecordsAggregate) this._cfHeaders.get(index);
    }

    public void remove(int index) {
        checkIndex(index);
        this._cfHeaders.remove(index);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this._cfHeaders.size()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Specified CF index ");
            sb.append(index);
            sb.append(" is outside the allowable range (0..");
            sb.append(this._cfHeaders.size() - 1);
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public void updateFormulasAfterCellShift(FormulaShifter shifter, int externSheetIndex) {
        int i = 0;
        while (i < this._cfHeaders.size()) {
            if (!((CFRecordsAggregate) this._cfHeaders.get(i)).updateFormulasAfterCellShift(shifter, externSheetIndex)) {
                this._cfHeaders.remove(i);
                i--;
            }
            i++;
        }
    }
}
