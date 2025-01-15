package org.apache.poi.hssf.model;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherDggRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherSplitMenuColorsRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.BookBoolRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.DSFRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FileSharingRecord;
import org.apache.poi.hssf.record.FnGroupCountRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.HideObjRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.MMSRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PasswordRev4Record;
import org.apache.poi.hssf.record.PrecisionRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.ProtectionRev4Record;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RefreshAllRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.record.TabIdRecord;
import org.apache.poi.hssf.record.UseSelFSRecord;
import org.apache.poi.hssf.record.WindowOneRecord;
import org.apache.poi.hssf.record.WindowProtectRecord;
import org.apache.poi.hssf.record.WriteAccessRecord;
import org.apache.poi.hssf.record.WriteProtectRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.formula.FormulaShifter;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

@Internal
public final class InternalWorkbook {
    private static final short CODEPAGE = 1200;
    private static final int DEBUG = POILogger.DEBUG;
    private static final int MAX_SENSITIVE_SHEET_NAME_LEN = 31;
    private static final POILogger log = POILogFactory.getLogger(InternalWorkbook.class);
    private final List<BoundSheetRecord> boundsheets = new ArrayList();
    private final Map<String, NameCommentRecord> commentRecords = new LinkedHashMap();
    private DrawingManager2 drawingManager;
    private List<EscherBSERecord> escherBSERecords = new ArrayList();
    private FileSharingRecord fileShare;
    private final List<FormatRecord> formats = new ArrayList();
    private final List<HyperlinkRecord> hyperlinks = new ArrayList();
    private LinkTable linkTable;
    private int maxformatid = -1;
    private int numfonts = 0;
    private int numxfs = 0;
    private final WorkbookRecordList records = new WorkbookRecordList();
    protected SSTRecord sst;
    private boolean uses1904datewindowing = false;
    private WindowOneRecord windowOne;
    private WriteAccessRecord writeAccess;
    private WriteProtectRecord writeProtect;

    private InternalWorkbook() {
    }

