package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.ContinueRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.HeaderFooterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.HorizontalPageBreakRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.Margin;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.UserSViewBegin;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.VerticalPageBreakRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;

public final class PageSettingsBlock extends RecordAggregate {
    private Record _bitmap;
    private BottomMarginRecord _bottomMargin;
    private PageBreakRecord _columnBreaksRecord;
    private FooterRecord _footer;
    private HCenterRecord _hCenter;
    private HeaderRecord _header;
    private HeaderFooterRecord _headerFooter;
    private LeftMarginRecord _leftMargin;
    private final List<PLSAggregate> _plsRecords;
    private PrintSetupRecord _printSetup;
    private Record _printSize;
    private RightMarginRecord _rightMargin;
    private PageBreakRecord _rowBreaksRecord;
    /* access modifiers changed from: private */
    public List<HeaderFooterRecord> _sviewHeaderFooters;
    private TopMarginRecord _topMargin;
    private VCenterRecord _vCenter;

    private static final class PLSAggregate extends RecordAggregate {
        private static final ContinueRecord[] EMPTY_CONTINUE_RECORD_ARRAY = new ContinueRecord[0];
        private final Record _pls;
        private ContinueRecord[] _plsContinues;

        public PLSAggregate(RecordStream rs) {
            this._pls = rs.getNext();
            if (rs.peekNextSid() == 60) {
                List<ContinueRecord> temp = new ArrayList<>();
                while (rs.peekNextSid() == 60) {
                    temp.add((ContinueRecord) rs.getNext());
                }
                ContinueRecord[] continueRecordArr = new ContinueRecord[temp.size()];
                this._plsContinues = continueRecordArr;
                temp.toArray(continueRecordArr);
                return;
            }
            this._plsContinues = EMPTY_CONTINUE_RECORD_ARRAY;
        }

