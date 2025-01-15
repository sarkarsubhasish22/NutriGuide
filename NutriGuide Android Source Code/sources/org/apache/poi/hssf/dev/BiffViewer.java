package org.apache.poi.hssf.dev;

import androidx.core.view.InputDeviceCompat;
import androidx.fragment.app.FragmentTransaction;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.AutoFilterInfoRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BookBoolRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CFHeaderRecord;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.poi.hssf.record.CalcModeRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.ContinueRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.DBCellRecord;
import org.apache.poi.hssf.record.DSFRecord;
import org.apache.poi.hssf.record.DVALRecord;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.DefaultColWidthRecord;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.hssf.record.DeltaRecord;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.DrawingRecordForBiffViewer;
import org.apache.poi.hssf.record.DrawingSelectionRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.ExternalNameRecord;
import org.apache.poi.hssf.record.FeatHdrRecord;
import org.apache.poi.hssf.record.FeatRecord;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.record.FileSharingRecord;
import org.apache.poi.hssf.record.FnGroupCountRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.GridsetRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.HideObjRecord;
import org.apache.poi.hssf.record.HorizontalPageBreakRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.IterationRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.MMSRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.PaneRecord;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PasswordRev4Record;
import org.apache.poi.hssf.record.PrecisionRecord;
import org.apache.poi.hssf.record.PrintGridlinesRecord;
import org.apache.poi.hssf.record.PrintHeadersRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.ProtectionRev4Record;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.RefModeRecord;
import org.apache.poi.hssf.record.RefreshAllRecord;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.apache.poi.hssf.record.SelectionRecord;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.record.TabIdRecord;
import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.TableStylesRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.UseSelFSRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.VerticalPageBreakRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.WindowOneRecord;
import org.apache.poi.hssf.record.WindowProtectRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.WriteAccessRecord;
import org.apache.poi.hssf.record.WriteProtectRecord;
import org.apache.poi.hssf.record.chart.AreaFormatRecord;
import org.apache.poi.hssf.record.chart.AreaRecord;
import org.apache.poi.hssf.record.chart.AxisLineFormatRecord;
import org.apache.poi.hssf.record.chart.AxisOptionsRecord;
import org.apache.poi.hssf.record.chart.AxisParentRecord;
import org.apache.poi.hssf.record.chart.AxisRecord;
import org.apache.poi.hssf.record.chart.AxisUsedRecord;
import org.apache.poi.hssf.record.chart.BarRecord;
import org.apache.poi.hssf.record.chart.BeginRecord;
import org.apache.poi.hssf.record.chart.CatLabRecord;
import org.apache.poi.hssf.record.chart.CategorySeriesAxisRecord;
import org.apache.poi.hssf.record.chart.ChartEndBlockRecord;
import org.apache.poi.hssf.record.chart.ChartEndObjectRecord;
import org.apache.poi.hssf.record.chart.ChartFRTInfoRecord;
import org.apache.poi.hssf.record.chart.ChartFormatRecord;
import org.apache.poi.hssf.record.chart.ChartRecord;
import org.apache.poi.hssf.record.chart.ChartStartBlockRecord;
import org.apache.poi.hssf.record.chart.ChartStartObjectRecord;
import org.apache.poi.hssf.record.chart.DatRecord;
import org.apache.poi.hssf.record.chart.DataFormatRecord;
import org.apache.poi.hssf.record.chart.DefaultDataLabelTextPropertiesRecord;
import org.apache.poi.hssf.record.chart.EndRecord;
import org.apache.poi.hssf.record.chart.FontBasisRecord;
import org.apache.poi.hssf.record.chart.FontIndexRecord;
import org.apache.poi.hssf.record.chart.FrameRecord;
import org.apache.poi.hssf.record.chart.LegendRecord;
import org.apache.poi.hssf.record.chart.LineFormatRecord;
import org.apache.poi.hssf.record.chart.LinkedDataRecord;
import org.apache.poi.hssf.record.chart.ObjectLinkRecord;
import org.apache.poi.hssf.record.chart.PlotAreaRecord;
import org.apache.poi.hssf.record.chart.PlotGrowthRecord;
import org.apache.poi.hssf.record.chart.SeriesIndexRecord;
import org.apache.poi.hssf.record.chart.SeriesListRecord;
import org.apache.poi.hssf.record.chart.SeriesRecord;
import org.apache.poi.hssf.record.chart.SeriesTextRecord;
import org.apache.poi.hssf.record.chart.SeriesToChartGroupRecord;
import org.apache.poi.hssf.record.chart.SheetPropertiesRecord;
import org.apache.poi.hssf.record.chart.TextRecord;
import org.apache.poi.hssf.record.chart.TickRecord;
import org.apache.poi.hssf.record.chart.UnitsRecord;
import org.apache.poi.hssf.record.chart.ValueRangeRecord;
import org.apache.poi.hssf.record.pivottable.DataItemRecord;
import org.apache.poi.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.PageItemRecord;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;
import org.apache.poi.hssf.record.pivottable.ViewDefinitionRecord;
import org.apache.poi.hssf.record.pivottable.ViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.ViewSourceRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

