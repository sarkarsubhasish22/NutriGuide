package org.apache.poi.hssf.model;

import java.util.List;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import org.apache.poi.hssf.record.aggregates.ConditionalFormattingTable;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.hssf.record.aggregates.MergedCellsTable;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;
import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;

final class RecordOrderer {
    private RecordOrderer() {
    }

    public static void addNewSheetRecord(List<RecordBase> sheetRecords, RecordBase newRecord) {
        sheetRecords.add(findSheetInsertPos(sheetRecords, newRecord.getClass()), newRecord);
    }

    private static int findSheetInsertPos(List<RecordBase> records, Class<? extends RecordBase> recClass) {
        if (recClass == DataValidityTable.class) {
            return findDataValidationTableInsertPos(records);
        }
        if (recClass == MergedCellsTable.class) {
            return findInsertPosForNewMergedRecordTable(records);
        }
        if (recClass == ConditionalFormattingTable.class) {
            return findInsertPosForNewCondFormatTable(records);
        }
        if (recClass == GutsRecord.class) {
            return getGutsRecordInsertPos(records);
        }
        if (recClass == PageSettingsBlock.class) {
            return getPageBreakRecordInsertPos(records);
        }
        if (recClass == WorksheetProtectionBlock.class) {
            return getWorksheetProtectionBlockInsertPos(records);
        }
        throw new RuntimeException("Unexpected record class (" + recClass.getName() + ")");
    }

    private static int getWorksheetProtectionBlockInsertPos(List<RecordBase> records) {
        int i = getDimensionsIndex(records);
        while (i > 0) {
            i--;
            if (!isProtectionSubsequentRecord(records.get(i))) {
                return i + 1;
            }
        }
        throw new IllegalStateException("did not find insert pos for protection block");
    }

    private static boolean isProtectionSubsequentRecord(Object rb) {
        if (rb instanceof ColumnInfoRecordsAggregate) {
            return true;
        }
        if (!(rb instanceof Record)) {
            return false;
        }
        short sid = ((Record) rb).getSid();
        if (sid == 85 || sid == 144) {
            return true;
        }
        return false;
    }

    private static int getPageBreakRecordInsertPos(List<RecordBase> records) {
        int i = getDimensionsIndex(records) - 1;
        while (i > 0) {
            i--;
            if (isPageBreakPriorRecord(records.get(i))) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find insert point for GUTS");
    }

    private static boolean isPageBreakPriorRecord(Object rb) {
        if (!(rb instanceof Record)) {
            return false;
        }
        short sid = ((Record) rb).getSid();
        if (sid == 34 || sid == 523 || sid == 549 || sid == 2057 || sid == 42 || sid == 43 || sid == 94 || sid == 95 || sid == 129 || sid == 130) {
            return true;
        }
        switch (sid) {
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return true;
            default:
                return false;
        }
    }

    private static int findInsertPosForNewCondFormatTable(List<RecordBase> records) {
        short sid;
        for (int i = records.size() - 2; i >= 0; i--) {
            RecordBase recordBase = records.get(i);
            if (recordBase instanceof MergedCellsTable) {
                return i + 1;
            }
            if (!(recordBase instanceof DataValidityTable) && ((sid = ((Record) recordBase).getSid()) == 29 || sid == 65 || sid == 153 || sid == 160 || sid == 239 || sid == 351 || sid == 574)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find Window2 record");
    }

    private static int findInsertPosForNewMergedRecordTable(List<RecordBase> records) {
        short sid;
        for (int i = records.size() - 2; i >= 0; i--) {
            RecordBase recordBase = records.get(i);
            if ((recordBase instanceof Record) && ((sid = ((Record) recordBase).getSid()) == 29 || sid == 65 || sid == 153 || sid == 160 || sid == 574)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find Window2 record");
    }

    private static int findDataValidationTableInsertPos(List<RecordBase> records) {
        int i = records.size() - 1;
        if (records.get(i) instanceof EOFRecord) {
            while (i > 0) {
                i--;
                RecordBase rb = records.get(i);
                if (isDVTPriorRecord(rb)) {
                    Record nextRec = (Record) records.get(i + 1);
                    if (isDVTSubsequentRecord(nextRec.getSid())) {
                        return i + 1;
                    }
                    throw new IllegalStateException("Unexpected (" + nextRec.getClass().getName() + ") found after (" + rb.getClass().getName() + ")");
                }
                Record rec = (Record) rb;
                if (!isDVTSubsequentRecord(rec.getSid())) {
                    throw new IllegalStateException("Unexpected (" + rec.getClass().getName() + ") while looking for DV Table insert pos");
                }
            }
            return 0;
        }
        throw new IllegalStateException("Last sheet record should be EOFRecord");
    }

    private static boolean isDVTPriorRecord(RecordBase rb) {
        short sid;
        if ((rb instanceof MergedCellsTable) || (rb instanceof ConditionalFormattingTable) || (sid = ((Record) rb).getSid()) == 29 || sid == 65 || sid == 153 || sid == 160 || sid == 239 || sid == 351 || sid == 440 || sid == 442 || sid == 574 || sid == 2048) {
            return true;
        }
        return false;
    }

    private static boolean isDVTSubsequentRecord(short sid) {
        if (sid == 10 || sid == 2146 || sid == 2151 || sid == 2152) {
            return true;
        }
        return false;
    }

    private static int getDimensionsIndex(List<RecordBase> records) {
        int nRecs = records.size();
        for (int i = 0; i < nRecs; i++) {
            if (records.get(i) instanceof DimensionsRecord) {
                return i;
            }
        }
        throw new RuntimeException("DimensionsRecord not found");
    }

    private static int getGutsRecordInsertPos(List<RecordBase> records) {
        int i = getDimensionsIndex(records) - 1;
        while (i > 0) {
            i--;
            if (isGutsPriorRecord(records.get(i))) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find insert point for GUTS");
    }

    private static boolean isGutsPriorRecord(RecordBase rb) {
        if (!(rb instanceof Record)) {
            return false;
        }
        short sid = ((Record) rb).getSid();
        if (sid == 34 || sid == 130 || sid == 523 || sid == 2057 || sid == 42 || sid == 43 || sid == 94 || sid == 95) {
            return true;
        }
        switch (sid) {
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return true;
            default:
                return false;
        }
    }

    public static boolean isEndOfRowBlock(int sid) {
        if (sid == 10) {
            throw new RuntimeException("Found EOFRecord before WindowTwoRecord was encountered");
        } else if (sid == 61 || sid == 93 || sid == 176 || sid == 434 || sid == 438 || sid == 574 || sid == 236 || sid == 237) {
            return true;
        } else {
            return PageSettingsBlock.isComponentRecord(sid);
        }
    }

    public static boolean isRowBlockRecord(int sid) {
        if (sid == 6 || sid == 253 || sid == 513 || sid == 520 || sid == 545 || sid == 566 || sid == 638 || sid == 1212) {
            return true;
        }
        switch (sid) {
            case 515:
            case 516:
            case 517:
                return true;
            default:
                return false;
        }
    }
}