        public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
            rv.visitRecord(this._pls);
            int i = 0;
            while (true) {
                ContinueRecord[] continueRecordArr = this._plsContinues;
                if (i < continueRecordArr.length) {
                    rv.visitRecord(continueRecordArr[i]);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public PageSettingsBlock(RecordStream rs) {
        this._sviewHeaderFooters = new ArrayList();
        this._plsRecords = new ArrayList();
        do {
        } while (readARecord(rs));
    }

    public PageSettingsBlock() {
        this._sviewHeaderFooters = new ArrayList();
        this._plsRecords = new ArrayList();
        this._rowBreaksRecord = new HorizontalPageBreakRecord();
        this._columnBreaksRecord = new VerticalPageBreakRecord();
        this._header = new HeaderRecord("");
        this._footer = new FooterRecord("");
        this._hCenter = createHCenter();
        this._vCenter = createVCenter();
        this._printSetup = createPrintSetup();
    }

    public static boolean isComponentRecord(int sid) {
        if (sid == 20 || sid == 21 || sid == 26 || sid == 27 || sid == 51 || sid == 77 || sid == 161 || sid == 233 || sid == 2204 || sid == 131 || sid == 132) {
            return true;
        }
        switch (sid) {
            case 38:
            case 39:
            case 40:
            case 41:
                return true;
            default:
                return false;
        }
    }

    private boolean readARecord(RecordStream rs) {
        int peekNextSid = rs.peekNextSid();
        if (peekNextSid == 20) {
            checkNotPresent(this._header);
            this._header = (HeaderRecord) rs.getNext();
            return true;
        } else if (peekNextSid == 21) {
            checkNotPresent(this._footer);
            this._footer = (FooterRecord) rs.getNext();
            return true;
        } else if (peekNextSid == 26) {
            checkNotPresent(this._columnBreaksRecord);
            this._columnBreaksRecord = (PageBreakRecord) rs.getNext();
            return true;
        } else if (peekNextSid == 27) {
            checkNotPresent(this._rowBreaksRecord);
            this._rowBreaksRecord = (PageBreakRecord) rs.getNext();
            return true;
        } else if (peekNextSid == 51) {
            checkNotPresent(this._printSize);
            this._printSize = rs.getNext();
            return true;
        } else if (peekNextSid == 77) {
            this._plsRecords.add(new PLSAggregate(rs));
            return true;
        } else if (peekNextSid == 161) {
            checkNotPresent(this._printSetup);
            this._printSetup = (PrintSetupRecord) rs.getNext();
            return true;
        } else if (peekNextSid == 233) {
            checkNotPresent(this._bitmap);
            this._bitmap = rs.getNext();
            return true;
        } else if (peekNextSid == 2204) {
            HeaderFooterRecord hf = (HeaderFooterRecord) rs.getNext();
            if (hf.isCurrentSheet()) {
                this._headerFooter = hf;
                return true;
            }
            this._sviewHeaderFooters.add(hf);
            return true;
        } else if (peekNextSid == 131) {
            checkNotPresent(this._hCenter);
            this._hCenter = (HCenterRecord) rs.getNext();
            return true;
        } else if (peekNextSid != 132) {
            switch (peekNextSid) {
                case 38:
                    checkNotPresent(this._leftMargin);
                    this._leftMargin = (LeftMarginRecord) rs.getNext();
                    return true;
                case 39:
                    checkNotPresent(this._rightMargin);
                    this._rightMargin = (RightMarginRecord) rs.getNext();
                    return true;
                case 40:
                    checkNotPresent(this._topMargin);
                    this._topMargin = (TopMarginRecord) rs.getNext();
                    return true;
                case 41:
                    checkNotPresent(this._bottomMargin);
                    this._bottomMargin = (BottomMarginRecord) rs.getNext();
                    return true;
                default:
                    return false;
            }
        } else {
            checkNotPresent(this._vCenter);
            this._vCenter = (VCenterRecord) rs.getNext();
            return true;
        }
    }

    private void checkNotPresent(Record rec) {
        if (rec != null) {
            throw new RecordFormatException("Duplicate PageSettingsBlock record (sid=0x" + Integer.toHexString(rec.getSid()) + ")");
        }
    }

    private PageBreakRecord getRowBreaksRecord() {
        if (this._rowBreaksRecord == null) {
            this._rowBreaksRecord = new HorizontalPageBreakRecord();
        }
        return this._rowBreaksRecord;
    }

    private PageBreakRecord getColumnBreaksRecord() {
        if (this._columnBreaksRecord == null) {
            this._columnBreaksRecord = new VerticalPageBreakRecord();
        }
        return this._columnBreaksRecord;
    }

    public void setColumnBreak(short column, short fromRow, short toRow) {
        getColumnBreaksRecord().addBreak(column, fromRow, toRow);
    }

    public void removeColumnBreak(int column) {
        getColumnBreaksRecord().removeBreak(column);
    }

    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        visitIfPresent(this._rowBreaksRecord, rv);
        visitIfPresent(this._columnBreaksRecord, rv);
        HeaderRecord headerRecord = this._header;
        if (headerRecord == null) {
            rv.visitRecord(new HeaderRecord(""));
        } else {
            rv.visitRecord(headerRecord);
        }
        FooterRecord footerRecord = this._footer;
        if (footerRecord == null) {
            rv.visitRecord(new FooterRecord(""));
        } else {
            rv.visitRecord(footerRecord);
        }
        visitIfPresent((Record) this._hCenter, rv);
        visitIfPresent((Record) this._vCenter, rv);
        visitIfPresent((Record) this._leftMargin, rv);
        visitIfPresent((Record) this._rightMargin, rv);
        visitIfPresent((Record) this._topMargin, rv);
        visitIfPresent((Record) this._bottomMargin, rv);
        for (PLSAggregate pls : this._plsRecords) {
            pls.visitContainedRecords(rv);
        }
        visitIfPresent((Record) this._printSetup, rv);
        visitIfPresent(this._bitmap, rv);
        visitIfPresent(this._printSize, rv);
        visitIfPresent((Record) this._headerFooter, rv);
    }

    private static void visitIfPresent(Record r, RecordAggregate.RecordVisitor rv) {
        if (r != null) {
            rv.visitRecord(r);
        }
    }

    private static void visitIfPresent(PageBreakRecord r, RecordAggregate.RecordVisitor rv) {
        if (r != null && !r.isEmpty()) {
            rv.visitRecord(r);
        }
    }

    private static HCenterRecord createHCenter() {
        HCenterRecord retval = new HCenterRecord();
        retval.setHCenter(false);
        return retval;
    }

    private static VCenterRecord createVCenter() {
        VCenterRecord retval = new VCenterRecord();
        retval.setVCenter(false);
        return retval;
    }

    private static PrintSetupRecord createPrintSetup() {
        PrintSetupRecord retval = new PrintSetupRecord();
        retval.setPaperSize(1);
        retval.setScale(100);
        retval.setPageStart(1);
        retval.setFitWidth(1);
        retval.setFitHeight(1);
        retval.setOptions(2);
        retval.setHResolution(300);
        retval.setVResolution(300);
        retval.setHeaderMargin(0.5d);
        retval.setFooterMargin(0.5d);
        retval.setCopies(1);
        return retval;
    }

    public HeaderRecord getHeader() {
        return this._header;
    }

    public void setHeader(HeaderRecord newHeader) {
        this._header = newHeader;
    }

    public FooterRecord getFooter() {
        return this._footer;
    }

    public void setFooter(FooterRecord newFooter) {
        this._footer = newFooter;
    }

    public PrintSetupRecord getPrintSetup() {
        return this._printSetup;
    }

    public void setPrintSetup(PrintSetupRecord newPrintSetup) {
        this._printSetup = newPrintSetup;
    }

    private Margin getMarginRec(int marginIndex) {
        if (marginIndex == 0) {
            return this._leftMargin;
        }
        if (marginIndex == 1) {
            return this._rightMargin;
        }
        if (marginIndex == 2) {
            return this._topMargin;
        }
        if (marginIndex == 3) {
            return this._bottomMargin;
        }
        throw new IllegalArgumentException("Unknown margin constant:  " + marginIndex);
    }

    public double getMargin(short margin) {
        Margin m = getMarginRec(margin);
        if (m != null) {
            return m.getMargin();
        }
        if (margin == 0 || margin == 1) {
            return 0.75d;
        }
        if (margin == 2 || margin == 3) {
            return 1.0d;
        }
        throw new IllegalArgumentException("Unknown margin constant:  " + margin);
    }

    public void setMargin(short margin, double size) {
        Margin m = getMarginRec(margin);
        if (m == null) {
            if (margin == 0) {
                this._leftMargin = new LeftMarginRecord();
                m = this._leftMargin;
            } else if (margin == 1) {
                this._rightMargin = new RightMarginRecord();
                m = this._rightMargin;
            } else if (margin == 2) {
                this._topMargin = new TopMarginRecord();
                m = this._topMargin;
            } else if (margin == 3) {
                this._bottomMargin = new BottomMarginRecord();
                m = this._bottomMargin;
            } else {
                throw new IllegalArgumentException("Unknown margin constant:  " + margin);
            }
        }
        m.setMargin(size);
    }

    private static void shiftBreaks(PageBreakRecord breaks, int start, int stop, int count) {
        Iterator<PageBreakRecord.Break> iterator = breaks.getBreaksIterator();
        List<PageBreakRecord.Break> shiftedBreak = new ArrayList<>();
        while (iterator.hasNext()) {
            PageBreakRecord.Break breakItem = iterator.next();
            int breakLocation = breakItem.main;
            boolean inEnd = true;
            boolean inStart = breakLocation >= start;
            if (breakLocation > stop) {
                inEnd = false;
            }
            if (inStart && inEnd) {
                shiftedBreak.add(breakItem);
            }
        }
        for (PageBreakRecord.Break breakItem2 : shiftedBreak) {
            breaks.removeBreak(breakItem2.main);
            breaks.addBreak((short) (breakItem2.main + count), breakItem2.subFrom, breakItem2.subTo);
        }
    }

    public void setRowBreak(int row, short fromCol, short toCol) {
        getRowBreaksRecord().addBreak((short) row, fromCol, toCol);
    }

    public void removeRowBreak(int row) {
        if (getRowBreaksRecord().getBreaks().length >= 1) {
            getRowBreaksRecord().removeBreak((short) row);
            return;
        }
        throw new IllegalArgumentException("Sheet does not define any row breaks");
    }

    public boolean isRowBroken(int row) {
        return getRowBreaksRecord().getBreak(row) != null;
    }

    public boolean isColumnBroken(int column) {
        return getColumnBreaksRecord().getBreak(column) != null;
    }

    public void shiftRowBreaks(int startingRow, int endingRow, int count) {
        shiftBreaks(getRowBreaksRecord(), startingRow, endingRow, count);
    }

    public void shiftColumnBreaks(short startingCol, short endingCol, short count) {
        shiftBreaks(getColumnBreaksRecord(), startingCol, endingCol, count);
    }

    public int[] getRowBreaks() {
        return getRowBreaksRecord().getBreaks();
    }

    public int getNumRowBreaks() {
        return getRowBreaksRecord().getNumBreaks();
    }

    public int[] getColumnBreaks() {
        return getColumnBreaksRecord().getBreaks();
    }

    public int getNumColumnBreaks() {
        return getColumnBreaksRecord().getNumBreaks();
    }

    public VCenterRecord getVCenter() {
        return this._vCenter;
    }

    public HCenterRecord getHCenter() {
        return this._hCenter;
    }

    public void addLateHeaderFooter(HeaderFooterRecord rec) {
        if (this._headerFooter != null) {
            throw new IllegalStateException("This page settings block already has a header/footer record");
        } else if (rec.getSid() == 2204) {
            this._headerFooter = rec;
        } else {
            throw new RecordFormatException("Unexpected header-footer record sid: 0x" + Integer.toHexString(rec.getSid()));
        }
    }

    public void addLateRecords(RecordStream rs) {
        do {
        } while (readARecord(rs));
    }

    public void positionRecords(List<RecordBase> sheetRecords) {
        for (final HeaderFooterRecord hf : new ArrayList<>(this._sviewHeaderFooters)) {
            for (RecordBase rb : sheetRecords) {
                if (rb instanceof CustomViewSettingsRecordAggregate) {
                    final CustomViewSettingsRecordAggregate cv = (CustomViewSettingsRecordAggregate) rb;
                    cv.visitContainedRecords(new RecordAggregate.RecordVisitor() {
                        public void visitRecord(Record r) {
                            if (r.getSid() == 426 && Arrays.equals(((UserSViewBegin) r).getGuid(), hf.getGuid())) {
                                cv.append(hf);
                                PageSettingsBlock.this._sviewHeaderFooters.remove(hf);
                            }
                        }
                    });
                }
            }
        }
    }
}