public final class BiffViewer {
    private static final char[] COLUMN_SEPARATOR = " | ".toCharArray();
    private static final int DUMP_LINE_LEN = 16;
    static final char[] NEW_LINE_CHARS = System.getProperty("line.separator").toCharArray();

    private interface IBiffRecordListener {
        void processRecord(int i, int i2, int i3, int i4, byte[] bArr);
    }

    private BiffViewer() {
    }

    public static Record[] createRecords(InputStream is, PrintStream ps, BiffRecordListener recListener, boolean dumpInterpretedRecords) throws RecordFormatException {
        boolean hasNext;
        List<Record> temp = new ArrayList<>();
        RecordInputStream recStream = new RecordInputStream(is);
        while (true) {
            try {
                hasNext = recStream.hasNextRecord();
            } catch (RecordInputStream.LeftoverDataException e) {
                e.printStackTrace();
                System.err.println("Discarding " + recStream.remaining() + " bytes and continuing");
                recStream.readRemainder();
                hasNext = recStream.hasNextRecord();
            }
            if (!hasNext) {
                Record[] result = new Record[temp.size()];
                temp.toArray(result);
                return result;
            }
            recStream.nextRecord();
            if (recStream.getSid() != 0) {
                if (dumpInterpretedRecords) {
                    Record record = createRecord(recStream);
                    if (record.getSid() != 60) {
                        temp.add(record);
                        if (dumpInterpretedRecords) {
                            String[] headers = recListener.getRecentHeaders();
                            for (String println : headers) {
                                ps.println(println);
                            }
                            ps.print(record.toString());
                        }
                    }
                } else {
                    recStream.readRemainder();
                }
                ps.println();
            }
        }
    }

