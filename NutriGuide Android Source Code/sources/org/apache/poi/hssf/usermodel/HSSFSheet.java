package org.apache.poi.hssf.usermodel;

import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.PrintWriter;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.AutoFilterInfoRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.FormulaShifter;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.util.PaneInformation;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.Region;
import org.apache.poi.ss.util.SSCellRange;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public final class HSSFSheet implements Sheet {
    private static final int DEBUG = POILogger.DEBUG;
    public static final int INITIAL_CAPACITY = 20;
    private static final POILogger log = POILogFactory.getLogger(HSSFSheet.class);
    protected final InternalWorkbook _book;
    private int _firstrow;
    private int _lastrow;
    private HSSFPatriarch _patriarch;
    private final TreeMap<Integer, HSSFRow> _rows;
    private final InternalSheet _sheet;
    protected final HSSFWorkbook _workbook;

    protected HSSFSheet(HSSFWorkbook workbook) {
        this._sheet = InternalSheet.createSheet();
        this._rows = new TreeMap<>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
    }

    protected HSSFSheet(HSSFWorkbook workbook, InternalSheet sheet) {
        this._sheet = sheet;
        this._rows = new TreeMap<>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
        setPropertiesFromSheet(sheet);
    }

    /* access modifiers changed from: package-private */
    public HSSFSheet cloneSheet(HSSFWorkbook workbook) {
        return new HSSFSheet(workbook, this._sheet.cloneSheet());
    }

    public HSSFWorkbook getWorkbook() {
        return this._workbook;
    }

    private void setPropertiesFromSheet(InternalSheet sheet) {
        HSSFSheet hSSFSheet = this;
        RowRecord row = sheet.getNextRow();
        boolean rowRecordsAlreadyPresent = row != null;
        while (row != null) {
            hSSFSheet.createRowFromRecord(row);
            row = sheet.getNextRow();
        }
        CellValueRecordInterface[] cvals = sheet.getValueRecords();
        long timestart = System.currentTimeMillis();
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "Time at start of cell creating in HSSF sheet = ", (Object) Long.valueOf(timestart));
        }
        HSSFRow lastrow = null;
        int i = 0;
        while (i < cvals.length) {
            CellValueRecordInterface cval = cvals[i];
            long cellstart = System.currentTimeMillis();
            HSSFRow hrow = lastrow;
            if (hrow == null || hrow.getRowNum() != cval.getRow()) {
                hrow = hSSFSheet.getRow(cval.getRow());
                lastrow = hrow;
                if (hrow != null) {
                    InternalSheet internalSheet = sheet;
                } else if (!rowRecordsAlreadyPresent) {
                    RowRecord rowRec = new RowRecord(cval.getRow());
                    sheet.addRow(rowRec);
                    hrow = hSSFSheet.createRowFromRecord(rowRec);
                } else {
                    InternalSheet internalSheet2 = sheet;
                    throw new RuntimeException("Unexpected missing row when some rows already present");
                }
            } else {
                InternalSheet internalSheet3 = sheet;
            }
            POILogger pOILogger2 = log;
            if (pOILogger2.check(POILogger.DEBUG)) {
                int i2 = DEBUG;
                pOILogger2.log(i2, (Object) "record id = " + Integer.toHexString(((Record) cval).getSid()));
            }
            hrow.createCellFromRecord(cval);
            if (pOILogger2.check(POILogger.DEBUG)) {
                pOILogger2.log(DEBUG, (Object) "record took ", (Object) Long.valueOf(System.currentTimeMillis() - cellstart));
            }
            i++;
            hSSFSheet = this;
        }
        InternalSheet internalSheet4 = sheet;
        POILogger pOILogger3 = log;
        if (pOILogger3.check(POILogger.DEBUG)) {
            pOILogger3.log(DEBUG, (Object) "total sheet cell creation took ", (Object) Long.valueOf(System.currentTimeMillis() - timestart));
        }
    }

    public HSSFRow createRow(int rownum) {
        HSSFRow row = new HSSFRow(this._workbook, this, rownum);
        addRow(row, true);
        return row;
    }

    private HSSFRow createRowFromRecord(RowRecord row) {
        HSSFRow hrow = new HSSFRow(this._workbook, this, row);
        addRow(hrow, false);
        return hrow;
    }

    public void removeRow(Row row) {
        HSSFRow hrow = (HSSFRow) row;
        if (row.getSheet() == this) {
            Iterator i$ = row.iterator();
            while (i$.hasNext()) {
                HSSFCell xcell = (HSSFCell) ((Cell) i$.next());
                if (xcell.isPartOfArrayFormulaGroup()) {
                    xcell.notifyArrayFormulaChanging("Row[rownum=" + row.getRowNum() + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.");
                }
            }
            if (this._rows.size() > 0) {
                if (this._rows.remove(Integer.valueOf(row.getRowNum())) == row) {
                    if (hrow.getRowNum() == getLastRowNum()) {
                        this._lastrow = findLastRow(this._lastrow);
                    }
                    if (hrow.getRowNum() == getFirstRowNum()) {
                        this._firstrow = findFirstRow(this._firstrow);
                    }
                    this._sheet.removeRow(hrow.getRowRecord());
                    return;
                }
                throw new IllegalArgumentException("Specified row does not belong to this sheet");
            }
            return;
        }
        throw new IllegalArgumentException("Specified row does not belong to this sheet");
    }

    private int findLastRow(int lastrow) {
        if (lastrow < 1) {
            return 0;
        }
        int rownum = lastrow - 1;
        HSSFRow r = getRow(rownum);
        while (r == null && rownum > 0) {
            rownum--;
            r = getRow(rownum);
        }
        if (r == null) {
            return 0;
        }
        return rownum;
    }

    private int findFirstRow(int firstrow) {
        int rownum = firstrow + 1;
        HSSFRow r = getRow(rownum);
        while (r == null && rownum <= getLastRowNum()) {
            rownum++;
            r = getRow(rownum);
        }
        if (rownum > getLastRowNum()) {
            return 0;
        }
        return rownum;
    }

    private void addRow(HSSFRow row, boolean addLow) {
        this._rows.put(Integer.valueOf(row.getRowNum()), row);
        if (addLow) {
            this._sheet.addRow(row.getRowRecord());
        }
        boolean z = true;
        if (this._rows.size() != 1) {
            z = false;
        }
        boolean firstRow = z;
        if (row.getRowNum() > getLastRowNum() || firstRow) {
            this._lastrow = row.getRowNum();
        }
        if (row.getRowNum() < getFirstRowNum() || firstRow) {
            this._firstrow = row.getRowNum();
        }
    }

    public HSSFRow getRow(int rowIndex) {
        return this._rows.get(Integer.valueOf(rowIndex));
    }

    public int getPhysicalNumberOfRows() {
        return this._rows.size();
    }

    public int getFirstRowNum() {
        return this._firstrow;
    }

    public int getLastRowNum() {
        return this._lastrow;
    }

    public void addValidationData(DataValidation dataValidation) {
        if (dataValidation != null) {
            this._sheet.getOrCreateDataValidityTable().addDataValidation(((HSSFDataValidation) dataValidation).createDVRecord(this));
            return;
        }
        throw new IllegalArgumentException("objValidation must not be null");
    }

    public void setColumnHidden(short columnIndex, boolean hidden) {
        setColumnHidden((int) 65535 & columnIndex, hidden);
    }

    public boolean isColumnHidden(short columnIndex) {
        return isColumnHidden((int) 65535 & columnIndex);
    }

    public void setColumnWidth(short columnIndex, short width) {
        setColumnWidth((int) columnIndex & 65535, (int) 65535 & width);
    }

    public short getColumnWidth(short columnIndex) {
        return (short) getColumnWidth((int) 65535 & columnIndex);
    }

    public void setDefaultColumnWidth(short width) {
        setDefaultColumnWidth((int) 65535 & width);
    }

    public void setColumnHidden(int columnIndex, boolean hidden) {
        this._sheet.setColumnHidden(columnIndex, hidden);
    }

    public boolean isColumnHidden(int columnIndex) {
        return this._sheet.isColumnHidden(columnIndex);
    }

    public void setColumnWidth(int columnIndex, int width) {
        this._sheet.setColumnWidth(columnIndex, width);
    }

    public int getColumnWidth(int columnIndex) {
        return this._sheet.getColumnWidth(columnIndex);
    }

    public int getDefaultColumnWidth() {
        return this._sheet.getDefaultColumnWidth();
    }

    public void setDefaultColumnWidth(int width) {
        this._sheet.setDefaultColumnWidth(width);
    }

    public short getDefaultRowHeight() {
        return this._sheet.getDefaultRowHeight();
    }

    public float getDefaultRowHeightInPoints() {
        return ((float) this._sheet.getDefaultRowHeight()) / 20.0f;
    }

    public void setDefaultRowHeight(short height) {
        this._sheet.setDefaultRowHeight(height);
    }

    public void setDefaultRowHeightInPoints(float height) {
        this._sheet.setDefaultRowHeight((short) ((int) (20.0f * height)));
    }

    public HSSFCellStyle getColumnStyle(int column) {
        short styleIndex = this._sheet.getXFIndexForColAt((short) column);
        if (styleIndex == 15) {
            return null;
        }
        return new HSSFCellStyle(styleIndex, this._book.getExFormatAt(styleIndex), this._book);
    }

    public boolean isGridsPrinted() {
        return this._sheet.isGridsPrinted();
    }

    public void setGridsPrinted(boolean value) {
        this._sheet.setGridsPrinted(value);
    }

    public int addMergedRegion(Region region) {
        return this._sheet.addMergedRegion(region.getRowFrom(), region.getColumnFrom(), region.getRowTo(), region.getColumnTo());
    }

    public int addMergedRegion(CellRangeAddress region) {
        region.validate(SpreadsheetVersion.EXCEL97);
        validateArrayFormulas(region);
        return this._sheet.addMergedRegion(region.getFirstRow(), region.getFirstColumn(), region.getLastRow(), region.getLastColumn());
    }

    private void validateArrayFormulas(CellRangeAddress region) {
        HSSFCell cell;
        int firstRow = region.getFirstRow();
        int firstColumn = region.getFirstColumn();
        int lastRow = region.getLastRow();
        int lastColumn = region.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                HSSFRow row = getRow(rowIn);
                if (!(row == null || (cell = row.getCell(colIn)) == null || !cell.isPartOfArrayFormulaGroup())) {
                    CellRangeAddress arrayRange = cell.getArrayFormulaRange();
                    if (arrayRange.getNumberOfCells() > 1 && (arrayRange.isInRange(region.getFirstRow(), region.getFirstColumn()) || arrayRange.isInRange(region.getFirstRow(), region.getFirstColumn()))) {
                        throw new IllegalStateException("The range " + region.formatAsString() + " intersects with a multi-cell array formula. " + "You cannot merge cells of an array.");
                    }
                }
            }
        }
    }

    public void setForceFormulaRecalculation(boolean value) {
        this._sheet.setUncalced(value);
    }

    public boolean getForceFormulaRecalculation() {
        return this._sheet.getUncalced();
    }

    public void setVerticallyCenter(boolean value) {
        this._sheet.getPageSettings().getVCenter().setVCenter(value);
    }

    public boolean getVerticallyCenter(boolean value) {
        return getVerticallyCenter();
    }

    public boolean getVerticallyCenter() {
        return this._sheet.getPageSettings().getVCenter().getVCenter();
    }

    public void setHorizontallyCenter(boolean value) {
        this._sheet.getPageSettings().getHCenter().setHCenter(value);
    }

    public boolean getHorizontallyCenter() {
        return this._sheet.getPageSettings().getHCenter().getHCenter();
    }

    public void setRightToLeft(boolean value) {
        this._sheet.getWindowTwo().setArabic(value);
    }

    public boolean isRightToLeft() {
        return this._sheet.getWindowTwo().getArabic();
    }

    public void removeMergedRegion(int index) {
        this._sheet.removeMergedRegion(index);
    }

    public int getNumMergedRegions() {
        return this._sheet.getNumMergedRegions();
    }

    public org.apache.poi.hssf.util.Region getMergedRegionAt(int index) {
        CellRangeAddress cra = getMergedRegion(index);
        return new org.apache.poi.hssf.util.Region(cra.getFirstRow(), (short) cra.getFirstColumn(), cra.getLastRow(), (short) cra.getLastColumn());
    }

    public CellRangeAddress getMergedRegion(int index) {
        return this._sheet.getMergedRegionAt(index);
    }

    public Iterator<Row> rowIterator() {
        return this._rows.values().iterator();
    }

    public Iterator<Row> iterator() {
        return rowIterator();
    }

    /* access modifiers changed from: package-private */
    public InternalSheet getSheet() {
        return this._sheet;
    }

    public void setAlternativeExpression(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setAlternateExpression(b);
    }

    public void setAlternativeFormula(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setAlternateFormula(b);
    }

    public void setAutobreaks(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setAutobreaks(b);
    }

    public void setDialog(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setDialog(b);
    }

    public void setDisplayGuts(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setDisplayGuts(b);
    }

    public void setFitToPage(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setFitToPage(b);
    }

    public void setRowSumsBelow(boolean b) {
        WSBoolRecord record = (WSBoolRecord) this._sheet.findFirstRecordBySid(129);
        record.setRowSumsBelow(b);
        record.setAlternateExpression(b);
    }

    public void setRowSumsRight(boolean b) {
        ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).setRowSumsRight(b);
    }

    public boolean getAlternateExpression() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getAlternateExpression();
    }

    public boolean getAlternateFormula() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getAlternateFormula();
    }

    public boolean getAutobreaks() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getAutobreaks();
    }

    public boolean getDialog() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getDialog();
    }

    public boolean getDisplayGuts() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getDisplayGuts();
    }

    public boolean isDisplayZeros() {
        return this._sheet.getWindowTwo().getDisplayZeros();
    }

    public void setDisplayZeros(boolean value) {
        this._sheet.getWindowTwo().setDisplayZeros(value);
    }

    public boolean getFitToPage() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getFitToPage();
    }

    public boolean getRowSumsBelow() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getRowSumsBelow();
    }

    public boolean getRowSumsRight() {
        return ((WSBoolRecord) this._sheet.findFirstRecordBySid(129)).getRowSumsRight();
    }

    public boolean isPrintGridlines() {
        return getSheet().getPrintGridlines().getPrintGridlines();
    }

    public void setPrintGridlines(boolean newPrintGridlines) {
        getSheet().getPrintGridlines().setPrintGridlines(newPrintGridlines);
    }

    public HSSFPrintSetup getPrintSetup() {
        return new HSSFPrintSetup(this._sheet.getPageSettings().getPrintSetup());
    }

    public HSSFHeader getHeader() {
        return new HSSFHeader(this._sheet.getPageSettings());
    }

    public HSSFFooter getFooter() {
        return new HSSFFooter(this._sheet.getPageSettings());
    }

    public boolean isSelected() {
        return getSheet().getWindowTwo().getSelected();
    }

    public void setSelected(boolean sel) {
        getSheet().getWindowTwo().setSelected(sel);
    }

    public boolean isActive() {
        return getSheet().getWindowTwo().isActive();
    }

    public void setActive(boolean sel) {
        getSheet().getWindowTwo().setActive(sel);
    }

    public double getMargin(short margin) {
        return this._sheet.getPageSettings().getMargin(margin);
    }

    public void setMargin(short margin, double size) {
        this._sheet.getPageSettings().setMargin(margin, size);
    }

    private WorksheetProtectionBlock getProtectionBlock() {
        return this._sheet.getProtectionBlock();
    }

    public boolean getProtect() {
        return getProtectionBlock().isSheetProtected();
    }

    public short getPassword() {
        return (short) getProtectionBlock().getPasswordHash();
    }

    public boolean getObjectProtect() {
        return getProtectionBlock().isObjectProtected();
    }

    public boolean getScenarioProtect() {
        return getProtectionBlock().isScenarioProtected();
    }

    public void protectSheet(String password) {
        getProtectionBlock().protectSheet(password, true, true);
    }

    public void setZoom(int numerator, int denominator) {
        if (numerator < 1 || numerator > 65535) {
            throw new IllegalArgumentException("Numerator must be greater than 1 and less than 65536");
        } else if (denominator < 1 || denominator > 65535) {
            throw new IllegalArgumentException("Denominator must be greater than 1 and less than 65536");
        } else {
            SCLRecord sclRecord = new SCLRecord();
            sclRecord.setNumerator((short) numerator);
            sclRecord.setDenominator((short) denominator);
            getSheet().setSCLRecord(sclRecord);
        }
    }

    public short getTopRow() {
        return this._sheet.getTopRow();
    }

    public short getLeftCol() {
        return this._sheet.getLeftCol();
    }

    public void showInPane(short toprow, short leftcol) {
        this._sheet.setTopRow(toprow);
        this._sheet.setLeftCol(leftcol);
    }

    /* access modifiers changed from: protected */
    public void shiftMerged(int startRow, int endRow, int n, boolean isRow) {
        List<CellRangeAddress> shiftedRegions = new ArrayList<>();
        int i = 0;
        while (i < getNumMergedRegions()) {
            CellRangeAddress merged = getMergedRegion(i);
            boolean inStart = merged.getFirstRow() >= startRow || merged.getLastRow() >= startRow;
            boolean inEnd = merged.getFirstRow() <= endRow || merged.getLastRow() <= endRow;
            if (inStart && inEnd && !containsCell(merged, startRow - 1, 0) && !containsCell(merged, endRow + 1, 0)) {
                merged.setFirstRow(merged.getFirstRow() + n);
                merged.setLastRow(merged.getLastRow() + n);
                shiftedRegions.add(merged);
                removeMergedRegion(i);
                i--;
            }
            i++;
        }
        for (CellRangeAddress region : shiftedRegions) {
            addMergedRegion(region);
        }
    }

    private static boolean containsCell(CellRangeAddress cr, int rowIx, int colIx) {
        if (cr.getFirstRow() > rowIx || cr.getLastRow() < rowIx || cr.getFirstColumn() > colIx || cr.getLastColumn() < colIx) {
            return false;
        }
        return true;
    }

    public void shiftRows(int startRow, int endRow, int n) {
        shiftRows(startRow, endRow, n, false, false);
    }

    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {
        shiftRows(startRow, endRow, n, copyRowHeight, resetOriginalRowHeight, true);
    }

    public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight, boolean moveComments) {
        int inc;
        int s;
        NoteRecord[] noteRecs;
        HSSFComment comment;
        int i = startRow;
        int i2 = endRow;
        int i3 = n;
        if (i3 < 0) {
            s = startRow;
            inc = 1;
        } else {
            s = endRow;
            inc = -1;
        }
        if (moveComments) {
            noteRecs = this._sheet.getNoteRecords();
        } else {
            noteRecs = NoteRecord.EMPTY_ARRAY;
        }
        shiftMerged(i, i2, i3, true);
        this._sheet.getPageSettings().shiftRowBreaks(i, i2, i3);
        int rowNum = s;
        while (rowNum >= i && rowNum <= i2 && rowNum >= 0 && rowNum < 65536) {
            HSSFRow row = getRow(rowNum);
            if (row != null) {
                notifyRowShifting(row);
            }
            HSSFRow row2Replace = getRow(rowNum + i3);
            if (row2Replace == null) {
                row2Replace = createRow(rowNum + i3);
            }
            row2Replace.removeAllCells();
            if (row != null) {
                if (copyRowHeight) {
                    row2Replace.setHeight(row.getHeight());
                }
                if (resetOriginalRowHeight) {
                    row.setHeight(255);
                }
                Iterator<Cell> cells = row.cellIterator();
                while (cells.hasNext()) {
                    HSSFCell cell = (HSSFCell) cells.next();
                    row.removeCell(cell);
                    CellValueRecordInterface cellRecord = cell.getCellValueRecord();
                    cellRecord.setRow(rowNum + i3);
                    row2Replace.createCellFromRecord(cellRecord);
                    this._sheet.addValueRecord(rowNum + i3, cellRecord);
                    HSSFHyperlink link = cell.getHyperlink();
                    if (link != null) {
                        link.setFirstRow(link.getFirstRow() + i3);
                        link.setLastRow(link.getLastRow() + i3);
                    }
                }
                row.removeAllCells();
                if (moveComments) {
                    for (int i4 = noteRecs.length - 1; i4 >= 0; i4--) {
                        NoteRecord nr = noteRecs[i4];
                        if (nr.getRow() == rowNum && (comment = getCellComment(rowNum, nr.getColumn())) != null) {
                            comment.setRow(rowNum + i3);
                        }
                    }
                }
            }
            rowNum += inc;
        }
        int i5 = this._lastrow;
        if (i2 == i5 || i2 + i3 > i5) {
            this._lastrow = Math.min(i2 + i3, SpreadsheetVersion.EXCEL97.getLastRowIndex());
        }
        int i6 = this._firstrow;
        if (i == i6 || i + i3 < i6) {
            this._firstrow = Math.max(i + i3, 0);
        }
        short externSheetIndex = this._book.checkExternSheet(this._workbook.getSheetIndex((Sheet) this));
        FormulaShifter shifter = FormulaShifter.createForRowShift(externSheetIndex, i, i2, i3);
        this._sheet.updateFormulasAfterCellShift(shifter, externSheetIndex);
        int nSheets = this._workbook.getNumberOfSheets();
        for (int i7 = 0; i7 < nSheets; i7++) {
            InternalSheet otherSheet = this._workbook.getSheetAt(i7).getSheet();
            if (otherSheet != this._sheet) {
                otherSheet.updateFormulasAfterCellShift(shifter, this._book.checkExternSheet(i7));
            }
        }
        this._workbook.getWorkbook().updateNamesAfterCellShift(shifter);
    }

    /* access modifiers changed from: protected */
    public void insertChartRecords(List<Record> records) {
        this._sheet.getRecords().addAll(this._sheet.findFirstRecordLocBySid(574), records);
    }

    private void notifyRowShifting(HSSFRow row) {
        String msg = "Row[rownum=" + row.getRowNum() + "] contains cell(s) included in a multi-cell array formula. " + "You cannot change part of an array.";
        Iterator i$ = row.iterator();
        while (i$.hasNext()) {
            HSSFCell hcell = (HSSFCell) ((Cell) i$.next());
            if (hcell.isPartOfArrayFormulaGroup()) {
                hcell.notifyArrayFormulaChanging(msg);
            }
        }
    }

    public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        validateColumn(colSplit);
        validateRow(rowSplit);
        if (leftmostColumn < colSplit) {
            throw new IllegalArgumentException("leftmostColumn parameter must not be less than colSplit parameter");
        } else if (topRow >= rowSplit) {
            getSheet().createFreezePane(colSplit, rowSplit, topRow, leftmostColumn);
        } else {
            throw new IllegalArgumentException("topRow parameter must not be less than leftmostColumn parameter");
        }
    }

    public void createFreezePane(int colSplit, int rowSplit) {
        createFreezePane(colSplit, rowSplit, colSplit, rowSplit);
    }

    public void createSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow, int activePane) {
        getSheet().createSplitPane(xSplitPos, ySplitPos, topRow, leftmostColumn, activePane);
    }

    public PaneInformation getPaneInformation() {
        return getSheet().getPaneInformation();
    }

    public void setDisplayGridlines(boolean show) {
        this._sheet.setDisplayGridlines(show);
    }

    public boolean isDisplayGridlines() {
        return this._sheet.isDisplayGridlines();
    }

    public void setDisplayFormulas(boolean show) {
        this._sheet.setDisplayFormulas(show);
    }

    public boolean isDisplayFormulas() {
        return this._sheet.isDisplayFormulas();
    }

    public void setDisplayRowColHeadings(boolean show) {
        this._sheet.setDisplayRowColHeadings(show);
    }

    public boolean isDisplayRowColHeadings() {
        return this._sheet.isDisplayRowColHeadings();
    }

    public void setRowBreak(int row) {
        validateRow(row);
        this._sheet.getPageSettings().setRowBreak(row, 0, 255);
    }

    public boolean isRowBroken(int row) {
        return this._sheet.getPageSettings().isRowBroken(row);
    }

    public void removeRowBreak(int row) {
        this._sheet.getPageSettings().removeRowBreak(row);
    }

    public int[] getRowBreaks() {
        return this._sheet.getPageSettings().getRowBreaks();
    }

    public int[] getColumnBreaks() {
        return this._sheet.getPageSettings().getColumnBreaks();
    }

    public void setColumnBreak(int column) {
        validateColumn((short) column);
        this._sheet.getPageSettings().setColumnBreak((short) column, 0, (short) SpreadsheetVersion.EXCEL97.getLastRowIndex());
    }

    public boolean isColumnBroken(int column) {
        return this._sheet.getPageSettings().isColumnBroken(column);
    }

    public void removeColumnBreak(int column) {
        this._sheet.getPageSettings().removeColumnBreak(column);
    }

    /* access modifiers changed from: protected */
    public void validateRow(int row) {
        int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (row > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        } else if (row < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }

    /* access modifiers changed from: protected */
    public void validateColumn(int column) {
        int maxcol = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        if (column > maxcol) {
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        } else if (column < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }

    public void dumpDrawingRecords(boolean fat) {
        this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), false);
        List<EscherRecord> escherRecords = ((EscherAggregate) getSheet().findFirstRecordBySid(EscherAggregate.sid)).getEscherRecords();
        PrintWriter w = new PrintWriter(System.out);
        for (EscherRecord escherRecord : escherRecords) {
            if (fat) {
                System.out.println(escherRecord.toString());
            } else {
                escherRecord.display(w, 0);
            }
        }
        w.flush();
    }

    public HSSFPatriarch createDrawingPatriarch() {
        if (this._patriarch == null) {
            this._book.createDrawingGroup();
            this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), true);
            EscherAggregate agg = (EscherAggregate) this._sheet.findFirstRecordBySid(EscherAggregate.sid);
            this._patriarch = new HSSFPatriarch(this, agg);
            agg.clear();
            agg.setPatriarch(this._patriarch);
        }
        return this._patriarch;
    }

    public EscherAggregate getDrawingEscherAggregate() {
        this._book.findDrawingGroup();
        if (this._book.getDrawingManager() == null || this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), false) == -1) {
            return null;
        }
        return (EscherAggregate) this._sheet.findFirstRecordBySid(EscherAggregate.sid);
    }

    public HSSFPatriarch getDrawingPatriarch() {
        HSSFPatriarch hSSFPatriarch = this._patriarch;
        if (hSSFPatriarch != null) {
            return hSSFPatriarch;
        }
        EscherAggregate agg = getDrawingEscherAggregate();
        if (agg == null) {
            return null;
        }
        HSSFPatriarch hSSFPatriarch2 = new HSSFPatriarch(this, agg);
        this._patriarch = hSSFPatriarch2;
        agg.setPatriarch(hSSFPatriarch2);
        agg.convertRecordsToUserModel();
        return this._patriarch;
    }

    public void setColumnGroupCollapsed(short columnNumber, boolean collapsed) {
        setColumnGroupCollapsed((int) 65535 & columnNumber, collapsed);
    }

    public void groupColumn(short fromColumn, short toColumn) {
        groupColumn((int) fromColumn & 65535, (int) 65535 & toColumn);
    }

    public void ungroupColumn(short fromColumn, short toColumn) {
        ungroupColumn((int) fromColumn & 65535, (int) 65535 & toColumn);
    }

    public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {
        this._sheet.setColumnGroupCollapsed(columnNumber, collapsed);
    }

    public void groupColumn(int fromColumn, int toColumn) {
        this._sheet.groupColumnRange(fromColumn, toColumn, true);
    }

    public void ungroupColumn(int fromColumn, int toColumn) {
        this._sheet.groupColumnRange(fromColumn, toColumn, false);
    }

    public void groupRow(int fromRow, int toRow) {
        this._sheet.groupRowRange(fromRow, toRow, true);
    }

    public void ungroupRow(int fromRow, int toRow) {
        this._sheet.groupRowRange(fromRow, toRow, false);
    }

    public void setRowGroupCollapsed(int rowIndex, boolean collapse) {
        if (collapse) {
            this._sheet.getRowsAggregate().collapseRow(rowIndex);
        } else {
            this._sheet.getRowsAggregate().expandRow(rowIndex);
        }
    }

    public void setDefaultColumnStyle(int column, CellStyle style) {
        this._sheet.setDefaultColumnStyle(column, ((HSSFCellStyle) style).getIndex());
    }

    public void autoSizeColumn(int column) {
        autoSizeColumn(column, false);
    }

    public void autoSizeColumn(int column, boolean useMergedCells) {
        double width;
        HSSFFont defaultFont;
        double fontHeightMultiple;
        HSSFWorkbook wb;
        char defaultChar;
        FontRenderContext frc;
        AttributedString str;
        HSSFSheet hSSFSheet;
        AffineTransform layout;
        String str2;
        HSSFCell cell;
        AffineTransform layout2;
        HSSFRichTextString rt;
        double fontHeightMultiple2;
        HSSFCellStyle style;
        HSSFWorkbook wb2;
        int colspan;
        AffineTransform trans;
        HSSFRichTextString rt2;
        HSSFSheet hSSFSheet2 = this;
        int i = column;
        char defaultChar2 = '0';
        double fontHeightMultiple3 = 2.0d;
        FontRenderContext frc2 = new FontRenderContext((AffineTransform) null, true, true);
        HSSFWorkbook wb3 = HSSFWorkbook.create(hSSFSheet2._book);
        HSSFDataFormatter formatter = new HSSFDataFormatter();
        HSSFFont defaultFont2 = wb3.getFontAt(0);
        StringBuilder sb = new StringBuilder();
        String str3 = "";
        sb.append(str3);
        sb.append('0');
        AttributedString str4 = new AttributedString(sb.toString());
        hSSFSheet2.copyAttributes(defaultFont2, str4, 0, 1);
        AffineTransform layout3 = new TextLayout(str4.getIterator(), frc2);
        int defaultCharWidth = (int) layout3.getAdvance();
        Iterator<Row> it = rowIterator();
        double width2 = -1.0d;
        AffineTransform str5 = layout3;
        AttributedString str6 = str4;
        while (it.hasNext()) {
            HSSFRow row = (HSSFRow) it.next();
            HSSFCell cell2 = row.getCell(i);
            if (cell2 == null) {
                defaultFont = defaultFont2;
            } else {
                HSSFCell cell3 = cell2;
                int colspan2 = 1;
                int i2 = 0;
                while (true) {
                    if (i2 < getNumMergedRegions()) {
                        CellRangeAddress region = hSSFSheet2.getMergedRegion(i2);
                        defaultFont = defaultFont2;
                        if (containsCell(region, row.getRowNum(), i)) {
                            if (!useMergedCells) {
                                break;
                            }
                            HSSFCell cell4 = row.getCell(region.getFirstColumn());
                            colspan2 = (region.getLastColumn() + 1) - region.getFirstColumn();
                            cell3 = cell4;
                        }
                        i2++;
                        defaultFont2 = defaultFont;
                    } else {
                        HSSFFont defaultFont3 = defaultFont2;
                        HSSFCellStyle style2 = cell3.getCellStyle();
                        int cellType = cell3.getCellType();
                        if (cellType == 2) {
                            cellType = cell3.getCachedFormulaResultType();
                        }
                        int cellType2 = cellType;
                        HSSFRow hSSFRow = row;
                        HSSFFont font = wb3.getFontAt(style2.getFontIndex());
                        AttributedString str7 = str6;
                        String str8 = str3;
                        if (cellType2 == 1) {
                            HSSFRichTextString rt3 = cell3.getRichStringCellValue();
                            String[] lines = rt3.getString().split("\\n");
                            int i3 = 0;
                            double width3 = width2;
                            while (true) {
                                layout2 = str5;
                                if (i3 >= lines.length) {
                                    break;
                                }
                                String txt = lines[i3] + defaultChar2;
                                AttributedString str9 = new AttributedString(txt);
                                char defaultChar3 = defaultChar2;
                                String str10 = txt;
                                hSSFSheet2.copyAttributes(font, str9, 0, txt.length());
                                if (rt3.numFormattingRuns() > 0) {
                                    int j = 0;
                                    while (j < lines[i3].length()) {
                                        short fontAtIndex = rt3.getFontAtIndex(j);
                                        if (fontAtIndex != 0) {
                                            rt2 = rt3;
                                            short s = fontAtIndex;
                                            hSSFSheet2.copyAttributes(wb3.getFontAt((short) fontAtIndex), str9, j, j + 1);
                                        } else {
                                            rt2 = rt3;
                                            short s2 = fontAtIndex;
                                        }
                                        j++;
                                        rt3 = rt2;
                                    }
                                    rt = rt3;
                                } else {
                                    rt = rt3;
                                }
                                AffineTransform layout4 = new TextLayout(str9.getIterator(), frc2);
                                if (style2.getRotation() != 0) {
                                    AffineTransform trans2 = new AffineTransform();
                                    style = style2;
                                    wb2 = wb3;
                                    double rotation = (double) style2.getRotation();
                                    Double.isNaN(rotation);
                                    trans2.concatenate(AffineTransform.getRotateInstance(((rotation * 2.0d) * 3.141592653589793d) / 360.0d));
                                    trans2.concatenate(AffineTransform.getScaleInstance(1.0d, fontHeightMultiple3));
                                    double width4 = layout4.getOutline(trans2).getBounds().getWidth();
                                    fontHeightMultiple2 = fontHeightMultiple3;
                                    colspan = colspan2;
                                    double fontHeightMultiple4 = (double) colspan;
                                    Double.isNaN(fontHeightMultiple4);
                                    double d = width4 / fontHeightMultiple4;
                                    double d2 = (double) defaultCharWidth;
                                    Double.isNaN(d2);
                                    double d3 = d / d2;
                                    double indention = (double) cell3.getCellStyle().getIndention();
                                    Double.isNaN(indention);
                                    trans = layout4;
                                    width3 = Math.max(width3, d3 + indention);
                                } else {
                                    fontHeightMultiple2 = fontHeightMultiple3;
                                    style = style2;
                                    wb2 = wb3;
                                    colspan = colspan2;
                                    double width5 = layout4.getBounds().getWidth();
                                    trans = layout4;
                                    double d4 = (double) colspan;
                                    Double.isNaN(d4);
                                    double d5 = width5 / d4;
                                    double d6 = (double) defaultCharWidth;
                                    Double.isNaN(d6);
                                    double d7 = d5 / d6;
                                    double indention2 = (double) cell3.getCellStyle().getIndention();
                                    Double.isNaN(indention2);
                                    width3 = Math.max(width3, d7 + indention2);
                                }
                                i3++;
                                hSSFSheet2 = this;
                                colspan2 = colspan;
                                str7 = str9;
                                defaultChar2 = defaultChar3;
                                rt3 = rt;
                                wb3 = wb2;
                                style2 = style;
                                fontHeightMultiple3 = fontHeightMultiple2;
                                str5 = trans;
                                int i4 = column;
                            }
                            defaultChar = defaultChar2;
                            fontHeightMultiple = fontHeightMultiple3;
                            HSSFCellStyle hSSFCellStyle = style2;
                            wb = wb3;
                            int i5 = colspan2;
                            hSSFSheet = this;
                            frc = frc2;
                            layout = layout2;
                            str = str7;
                            width2 = width3;
                        } else {
                            defaultChar = defaultChar2;
                            fontHeightMultiple = fontHeightMultiple3;
                            HSSFCellStyle style3 = style2;
                            wb = wb3;
                            int colspan3 = colspan2;
                            String sval = null;
                            if (cellType2 == 0) {
                                cell = cell3;
                                try {
                                    sval = formatter.formatCellValue(cell);
                                    str2 = str8;
                                } catch (Exception e) {
                                    Exception exc = e;
                                    StringBuilder sb2 = new StringBuilder();
                                    str2 = str8;
                                    sb2.append(str2);
                                    sb2.append(cell.getNumericCellValue());
                                    sval = sb2.toString();
                                }
                            } else {
                                cell = cell3;
                                str2 = str8;
                                if (cellType2 == 4) {
                                    sval = String.valueOf(cell.getBooleanCellValue());
                                }
                            }
                            if (sval != null) {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(sval);
                                char defaultChar4 = defaultChar;
                                sb3.append(defaultChar4);
                                String txt2 = sb3.toString();
                                AttributedString str11 = new AttributedString(txt2);
                                hSSFSheet = this;
                                hSSFSheet.copyAttributes(font, str11, 0, txt2.length());
                                layout = new TextLayout(str11.getIterator(), frc2);
                                if (style3.getRotation() != 0) {
                                    AffineTransform trans3 = new AffineTransform();
                                    String str12 = txt2;
                                    String str13 = sval;
                                    double rotation2 = (double) style3.getRotation();
                                    Double.isNaN(rotation2);
                                    trans3.concatenate(AffineTransform.getRotateInstance(((rotation2 * 2.0d) * 3.141592653589793d) / 360.0d));
                                    defaultChar = defaultChar4;
                                    str8 = str2;
                                    FontRenderContext frc3 = frc2;
                                    trans3.concatenate(AffineTransform.getScaleInstance(1.0d, fontHeightMultiple));
                                    double width6 = layout.getOutline(trans3).getBounds().getWidth();
                                    str = str11;
                                    frc = frc3;
                                    double d8 = (double) colspan3;
                                    Double.isNaN(d8);
                                    double d9 = width6 / d8;
                                    double d10 = (double) defaultCharWidth;
                                    Double.isNaN(d10);
                                    double d11 = d9 / d10;
                                    double indention3 = (double) cell.getCellStyle().getIndention();
                                    Double.isNaN(indention3);
                                    width2 = Math.max(width2, d11 + indention3);
                                } else {
                                    String str14 = sval;
                                    defaultChar = defaultChar4;
                                    str8 = str2;
                                    frc = frc2;
                                    str = str11;
                                    double width7 = layout.getBounds().getWidth();
                                    double d12 = (double) colspan3;
                                    Double.isNaN(d12);
                                    double d13 = width7 / d12;
                                    double d14 = (double) defaultCharWidth;
                                    Double.isNaN(d14);
                                    double d15 = d13 / d14;
                                    double indention4 = (double) cell.getCellStyle().getIndention();
                                    Double.isNaN(indention4);
                                    width2 = Math.max(width2, d15 + indention4);
                                }
                            } else {
                                hSSFSheet = this;
                                String str15 = sval;
                                str8 = str2;
                                frc = frc2;
                                double d16 = width2;
                                layout = str5;
                                str = str7;
                            }
                        }
                        i = column;
                        hSSFSheet2 = hSSFSheet;
                        str6 = str;
                        defaultFont2 = defaultFont3;
                        str3 = str8;
                        frc2 = frc;
                        defaultChar2 = defaultChar;
                        fontHeightMultiple3 = fontHeightMultiple;
                        str5 = layout;
                        wb3 = wb;
                    }
                }
            }
            defaultFont2 = defaultFont;
        }
        double d17 = fontHeightMultiple3;
        FontRenderContext fontRenderContext = frc2;
        HSSFWorkbook hSSFWorkbook = wb3;
        HSSFFont hSSFFont = defaultFont2;
        AttributedString attributedString = str6;
        double width8 = width2;
        HSSFSheet hSSFSheet3 = hSSFSheet2;
        if (width8 != -1.0d) {
            double width9 = width8 * 256.0d;
            if (width9 > 32767.0d) {
                width = 32767.0d;
            } else {
                width = width9;
            }
            hSSFSheet3._sheet.setColumnWidth(column, (short) ((int) width));
            double d18 = width;
            return;
        }
        int i6 = column;
        double d19 = width8;
    }

    private void copyAttributes(HSSFFont font, AttributedString str, int startIdx, int endIdx) {
        str.addAttribute(TextAttribute.FAMILY, font.getFontName(), startIdx, endIdx);
        str.addAttribute(TextAttribute.SIZE, new Float((float) font.getFontHeightInPoints()));
        if (font.getBoldweight() == 700) {
            str.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, startIdx, endIdx);
        }
        if (font.getItalic()) {
            str.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, startIdx, endIdx);
        }
        if (font.getUnderline() == 1) {
            str.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, startIdx, endIdx);
        }
    }

    public HSSFComment getCellComment(int row, int column) {
        HSSFRow r = getRow(row);
        if (r == null) {
            return null;
        }
        HSSFCell c = r.getCell(column);
        if (c != null) {
            return c.getCellComment();
        }
        return HSSFCell.findCellComment(this._sheet, row, column);
    }

    public HSSFSheetConditionalFormatting getSheetConditionalFormatting() {
        return new HSSFSheetConditionalFormatting(this);
    }

    public String getSheetName() {
        HSSFWorkbook wb = getWorkbook();
        return wb.getSheetName(wb.getSheetIndex((Sheet) this));
    }

    private CellRange<HSSFCell> getCellRange(CellRangeAddress range) {
        int firstRow = range.getFirstRow();
        int firstColumn = range.getFirstColumn();
        int lastRow = range.getLastRow();
        int lastColumn = range.getLastColumn();
        int height = (lastRow - firstRow) + 1;
        int width = (lastColumn - firstColumn) + 1;
        List<HSSFCell> temp = new ArrayList<>(height * width);
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                HSSFRow row = getRow(rowIn);
                if (row == null) {
                    row = createRow(rowIn);
                }
                HSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    cell = row.createCell(colIn);
                }
                temp.add(cell);
            }
        }
        return SSCellRange.create(firstRow, firstColumn, height, width, temp, HSSFCell.class);
    }

    public CellRange<HSSFCell> setArrayFormula(String formula, CellRangeAddress range) {
        Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._workbook, 2, this._workbook.getSheetIndex((Sheet) this));
        CellRange<HSSFCell> cells = getCellRange(range);
        for (HSSFCell c : cells) {
            c.setCellArrayFormula(range);
        }
        ((FormulaRecordAggregate) cells.getTopLeftCell().getCellValueRecord()).setArrayFormula(range, ptgs);
        return cells;
    }

    public CellRange<HSSFCell> removeArrayFormula(Cell cell) {
        if (cell.getSheet() == this) {
            CellValueRecordInterface rec = ((HSSFCell) cell).getCellValueRecord();
            if (rec instanceof FormulaRecordAggregate) {
                CellRange<HSSFCell> result = getCellRange(((FormulaRecordAggregate) rec).removeArrayFormula(cell.getRowIndex(), cell.getColumnIndex()));
                for (HSSFCell c : result) {
                    c.setCellType(3);
                }
                return result;
            }
            String ref = new CellReference(cell).formatAsString();
            throw new IllegalArgumentException("Cell " + ref + " is not part of an array formula.");
        }
        throw new IllegalArgumentException("Specified cell does not belong to this sheet.");
    }

    public DataValidationHelper getDataValidationHelper() {
        return new HSSFDataValidationHelper(this);
    }

    public HSSFAutoFilter setAutoFilter(CellRangeAddress range) {
        NameRecord name;
        InternalWorkbook workbook = this._workbook.getWorkbook();
        int sheetIndex = this._workbook.getSheetIndex((Sheet) this);
        NameRecord name2 = workbook.getSpecificBuiltinRecord((byte) 13, sheetIndex + 1);
        if (name2 == null) {
            name = workbook.createBuiltInName((byte) 13, sheetIndex + 1);
        } else {
            name = name2;
        }
        name.setNameDefinition(new Ptg[]{new Area3DPtg(range.getFirstRow(), range.getLastRow(), range.getFirstColumn(), range.getLastColumn(), false, false, false, false, sheetIndex)});
        AutoFilterInfoRecord r = new AutoFilterInfoRecord();
        r.setNumEntries((short) ((range.getLastColumn() + 1) - range.getFirstColumn()));
        this._sheet.getRecords().add(this._sheet.findFirstRecordLocBySid(512), r);
        HSSFPatriarch p = createDrawingPatriarch();
        for (int col = range.getFirstColumn(); col <= range.getLastColumn(); col++) {
            p.createComboBox(new HSSFClientAnchor(0, 0, 0, 0, (short) col, range.getFirstRow(), (short) (col + 1), range.getFirstRow() + 1));
        }
        return new HSSFAutoFilter(this);
    }
}
