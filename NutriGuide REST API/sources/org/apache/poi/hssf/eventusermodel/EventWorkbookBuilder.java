package org.apache.poi.hssf.eventusermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class EventWorkbookBuilder {
    public static InternalWorkbook createStubWorkbook(ExternSheetRecord[] externs, BoundSheetRecord[] bounds, SSTRecord sst) {
        List wbRecords = new ArrayList();
        if (bounds != null) {
            for (BoundSheetRecord add : bounds) {
                wbRecords.add(add);
            }
        }
        if (sst != null) {
            wbRecords.add(sst);
        }
        if (externs != null) {
            wbRecords.add(SupBookRecord.createInternalReferences((short) externs.length));
            for (ExternSheetRecord add2 : externs) {
                wbRecords.add(add2);
            }
        }
        wbRecords.add(EOFRecord.instance);
        return InternalWorkbook.createWorkbook(wbRecords);
    }

    public static InternalWorkbook createStubWorkbook(ExternSheetRecord[] externs, BoundSheetRecord[] bounds) {
        return createStubWorkbook(externs, bounds, (SSTRecord) null);
    }

    public static class SheetRecordCollectingListener implements HSSFListener {
        private List boundSheetRecords = new ArrayList();
        private HSSFListener childListener;
        private List externSheetRecords = new ArrayList();
        private SSTRecord sstRecord = null;

        public SheetRecordCollectingListener(HSSFListener childListener2) {
            this.childListener = childListener2;
        }

        public BoundSheetRecord[] getBoundSheetRecords() {
            List list = this.boundSheetRecords;
            return (BoundSheetRecord[]) list.toArray(new BoundSheetRecord[list.size()]);
        }

        public ExternSheetRecord[] getExternSheetRecords() {
            List list = this.externSheetRecords;
            return (ExternSheetRecord[]) list.toArray(new ExternSheetRecord[list.size()]);
        }

        public SSTRecord getSSTRecord() {
            return this.sstRecord;
        }

        public HSSFWorkbook getStubHSSFWorkbook() {
            return HSSFWorkbook.create(getStubWorkbook());
        }

        public InternalWorkbook getStubWorkbook() {
            return EventWorkbookBuilder.createStubWorkbook(getExternSheetRecords(), getBoundSheetRecords(), getSSTRecord());
        }

        public void processRecord(Record record) {
            processRecordInternally(record);
            this.childListener.processRecord(record);
        }

        public void processRecordInternally(Record record) {
            if (record instanceof BoundSheetRecord) {
                this.boundSheetRecords.add(record);
            } else if (record instanceof ExternSheetRecord) {
                this.externSheetRecords.add(record);
            } else if (record instanceof SSTRecord) {
                this.sstRecord = (SSTRecord) record;
            }
        }
    }
}