    private static Record createRecord(RecordInputStream in) {
        short sid = in.getSid();
        if (sid == 34) {
            return new DateWindow1904Record(in);
        }
        if (sid == 35) {
            return new ExternalNameRecord(in);
        }
        if (sid == 60) {
            return new ContinueRecord(in);
        }
        if (sid == 61) {
            return new WindowOneRecord(in);
        }
        if (sid == 140) {
            return new CountryRecord(in);
        }
        if (sid == 141) {
            return new HideObjRecord(in);
        }
        if (sid == 156) {
            return new FnGroupCountRecord(in);
        }
        if (sid == 157) {
            return new AutoFilterInfoRecord(in);
        }
        switch (sid) {
            case 6:
                return new FormulaRecord(in);
            case 10:
                return new EOFRecord(in);
            case 12:
                return new CalcCountRecord(in);
            case 13:
                return new CalcModeRecord(in);
            case 14:
                return new PrecisionRecord(in);
            case 15:
                return new RefModeRecord(in);
            case 16:
                return new DeltaRecord(in);
            case 17:
                return new IterationRecord(in);
            case 18:
                return new ProtectRecord(in);
            case 19:
                return new PasswordRecord(in);
            case 20:
                return new HeaderRecord(in);
            case 21:
                return new FooterRecord(in);
            case 47:
                return new FilePassRecord(in);
            case 49:
                return new FontRecord(in);
            case 85:
                return new DefaultColWidthRecord(in);
            case 95:
                return new SaveRecalcRecord(in);
            case ShapeTypes.ELLIPSE_RIBBON_2 /*125*/:
                return new ColumnInfoRecord(in);
            case ShapeTypes.FLOW_CHART_SUMMING_JUNCTION /*146*/:
                return new PaletteRecord(in);
            case 160:
                return new SCLRecord(in);
            case ShapeTypes.ACTION_BUTTON_BLANK /*161*/:
                return new PrintSetupRecord(in);
            case ShapeTypes.MATH_PLUS /*176*/:
                return new ViewDefinitionRecord(in);
            case ShapeTypes.MATH_MINUS /*177*/:
                return new ViewFieldsRecord(in);
            case ShapeTypes.CORNER_TABS /*182*/:
                return new PageItemRecord(in);
            case 189:
                return new MulRKRecord(in);
            case 190:
                return new MulBlankRecord(in);
            case 193:
                return new MMSRecord(in);
            case 197:
                return new DataItemRecord(in);
            case 213:
                return new StreamIDRecord(in);
            case 215:
                return new DBCellRecord(in);
            case 218:
                return new BookBoolRecord(in);
            case 224:
                return new ExtendedFormatRecord(in);
            case 225:
                return new InterfaceHdrRecord(in);
            case 226:
                return InterfaceEndRecord.create(in);
            case 227:
                return new ViewSourceRecord(in);
            case 229:
                return new MergeCellsRecord(in);
            case 235:
                return new DrawingGroupRecord(in);
            case 236:
                return new DrawingRecordForBiffViewer(in);
            case 237:
                return new DrawingSelectionRecord(in);
            case 252:
                return new SSTRecord(in);
            case 253:
                return new LabelSSTRecord(in);
            case 255:
                return new ExtSSTRecord(in);
            case 256:
                return new ExtendedPivotTableViewFieldsRecord(in);
            case 317:
                return new TabIdRecord(in);
            case 352:
                return new UseSelFSRecord(in);
            case 353:
                return new DSFRecord(in);
            case 430:
                return new SupBookRecord(in);
            case 431:
                return new ProtectionRev4Record(in);
            case 432:
                return new CFHeaderRecord(in);
            case 433:
                return new CFRuleRecord(in);
            case 434:
                return new DVALRecord(in);
            case 438:
                return new TextObjectRecord(in);
            case 439:
                return new RefreshAllRecord(in);
            case 440:
                return new HyperlinkRecord(in);
            case 444:
                return new PasswordRev4Record(in);
            case 446:
                return new DVRecord(in);
            case 449:
                return new RecalcIdRecord(in);
            case 512:
                return new DimensionsRecord(in);
            case InputDeviceCompat.SOURCE_DPAD:
                return new BlankRecord(in);
            case 515:
                return new NumberRecord(in);
            case 516:
                return new LabelRecord(in);
            case 517:
                return new BoolErrRecord(in);
            case 519:
                return new StringRecord(in);
            case 520:
                return new RowRecord(in);
            case 523:
                return new IndexRecord(in);
            case 545:
                return new ArrayRecord(in);
            case 549:
                return new DefaultRowHeightRecord(in);
            case 566:
                return new TableRecord(in);
            case 574:
                return new WindowTwoRecord(in);
            case 638:
                return new RKRecord(in);
            case 659:
                return new StyleRecord(in);
            case 1054:
                return new FormatRecord(in);
            case 1212:
                return new SharedFormulaRecord(in);
            case 2057:
                return new BOFRecord(in);
            case 2128:
                return new ChartFRTInfoRecord(in);
            case 2130:
                return new ChartStartBlockRecord(in);
            case 2131:
                return new ChartEndBlockRecord(in);
            case 2132:
                return new ChartStartObjectRecord(in);
            case 2133:
                return new ChartEndObjectRecord(in);
            case 2134:
                return new CatLabRecord(in);
            case UnknownRecord.SHEETPROTECTION_0867 /*2151*/:
                return new FeatHdrRecord(in);
            case 2152:
                return new FeatRecord(in);
            case 2190:
                return new TableStylesRecord(in);
            case 2196:
                return new NameCommentRecord(in);
            case FragmentTransaction.TRANSIT_FRAGMENT_OPEN:
                return new UnitsRecord(in);
            case InputDeviceCompat.SOURCE_TOUCHSCREEN:
                return new ChartRecord(in);
            case FragmentTransaction.TRANSIT_FRAGMENT_FADE:
                return new SeriesRecord(in);
            case 4102:
                return new DataFormatRecord(in);
            case 4103:
                return new LineFormatRecord(in);
            case 4106:
                return new AreaFormatRecord(in);
            case 4109:
                return new SeriesTextRecord(in);
            case 4116:
                return new ChartFormatRecord(in);
            case 4117:
                return new LegendRecord(in);
            case 4118:
                return new SeriesListRecord(in);
            case 4119:
                return new BarRecord(in);
            case 4122:
                return new AreaRecord(in);
            case 4125:
                return new AxisRecord(in);
            case 4126:
                return new TickRecord(in);
            case 4127:
                return new ValueRangeRecord(in);
            case 4128:
                return new CategorySeriesAxisRecord(in);
            case 4129:
                return new AxisLineFormatRecord(in);
            case 4132:
                return new DefaultDataLabelTextPropertiesRecord(in);
            case 4133:
                return new TextRecord(in);
            case 4134:
                return new FontIndexRecord(in);
            case 4135:
                return new ObjectLinkRecord(in);
            case 4146:
                return new FrameRecord(in);
            case 4147:
                return new BeginRecord(in);
            case 4148:
                return new EndRecord(in);
            case 4149:
                return new PlotAreaRecord(in);
            case 4161:
                return new AxisParentRecord(in);
            case 4164:
                return new SheetPropertiesRecord(in);
            case 4165:
                return new SeriesToChartGroupRecord(in);
            case 4166:
                return new AxisUsedRecord(in);
            case 4177:
                return new LinkedDataRecord(in);
            case 4192:
                return new FontBasisRecord(in);
            case 4194:
                return new AxisOptionsRecord(in);
            case 4195:
                return new DatRecord(in);
            case 4196:
                return new PlotGrowthRecord(in);
            case 4197:
                return new SeriesIndexRecord(in);
            default:
                switch (sid) {
                    case 23:
                        return new ExternSheetRecord(in);
                    case 24:
                        return new NameRecord(in);
                    case 25:
                        return new WindowProtectRecord(in);
                    case 26:
                        return new VerticalPageBreakRecord(in);
                    case 27:
                        return new HorizontalPageBreakRecord(in);
                    case 28:
                        return new NoteRecord(in);
                    case 29:
                        return new SelectionRecord(in);
                    default:
                        switch (sid) {
                            case 38:
                                return new LeftMarginRecord(in);
                            case 39:
                                return new RightMarginRecord(in);
                            case 40:
                                return new TopMarginRecord(in);
                            case 41:
                                return new BottomMarginRecord(in);
                            case 42:
                                return new PrintHeadersRecord(in);
                            case 43:
                                return new PrintGridlinesRecord(in);
                            default:
                                switch (sid) {
                                    case 64:
                                        return new BackupRecord(in);
                                    case 65:
                                        return new PaneRecord(in);
                                    case 66:
                                        return new CodepageRecord(in);
                                    default:
                                        switch (sid) {
                                            case 91:
                                                return new FileSharingRecord(in);
                                            case 92:
                                                return new WriteAccessRecord(in);
                                            case 93:
                                                return new ObjRecord(in);
                                            default:
                                                switch (sid) {
                                                    case 128:
                                                        return new GutsRecord(in);
                                                    case 129:
                                                        return new WSBoolRecord(in);
                                                    case ShapeTypes.DOUBLE_WAVE /*130*/:
                                                        return new GridsetRecord(in);
                                                    case ShapeTypes.PLUS /*131*/:
                                                        return new HCenterRecord(in);
                                                    case ShapeTypes.FLOW_CHART_PROCESS /*132*/:
                                                        return new VCenterRecord(in);
                                                    case ShapeTypes.FLOW_CHART_DECISION /*133*/:
                                                        return new BoundSheetRecord(in);
                                                    case ShapeTypes.FLOW_CHART_INPUT_OUTPUT /*134*/:
                                                        return new WriteProtectRecord(in);
                                                    default:
                                                        return new UnknownRecord(in);
                                                }
                                        }
                                }
                        }
                }
        }
    }