    public static InternalWorkbook createWorkbook(List<Record> recs) {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "Workbook (readfile) created with reclen=", (Object) Integer.valueOf(recs.size()));
        }
        InternalWorkbook retval = new InternalWorkbook();
        List<Record> records2 = new ArrayList<>(recs.size() / 3);
        retval.records.setRecords(records2);
        int k = 0;
        while (true) {
            if (k < recs.size()) {
                Record rec = recs.get(k);
                if (rec.getSid() == 10) {
                    records2.add(rec);
                    POILogger pOILogger2 = log;
                    if (pOILogger2.check(POILogger.DEBUG)) {
                        pOILogger2.log(DEBUG, (Object) "found workbook eof record at " + k);
                    }
                } else {
                    switch (rec.getSid()) {
                        case 18:
                            POILogger pOILogger3 = log;
                            if (pOILogger3.check(POILogger.DEBUG)) {
                                pOILogger3.log(DEBUG, (Object) "found protect record at " + k);
                            }
                            retval.records.setProtpos(k);
                            break;
                        case 23:
                            throw new RuntimeException("Extern sheet is part of LinkTable");
                        case 24:
                        case 430:
                            POILogger pOILogger4 = log;
                            if (pOILogger4.check(POILogger.DEBUG)) {
                                pOILogger4.log(DEBUG, (Object) "found SupBook record at " + k);
                            }
                            LinkTable linkTable2 = new LinkTable(recs, k, retval.records, retval.commentRecords);
                            retval.linkTable = linkTable2;
                            k += linkTable2.getRecordCount() - 1;
                            continue;
                        case 34:
                            POILogger pOILogger5 = log;
                            if (pOILogger5.check(POILogger.DEBUG)) {
                                pOILogger5.log(DEBUG, (Object) "found datewindow1904 record at " + k);
                            }
                            retval.uses1904datewindowing = ((DateWindow1904Record) rec).getWindowing() == 1;
                            break;
                        case 49:
                            POILogger pOILogger6 = log;
                            if (pOILogger6.check(POILogger.DEBUG)) {
                                pOILogger6.log(DEBUG, (Object) "found font record at " + k);
                            }
                            retval.records.setFontpos(k);
                            retval.numfonts++;
                            break;
                        case 61:
                            POILogger pOILogger7 = log;
                            if (pOILogger7.check(POILogger.DEBUG)) {
                                pOILogger7.log(DEBUG, (Object) "found WindowOneRecord at " + k);
                            }
                            retval.windowOne = (WindowOneRecord) rec;
                            break;
                        case 64:
                            POILogger pOILogger8 = log;
                            if (pOILogger8.check(POILogger.DEBUG)) {
                                pOILogger8.log(DEBUG, (Object) "found backup record at " + k);
                            }
                            retval.records.setBackuppos(k);
                            break;
                        case 91:
                            POILogger pOILogger9 = log;
                            if (pOILogger9.check(POILogger.DEBUG)) {
                                pOILogger9.log(DEBUG, (Object) "found FileSharing at " + k);
                            }
                            retval.fileShare = (FileSharingRecord) rec;
                            break;
                        case 92:
                            POILogger pOILogger10 = log;
                            if (pOILogger10.check(POILogger.DEBUG)) {
                                pOILogger10.log(DEBUG, (Object) "found WriteAccess at " + k);
                            }
                            retval.writeAccess = (WriteAccessRecord) rec;
                            break;
                        case ShapeTypes.FLOW_CHART_DECISION /*133*/:
                            POILogger pOILogger11 = log;
                            if (pOILogger11.check(POILogger.DEBUG)) {
                                pOILogger11.log(DEBUG, (Object) "found boundsheet record at " + k);
                            }
                            retval.boundsheets.add((BoundSheetRecord) rec);
                            retval.records.setBspos(k);
                            break;
                        case ShapeTypes.FLOW_CHART_INPUT_OUTPUT /*134*/:
                            POILogger pOILogger12 = log;
                            if (pOILogger12.check(POILogger.DEBUG)) {
                                pOILogger12.log(DEBUG, (Object) "found WriteProtect at " + k);
                            }
                            retval.writeProtect = (WriteProtectRecord) rec;
                            break;
                        case ShapeTypes.FLOW_CHART_SUMMING_JUNCTION /*146*/:
                            POILogger pOILogger13 = log;
                            if (pOILogger13.check(POILogger.DEBUG)) {
                                pOILogger13.log(DEBUG, (Object) "found palette record at " + k);
                            }
                            retval.records.setPalettepos(k);
                            break;
                        case 224:
                            POILogger pOILogger14 = log;
                            if (pOILogger14.check(POILogger.DEBUG)) {
                                pOILogger14.log(DEBUG, (Object) "found XF record at " + k);
                            }
                            retval.records.setXfpos(k);
                            retval.numxfs++;
                            break;
                        case 252:
                            POILogger pOILogger15 = log;
                            if (pOILogger15.check(POILogger.DEBUG)) {
                                pOILogger15.log(DEBUG, (Object) "found sst record at " + k);
                            }
                            retval.sst = (SSTRecord) rec;
                            break;
                        case 317:
                            POILogger pOILogger16 = log;
                            if (pOILogger16.check(POILogger.DEBUG)) {
                                pOILogger16.log(DEBUG, (Object) "found tabid record at " + k);
                            }
                            retval.records.setTabpos(k);
                            break;
                        case 1054:
                            POILogger pOILogger17 = log;
                            if (pOILogger17.check(POILogger.DEBUG)) {
                                pOILogger17.log(DEBUG, (Object) "found format record at " + k);
                            }
                            retval.formats.add((FormatRecord) rec);
                            retval.maxformatid = retval.maxformatid >= ((FormatRecord) rec).getIndexCode() ? retval.maxformatid : ((FormatRecord) rec).getIndexCode();
                            break;
                        case 2196:
                            NameCommentRecord ncr = (NameCommentRecord) rec;
                            POILogger pOILogger18 = log;
                            if (pOILogger18.check(POILogger.DEBUG)) {
                                pOILogger18.log(DEBUG, (Object) "found NameComment at " + k);
                            }
                            retval.commentRecords.put(ncr.getNameText(), ncr);
                            break;
                    }
                    records2.add(rec);
                    k++;
                }
            }
        }
        while (k < recs.size()) {
            Record rec2 = recs.get(k);
            if (rec2.getSid() == 440) {
                retval.hyperlinks.add((HyperlinkRecord) rec2);
            }
            k++;
        }
        if (retval.windowOne == null) {
            retval.windowOne = createWindowOne();
        }
        POILogger pOILogger19 = log;
        if (pOILogger19.check(POILogger.DEBUG)) {
            pOILogger19.log(DEBUG, (Object) "exit create workbook from existing file function");
        }
        return retval;
    }

    public static InternalWorkbook createWorkbook() {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "creating new workbook from scratch");
        }
        InternalWorkbook retval = new InternalWorkbook();
        List<Record> records2 = new ArrayList<>(30);
        retval.records.setRecords(records2);
        List<FormatRecord> formats2 = retval.formats;
        records2.add(createBOF());
        records2.add(new InterfaceHdrRecord(1200));
        records2.add(createMMS());
        records2.add(InterfaceEndRecord.instance);
        records2.add(createWriteAccess());
        records2.add(createCodepage());
        records2.add(createDSF());
        records2.add(createTabId());
        retval.records.setTabpos(records2.size() - 1);
        records2.add(createFnGroupCount());
        records2.add(createWindowProtect());
        records2.add(createProtect());
        retval.records.setProtpos(records2.size() - 1);
        records2.add(createPassword());
        records2.add(createProtectionRev4());
        records2.add(createPasswordRev4());
        WindowOneRecord createWindowOne = createWindowOne();
        retval.windowOne = createWindowOne;
        records2.add(createWindowOne);
        records2.add(createBackup());
        retval.records.setBackuppos(records2.size() - 1);
        records2.add(createHideObj());
        records2.add(createDateWindow1904());
        records2.add(createPrecision());
        records2.add(createRefreshAll());
        records2.add(createBookBool());
        records2.add(createFont());
        records2.add(createFont());
        records2.add(createFont());
        records2.add(createFont());
        retval.records.setFontpos(records2.size() - 1);
        retval.numfonts = 4;
        for (int i = 0; i <= 7; i++) {
            FormatRecord rec = createFormat(i);
            retval.maxformatid = retval.maxformatid >= rec.getIndexCode() ? retval.maxformatid : rec.getIndexCode();
            formats2.add(rec);
            records2.add(rec);
        }
        for (int k = 0; k < 21; k++) {
            records2.add(createExtendedFormat(k));
            retval.numxfs++;
        }
        retval.records.setXfpos(records2.size() - 1);
        for (int k2 = 0; k2 < 6; k2++) {
            records2.add(createStyle(k2));
        }
        records2.add(createUseSelFS());
        for (int k3 = 0; k3 < 1; k3++) {
            BoundSheetRecord bsr = createBoundSheet(k3);
            records2.add(bsr);
            retval.boundsheets.add(bsr);
            retval.records.setBspos(records2.size() - 1);
        }
        records2.add(createCountry());
        for (int k4 = 0; k4 < 1; k4++) {
            retval.getOrCreateLinkTable().checkExternSheet(k4);
        }
        SSTRecord sSTRecord = new SSTRecord();
        retval.sst = sSTRecord;
        records2.add(sSTRecord);
        records2.add(createExtendedSST());
        records2.add(EOFRecord.instance);
        POILogger pOILogger2 = log;
        if (pOILogger2.check(POILogger.DEBUG)) {
            pOILogger2.log(DEBUG, (Object) "exit create new workbook from scratch");
        }
        return retval;
    }

    public NameRecord getSpecificBuiltinRecord(byte name, int sheetNumber) {
        return getOrCreateLinkTable().getSpecificBuiltinRecord(name, sheetNumber);
    }

    public void removeBuiltinRecord(byte name, int sheetIndex) {
        this.linkTable.removeBuiltinRecord(name, sheetIndex);
    }

    public int getNumRecords() {
        return this.records.size();
    }

    public FontRecord getFontRecordAt(int idx) {
        int index = idx;
        if (index > 4) {
            index--;
        }
        if (index <= this.numfonts - 1) {
            WorkbookRecordList workbookRecordList = this.records;
            return (FontRecord) workbookRecordList.get((workbookRecordList.getFontpos() - (this.numfonts - 1)) + index);
        }
        throw new ArrayIndexOutOfBoundsException("There are only " + this.numfonts + " font records, you asked for " + idx);
    }

    public int getFontIndex(FontRecord font) {
        int i = 0;
        while (i <= this.numfonts) {
            WorkbookRecordList workbookRecordList = this.records;
            if (((FontRecord) workbookRecordList.get((workbookRecordList.getFontpos() - (this.numfonts - 1)) + i)) != font) {
                i++;
            } else if (i > 3) {
                return i + 1;
            } else {
                return i;
            }
        }
        throw new IllegalArgumentException("Could not find that font!");
    }

    public FontRecord createNewFont() {
        FontRecord rec = createFont();
        WorkbookRecordList workbookRecordList = this.records;
        workbookRecordList.add(workbookRecordList.getFontpos() + 1, rec);
        WorkbookRecordList workbookRecordList2 = this.records;
        workbookRecordList2.setFontpos(workbookRecordList2.getFontpos() + 1);
        this.numfonts++;
        return rec;
    }

    public void removeFontRecord(FontRecord rec) {
        this.records.remove((Object) rec);
        this.numfonts--;
    }

    public int getNumberOfFontRecords() {
        return this.numfonts;
    }

    public void setSheetBof(int sheetIndex, int pos) {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "setting bof for sheetnum =", (Object) Integer.valueOf(sheetIndex), (Object) " at pos=", (Object) Integer.valueOf(pos));
        }
        checkSheets(sheetIndex);
        getBoundSheetRec(sheetIndex).setPositionOfBof(pos);
    }

    private BoundSheetRecord getBoundSheetRec(int sheetIndex) {
        return this.boundsheets.get(sheetIndex);
    }

    public BackupRecord getBackupRecord() {
        WorkbookRecordList workbookRecordList = this.records;
        return (BackupRecord) workbookRecordList.get(workbookRecordList.getBackuppos());
    }

    public void setSheetName(int sheetnum, String sheetname) {
        checkSheets(sheetnum);
        this.boundsheets.get(sheetnum).setSheetname(sheetname);
    }

    public boolean doesContainsSheetName(String name, int excludeSheetIdx) {
        String aName = name;
        if (aName.length() > 31) {
            aName = aName.substring(0, 31);
        }
        for (int i = 0; i < this.boundsheets.size(); i++) {
            BoundSheetRecord boundSheetRecord = getBoundSheetRec(i);
            if (excludeSheetIdx != i) {
                String bName = boundSheetRecord.getSheetname();
                if (bName.length() > 31) {
                    bName = bName.substring(0, 31);
                }
                if (aName.equalsIgnoreCase(bName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setSheetOrder(String sheetname, int pos) {
        int sheetNumber = getSheetIndex(sheetname);
        List<BoundSheetRecord> list = this.boundsheets;
        list.add(pos, list.remove(sheetNumber));
    }

    public String getSheetName(int sheetIndex) {
        return getBoundSheetRec(sheetIndex).getSheetname();
    }

    public boolean isSheetHidden(int sheetnum) {
        return getBoundSheetRec(sheetnum).isHidden();
    }

    public boolean isSheetVeryHidden(int sheetnum) {
        return getBoundSheetRec(sheetnum).isVeryHidden();
    }

    public void setSheetHidden(int sheetnum, boolean hidden) {
        getBoundSheetRec(sheetnum).setHidden(hidden);
    }

    public void setSheetHidden(int sheetnum, int hidden) {
        BoundSheetRecord bsr = getBoundSheetRec(sheetnum);
        boolean h = false;
        boolean vh = false;
        if (hidden != 0) {
            if (hidden == 1) {
                h = true;
            } else if (hidden == 2) {
                vh = true;
            } else {
                throw new IllegalArgumentException("Invalid hidden flag " + hidden + " given, must be 0, 1 or 2");
            }
        }
        bsr.setHidden(h);
        bsr.setVeryHidden(vh);
    }

    public int getSheetIndex(String name) {
        for (int k = 0; k < this.boundsheets.size(); k++) {
            if (getSheetName(k).equalsIgnoreCase(name)) {
                return k;
            }
        }
        return -1;
    }

    private void checkSheets(int sheetnum) {
        if (this.boundsheets.size() <= sheetnum) {
            if (this.boundsheets.size() + 1 > sheetnum) {
                BoundSheetRecord bsr = createBoundSheet(sheetnum);
                WorkbookRecordList workbookRecordList = this.records;
                workbookRecordList.add(workbookRecordList.getBspos() + 1, bsr);
                WorkbookRecordList workbookRecordList2 = this.records;
                workbookRecordList2.setBspos(workbookRecordList2.getBspos() + 1);
                this.boundsheets.add(bsr);
                getOrCreateLinkTable().checkExternSheet(sheetnum);
                fixTabIdRecord();
                return;
            }
            throw new RuntimeException("Sheet number out of bounds!");
        } else if (this.records.getTabpos() > 0) {
            WorkbookRecordList workbookRecordList3 = this.records;
            if (((TabIdRecord) workbookRecordList3.get(workbookRecordList3.getTabpos()))._tabids.length < this.boundsheets.size()) {
                fixTabIdRecord();
            }
        }
    }

    public void removeSheet(int sheetIndex) {
        if (this.boundsheets.size() > sheetIndex) {
            WorkbookRecordList workbookRecordList = this.records;
            workbookRecordList.remove((workbookRecordList.getBspos() - (this.boundsheets.size() - 1)) + sheetIndex);
            this.boundsheets.remove(sheetIndex);
            fixTabIdRecord();
        }
        int sheetNum1Based = sheetIndex + 1;
        for (int i = 0; i < getNumNames(); i++) {
            NameRecord nr = getNameRecord(i);
            if (nr.getSheetNumber() == sheetNum1Based) {
                nr.setSheetNumber(0);
            } else if (nr.getSheetNumber() > sheetNum1Based) {
                nr.setSheetNumber(nr.getSheetNumber() - 1);
            }
        }
    }

    private void fixTabIdRecord() {
        WorkbookRecordList workbookRecordList = this.records;
        TabIdRecord tir = (TabIdRecord) workbookRecordList.get(workbookRecordList.getTabpos());
        short[] tia = new short[this.boundsheets.size()];
        for (short k = 0; k < tia.length; k = (short) (k + 1)) {
            tia[k] = k;
        }
        tir.setTabIdArray(tia);
    }

    public int getNumSheets() {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "getNumSheets=", (Object) Integer.valueOf(this.boundsheets.size()));
        }
        return this.boundsheets.size();
    }

    public int getNumExFormats() {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "getXF=", (Object) Integer.valueOf(this.numxfs));
        }
        return this.numxfs;
    }

    public ExtendedFormatRecord getExFormatAt(int index) {
        return (ExtendedFormatRecord) this.records.get((this.records.getXfpos() - (this.numxfs - 1)) + index);
    }

    public void removeExFormatRecord(ExtendedFormatRecord rec) {
        this.records.remove((Object) rec);
        this.numxfs--;
    }

    public ExtendedFormatRecord createCellXF() {
        ExtendedFormatRecord xf = createExtendedFormat();
        WorkbookRecordList workbookRecordList = this.records;
        workbookRecordList.add(workbookRecordList.getXfpos() + 1, xf);
        WorkbookRecordList workbookRecordList2 = this.records;
        workbookRecordList2.setXfpos(workbookRecordList2.getXfpos() + 1);
        this.numxfs++;
        return xf;
    }

    public StyleRecord getStyleRecord(int xfIndex) {
        for (int i = this.records.getXfpos(); i < this.records.size(); i++) {
            Record r = this.records.get(i);
            if (!(r instanceof ExtendedFormatRecord) && (r instanceof StyleRecord)) {
                StyleRecord sr = (StyleRecord) r;
                if (sr.getXFIndex() == xfIndex) {
                    return sr;
                }
            }
        }
        return null;
    }

    public StyleRecord createStyleRecord(int xfIndex) {
        StyleRecord newSR = new StyleRecord();
        newSR.setXFIndex(xfIndex);
        int addAt = -1;
        for (int i = this.records.getXfpos(); i < this.records.size() && addAt == -1; i++) {
            Record r = this.records.get(i);
            if (!(r instanceof ExtendedFormatRecord) && !(r instanceof StyleRecord)) {
                addAt = i;
            }
        }
        if (addAt != -1) {
            this.records.add(addAt, newSR);
            return newSR;
        }
        throw new IllegalStateException("No XF Records found!");
    }

    public int addSSTString(UnicodeString string) {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "insert to sst string='", (Object) string);
        }
        if (this.sst == null) {
            insertSST();
        }
        return this.sst.addString(string);
    }

    public UnicodeString getSSTString(int str) {
        if (this.sst == null) {
            insertSST();
        }
        UnicodeString retval = this.sst.getString(str);
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "Returning SST for index=", (Object) Integer.valueOf(str), (Object) " String= ", (Object) retval);
        }
        return retval;
    }

    public void insertSST() {
        POILogger pOILogger = log;
        if (pOILogger.check(POILogger.DEBUG)) {
            pOILogger.log(DEBUG, (Object) "creating new SST via insertSST!");
        }
        this.sst = new SSTRecord();
        WorkbookRecordList workbookRecordList = this.records;
        workbookRecordList.add(workbookRecordList.size() - 1, createExtendedSST());
        WorkbookRecordList workbookRecordList2 = this.records;
        workbookRecordList2.add(workbookRecordList2.size() - 2, this.sst);
    }

    /*  JADX ERROR: IF instruction can be used only in fallback mode
        jadx.core.utils.exceptions.CodegenException: IF instruction can be used only in fallback mode
        	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:579)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:485)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
        	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:221)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:66)
        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
        	at java.util.ArrayList.forEach(ArrayList.java:1259)
        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        */
    public int serialize(int r11, byte[] r12) {
        /*
            r10 = this;
            org.apache.poi.util.POILogger r0 = log
            int r1 = org.apache.poi.util.POILogger.DEBUG
            boolean r1 = r0.check(r1)
            if (r1 == 0) goto L_0x0011
            int r1 = DEBUG
            java.lang.String r2 = "Serializing Workbook with offsets"
            r0.log((int) r1, (java.lang.Object) r2)
        L_0x0011:
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
        L_0x0016:
            org.apache.poi.hssf.model.WorkbookRecordList r5 = r10.records
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0078
            org.apache.poi.hssf.model.WorkbookRecordList r5 = r10.records
            org.apache.poi.hssf.record.Record r5 = r5.get(r4)
            short r6 = r5.getSid()
            r7 = 449(0x1c1, float:6.29E-43)
            if (r6 != r7) goto L_0x0035
            r6 = r5
            org.apache.poi.hssf.record.RecalcIdRecord r6 = (org.apache.poi.hssf.record.RecalcIdRecord) r6
            boolean r6 = r6.isNeeded()
            if (r6 == 0) goto L_0x0075
        L_0x0035:
            r6 = 0
            boolean r7 = r5 instanceof org.apache.poi.hssf.record.SSTRecord
            if (r7 == 0) goto L_0x003e
            r1 = r5
            org.apache.poi.hssf.record.SSTRecord r1 = (org.apache.poi.hssf.record.SSTRecord) r1
            r2 = r0
        L_0x003e:
            short r7 = r5.getSid()
            r8 = 255(0xff, float:3.57E-43)
            if (r7 != r8) goto L_0x004e
            if (r1 == 0) goto L_0x004e
            int r7 = r2 + r11
            org.apache.poi.hssf.record.ExtSSTRecord r5 = r1.createExtSSTRecord(r7)
        L_0x004e:
            boolean r7 = r5 instanceof org.apache.poi.hssf.record.BoundSheetRecord
            if (r7 == 0) goto L_0x006e
            if (r3 != 0) goto L_0x0074
            r7 = 0
        L_0x0055:
            java.util.List<org.apache.poi.hssf.record.BoundSheetRecord> r8 = r10.boundsheets
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x006c
            org.apache.poi.hssf.record.BoundSheetRecord r8 = r10.getBoundSheetRec(r7)
            int r9 = r0 + r11
            int r9 = r9 + r6
            int r8 = r8.serialize(r9, r12)
            int r6 = r6 + r8
            int r7 = r7 + 1
            goto L_0x0055
        L_0x006c:
            r3 = 1
            goto L_0x0074
        L_0x006e:
            int r7 = r0 + r11
            int r6 = r5.serialize(r7, r12)
        L_0x0074:
            int r0 = r0 + r6
        L_0x0075:
            int r4 = r4 + 1
            goto L_0x0016
        L_0x0078:
            org.apache.poi.util.POILogger r4 = log
            int r5 = org.apache.poi.util.POILogger.DEBUG
            boolean r5 = r4.check(r5)
            if (r5 == 0) goto L_0x0089
            int r5 = DEBUG
            java.lang.String r6 = "Exiting serialize workbook"
            r4.log((int) r5, (java.lang.Object) r6)
        L_0x0089:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.model.InternalWorkbook.serialize(int, byte[]):int");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: org.apache.poi.hssf.record.Record} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.apache.poi.hssf.record.SSTRecord} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.apache.poi.hssf.record.RecalcIdRecord} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getSize() {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            r2 = 0
        L_0x0003:
            org.apache.poi.hssf.model.WorkbookRecordList r3 = r6.records
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0041
            org.apache.poi.hssf.model.WorkbookRecordList r3 = r6.records
            org.apache.poi.hssf.record.Record r3 = r3.get(r2)
            short r4 = r3.getSid()
            r5 = 449(0x1c1, float:6.29E-43)
            if (r4 != r5) goto L_0x0022
            r4 = r3
            org.apache.poi.hssf.record.RecalcIdRecord r4 = (org.apache.poi.hssf.record.RecalcIdRecord) r4
            boolean r4 = r4.isNeeded()
            if (r4 == 0) goto L_0x003e
        L_0x0022:
            boolean r4 = r3 instanceof org.apache.poi.hssf.record.SSTRecord
            if (r4 == 0) goto L_0x0029
            r1 = r3
            org.apache.poi.hssf.record.SSTRecord r1 = (org.apache.poi.hssf.record.SSTRecord) r1
        L_0x0029:
            short r4 = r3.getSid()
            r5 = 255(0xff, float:3.57E-43)
            if (r4 != r5) goto L_0x0039
            if (r1 == 0) goto L_0x0039
            int r4 = r1.calcExtSSTRecordSize()
            int r0 = r0 + r4
            goto L_0x003e
        L_0x0039:
            int r4 = r3.getRecordSize()
            int r0 = r0 + r4
        L_0x003e:
            int r2 = r2 + 1
            goto L_0x0003
        L_0x0041:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.model.InternalWorkbook.getSize():int");
    }

    private static BOFRecord createBOF() {
        BOFRecord retval = new BOFRecord();
        retval.setVersion(BOFRecord.VERSION);
        retval.setType(5);
        retval.setBuild(BOFRecord.BUILD);
        retval.setBuildYear(BOFRecord.BUILD_YEAR);
        retval.setHistoryBitMask(65);
        retval.setRequiredVersion(6);
        return retval;
    }

    private static MMSRecord createMMS() {
        MMSRecord retval = new MMSRecord();
        retval.setAddMenuCount((byte) 0);
        retval.setDelMenuCount((byte) 0);
        return retval;
    }

    private static WriteAccessRecord createWriteAccess() {
        WriteAccessRecord retval = new WriteAccessRecord();
        try {
            retval.setUsername(System.getProperty("user.name"));
        } catch (AccessControlException e) {
            retval.setUsername("POI");
        }
        return retval;
    }

    private static CodepageRecord createCodepage() {
        CodepageRecord retval = new CodepageRecord();
        retval.setCodepage(1200);
        return retval;
    }

    private static DSFRecord createDSF() {
        return new DSFRecord(false);
    }

    private static TabIdRecord createTabId() {
        return new TabIdRecord();
    }

    private static FnGroupCountRecord createFnGroupCount() {
        FnGroupCountRecord retval = new FnGroupCountRecord();
        retval.setCount(14);
        return retval;
    }

    private static WindowProtectRecord createWindowProtect() {
        return new WindowProtectRecord(false);
    }

    private static ProtectRecord createProtect() {
        return new ProtectRecord(false);
    }

    private static PasswordRecord createPassword() {
        return new PasswordRecord(0);
    }

    private static ProtectionRev4Record createProtectionRev4() {
        return new ProtectionRev4Record(false);
    }

    private static PasswordRev4Record createPasswordRev4() {
        return new PasswordRev4Record(0);
    }

    private static WindowOneRecord createWindowOne() {
        WindowOneRecord retval = new WindowOneRecord();
        retval.setHorizontalHold(360);
        retval.setVerticalHold(EscherProperties.BLIP__PICTURELINE);
        retval.setWidth(14940);
        retval.setHeight(9150);
        retval.setOptions(56);
        retval.setActiveSheetIndex(0);
        retval.setFirstVisibleTab(0);
        retval.setNumSelectedTabs(1);
        retval.setTabWidthRatio(600);
        return retval;
    }

    private static BackupRecord createBackup() {
        BackupRecord retval = new BackupRecord();
        retval.setBackup(0);
        return retval;
    }

    private static HideObjRecord createHideObj() {
        HideObjRecord retval = new HideObjRecord();
        retval.setHideObj(0);
        return retval;
    }

    private static DateWindow1904Record createDateWindow1904() {
        DateWindow1904Record retval = new DateWindow1904Record();
        retval.setWindowing(0);
        return retval;
    }

    private static PrecisionRecord createPrecision() {
        PrecisionRecord retval = new PrecisionRecord();
        retval.setFullPrecision(true);
        return retval;
    }

    private static RefreshAllRecord createRefreshAll() {
        return new RefreshAllRecord(false);
    }

    private static BookBoolRecord createBookBool() {
        BookBoolRecord retval = new BookBoolRecord();
        retval.setSaveLinkValues(0);
        return retval;
    }

    private static FontRecord createFont() {
        FontRecord retval = new FontRecord();
        retval.setFontHeight(EscherAggregate.ST_ACTIONBUTTONMOVIE);
        retval.setAttributes(0);
        retval.setColorPaletteIndex(Font.COLOR_NORMAL);
        retval.setBoldWeight(400);
        retval.setFontName(HSSFFont.FONT_ARIAL);
        return retval;
    }

    private static FormatRecord createFormat(int id) {
        switch (id) {
            case 0:
                return new FormatRecord(5, "\"$\"#,##0_);\\(\"$\"#,##0\\)");
            case 1:
                return new FormatRecord(6, "\"$\"#,##0_);[Red]\\(\"$\"#,##0\\)");
            case 2:
                return new FormatRecord(7, "\"$\"#,##0.00_);\\(\"$\"#,##0.00\\)");
            case 3:
                return new FormatRecord(8, "\"$\"#,##0.00_);[Red]\\(\"$\"#,##0.00\\)");
            case 4:
                return new FormatRecord(42, "_(\"$\"* #,##0_);_(\"$\"* \\(#,##0\\);_(\"$\"* \"-\"_);_(@_)");
            case 5:
                return new FormatRecord(41, "_(* #,##0_);_(* \\(#,##0\\);_(* \"-\"_);_(@_)");
            case 6:
                return new FormatRecord(44, "_(\"$\"* #,##0.00_);_(\"$\"* \\(#,##0.00\\);_(\"$\"* \"-\"??_);_(@_)");
            case 7:
                return new FormatRecord(43, "_(* #,##0.00_);_(* \\(#,##0.00\\);_(* \"-\"??_);_(@_)");
            default:
                throw new IllegalArgumentException("Unexpected id " + id);
        }
    }

    private static ExtendedFormatRecord createExtendedFormat(int id) {
        ExtendedFormatRecord retval = new ExtendedFormatRecord();
        switch (id) {
            case 0:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(0);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 1:
                retval.setFontIndex(1);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 2:
                retval.setFontIndex(1);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 3:
                retval.setFontIndex(2);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 4:
                retval.setFontIndex(2);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 5:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 6:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 7:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 8:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 9:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 10:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 11:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 12:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 13:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 14:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-3072);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 15:
                retval.setFontIndex(0);
                retval.setFormatIndex(0);
                retval.setCellOptions(1);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(0);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 16:
                retval.setFontIndex(1);
                retval.setFormatIndex(43);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-2048);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 17:
                retval.setFontIndex(1);
                retval.setFormatIndex(41);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-2048);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 18:
                retval.setFontIndex(1);
                retval.setFormatIndex(44);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-2048);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 19:
                retval.setFontIndex(1);
                retval.setFormatIndex(42);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-2048);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 20:
                retval.setFontIndex(1);
                retval.setFormatIndex(9);
                retval.setCellOptions(-11);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(-2048);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 21:
                retval.setFontIndex(5);
                retval.setFormatIndex(0);
                retval.setCellOptions(1);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(2048);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 22:
                retval.setFontIndex(6);
                retval.setFormatIndex(0);
                retval.setCellOptions(1);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(23552);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 23:
                retval.setFontIndex(0);
                retval.setFormatIndex(49);
                retval.setCellOptions(1);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(23552);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 24:
                retval.setFontIndex(0);
                retval.setFormatIndex(8);
                retval.setCellOptions(1);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(23552);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
            case 25:
                retval.setFontIndex(6);
                retval.setFormatIndex(8);
                retval.setCellOptions(1);
                retval.setAlignmentOptions(32);
                retval.setIndentionOptions(23552);
                retval.setBorderOptions(0);
                retval.setPaletteOptions(0);
                retval.setAdtlPaletteOptions(0);
                retval.setFillPaletteOptions(8384);
                break;
        }
        return retval;
    }

    private static ExtendedFormatRecord createExtendedFormat() {
        ExtendedFormatRecord retval = new ExtendedFormatRecord();
        retval.setFontIndex(0);
        retval.setFormatIndex(0);
        retval.setCellOptions(1);
        retval.setAlignmentOptions(32);
        retval.setIndentionOptions(0);
        retval.setBorderOptions(0);
        retval.setPaletteOptions(0);
        retval.setAdtlPaletteOptions(0);
        retval.setFillPaletteOptions(8384);
        retval.setTopBorderPaletteIdx(8);
        retval.setBottomBorderPaletteIdx(8);
        retval.setLeftBorderPaletteIdx(8);
        retval.setRightBorderPaletteIdx(8);
        return retval;
    }

    private static StyleRecord createStyle(int id) {
        StyleRecord retval = new StyleRecord();
        if (id == 0) {
            retval.setXFIndex(16);
            retval.setBuiltinStyle(3);
            retval.setOutlineStyleLevel(-1);
        } else if (id == 1) {
            retval.setXFIndex(17);
            retval.setBuiltinStyle(6);
            retval.setOutlineStyleLevel(-1);
        } else if (id == 2) {
            retval.setXFIndex(18);
            retval.setBuiltinStyle(4);
            retval.setOutlineStyleLevel(-1);
        } else if (id == 3) {
            retval.setXFIndex(19);
            retval.setBuiltinStyle(7);
            retval.setOutlineStyleLevel(-1);
        } else if (id == 4) {
            retval.setXFIndex(0);
            retval.setBuiltinStyle(0);
            retval.setOutlineStyleLevel(-1);
        } else if (id == 5) {
            retval.setXFIndex(20);
            retval.setBuiltinStyle(5);
            retval.setOutlineStyleLevel(-1);
        }
        return retval;
    }

    private static PaletteRecord createPalette() {
        return new PaletteRecord();
    }

    private static UseSelFSRecord createUseSelFS() {
        return new UseSelFSRecord(false);
    }

    private static BoundSheetRecord createBoundSheet(int id) {
        return new BoundSheetRecord("Sheet" + (id + 1));
    }

    private static CountryRecord createCountry() {
        CountryRecord retval = new CountryRecord();
        retval.setDefaultCountry(1);
        if (Locale.getDefault().toString().equals("ru_RU")) {
            retval.setCurrentCountry(7);
        } else {
            retval.setCurrentCountry(1);
        }
        return retval;
    }

    private static ExtSSTRecord createExtendedSST() {
        ExtSSTRecord retval = new ExtSSTRecord();
        retval.setNumStringsPerBucket(8);
        return retval;
    }

    private LinkTable getOrCreateLinkTable() {
        if (this.linkTable == null) {
            this.linkTable = new LinkTable((short) getNumSheets(), this.records);
        }
        return this.linkTable;
    }

    public String findSheetNameFromExternSheet(int externSheetIndex) {
        int indexToSheet = this.linkTable.getIndexToInternalSheet(externSheetIndex);
        if (indexToSheet >= 0 && indexToSheet < this.boundsheets.size()) {
            return getSheetName(indexToSheet);
        }
        return "";
    }

    public EvaluationWorkbook.ExternalSheet getExternalSheet(int externSheetIndex) {
        String[] extNames = this.linkTable.getExternalBookAndSheetName(externSheetIndex);
        if (extNames == null) {
            return null;
        }
        return new EvaluationWorkbook.ExternalSheet(extNames[0], extNames[1]);
    }

    public EvaluationWorkbook.ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        String nameName = this.linkTable.resolveNameXText(externSheetIndex, externNameIndex);
        if (nameName == null) {
            return null;
        }
        return new EvaluationWorkbook.ExternalName(nameName, externNameIndex, this.linkTable.resolveNameXIx(externSheetIndex, externNameIndex));
    }

    public int getSheetIndexFromExternSheetIndex(int externSheetNumber) {
        return this.linkTable.getSheetIndexFromExternSheetIndex(externSheetNumber);
    }

    public short checkExternSheet(int sheetNumber) {
        return (short) getOrCreateLinkTable().checkExternSheet(sheetNumber);
    }

    public int getExternalSheetIndex(String workbookName, String sheetName) {
        return getOrCreateLinkTable().getExternalSheetIndex(workbookName, sheetName);
    }

    public int getNumNames() {
        LinkTable linkTable2 = this.linkTable;
        if (linkTable2 == null) {
            return 0;
        }
        return linkTable2.getNumNames();
    }

    public NameRecord getNameRecord(int index) {
        return this.linkTable.getNameRecord(index);
    }

    public NameCommentRecord getNameCommentRecord(NameRecord nameRecord) {
        return this.commentRecords.get(nameRecord.getNameText());
    }

    public NameRecord createName() {
        return addName(new NameRecord());
    }

    public NameRecord addName(NameRecord name) {
        getOrCreateLinkTable().addName(name);
        return name;
    }

    public NameRecord createBuiltInName(byte builtInName, int sheetNumber) {
        if (sheetNumber < 0 || sheetNumber + 1 > 32767) {
            throw new IllegalArgumentException("Sheet number [" + sheetNumber + "]is not valid ");
        }
        NameRecord name = new NameRecord(builtInName, sheetNumber);
        if (!this.linkTable.nameAlreadyExists(name)) {
            addName(name);
            return name;
        }
        throw new RuntimeException("Builtin (" + builtInName + ") already exists for sheet (" + sheetNumber + ")");
    }

    public void removeName(int nameIndex) {
        if (this.linkTable.getNumNames() > nameIndex) {
            this.records.remove(findFirstRecordLocBySid(24) + nameIndex);
            this.linkTable.removeName(nameIndex);
        }
    }

    public void updateNameCommentRecordCache(NameCommentRecord commentRecord) {
        if (this.commentRecords.containsValue(commentRecord)) {
            Iterator i$ = this.commentRecords.entrySet().iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Map.Entry<String, NameCommentRecord> entry = i$.next();
                if (entry.getValue().equals(commentRecord)) {
                    this.commentRecords.remove(entry.getKey());
                    break;
                }
            }
        }
        this.commentRecords.put(commentRecord.getNameText(), commentRecord);
    }

    public short getFormat(String format, boolean createIfNotFound) {
        for (FormatRecord r : this.formats) {
            if (r.getFormatString().equals(format)) {
                return (short) r.getIndexCode();
            }
        }
        if (createIfNotFound) {
            return (short) createFormat(format);
        }
        return -1;
    }

    public List<FormatRecord> getFormats() {
        return this.formats;
    }

    public int createFormat(String formatString) {
        int i = this.maxformatid;
        int i2 = 164;
        if (i >= 164) {
            i2 = i + 1;
        }
        this.maxformatid = i2;
        FormatRecord rec = new FormatRecord(i2, formatString);
        int pos = 0;
        while (pos < this.records.size() && this.records.get(pos).getSid() != 1054) {
            pos++;
        }
        int pos2 = pos + this.formats.size();
        this.formats.add(rec);
        this.records.add(pos2, rec);
        return this.maxformatid;
    }

    public Record findFirstRecordBySid(short sid) {
        Iterator i$ = this.records.iterator();
        while (i$.hasNext()) {
            Record record = i$.next();
            if (record.getSid() == sid) {
                return record;
            }
        }
        return null;
    }

    public int findFirstRecordLocBySid(short sid) {
        int index = 0;
        Iterator i$ = this.records.iterator();
        while (i$.hasNext()) {
            if (i$.next().getSid() == sid) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public Record findNextRecordBySid(short sid, int pos) {
        int matches = 0;
        Iterator i$ = this.records.iterator();
        while (i$.hasNext()) {
            Record record = i$.next();
            if (record.getSid() == sid) {
                int matches2 = matches + 1;
                if (matches == pos) {
                    return record;
                }
                matches = matches2;
            }
        }
        return null;
    }

    public List<HyperlinkRecord> getHyperlinks() {
        return this.hyperlinks;
    }

    public List<Record> getRecords() {
        return this.records.getRecords();
    }

    public boolean isUsing1904DateWindowing() {
        return this.uses1904datewindowing;
    }

    public PaletteRecord getCustomPalette() {
        int palettePos = this.records.getPalettepos();
        if (palettePos != -1) {
            Record rec = this.records.get(palettePos);
            if (rec instanceof PaletteRecord) {
                return (PaletteRecord) rec;
            }
            throw new RuntimeException("InternalError: Expected PaletteRecord but got a '" + rec + "'");
        }
        PaletteRecord palette = createPalette();
        this.records.add(1, palette);
        this.records.setPalettepos(1);
        return palette;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.apache.poi.ddf.EscherDggRecord} */
    /* JADX WARNING: type inference failed for: r4v2, types: [org.apache.poi.ddf.EscherRecord] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void findDrawingGroup() {
        /*
            r8 = this;
            org.apache.poi.hssf.model.DrawingManager2 r0 = r8.drawingManager
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            org.apache.poi.hssf.model.WorkbookRecordList r0 = r8.records
            java.util.Iterator r0 = r0.iterator()
        L_0x000b:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x004a
            java.lang.Object r1 = r0.next()
            org.apache.poi.hssf.record.Record r1 = (org.apache.poi.hssf.record.Record) r1
            boolean r2 = r1 instanceof org.apache.poi.hssf.record.DrawingGroupRecord
            if (r2 == 0) goto L_0x0049
            r2 = r1
            org.apache.poi.hssf.record.DrawingGroupRecord r2 = (org.apache.poi.hssf.record.DrawingGroupRecord) r2
            r2.processChildRecords()
            org.apache.poi.ddf.EscherContainerRecord r3 = r2.getEscherContainer()
            if (r3 != 0) goto L_0x0028
            goto L_0x000b
        L_0x0028:
            r4 = 0
            java.util.Iterator r5 = r3.getChildIterator()
        L_0x002d:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x003f
            java.lang.Object r6 = r5.next()
            boolean r7 = r6 instanceof org.apache.poi.ddf.EscherDggRecord
            if (r7 == 0) goto L_0x003e
            r4 = r6
            org.apache.poi.ddf.EscherDggRecord r4 = (org.apache.poi.ddf.EscherDggRecord) r4
        L_0x003e:
            goto L_0x002d
        L_0x003f:
            if (r4 == 0) goto L_0x0049
            org.apache.poi.hssf.model.DrawingManager2 r5 = new org.apache.poi.hssf.model.DrawingManager2
            r5.<init>(r4)
            r8.drawingManager = r5
            return
        L_0x0049:
            goto L_0x000b
        L_0x004a:
            r0 = 235(0xeb, float:3.3E-43)
            int r0 = r8.findFirstRecordLocBySid(r0)
            r1 = -1
            if (r0 == r1) goto L_0x0081
            org.apache.poi.hssf.model.WorkbookRecordList r1 = r8.records
            org.apache.poi.hssf.record.Record r1 = r1.get(r0)
            org.apache.poi.hssf.record.DrawingGroupRecord r1 = (org.apache.poi.hssf.record.DrawingGroupRecord) r1
            r2 = 0
            java.util.List r3 = r1.getEscherRecords()
            java.util.Iterator r3 = r3.iterator()
        L_0x0064:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0078
            java.lang.Object r4 = r3.next()
            org.apache.poi.ddf.EscherRecord r4 = (org.apache.poi.ddf.EscherRecord) r4
            boolean r5 = r4 instanceof org.apache.poi.ddf.EscherDggRecord
            if (r5 == 0) goto L_0x0077
            r2 = r4
            org.apache.poi.ddf.EscherDggRecord r2 = (org.apache.poi.ddf.EscherDggRecord) r2
        L_0x0077:
            goto L_0x0064
        L_0x0078:
            if (r2 == 0) goto L_0x0081
            org.apache.poi.hssf.model.DrawingManager2 r3 = new org.apache.poi.hssf.model.DrawingManager2
            r3.<init>(r2)
            r8.drawingManager = r3
        L_0x0081:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.model.InternalWorkbook.findDrawingGroup():void");
    }

    public void createDrawingGroup() {
        if (this.drawingManager == null) {
            EscherContainerRecord dggContainer = new EscherContainerRecord();
            EscherDggRecord dgg = new EscherDggRecord();
            EscherOptRecord opt = new EscherOptRecord();
            EscherSplitMenuColorsRecord splitMenuColors = new EscherSplitMenuColorsRecord();
            dggContainer.setRecordId(EscherContainerRecord.DGG_CONTAINER);
            dggContainer.setOptions(15);
            dgg.setRecordId(EscherDggRecord.RECORD_ID);
            dgg.setOptions(0);
            dgg.setShapeIdMax(1024);
            dgg.setNumShapesSaved(0);
            dgg.setDrawingsSaved(0);
            dgg.setFileIdClusters(new EscherDggRecord.FileIdCluster[0]);
            this.drawingManager = new DrawingManager2(dgg);
            EscherContainerRecord bstoreContainer = null;
            if (this.escherBSERecords.size() > 0) {
                bstoreContainer = new EscherContainerRecord();
                bstoreContainer.setRecordId(EscherContainerRecord.BSTORE_CONTAINER);
                bstoreContainer.setOptions((short) (15 | (this.escherBSERecords.size() << 4)));
                for (EscherBSERecord escherRecord : this.escherBSERecords) {
                    bstoreContainer.addChildRecord(escherRecord);
                }
            }
            opt.setRecordId(EscherOptRecord.RECORD_ID);
            opt.setOptions(51);
            opt.addEscherProperty(new EscherBoolProperty(191, 524296));
            opt.addEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, 134217793));
            opt.addEscherProperty(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, 134217792));
            splitMenuColors.setRecordId(EscherSplitMenuColorsRecord.RECORD_ID);
            splitMenuColors.setOptions(64);
            splitMenuColors.setColor1(134217741);
            splitMenuColors.setColor2(134217740);
            splitMenuColors.setColor3(134217751);
            splitMenuColors.setColor4(268435703);
            dggContainer.addChildRecord(dgg);
            if (bstoreContainer != null) {
                dggContainer.addChildRecord(bstoreContainer);
            }
            dggContainer.addChildRecord(opt);
            dggContainer.addChildRecord(splitMenuColors);
            int dgLoc = findFirstRecordLocBySid(DrawingGroupRecord.sid);
            if (dgLoc == -1) {
                DrawingGroupRecord drawingGroup = new DrawingGroupRecord();
                drawingGroup.addEscherRecord(dggContainer);
                getRecords().add(findFirstRecordLocBySid(140) + 1, drawingGroup);
                return;
            }
            DrawingGroupRecord drawingGroup2 = new DrawingGroupRecord();
            drawingGroup2.addEscherRecord(dggContainer);
            getRecords().set(dgLoc, drawingGroup2);
        }
    }

    public WindowOneRecord getWindowOne() {
        return this.windowOne;
    }

    public EscherBSERecord getBSERecord(int pictureIndex) {
        return this.escherBSERecords.get(pictureIndex - 1);
    }

    public int addBSERecord(EscherBSERecord e) {
        EscherContainerRecord bstoreContainer;
        createDrawingGroup();
        this.escherBSERecords.add(e);
        EscherContainerRecord dggContainer = (EscherContainerRecord) ((DrawingGroupRecord) getRecords().get(findFirstRecordLocBySid(DrawingGroupRecord.sid))).getEscherRecord(0);
        if (dggContainer.getChild(1).getRecordId() == -4095) {
            bstoreContainer = (EscherContainerRecord) dggContainer.getChild(1);
        } else {
            EscherContainerRecord bstoreContainer2 = new EscherContainerRecord();
            bstoreContainer2.setRecordId(EscherContainerRecord.BSTORE_CONTAINER);
            List<EscherRecord> childRecords = dggContainer.getChildRecords();
            childRecords.add(1, bstoreContainer2);
            dggContainer.setChildRecords(childRecords);
            bstoreContainer = bstoreContainer2;
        }
        bstoreContainer.setOptions((short) ((this.escherBSERecords.size() << 4) | 15));
        bstoreContainer.addChildRecord(e);
        return this.escherBSERecords.size();
    }

    public DrawingManager2 getDrawingManager() {
        return this.drawingManager;
    }

    public WriteProtectRecord getWriteProtect() {
        if (this.writeProtect == null) {
            this.writeProtect = new WriteProtectRecord();
            int i = 0;
            while (i < this.records.size() && !(this.records.get(i) instanceof BOFRecord)) {
                i++;
            }
            this.records.add(i + 1, this.writeProtect);
        }
        return this.writeProtect;
    }

    public WriteAccessRecord getWriteAccess() {
        if (this.writeAccess == null) {
            this.writeAccess = createWriteAccess();
            int i = 0;
            while (i < this.records.size() && !(this.records.get(i) instanceof InterfaceEndRecord)) {
                i++;
            }
            this.records.add(i + 1, this.writeAccess);
        }
        return this.writeAccess;
    }

    public FileSharingRecord getFileSharing() {
        if (this.fileShare == null) {
            this.fileShare = new FileSharingRecord();
            int i = 0;
            while (i < this.records.size() && !(this.records.get(i) instanceof WriteAccessRecord)) {
                i++;
            }
            this.records.add(i + 1, this.fileShare);
        }
        return this.fileShare;
    }

    public boolean isWriteProtected() {
        if (this.fileShare != null && getFileSharing().getReadOnly() == 1) {
            return true;
        }
        return false;
    }

    public void writeProtectWorkbook(String password, String username) {
        FileSharingRecord frec = getFileSharing();
        WriteAccessRecord waccess = getWriteAccess();
        WriteProtectRecord writeProtect2 = getWriteProtect();
        frec.setReadOnly(1);
        frec.setPassword(FileSharingRecord.hashPassword(password));
        frec.setUsername(username);
        waccess.setUsername(username);
    }

    public void unwriteProtectWorkbook() {
        this.records.remove((Object) this.fileShare);
        this.records.remove((Object) this.writeProtect);
        this.fileShare = null;
        this.writeProtect = null;
    }

    public String resolveNameXText(int refIndex, int definedNameIndex) {
        return this.linkTable.resolveNameXText(refIndex, definedNameIndex);
    }

    public NameXPtg getNameXPtg(String name) {
        return getOrCreateLinkTable().getNameXPtg(name);
    }

    /* JADX WARNING: type inference failed for: r7v2, types: [org.apache.poi.ddf.EscherRecord] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cloneDrawings(org.apache.poi.hssf.model.InternalSheet r15) {
        /*
            r14 = this;
            r14.findDrawingGroup()
            org.apache.poi.hssf.model.DrawingManager2 r0 = r14.drawingManager
            if (r0 != 0) goto L_0x0008
            return
        L_0x0008:
            r1 = 0
            int r0 = r15.aggregateDrawingRecords(r0, r1)
            r2 = -1
            if (r0 == r2) goto L_0x008c
            r2 = 9876(0x2694, float:1.3839E-41)
            org.apache.poi.hssf.record.Record r2 = r15.findFirstRecordBySid(r2)
            org.apache.poi.hssf.record.EscherAggregate r2 = (org.apache.poi.hssf.record.EscherAggregate) r2
            org.apache.poi.ddf.EscherContainerRecord r3 = r2.getEscherContainer()
            if (r3 != 0) goto L_0x001f
            return
        L_0x001f:
            org.apache.poi.hssf.model.DrawingManager2 r4 = r14.drawingManager
            org.apache.poi.ddf.EscherDggRecord r4 = r4.getDgg()
            org.apache.poi.hssf.model.DrawingManager2 r5 = r14.drawingManager
            short r5 = r5.findNewDrawingGroupId()
            r4.addCluster(r5, r1)
            int r1 = r4.getDrawingsSaved()
            int r1 = r1 + 1
            r4.setDrawingsSaved(r1)
            r1 = 0
            java.util.Iterator r6 = r3.getChildIterator()
        L_0x003c:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x008c
            java.lang.Object r7 = r6.next()
            org.apache.poi.ddf.EscherRecord r7 = (org.apache.poi.ddf.EscherRecord) r7
            boolean r8 = r7 instanceof org.apache.poi.ddf.EscherDgRecord
            if (r8 == 0) goto L_0x0056
            r1 = r7
            org.apache.poi.ddf.EscherDgRecord r1 = (org.apache.poi.ddf.EscherDgRecord) r1
            int r8 = r5 << 4
            short r8 = (short) r8
            r1.setOptions(r8)
            goto L_0x008b
        L_0x0056:
            boolean r8 = r7 instanceof org.apache.poi.ddf.EscherContainerRecord
            if (r8 == 0) goto L_0x008b
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r9 = r7
            org.apache.poi.ddf.EscherContainerRecord r9 = (org.apache.poi.ddf.EscherContainerRecord) r9
            r10 = -4086(0xfffffffffffff00a, float:NaN)
            r9.getRecordsById(r10, r8)
            java.util.Iterator r10 = r8.iterator()
        L_0x006b:
            boolean r11 = r10.hasNext()
            if (r11 == 0) goto L_0x008b
            java.lang.Object r11 = r10.next()
            org.apache.poi.ddf.EscherSpRecord r11 = (org.apache.poi.ddf.EscherSpRecord) r11
            org.apache.poi.hssf.model.DrawingManager2 r12 = r14.drawingManager
            short r13 = (short) r5
            int r12 = r12.allocateShapeId(r13, r1)
            int r13 = r1.getNumShapes()
            int r13 = r13 + -1
            r1.setNumShapes(r13)
            r11.setShapeId(r12)
            goto L_0x006b
        L_0x008b:
            goto L_0x003c
        L_0x008c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.model.InternalWorkbook.cloneDrawings(org.apache.poi.hssf.model.InternalSheet):void");
    }

    public void updateNamesAfterCellShift(FormulaShifter shifter) {
        for (int i = 0; i < getNumNames(); i++) {
            NameRecord nr = getNameRecord(i);
            Ptg[] ptgs = nr.getNameDefinition();
            if (shifter.adjustFormula(ptgs, nr.getSheetNumber())) {
                nr.setNameDefinition(ptgs);
            }
        }
    }
}
