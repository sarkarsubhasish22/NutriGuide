package org.apache.poi.hssf.usermodel;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.POIDocument;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBitmapBlip;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.AbstractEscherHolderRecord;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.IntersectionPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.SheetNameFormatter;
import org.apache.poi.hssf.record.formula.StringPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.poi.hssf.record.formula.UnionPtg;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

public final class HSSFWorkbook extends POIDocument implements Workbook {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final int DEBUG = POILogger.DEBUG;
    public static final int INITIAL_CAPACITY = 3;
    private static final short MAX_COLUMN = 255;
    private static final int MAX_ROW = 65535;
    private static final String[] WORKBOOK_DIR_ENTRY_NAMES = {"Workbook", "WORKBOOK"};
    private static POILogger log = POILogFactory.getLogger(HSSFWorkbook.class);
    protected List _sheets;
    private Hashtable fonts;
    private HSSFDataFormat formatter;
    private Row.MissingCellPolicy missingCellPolicy;
    private ArrayList names;
    private boolean preserveNodes;
    private InternalWorkbook workbook;

    public static HSSFWorkbook create(InternalWorkbook book) {
        return new HSSFWorkbook(book);
    }

    public HSSFWorkbook() {
        this(InternalWorkbook.createWorkbook());
    }

    private HSSFWorkbook(InternalWorkbook book) {
        super((DirectoryNode) null, (POIFSFileSystem) null);
        this.missingCellPolicy = HSSFRow.RETURN_NULL_AND_BLANK;
        this.workbook = book;
        this._sheets = new ArrayList(3);
        this.names = new ArrayList(3);
    }

    public HSSFWorkbook(POIFSFileSystem fs) throws IOException {
        this(fs, true);
    }

    public HSSFWorkbook(POIFSFileSystem fs, boolean preserveNodes2) throws IOException {
        this(fs.getRoot(), fs, preserveNodes2);
    }

    private static String getWorkbookDirEntryName(DirectoryNode directory) {
        String[] potentialNames = WORKBOOK_DIR_ENTRY_NAMES;
        int i = 0;
        while (i < potentialNames.length) {
            String wbName = potentialNames[i];
            try {
                directory.getEntry(wbName);
                return wbName;
            } catch (FileNotFoundException e) {
                i++;
            }
        }
        try {
            directory.getEntry("Book");
            throw new OldExcelFormatException("The supplied spreadsheet seems to be Excel 5.0/7.0 (BIFF5) format. POI only supports BIFF8 format (from Excel versions 97/2000/XP/2003)");
        } catch (FileNotFoundException e2) {
            throw new IllegalArgumentException("The supplied POIFSFileSystem does not contain a BIFF8 'Workbook' entry. Is it really an excel file?");
        }
    }

    public HSSFWorkbook(DirectoryNode directory, POIFSFileSystem fs, boolean preserveNodes2) throws IOException {
        super(directory, fs);
        this.missingCellPolicy = HSSFRow.RETURN_NULL_AND_BLANK;
        String workbookName = getWorkbookDirEntryName(directory);
        this.preserveNodes = preserveNodes2;
        if (!preserveNodes2) {
            this.filesystem = null;
            this.directory = null;
        }
        this._sheets = new ArrayList(3);
        this.names = new ArrayList(3);
        List records = RecordFactory.createRecords(directory.createDocumentInputStream(workbookName));
        InternalWorkbook createWorkbook = InternalWorkbook.createWorkbook(records);
        this.workbook = createWorkbook;
        setPropertiesFromWorkbook(createWorkbook);
        int recOffset = this.workbook.getNumRecords();
        convertLabelRecords(records, recOffset);
        RecordStream rs = new RecordStream(records, recOffset);
        while (rs.hasNext()) {
            this._sheets.add(new HSSFSheet(this, InternalSheet.createSheet(rs)));
        }
        for (int i = 0; i < this.workbook.getNumNames(); i++) {
            NameRecord nameRecord = this.workbook.getNameRecord(i);
            this.names.add(new HSSFName(this, nameRecord, this.workbook.getNameCommentRecord(nameRecord)));
        }
    }

    public HSSFWorkbook(InputStream s) throws IOException {
        this(s, true);
    }

    public HSSFWorkbook(InputStream s, boolean preserveNodes2) throws IOException {
        this(new POIFSFileSystem(s), preserveNodes2);
    }

    private void setPropertiesFromWorkbook(InternalWorkbook book) {
        this.workbook = book;
    }

