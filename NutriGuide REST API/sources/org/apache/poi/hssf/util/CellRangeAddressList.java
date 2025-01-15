package org.apache.poi.hssf.util;

import org.apache.poi.hssf.record.RecordInputStream;

public class CellRangeAddressList extends org.apache.poi.ss.util.CellRangeAddressList {
    public CellRangeAddressList(int firstRow, int lastRow, int firstCol, int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }

    public CellRangeAddressList() {
    }

    public CellRangeAddressList(RecordInputStream in) {
        int nItems = in.readUShort();
        for (int k = 0; k < nItems; k++) {
            this._list.add(new CellRangeAddress(in));
        }
    }
}