    private static final class CommandArgs {
        private final boolean _biffhex;
        private final File _file;
        private final boolean _noHeader;
        private final boolean _noint;
        private final boolean _out;
        private final boolean _rawhex;

        private CommandArgs(boolean biffhex, boolean noint, boolean out, boolean rawhex, boolean noHeader, File file) {
            this._biffhex = biffhex;
            this._noint = noint;
            this._out = out;
            this._rawhex = rawhex;
            this._file = file;
            this._noHeader = noHeader;
        }

        public static CommandArgs parse(String[] args) throws CommandParseException {
            int nArgs = args.length;
            boolean biffhex = false;
            boolean noint = false;
            boolean out = false;
            boolean rawhex = false;
            boolean noheader = false;
            File file = null;
            for (int i = 0; i < nArgs; i++) {
                String arg = args[i];
                if (!arg.startsWith("--")) {
                    file = new File(arg);
                    if (!file.exists()) {
                        throw new CommandParseException("Specified file '" + arg + "' does not exist");
                    } else if (i + 1 < nArgs) {
                        throw new CommandParseException("File name must be the last arg");
                    }
                } else if ("--biffhex".equals(arg)) {
                    biffhex = true;
                } else if ("--noint".equals(arg)) {
                    noint = true;
                } else if ("--out".equals(arg)) {
                    out = true;
                } else if ("--escher".equals(arg)) {
                    System.setProperty("poi.deserialize.escher", "true");
                } else if ("--rawhex".equals(arg)) {
                    rawhex = true;
                } else if ("--noheader".equals(arg)) {
                    noheader = true;
                } else {
                    throw new CommandParseException("Unexpected option '" + arg + "'");
                }
            }
            if (file != null) {
                return new CommandArgs(biffhex, noint, out, rawhex, noheader, file);
            }
            throw new CommandParseException("Biff viewer needs a filename");
        }