    private void convertLabelRecords(List records, int offset) {
        if (log.check(POILogger.DEBUG)) {
            log.log(POILogger.DEBUG, (Object) "convertLabelRecords called");
        }
        for (int k = offset; k < records.size(); k++) {
            Record rec = (Record) records.get(k);
            if (rec.getSid() == 516) {
                LabelRecord oldrec = (LabelRecord) rec;
                records.remove(k);
                LabelSSTRecord newrec = new LabelSSTRecord();
                int stringid = this.workbook.addSSTString(new UnicodeString(oldrec.getValue()));
                newrec.setRow(oldrec.getRow());
                newrec.setColumn(oldrec.getColumn());
                newrec.setXFIndex(oldrec.getXFIndex());
                newrec.setSSTIndex(stringid);
                records.add(k, newrec);
            }
        }
        if (log.check(POILogger.DEBUG)) {
            log.log(POILogger.DEBUG, (Object) "convertLabelRecords exit");
        }
    }

    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this.missingCellPolicy;
    }

    public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy2) {
        this.missingCellPolicy = missingCellPolicy2;
    }

    public void setSheetOrder(String sheetname, int pos) {
        List list = this._sheets;
        list.add(pos, list.remove(getSheetIndex(sheetname)));
        this.workbook.setSheetOrder(sheetname, pos);
    }

    private void validateSheetIndex(int index) {
        int lastSheetIx = this._sheets.size() - 1;
        if (index < 0 || index > lastSheetIx) {
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range (0.." + lastSheetIx + ")");
        }
    }

    public void setSelectedTab(int index) {
        validateSheetIndex(index);
        int nSheets = this._sheets.size();
        int i = 0;
        while (true) {
            boolean z = true;
            if (i < nSheets) {
                HSSFSheet sheetAt = getSheetAt(i);
                if (i != index) {
                    z = false;
                }
                sheetAt.setSelected(z);
                i++;
            } else {
                this.workbook.getWindowOne().setNumSelectedTabs(1);
                return;
            }
        }
    }

    public void setSelectedTab(short index) {
        setSelectedTab((int) index);
    }

    public void setSelectedTabs(int[] indexes) {
        for (int validateSheetIndex : indexes) {
            validateSheetIndex(validateSheetIndex);
        }
        int nSheets = this._sheets.size();
        for (int i = 0; i < nSheets; i++) {
            boolean bSelect = false;
            int j = 0;
            while (true) {
                if (j >= indexes.length) {
                    break;
                } else if (indexes[j] == i) {
                    bSelect = true;
                    break;
                } else {
                    j++;
                }
            }
            getSheetAt(i).setSelected(bSelect);
        }
        this.workbook.getWindowOne().setNumSelectedTabs((short) indexes.length);
    }

    public void setActiveSheet(int index) {
        validateSheetIndex(index);
        int nSheets = this._sheets.size();
        int i = 0;
        while (i < nSheets) {
            getSheetAt(i).setActive(i == index);
            i++;
        }
        this.workbook.getWindowOne().setActiveSheetIndex(index);
    }

    public int getActiveSheetIndex() {
        return this.workbook.getWindowOne().getActiveSheetIndex();
    }

    public short getSelectedTab() {
        return (short) getActiveSheetIndex();
    }

    public void setFirstVisibleTab(int index) {
        this.workbook.getWindowOne().setFirstVisibleTab(index);
    }

    public void setDisplayedTab(short index) {
        setFirstVisibleTab(index);
    }

    public int getFirstVisibleTab() {
        return this.workbook.getWindowOne().getFirstVisibleTab();
    }

    public short getDisplayedTab() {
        return (short) getFirstVisibleTab();
    }

    public void setSheetName(int sheetIx, String name) {
        if (!this.workbook.doesContainsSheetName(name, sheetIx)) {
            validateSheetIndex(sheetIx);
            this.workbook.setSheetName(sheetIx, name);
            return;
        }
        throw new IllegalArgumentException("The workbook already contains a sheet with this name");
    }

    public String getSheetName(int sheetIndex) {
        validateSheetIndex(sheetIndex);
        return this.workbook.getSheetName(sheetIndex);
    }

    public boolean isHidden() {
        return this.workbook.getWindowOne().getHidden();
    }

    public void setHidden(boolean hiddenFlag) {
        this.workbook.getWindowOne().setHidden(hiddenFlag);
    }

    public boolean isSheetHidden(int sheetIx) {
        validateSheetIndex(sheetIx);
        return this.workbook.isSheetHidden(sheetIx);
    }

    public boolean isSheetVeryHidden(int sheetIx) {
        validateSheetIndex(sheetIx);
        return this.workbook.isSheetVeryHidden(sheetIx);
    }

    public void setSheetHidden(int sheetIx, boolean hidden) {
        validateSheetIndex(sheetIx);
        this.workbook.setSheetHidden(sheetIx, hidden);
    }

    public void setSheetHidden(int sheetIx, int hidden) {
        validateSheetIndex(sheetIx);
        WorkbookUtil.validateSheetState(hidden);
        this.workbook.setSheetHidden(sheetIx, hidden);
    }

    public int getSheetIndex(String name) {
        return this.workbook.getSheetIndex(name);
    }

    public int getSheetIndex(Sheet sheet) {
        for (int i = 0; i < this._sheets.size(); i++) {
            if (this._sheets.get(i) == sheet) {
                return i;
            }
        }
        return -1;
    }

    public int getExternalSheetIndex(int internalSheetIndex) {
        return this.workbook.checkExternSheet(internalSheetIndex);
    }

    public String findSheetNameFromExternSheet(int externSheetIndex) {
        return this.workbook.findSheetNameFromExternSheet(externSheetIndex);
    }

    public String resolveNameXText(int refIndex, int definedNameIndex) {
        return this.workbook.resolveNameXText(refIndex, definedNameIndex);
    }

    public HSSFSheet createSheet() {
        HSSFSheet sheet = new HSSFSheet(this);
        this._sheets.add(sheet);
        boolean z = true;
        this.workbook.setSheetName(this._sheets.size() - 1, "Sheet" + (this._sheets.size() - 1));
        if (this._sheets.size() != 1) {
            z = false;
        }
        boolean isOnlySheet = z;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: org.apache.poi.hssf.record.formula.Ptg[]} */
    /* JADX WARNING: type inference failed for: r11v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.poi.hssf.usermodel.HSSFSheet cloneSheet(int r14) {
        /*
            r13 = this;
            r13.validateSheetIndex(r14)
            java.util.List r0 = r13._sheets
            java.lang.Object r0 = r0.get(r14)
            org.apache.poi.hssf.usermodel.HSSFSheet r0 = (org.apache.poi.hssf.usermodel.HSSFSheet) r0
            org.apache.poi.hssf.model.InternalWorkbook r1 = r13.workbook
            java.lang.String r1 = r1.getSheetName(r14)
            org.apache.poi.hssf.usermodel.HSSFSheet r2 = r0.cloneSheet(r13)
            r3 = 0
            r2.setSelected(r3)
            r2.setActive(r3)
            java.lang.String r3 = r13.getUniqueSheetName(r1)
            java.util.List r4 = r13._sheets
            int r4 = r4.size()
            java.util.List r5 = r13._sheets
            r5.add(r2)
            org.apache.poi.hssf.model.InternalWorkbook r5 = r13.workbook
            r5.setSheetName(r4, r3)
            r5 = 13
            int r6 = r13.findExistingBuiltinNameRecordIdx(r14, r5)
            if (r6 < 0) goto L_0x009a
            org.apache.poi.hssf.model.InternalWorkbook r7 = r13.workbook
            org.apache.poi.hssf.record.NameRecord r7 = r7.getNameRecord(r6)
            org.apache.poi.hssf.model.InternalWorkbook r8 = r13.workbook
            short r8 = r8.checkExternSheet(r4)
            org.apache.poi.hssf.record.formula.Ptg[] r9 = r7.getNameDefinition()
            r10 = 0
        L_0x0049:
            int r11 = r9.length
            if (r10 >= r11) goto L_0x0078
            r11 = r9[r10]
            boolean r12 = r11 instanceof org.apache.poi.hssf.record.formula.Area3DPtg
            if (r12 == 0) goto L_0x0061
            r12 = r11
            org.apache.poi.hssf.record.formula.OperandPtg r12 = (org.apache.poi.hssf.record.formula.OperandPtg) r12
            org.apache.poi.hssf.record.formula.OperandPtg r12 = r12.copy()
            org.apache.poi.hssf.record.formula.Area3DPtg r12 = (org.apache.poi.hssf.record.formula.Area3DPtg) r12
            r12.setExternSheetIndex(r8)
            r9[r10] = r12
            goto L_0x0074
        L_0x0061:
            boolean r12 = r11 instanceof org.apache.poi.hssf.record.formula.Ref3DPtg
            if (r12 == 0) goto L_0x0074
            r12 = r11
            org.apache.poi.hssf.record.formula.OperandPtg r12 = (org.apache.poi.hssf.record.formula.OperandPtg) r12
            org.apache.poi.hssf.record.formula.OperandPtg r12 = r12.copy()
            org.apache.poi.hssf.record.formula.Ref3DPtg r12 = (org.apache.poi.hssf.record.formula.Ref3DPtg) r12
            r12.setExternSheetIndex(r8)
            r9[r10] = r12
            goto L_0x0075
        L_0x0074:
        L_0x0075:
            int r10 = r10 + 1
            goto L_0x0049
        L_0x0078:
            org.apache.poi.hssf.model.InternalWorkbook r10 = r13.workbook
            int r11 = r4 + 1
            org.apache.poi.hssf.record.NameRecord r5 = r10.createBuiltInName(r5, r11)
            r5.setNameDefinition(r9)
            r10 = 1
            r5.setHidden(r10)
            org.apache.poi.hssf.usermodel.HSSFName r10 = new org.apache.poi.hssf.usermodel.HSSFName
            r10.<init>(r13, r5)
            java.util.ArrayList r11 = r13.names
            r11.add(r10)
            org.apache.poi.hssf.model.InternalWorkbook r11 = r13.workbook
            org.apache.poi.hssf.model.InternalSheet r12 = r2.getSheet()
            r11.cloneDrawings(r12)
        L_0x009a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.usermodel.HSSFWorkbook.cloneSheet(int):org.apache.poi.hssf.usermodel.HSSFSheet");
    }

    private String getUniqueSheetName(String srcName) {
        String name;
        int uniqueIndex = 2;
        String baseName = srcName;
        int bracketPos = srcName.lastIndexOf(40);
        if (bracketPos > 0 && srcName.endsWith(")")) {
            try {
                uniqueIndex = Integer.parseInt(srcName.substring(bracketPos + 1, srcName.length() - ")".length()).trim()) + 1;
                baseName = srcName.substring(0, bracketPos).trim();
            } catch (NumberFormatException e) {
            }
        }
        while (true) {
            int uniqueIndex2 = uniqueIndex + 1;
            String index = Integer.toString(uniqueIndex);
            if (baseName.length() + index.length() + 2 < 31) {
                name = baseName + " (" + index + ")";
            } else {
                name = baseName.substring(0, (31 - index.length()) - 2) + "(" + index + ")";
            }
            if (this.workbook.getSheetIndex(name) == -1) {
                return name;
            }
            uniqueIndex = uniqueIndex2;
        }
    }

    public HSSFSheet createSheet(String sheetname) {
        if (!this.workbook.doesContainsSheetName(sheetname, this._sheets.size())) {
            HSSFSheet sheet = new HSSFSheet(this);
            this.workbook.setSheetName(this._sheets.size(), sheetname);
            this._sheets.add(sheet);
            boolean z = true;
            if (this._sheets.size() != 1) {
                z = false;
            }
            boolean isOnlySheet = z;
            sheet.setSelected(isOnlySheet);
            sheet.setActive(isOnlySheet);
            return sheet;
        }
        throw new IllegalArgumentException("The workbook already contains a sheet of this name");
    }

    public int getNumberOfSheets() {
        return this._sheets.size();
    }

    public int getSheetIndexFromExternSheetIndex(int externSheetNumber) {
        return this.workbook.getSheetIndexFromExternSheetIndex(externSheetNumber);
    }

    private HSSFSheet[] getSheets() {
        HSSFSheet[] result = new HSSFSheet[this._sheets.size()];
        this._sheets.toArray(result);
        return result;
    }

    public HSSFSheet getSheetAt(int index) {
        validateSheetIndex(index);
        return (HSSFSheet) this._sheets.get(index);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.apache.poi.hssf.usermodel.HSSFSheet} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.poi.hssf.usermodel.HSSFSheet getSheet(java.lang.String r5) {
        /*
            r4 = this;
            r0 = 0
            r1 = 0
        L_0x0002:
            java.util.List r2 = r4._sheets
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0022
            org.apache.poi.hssf.model.InternalWorkbook r2 = r4.workbook
            java.lang.String r2 = r2.getSheetName(r1)
            boolean r3 = r2.equalsIgnoreCase(r5)
            if (r3 == 0) goto L_0x001f
            java.util.List r3 = r4._sheets
            java.lang.Object r3 = r3.get(r1)
            r0 = r3
            org.apache.poi.hssf.usermodel.HSSFSheet r0 = (org.apache.poi.hssf.usermodel.HSSFSheet) r0
        L_0x001f:
            int r1 = r1 + 1
            goto L_0x0002
        L_0x0022:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.usermodel.HSSFWorkbook.getSheet(java.lang.String):org.apache.poi.hssf.usermodel.HSSFSheet");
    }

    public void removeSheetAt(int index) {
        validateSheetIndex(index);
        boolean wasActive = getSheetAt(index).isActive();
        boolean wasSelected = getSheetAt(index).isSelected();
        this._sheets.remove(index);
        this.workbook.removeSheet(index);
        int nSheets = this._sheets.size();
        if (nSheets >= 1) {
            int newSheetIndex = index;
            if (newSheetIndex >= nSheets) {
                newSheetIndex = nSheets - 1;
            }
            if (wasActive) {
                setActiveSheet(newSheetIndex);
            }
            if (wasSelected) {
                boolean someOtherSheetIsStillSelected = false;
                int i = 0;
                while (true) {
                    if (i >= nSheets) {
                        break;
                    } else if (getSheetAt(i).isSelected()) {
                        someOtherSheetIsStillSelected = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (!someOtherSheetIsStillSelected) {
                    setSelectedTab(newSheetIndex);
                }
            }
        }
    }

    public void setBackupFlag(boolean backupValue) {
        this.workbook.getBackupRecord().setBackup(backupValue);
    }

    public boolean getBackupFlag() {
        return this.workbook.getBackupRecord().getBackup() != 0;
    }

    public void setRepeatingRowsAndColumns(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {
        boolean isNewRecord;
        NameRecord nameRecord;
        NameRecord nameRecord2;
        HSSFSheet sheet;
        List temp;
        List temp2;
        int i = sheetIndex;
        int i2 = startColumn;
        int i3 = endColumn;
        int i4 = startRow;
        int i5 = endRow;
        if (i2 == -1 && i3 != -1) {
            throw new IllegalArgumentException("Invalid column range specification");
        } else if (i4 == -1 && i5 != -1) {
            throw new IllegalArgumentException("Invalid row range specification");
        } else if (i2 < -1 || i2 >= 255) {
            throw new IllegalArgumentException("Invalid column range specification");
        } else if (i3 < -1 || i3 >= 255) {
            throw new IllegalArgumentException("Invalid column range specification");
        } else if (i4 < -1 || i4 > 65535) {
            throw new IllegalArgumentException("Invalid row range specification");
        } else if (i5 < -1 || i5 > 65535) {
            throw new IllegalArgumentException("Invalid row range specification");
        } else if (i2 > i3) {
            throw new IllegalArgumentException("Invalid column range specification");
        } else if (i4 <= i5) {
            HSSFSheet sheet2 = getSheetAt(sheetIndex);
            short externSheetIndex = getWorkbook().checkExternSheet(i);
            boolean settingRowAndColumn = (i2 == -1 || i3 == -1 || i4 == -1 || i5 == -1) ? false : true;
            boolean removingRange = i2 == -1 && i3 == -1 && i4 == -1 && i5 == -1;
            int rowColHeaderNameIndex = findExistingBuiltinNameRecordIdx(i, (byte) 7);
            if (!removingRange) {
                if (rowColHeaderNameIndex < 0) {
                    nameRecord = this.workbook.createBuiltInName((byte) 7, i + 1);
                    isNewRecord = true;
                } else {
                    nameRecord = this.workbook.getNameRecord(rowColHeaderNameIndex);
                    isNewRecord = false;
                }
                List temp3 = new ArrayList();
                if (settingRowAndColumn) {
                    temp3.add(new MemFuncPtg(23));
                }
                if (i2 >= 0) {
                    nameRecord2 = nameRecord;
                    int i6 = rowColHeaderNameIndex;
                    sheet = sheet2;
                    temp = temp3;
                    temp.add(new Area3DPtg(0, 65535, startColumn, endColumn, false, false, false, false, externSheetIndex));
                } else {
                    nameRecord2 = nameRecord;
                    int i7 = rowColHeaderNameIndex;
                    sheet = sheet2;
                    temp = temp3;
                }
                if (i4 >= 0) {
                    temp2 = temp;
                    temp2.add(new Area3DPtg(startRow, endRow, 0, 255, false, false, false, false, externSheetIndex));
                } else {
                    temp2 = temp;
                }
                if (settingRowAndColumn) {
                    temp2.add(UnionPtg.instance);
                }
                Ptg[] ptgs = new Ptg[temp2.size()];
                temp2.toArray(ptgs);
                NameRecord nameRecord3 = nameRecord2;
                nameRecord3.setNameDefinition(ptgs);
                if (isNewRecord) {
                    this.names.add(new HSSFName(this, nameRecord3, nameRecord3.isBuiltInName() ? null : this.workbook.getNameCommentRecord(nameRecord3)));
                }
                sheet.getPrintSetup().setValidSettings(false);
                sheet.setActive(true);
            } else if (rowColHeaderNameIndex >= 0) {
                this.workbook.removeName(rowColHeaderNameIndex);
            }
        } else {
            throw new IllegalArgumentException("Invalid row range specification");
        }
    }

    private int findExistingBuiltinNameRecordIdx(int sheetIndex, byte builtinCode) {
        int defNameIndex = 0;
        while (defNameIndex < this.names.size()) {
            NameRecord r = this.workbook.getNameRecord(defNameIndex);
            if (r == null) {
                throw new RuntimeException("Unable to find all defined names to iterate over");
            } else if (r.isBuiltInName() && r.getBuiltInName() == builtinCode && r.getSheetNumber() - 1 == sheetIndex) {
                return defNameIndex;
            } else {
                defNameIndex++;
            }
        }
        return -1;
    }

    public HSSFFont createFont() {
        FontRecord createNewFont = this.workbook.createNewFont();
        short fontindex = (short) (getNumberOfFonts() - 1);
        if (fontindex > 3) {
            fontindex = (short) (fontindex + 1);
        }
        if (fontindex != Short.MAX_VALUE) {
            return getFontAt(fontindex);
        }
        throw new IllegalArgumentException("Maximum number of fonts was exceeded");
    }

    public HSSFFont findFont(short boldWeight, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline) {
        for (short i = 0; i <= getNumberOfFonts(); i = (short) (i + 1)) {
            if (i != 4) {
                HSSFFont hssfFont = getFontAt(i);
                if (hssfFont.getBoldweight() == boldWeight && hssfFont.getColor() == color && hssfFont.getFontHeight() == fontHeight && hssfFont.getFontName().equals(name) && hssfFont.getItalic() == italic && hssfFont.getStrikeout() == strikeout && hssfFont.getTypeOffset() == typeOffset && hssfFont.getUnderline() == underline) {
                    return hssfFont;
                }
            }
        }
        return null;
    }

    public short getNumberOfFonts() {
        return (short) this.workbook.getNumberOfFontRecords();
    }

    public HSSFFont getFontAt(short idx) {
        if (this.fonts == null) {
            this.fonts = new Hashtable();
        }
        Short sIdx = Short.valueOf(idx);
        if (this.fonts.containsKey(sIdx)) {
            return (HSSFFont) this.fonts.get(sIdx);
        }
        HSSFFont retval = new HSSFFont(idx, this.workbook.getFontRecordAt(idx));
        this.fonts.put(sIdx, retval);
        return retval;
    }

    /* access modifiers changed from: protected */
    public void resetFontCache() {
        this.fonts = new Hashtable();
    }

    public HSSFCellStyle createCellStyle() {
        return new HSSFCellStyle((short) (getNumCellStyles() - 1), this.workbook.createCellXF(), this);
    }

    public short getNumCellStyles() {
        return (short) this.workbook.getNumExFormats();
    }

    public HSSFCellStyle getCellStyleAt(short idx) {
        return new HSSFCellStyle(idx, this.workbook.getExFormatAt(idx), this);
    }

    public void write(OutputStream stream) throws IOException {
        byte[] bytes = getBytes();
        POIFSFileSystem fs = new POIFSFileSystem();
        List excepts = new ArrayList(1);
        fs.createDocument(new ByteArrayInputStream(bytes), "Workbook");
        writeProperties(fs, excepts);
        if (this.preserveNodes) {
            excepts.add("Workbook");
            excepts.add("WORKBOOK");
            POIFSFileSystem srcFs = this.filesystem;
            copyNodes(srcFs, fs, excepts);
            fs.getRoot().setStorageClsid(srcFs.getRoot().getStorageClsid());
        }
        fs.writeFilesystem(stream);
    }

    private static final class SheetRecordCollector implements RecordAggregate.RecordVisitor {
        private List _list = new ArrayList(128);
        private int _totalSize = 0;

        public int getTotalSize() {
            return this._totalSize;
        }

        public void visitRecord(Record r) {
            this._list.add(r);
            this._totalSize += r.getRecordSize();
        }

        public int serialize(int offset, byte[] data) {
            int result = 0;
            int nRecs = this._list.size();
            for (int i = 0; i < nRecs; i++) {
                result += ((Record) this._list.get(i)).serialize(offset + result, data);
            }
            return result;
        }
    }

    public byte[] getBytes() {
        if (log.check(POILogger.DEBUG)) {
            log.log(DEBUG, (Object) "HSSFWorkbook.getBytes()");
        }
        HSSFSheet[] sheets = getSheets();
        for (HSSFSheet sheet : sheets) {
            sheet.getSheet().preSerialize();
        }
        int totalsize = this.workbook.getSize();
        SheetRecordCollector[] srCollectors = new SheetRecordCollector[nSheets];
        for (int k = 0; k < nSheets; k++) {
            this.workbook.setSheetBof(k, totalsize);
            SheetRecordCollector src = new SheetRecordCollector();
            sheets[k].getSheet().visitContainedRecords(src, totalsize);
            totalsize += src.getTotalSize();
            srCollectors[k] = src;
        }
        byte[] retval = new byte[totalsize];
        int pos = this.workbook.serialize(0, retval);
        int k2 = 0;
        while (k2 < nSheets) {
            SheetRecordCollector src2 = srCollectors[k2];
            int serializedSize = src2.serialize(pos, retval);
            if (serializedSize == src2.getTotalSize()) {
                pos += serializedSize;
                k2++;
            } else {
                throw new IllegalStateException("Actual serialized sheet size (" + serializedSize + ") differs from pre-calculated size (" + src2.getTotalSize() + ") for sheet (" + k2 + ")");
            }
        }
        return retval;
    }

    public int addSSTString(String string) {
        return this.workbook.addSSTString(new UnicodeString(string));
    }

    public String getSSTString(int index) {
        return this.workbook.getSSTString(index).getString();
    }

    /* access modifiers changed from: package-private */
    public InternalWorkbook getWorkbook() {
        return this.workbook;
    }

    public int getNumberOfNames() {
        return this.names.size();
    }

    public HSSFName getName(String name) {
        int nameIndex = getNameIndex(name);
        if (nameIndex < 0) {
            return null;
        }
        return (HSSFName) this.names.get(nameIndex);
    }

    public HSSFName getNameAt(int nameIndex) {
        int nNames = this.names.size();
        if (nNames < 1) {
            throw new IllegalStateException("There are no defined names in this workbook");
        } else if (nameIndex >= 0 && nameIndex <= nNames) {
            return (HSSFName) this.names.get(nameIndex);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Specified name index ");
            sb.append(nameIndex);
            sb.append(" is outside the allowable range (0..");
            sb.append(nNames - 1);
            sb.append(").");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public NameRecord getNameRecord(int nameIndex) {
        return getWorkbook().getNameRecord(nameIndex);
    }

    public String getNameName(int index) {
        return getNameAt(index).getNameName();
    }

    public void setPrintArea(int sheetIndex, String reference) {
        NameRecord name = this.workbook.getSpecificBuiltinRecord((byte) 6, sheetIndex + 1);
        if (name == null) {
            name = this.workbook.createBuiltInName((byte) 6, sheetIndex + 1);
        }
        String[] parts = COMMA_PATTERN.split(reference);
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            SheetNameFormatter.appendFormat(sb, getSheetName(sheetIndex));
            sb.append("!");
            sb.append(parts[i]);
        }
        name.setNameDefinition(HSSFFormulaParser.parse(sb.toString(), this, 0, sheetIndex));
    }

    public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {
        String reference = new CellReference(startRow, startColumn, true, true).formatAsString();
        setPrintArea(sheetIndex, reference + ":" + new CellReference(endRow, endColumn, true, true).formatAsString());
    }

    public String getPrintArea(int sheetIndex) {
        NameRecord name = this.workbook.getSpecificBuiltinRecord((byte) 6, sheetIndex + 1);
        if (name == null) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(this, name.getNameDefinition());
    }

    public void removePrintArea(int sheetIndex) {
        getWorkbook().removeBuiltinRecord((byte) 6, sheetIndex + 1);
    }

    public HSSFName createName() {
        HSSFName newName = new HSSFName(this, this.workbook.createName());
        this.names.add(newName);
        return newName;
    }

    public int getNameIndex(String name) {
        for (int k = 0; k < this.names.size(); k++) {
            if (getNameName(k).equalsIgnoreCase(name)) {
                return k;
            }
        }
        return -1;
    }

    public void removeName(int index) {
        this.names.remove(index);
        this.workbook.removeName(index);
    }

    public HSSFDataFormat createDataFormat() {
        if (this.formatter == null) {
            this.formatter = new HSSFDataFormat(this.workbook);
        }
        return this.formatter;
    }

    public void removeName(String name) {
        removeName(getNameIndex(name));
    }

    public HSSFPalette getCustomPalette() {
        return new HSSFPalette(this.workbook.getCustomPalette());
    }

    public void insertChartRecord() {
        this.workbook.getRecords().add(this.workbook.findFirstRecordLocBySid(252), new UnknownRecord(235, new byte[]{IntersectionPtg.sid, 0, 0, -16, 82, 0, 0, 0, 0, 0, 6, -16, 24, 0, 0, 0, 1, 8, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 51, 0, 11, -16, UnaryPlusPtg.sid, 0, 0, 0, -65, 0, 8, 0, 8, 0, -127, 1, 9, 0, 0, 8, -64, 1, Ptg.CLASS_ARRAY, 0, 0, 8, Ptg.CLASS_ARRAY, 0, IntPtg.sid, -15, UnionPtg.sid, 0, 0, 0, 13, 0, 0, 8, 12, 0, 0, 8, StringPtg.sid, 0, 0, 8, -9, 0, 0, UnionPtg.sid}));
    }

    public void dumpDrawingGroupRecords(boolean fat) {
        DrawingGroupRecord r = (DrawingGroupRecord) this.workbook.findFirstRecordBySid(DrawingGroupRecord.sid);
        r.decode();
        List escherRecords = r.getEscherRecords();
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

    public int addPicture(byte[] pictureData, int format) {
        byte[] uid = newUID();
        EscherBitmapBlip blipRecord = new EscherBitmapBlip();
        blipRecord.setRecordId((short) (format - 4072));
        switch (format) {
            case 2:
                blipRecord.setOptions(15680);
                break;
            case 3:
                blipRecord.setOptions(8544);
                break;
            case 4:
                blipRecord.setOptions(21536);
                break;
            case 5:
                blipRecord.setOptions(HSSFPictureData.MSOBI_JPEG);
                break;
            case 6:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PNG);
                break;
            case 7:
                blipRecord.setOptions(HSSFPictureData.MSOBI_DIB);
                break;
        }
        blipRecord.setUID(uid);
        blipRecord.setMarker((byte) -1);
        blipRecord.setPictureData(pictureData);
        EscherBSERecord r = new EscherBSERecord();
        r.setRecordId(EscherBSERecord.RECORD_ID);
        r.setOptions((short) ((format << 4) | 2));
        r.setBlipTypeMacOS((byte) format);
        r.setBlipTypeWin32((byte) format);
        r.setUid(uid);
        r.setTag(255);
        r.setSize(pictureData.length + 25);
        r.setRef(1);
        r.setOffset(0);
        r.setBlipRecord(blipRecord);
        return this.workbook.addBSERecord(r);
    }

    public List<HSSFPictureData> getAllPictures() {
        List<HSSFPictureData> pictures = new ArrayList<>();
        for (Record r : this.workbook.getRecords()) {
            if (r instanceof AbstractEscherHolderRecord) {
                ((AbstractEscherHolderRecord) r).decode();
                searchForPictures(((AbstractEscherHolderRecord) r).getEscherRecords(), pictures);
            }
        }
        return pictures;
    }

    private void searchForPictures(List<EscherRecord> escherRecords, List<HSSFPictureData> pictures) {
        EscherBlipRecord blip;
        for (EscherRecord escherRecord : escherRecords) {
            if ((escherRecord instanceof EscherBSERecord) && (blip = ((EscherBSERecord) escherRecord).getBlipRecord()) != null) {
                pictures.add(new HSSFPictureData(blip));
            }
            searchForPictures(escherRecord.getChildRecords(), pictures);
        }
    }

    public boolean isWriteProtected() {
        return this.workbook.isWriteProtected();
    }

    public void writeProtectWorkbook(String password, String username) {
        this.workbook.writeProtectWorkbook(password, username);
    }

    public void unwriteProtectWorkbook() {
        this.workbook.unwriteProtectWorkbook();
    }

    public List<HSSFObjectData> getAllEmbeddedObjects() {
        List<HSSFObjectData> objects = new ArrayList<>();
        for (int i = 0; i < getNumberOfSheets(); i++) {
            getAllEmbeddedObjects(getSheetAt(i).getSheet().getRecords(), objects);
        }
        return objects;
    }

    private void getAllEmbeddedObjects(List records, List<HSSFObjectData> objects) {
        for (Object obj : records) {
            if (obj instanceof ObjRecord) {
                for (Object sub : ((ObjRecord) obj).getSubRecords()) {
                    if (sub instanceof EmbeddedObjectRefSubRecord) {
                        objects.add(new HSSFObjectData((ObjRecord) obj, this.filesystem));
                    }
                }
            }
        }
    }

    public CreationHelper getCreationHelper() {
        return new HSSFCreationHelper(this);
    }

    private static byte[] newUID() {
        return new byte[16];
    }

    public NameXPtg getNameXPtg(String name) {
        return this.workbook.getNameXPtg(name);
    }
}