        public boolean shouldDumpBiffHex() {
            return this._biffhex;
        }

        public boolean shouldDumpRecordInterpretations() {
            return !this._noint;
        }

        public boolean shouldOutputToFile() {
            return this._out;
        }

        public boolean shouldOutputRawHexOnly() {
            return this._rawhex;
        }

        public boolean suppressHeader() {
            return this._noHeader;
        }

        public File getFile() {
            return this._file;
        }
    }

    private static final class CommandParseException extends Exception {
        public CommandParseException(String msg) {
            super(msg);
        }
    }

    public static void main(String[] args) {
        PrintStream ps;
        OutputStreamWriter outputStreamWriter = null;
        try {
            CommandArgs cmdArgs = CommandArgs.parse(args);
            try {
                if (cmdArgs.shouldOutputToFile()) {
                    ps = new PrintStream(new FileOutputStream(cmdArgs.getFile().getAbsolutePath() + ".out"));
                } else {
                    ps = System.out;
                }
                InputStream is = new POIFSFileSystem(new FileInputStream(cmdArgs.getFile())).createDocumentInputStream("Workbook");
                if (cmdArgs.shouldOutputRawHexOnly()) {
                    byte[] data = new byte[is.available()];
                    is.read(data);
                    HexDump.dump(data, 0, (OutputStream) System.out, 0);
                } else {
                    boolean dumpInterpretedRecords = cmdArgs.shouldDumpRecordInterpretations();
                    boolean zeroAlignHexDump = dumpInterpretedRecords;
                    if (cmdArgs.shouldDumpBiffHex()) {
                        outputStreamWriter = new OutputStreamWriter(ps);
                    }
                    BiffRecordListener recListener = new BiffRecordListener(outputStreamWriter, zeroAlignHexDump, cmdArgs.suppressHeader());
                    createRecords(new BiffDumpingStream(is, recListener), ps, recListener, dumpInterpretedRecords);
                }
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (CommandParseException e2) {
            e2.printStackTrace();
        }
    }

    private static final class BiffRecordListener implements IBiffRecordListener {
        private final List<String> _headers = new ArrayList();
        private final Writer _hexDumpWriter;
        private final boolean _noHeader;
        private final boolean _zeroAlignEachRecord;

        public BiffRecordListener(Writer hexDumpWriter, boolean zeroAlignEachRecord, boolean noHeader) {
            this._hexDumpWriter = hexDumpWriter;
            this._zeroAlignEachRecord = zeroAlignEachRecord;
            this._noHeader = noHeader;
        }

        public void processRecord(int globalOffset, int recordCounter, int sid, int dataSize, byte[] data) {
            String header = formatRecordDetails(globalOffset, sid, dataSize, recordCounter);
            if (!this._noHeader) {
                this._headers.add(header);
            }
            Writer w = this._hexDumpWriter;
            if (w != null) {
                try {
                    w.write(header);
                    w.write(BiffViewer.NEW_LINE_CHARS);
                    BiffViewer.hexDumpAligned(w, data, dataSize + 4, globalOffset, this._zeroAlignEachRecord);
                    w.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public String[] getRecentHeaders() {
            String[] result = new String[this._headers.size()];
            this._headers.toArray(result);
            this._headers.clear();
            return result;
        }

        private static String formatRecordDetails(int globalOffset, int sid, int size, int recordCounter) {
            StringBuffer sb = new StringBuffer(64);
            sb.append("Offset=");
            sb.append(HexDump.intToHex(globalOffset));
            sb.append("(");
            sb.append(globalOffset);
            sb.append(")");
            sb.append(" recno=");
            sb.append(recordCounter);
            sb.append(" sid=");
            sb.append(HexDump.shortToHex(sid));
            sb.append(" size=");
            sb.append(HexDump.shortToHex(size));
            sb.append("(");
            sb.append(size);
            sb.append(")");
            return sb.toString();
        }
    }

    private static final class BiffDumpingStream extends InputStream {
        private int _currentPos = 0;
        private int _currentSize = 0;
        private final byte[] _data = new byte[8228];
        private boolean _innerHasReachedEOF;
        private final DataInputStream _is;
        private final IBiffRecordListener _listener;
        private int _overallStreamPos = 0;
        private int _recordCounter = 0;

        public BiffDumpingStream(InputStream is, IBiffRecordListener listener) {
            this._is = new DataInputStream(is);
            this._listener = listener;
        }

        public int read() throws IOException {
            if (this._currentPos >= this._currentSize) {
                fillNextBuffer();
            }
            int i = this._currentPos;
            if (i >= this._currentSize) {
                return -1;
            }
            int result = this._data[i] & 255;
            this._currentPos = i + 1;
            this._overallStreamPos++;
            formatBufferIfAtEndOfRec();
            return result;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int result;
            if (this._currentPos >= this._currentSize) {
                fillNextBuffer();
            }
            int i = this._currentPos;
            int i2 = this._currentSize;
            if (i >= i2) {
                return -1;
            }
            int availSize = i2 - i;
            if (len > availSize) {
                System.err.println("Unexpected request to read past end of current biff record");
                result = availSize;
            } else {
                result = len;
            }
            System.arraycopy(this._data, this._currentPos, b, off, result);
            this._currentPos += result;
            this._overallStreamPos += result;
            formatBufferIfAtEndOfRec();
            return result;
        }

        public int available() throws IOException {
            return (this._currentSize - this._currentPos) + this._is.available();
        }

        private void fillNextBuffer() throws IOException {
            if (!this._innerHasReachedEOF) {
                int b0 = this._is.read();
                if (b0 == -1) {
                    this._innerHasReachedEOF = true;
                    return;
                }
                byte[] bArr = this._data;
                bArr[0] = (byte) b0;
                this._is.readFully(bArr, 1, 3);
                int len = LittleEndian.getShort(this._data, 2);
                this._is.readFully(this._data, 4, len);
                this._currentPos = 0;
                this._currentSize = len + 4;
                this._recordCounter++;
            }
        }

        private void formatBufferIfAtEndOfRec() {
            int i = this._currentPos;
            int i2 = this._currentSize;
            if (i == i2) {
                short s = LittleEndian.getShort(this._data, 0);
                this._listener.processRecord(this._overallStreamPos - this._currentSize, this._recordCounter, s, i2 - 4, this._data);
            }
        }

        public void close() throws IOException {
            this._is.close();
        }
    }

    static void hexDumpAligned(Writer w, byte[] data, int dumpLen, int globalOffset, boolean zeroAlignEachRecord) {
        int endDelta;
        int endLineAddr;
        int endLineAddr2;
        int lineAddr;
        int globalStart = globalOffset + 0;
        int globalEnd = globalOffset + 0 + dumpLen;
        int startDelta = globalStart % 16;
        int endDelta2 = globalEnd % 16;
        if (zeroAlignEachRecord) {
            int endDelta3 = endDelta2 - startDelta;
            if (endDelta3 < 0) {
                endDelta3 += 16;
            }
            startDelta = 0;
            endDelta = endDelta3;
        } else {
            endDelta = endDelta2;
        }
        if (zeroAlignEachRecord) {
            endLineAddr = (globalEnd - endDelta) - (globalStart - startDelta);
            endLineAddr2 = 0;
        } else {
            endLineAddr = globalEnd - endDelta;
            endLineAddr2 = globalStart - startDelta;
        }
        int lineDataOffset = 0 - startDelta;
        int lineAddr2 = endLineAddr2;
        if (endLineAddr2 == endLineAddr) {
            hexDumpLine(w, data, lineAddr2, lineDataOffset, startDelta, endDelta);
            return;
        }
        hexDumpLine(w, data, lineAddr2, lineDataOffset, startDelta, 16);
        while (true) {
            lineAddr = lineAddr2 + 16;
            lineDataOffset += 16;
            if (lineAddr >= endLineAddr) {
                break;
            }
            hexDumpLine(w, data, lineAddr, lineDataOffset, 0, 16);
            lineAddr2 = lineAddr;
            endLineAddr2 = endLineAddr2;
        }
        if (endDelta != 0) {
            int i = endLineAddr2;
            hexDumpLine(w, data, lineAddr, lineDataOffset, 0, endDelta);
            return;
        }
        int startLineAddr = endLineAddr2;
    }

    private static void hexDumpLine(Writer w, byte[] data, int lineStartAddress, int lineDataOffset, int startDelta, int endDelta) {
        if (startDelta < endDelta) {
            try {
                writeHex(w, lineStartAddress, 8);
                w.write(COLUMN_SEPARATOR);
                for (int i = 0; i < 16; i++) {
                    if (i > 0) {
                        w.write(" ");
                    }
                    if (i < startDelta || i >= endDelta) {
                        w.write("  ");
                    } else {
                        writeHex(w, data[lineDataOffset + i], 2);
                    }
                }
                w.write(COLUMN_SEPARATOR);
                for (int i2 = 0; i2 < 16; i2++) {
                    if (i2 < startDelta || i2 >= endDelta) {
                        w.write(" ");
                    } else {
                        w.write(getPrintableChar(data[lineDataOffset + i2]));
                    }
                }
                w.write(NEW_LINE_CHARS);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Bad start/end delta");
        }
    }

    private static char getPrintableChar(byte b) {
        char ib = (char) (b & 255);
        if (ib < ' ' || ib > '~') {
            return '.';
        }
        return ib;
    }

    private static void writeHex(Writer w, int value, int nDigits) throws IOException {
        char[] buf = new char[nDigits];
        int acc = value;
        for (int i = nDigits - 1; i >= 0; i--) {
            int digit = acc & 15;
            buf[i] = (char) (digit < 10 ? digit + 48 : (digit + 65) - 10);
            acc >>= 4;
        }
        w.write(buf);
    }
}
