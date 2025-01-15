package org.apache.poi.hssf.record;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RecordFactory {
    private static final Class<?>[] CONSTRUCTOR_ARGS = {RecordInputStream.class};
    private static final int NUM_RECORDS = 512;
    private static short[] _allKnownRecordSIDs;
    private static final Map<Integer, I_RecordCreator> _recordCreatorsById;
    private static final Class<? extends Record>[] recordClasses;

    private interface I_RecordCreator {
        Record create(RecordInputStream recordInputStream);

        Class<? extends Record> getRecordClass();
    }

    private static final class ReflectionConstructorRecordCreator implements I_RecordCreator {
        private final Constructor<? extends Record> _c;

        public ReflectionConstructorRecordCreator(Constructor<? extends Record> c) {
            this._c = c;
        }

        public Record create(RecordInputStream in) {
            try {
                return (Record) this._c.newInstance(new Object[]{in});
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e2) {
                throw new RuntimeException(e2);
            } catch (IllegalAccessException e3) {
                throw new RuntimeException(e3);
            } catch (InvocationTargetException e4) {
                throw new RecordFormatException("Unable to construct record instance", e4.getTargetException());
            }
        }

        public Class<? extends Record> getRecordClass() {
            return this._c.getDeclaringClass();
        }
    }

    private static final class ReflectionMethodRecordCreator implements I_RecordCreator {
        private final Method _m;

        public ReflectionMethodRecordCreator(Method m) {
            this._m = m;
        }

        public Record create(RecordInputStream in) {
            try {
                return (Record) this._m.invoke((Object) null, new Object[]{in});
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                throw new RecordFormatException("Unable to construct record instance", e3.getTargetException());
            }
        }

        public Class<? extends Record> getRecordClass() {
            return this._m.getDeclaringClass();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: java.lang.Class<?>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.lang.Class<? extends org.apache.poi.hssf.record.Record>[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            r0 = 1
            java.lang.Class[] r1 = new java.lang.Class[r0]
            java.lang.Class<org.apache.poi.hssf.record.RecordInputStream> r2 = org.apache.poi.hssf.record.RecordInputStream.class
            r3 = 0
            r1[r3] = r2
            CONSTRUCTOR_ARGS = r1
            r1 = 135(0x87, float:1.89E-43)
            java.lang.Class[] r1 = new java.lang.Class[r1]
            java.lang.Class<org.apache.poi.hssf.record.ArrayRecord> r2 = org.apache.poi.hssf.record.ArrayRecord.class
            r1[r3] = r2
            java.lang.Class<org.apache.poi.hssf.record.AutoFilterInfoRecord> r2 = org.apache.poi.hssf.record.AutoFilterInfoRecord.class
            r1[r0] = r2
            r0 = 2
            java.lang.Class<org.apache.poi.hssf.record.BackupRecord> r2 = org.apache.poi.hssf.record.BackupRecord.class
            r1[r0] = r2
            r0 = 3
            java.lang.Class<org.apache.poi.hssf.record.BlankRecord> r2 = org.apache.poi.hssf.record.BlankRecord.class
            r1[r0] = r2
            r0 = 4
            java.lang.Class<org.apache.poi.hssf.record.BOFRecord> r2 = org.apache.poi.hssf.record.BOFRecord.class
            r1[r0] = r2
            r0 = 5
            java.lang.Class<org.apache.poi.hssf.record.BookBoolRecord> r2 = org.apache.poi.hssf.record.BookBoolRecord.class
            r1[r0] = r2
            r0 = 6
            java.lang.Class<org.apache.poi.hssf.record.BoolErrRecord> r2 = org.apache.poi.hssf.record.BoolErrRecord.class
            r1[r0] = r2
            r0 = 7
            java.lang.Class<org.apache.poi.hssf.record.BottomMarginRecord> r2 = org.apache.poi.hssf.record.BottomMarginRecord.class
            r1[r0] = r2
            r0 = 8
            java.lang.Class<org.apache.poi.hssf.record.BoundSheetRecord> r2 = org.apache.poi.hssf.record.BoundSheetRecord.class
            r1[r0] = r2
            r0 = 9
            java.lang.Class<org.apache.poi.hssf.record.CalcCountRecord> r2 = org.apache.poi.hssf.record.CalcCountRecord.class
            r1[r0] = r2
            r0 = 10
            java.lang.Class<org.apache.poi.hssf.record.CalcModeRecord> r2 = org.apache.poi.hssf.record.CalcModeRecord.class
            r1[r0] = r2
            r0 = 11
            java.lang.Class<org.apache.poi.hssf.record.CFHeaderRecord> r2 = org.apache.poi.hssf.record.CFHeaderRecord.class
            r1[r0] = r2
            r0 = 12
            java.lang.Class<org.apache.poi.hssf.record.CFRuleRecord> r2 = org.apache.poi.hssf.record.CFRuleRecord.class
            r1[r0] = r2
            r0 = 13
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartRecord> r2 = org.apache.poi.hssf.record.chart.ChartRecord.class
            r1[r0] = r2
            r0 = 14
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartTitleFormatRecord> r2 = org.apache.poi.hssf.record.chart.ChartTitleFormatRecord.class
            r1[r0] = r2
            r0 = 15
            java.lang.Class<org.apache.poi.hssf.record.CodepageRecord> r2 = org.apache.poi.hssf.record.CodepageRecord.class
            r1[r0] = r2
            r0 = 16
            java.lang.Class<org.apache.poi.hssf.record.ColumnInfoRecord> r2 = org.apache.poi.hssf.record.ColumnInfoRecord.class
            r1[r0] = r2
            r0 = 17
            java.lang.Class<org.apache.poi.hssf.record.ContinueRecord> r2 = org.apache.poi.hssf.record.ContinueRecord.class
            r1[r0] = r2
            r0 = 18
            java.lang.Class<org.apache.poi.hssf.record.CountryRecord> r2 = org.apache.poi.hssf.record.CountryRecord.class
            r1[r0] = r2
            r0 = 19
            java.lang.Class<org.apache.poi.hssf.record.CRNCountRecord> r2 = org.apache.poi.hssf.record.CRNCountRecord.class
            r1[r0] = r2
            r0 = 20
            java.lang.Class<org.apache.poi.hssf.record.CRNRecord> r2 = org.apache.poi.hssf.record.CRNRecord.class
            r1[r0] = r2
            r0 = 21
            java.lang.Class<org.apache.poi.hssf.record.DateWindow1904Record> r2 = org.apache.poi.hssf.record.DateWindow1904Record.class
            r1[r0] = r2
            r0 = 22
            java.lang.Class<org.apache.poi.hssf.record.DBCellRecord> r2 = org.apache.poi.hssf.record.DBCellRecord.class
            r1[r0] = r2
            r0 = 23
            java.lang.Class<org.apache.poi.hssf.record.DefaultColWidthRecord> r2 = org.apache.poi.hssf.record.DefaultColWidthRecord.class
            r1[r0] = r2
            r0 = 24
            java.lang.Class<org.apache.poi.hssf.record.DefaultRowHeightRecord> r2 = org.apache.poi.hssf.record.DefaultRowHeightRecord.class
            r1[r0] = r2
            r0 = 25
            java.lang.Class<org.apache.poi.hssf.record.DeltaRecord> r2 = org.apache.poi.hssf.record.DeltaRecord.class
            r1[r0] = r2
            r0 = 26
            java.lang.Class<org.apache.poi.hssf.record.DimensionsRecord> r2 = org.apache.poi.hssf.record.DimensionsRecord.class
            r1[r0] = r2
            r0 = 27
            java.lang.Class<org.apache.poi.hssf.record.DrawingGroupRecord> r2 = org.apache.poi.hssf.record.DrawingGroupRecord.class
            r1[r0] = r2
            r0 = 28
            java.lang.Class<org.apache.poi.hssf.record.DrawingRecord> r2 = org.apache.poi.hssf.record.DrawingRecord.class
            r1[r0] = r2
            r0 = 29
            java.lang.Class<org.apache.poi.hssf.record.DrawingSelectionRecord> r2 = org.apache.poi.hssf.record.DrawingSelectionRecord.class
            r1[r0] = r2
            r0 = 30
            java.lang.Class<org.apache.poi.hssf.record.DSFRecord> r2 = org.apache.poi.hssf.record.DSFRecord.class
            r1[r0] = r2
            r0 = 31
            java.lang.Class<org.apache.poi.hssf.record.DVALRecord> r2 = org.apache.poi.hssf.record.DVALRecord.class
            r1[r0] = r2
            r0 = 32
            java.lang.Class<org.apache.poi.hssf.record.DVRecord> r2 = org.apache.poi.hssf.record.DVRecord.class
            r1[r0] = r2
            r0 = 33
            java.lang.Class<org.apache.poi.hssf.record.EOFRecord> r2 = org.apache.poi.hssf.record.EOFRecord.class
            r1[r0] = r2
            r0 = 34
            java.lang.Class<org.apache.poi.hssf.record.ExtendedFormatRecord> r2 = org.apache.poi.hssf.record.ExtendedFormatRecord.class
            r1[r0] = r2
            r0 = 35
            java.lang.Class<org.apache.poi.hssf.record.ExternalNameRecord> r2 = org.apache.poi.hssf.record.ExternalNameRecord.class
            r1[r0] = r2
            r0 = 36
            java.lang.Class<org.apache.poi.hssf.record.ExternSheetRecord> r2 = org.apache.poi.hssf.record.ExternSheetRecord.class
            r1[r0] = r2
            r0 = 37
            java.lang.Class<org.apache.poi.hssf.record.ExtSSTRecord> r2 = org.apache.poi.hssf.record.ExtSSTRecord.class
            r1[r0] = r2
            r0 = 38
            java.lang.Class<org.apache.poi.hssf.record.FeatRecord> r2 = org.apache.poi.hssf.record.FeatRecord.class
            r1[r0] = r2
            r0 = 39
            java.lang.Class<org.apache.poi.hssf.record.FeatHdrRecord> r2 = org.apache.poi.hssf.record.FeatHdrRecord.class
            r1[r0] = r2
            r0 = 40
            java.lang.Class<org.apache.poi.hssf.record.FilePassRecord> r2 = org.apache.poi.hssf.record.FilePassRecord.class
            r1[r0] = r2
            r0 = 41
            java.lang.Class<org.apache.poi.hssf.record.FileSharingRecord> r2 = org.apache.poi.hssf.record.FileSharingRecord.class
            r1[r0] = r2
            r0 = 42
            java.lang.Class<org.apache.poi.hssf.record.FnGroupCountRecord> r2 = org.apache.poi.hssf.record.FnGroupCountRecord.class
            r1[r0] = r2
            r0 = 43
            java.lang.Class<org.apache.poi.hssf.record.FontRecord> r2 = org.apache.poi.hssf.record.FontRecord.class
            r1[r0] = r2
            r0 = 44
            java.lang.Class<org.apache.poi.hssf.record.FooterRecord> r2 = org.apache.poi.hssf.record.FooterRecord.class
            r1[r0] = r2
            r0 = 45
            java.lang.Class<org.apache.poi.hssf.record.FormatRecord> r2 = org.apache.poi.hssf.record.FormatRecord.class
            r1[r0] = r2
            r0 = 46
            java.lang.Class<org.apache.poi.hssf.record.FormulaRecord> r2 = org.apache.poi.hssf.record.FormulaRecord.class
            r1[r0] = r2
            r0 = 47
            java.lang.Class<org.apache.poi.hssf.record.GridsetRecord> r2 = org.apache.poi.hssf.record.GridsetRecord.class
            r1[r0] = r2
            r0 = 48
            java.lang.Class<org.apache.poi.hssf.record.GutsRecord> r2 = org.apache.poi.hssf.record.GutsRecord.class
            r1[r0] = r2
            r0 = 49
            java.lang.Class<org.apache.poi.hssf.record.HCenterRecord> r2 = org.apache.poi.hssf.record.HCenterRecord.class
            r1[r0] = r2
            r0 = 50
            java.lang.Class<org.apache.poi.hssf.record.HeaderRecord> r2 = org.apache.poi.hssf.record.HeaderRecord.class
            r1[r0] = r2
            r0 = 51
            java.lang.Class<org.apache.poi.hssf.record.HeaderFooterRecord> r2 = org.apache.poi.hssf.record.HeaderFooterRecord.class
            r1[r0] = r2
            r0 = 52
            java.lang.Class<org.apache.poi.hssf.record.HideObjRecord> r2 = org.apache.poi.hssf.record.HideObjRecord.class
            r1[r0] = r2
            r0 = 53
            java.lang.Class<org.apache.poi.hssf.record.HorizontalPageBreakRecord> r2 = org.apache.poi.hssf.record.HorizontalPageBreakRecord.class
            r1[r0] = r2
            r0 = 54
            java.lang.Class<org.apache.poi.hssf.record.HyperlinkRecord> r2 = org.apache.poi.hssf.record.HyperlinkRecord.class
            r1[r0] = r2
            r0 = 55
            java.lang.Class<org.apache.poi.hssf.record.IndexRecord> r2 = org.apache.poi.hssf.record.IndexRecord.class
            r1[r0] = r2
            r0 = 56
            java.lang.Class<org.apache.poi.hssf.record.InterfaceEndRecord> r2 = org.apache.poi.hssf.record.InterfaceEndRecord.class
            r1[r0] = r2
            r0 = 57
            java.lang.Class<org.apache.poi.hssf.record.InterfaceHdrRecord> r2 = org.apache.poi.hssf.record.InterfaceHdrRecord.class
            r1[r0] = r2
            r0 = 58
            java.lang.Class<org.apache.poi.hssf.record.IterationRecord> r2 = org.apache.poi.hssf.record.IterationRecord.class
            r1[r0] = r2
            r0 = 59
            java.lang.Class<org.apache.poi.hssf.record.LabelRecord> r2 = org.apache.poi.hssf.record.LabelRecord.class
            r1[r0] = r2
            r0 = 60
            java.lang.Class<org.apache.poi.hssf.record.LabelSSTRecord> r2 = org.apache.poi.hssf.record.LabelSSTRecord.class
            r1[r0] = r2
            r0 = 61
            java.lang.Class<org.apache.poi.hssf.record.LeftMarginRecord> r2 = org.apache.poi.hssf.record.LeftMarginRecord.class
            r1[r0] = r2
            r0 = 62
            java.lang.Class<org.apache.poi.hssf.record.chart.LegendRecord> r2 = org.apache.poi.hssf.record.chart.LegendRecord.class
            r1[r0] = r2
            r0 = 63
            java.lang.Class<org.apache.poi.hssf.record.MergeCellsRecord> r2 = org.apache.poi.hssf.record.MergeCellsRecord.class
            r1[r0] = r2
            r0 = 64
            java.lang.Class<org.apache.poi.hssf.record.MMSRecord> r2 = org.apache.poi.hssf.record.MMSRecord.class
            r1[r0] = r2
            r0 = 65
            java.lang.Class<org.apache.poi.hssf.record.MulBlankRecord> r2 = org.apache.poi.hssf.record.MulBlankRecord.class
            r1[r0] = r2
            r0 = 66
            java.lang.Class<org.apache.poi.hssf.record.MulRKRecord> r2 = org.apache.poi.hssf.record.MulRKRecord.class
            r1[r0] = r2
            r0 = 67
            java.lang.Class<org.apache.poi.hssf.record.NameRecord> r2 = org.apache.poi.hssf.record.NameRecord.class
            r1[r0] = r2
            r0 = 68
            java.lang.Class<org.apache.poi.hssf.record.NameCommentRecord> r2 = org.apache.poi.hssf.record.NameCommentRecord.class
            r1[r0] = r2
            r0 = 69
            java.lang.Class<org.apache.poi.hssf.record.NoteRecord> r2 = org.apache.poi.hssf.record.NoteRecord.class
            r1[r0] = r2
            r0 = 70
            java.lang.Class<org.apache.poi.hssf.record.NumberRecord> r2 = org.apache.poi.hssf.record.NumberRecord.class
            r1[r0] = r2
            r0 = 71
            java.lang.Class<org.apache.poi.hssf.record.ObjectProtectRecord> r2 = org.apache.poi.hssf.record.ObjectProtectRecord.class
            r1[r0] = r2
            r0 = 72
            java.lang.Class<org.apache.poi.hssf.record.ObjRecord> r2 = org.apache.poi.hssf.record.ObjRecord.class
            r1[r0] = r2
            r0 = 73
            java.lang.Class<org.apache.poi.hssf.record.PaletteRecord> r2 = org.apache.poi.hssf.record.PaletteRecord.class
            r1[r0] = r2
            r0 = 74
            java.lang.Class<org.apache.poi.hssf.record.PaneRecord> r2 = org.apache.poi.hssf.record.PaneRecord.class
            r1[r0] = r2
            r0 = 75
            java.lang.Class<org.apache.poi.hssf.record.PasswordRecord> r2 = org.apache.poi.hssf.record.PasswordRecord.class
            r1[r0] = r2
            r0 = 76
            java.lang.Class<org.apache.poi.hssf.record.PasswordRev4Record> r2 = org.apache.poi.hssf.record.PasswordRev4Record.class
            r1[r0] = r2
            r0 = 77
            java.lang.Class<org.apache.poi.hssf.record.PrecisionRecord> r2 = org.apache.poi.hssf.record.PrecisionRecord.class
            r1[r0] = r2
            r0 = 78
            java.lang.Class<org.apache.poi.hssf.record.PrintGridlinesRecord> r2 = org.apache.poi.hssf.record.PrintGridlinesRecord.class
            r1[r0] = r2
            r0 = 79
            java.lang.Class<org.apache.poi.hssf.record.PrintHeadersRecord> r2 = org.apache.poi.hssf.record.PrintHeadersRecord.class
            r1[r0] = r2
            r0 = 80
            java.lang.Class<org.apache.poi.hssf.record.PrintSetupRecord> r2 = org.apache.poi.hssf.record.PrintSetupRecord.class
            r1[r0] = r2
            r0 = 81
            java.lang.Class<org.apache.poi.hssf.record.ProtectionRev4Record> r2 = org.apache.poi.hssf.record.ProtectionRev4Record.class
            r1[r0] = r2
            r0 = 82
            java.lang.Class<org.apache.poi.hssf.record.ProtectRecord> r2 = org.apache.poi.hssf.record.ProtectRecord.class
            r1[r0] = r2
            r0 = 83
            java.lang.Class<org.apache.poi.hssf.record.RecalcIdRecord> r2 = org.apache.poi.hssf.record.RecalcIdRecord.class
            r1[r0] = r2
            r0 = 84
            java.lang.Class<org.apache.poi.hssf.record.RefModeRecord> r2 = org.apache.poi.hssf.record.RefModeRecord.class
            r1[r0] = r2
            r0 = 85
            java.lang.Class<org.apache.poi.hssf.record.RefreshAllRecord> r2 = org.apache.poi.hssf.record.RefreshAllRecord.class
            r1[r0] = r2
            r0 = 86
            java.lang.Class<org.apache.poi.hssf.record.RightMarginRecord> r2 = org.apache.poi.hssf.record.RightMarginRecord.class
            r1[r0] = r2
            r0 = 87
            java.lang.Class<org.apache.poi.hssf.record.RKRecord> r2 = org.apache.poi.hssf.record.RKRecord.class
            r1[r0] = r2
            r0 = 88
            java.lang.Class<org.apache.poi.hssf.record.RowRecord> r2 = org.apache.poi.hssf.record.RowRecord.class
            r1[r0] = r2
            r0 = 89
            java.lang.Class<org.apache.poi.hssf.record.SaveRecalcRecord> r2 = org.apache.poi.hssf.record.SaveRecalcRecord.class
            r1[r0] = r2
            r0 = 90
            java.lang.Class<org.apache.poi.hssf.record.ScenarioProtectRecord> r2 = org.apache.poi.hssf.record.ScenarioProtectRecord.class
            r1[r0] = r2
            r0 = 91
            java.lang.Class<org.apache.poi.hssf.record.SelectionRecord> r2 = org.apache.poi.hssf.record.SelectionRecord.class
            r1[r0] = r2
            r0 = 92
            java.lang.Class<org.apache.poi.hssf.record.chart.SeriesRecord> r2 = org.apache.poi.hssf.record.chart.SeriesRecord.class
            r1[r0] = r2
            r0 = 93
            java.lang.Class<org.apache.poi.hssf.record.chart.SeriesTextRecord> r2 = org.apache.poi.hssf.record.chart.SeriesTextRecord.class
            r1[r0] = r2
            r0 = 94
            java.lang.Class<org.apache.poi.hssf.record.SharedFormulaRecord> r2 = org.apache.poi.hssf.record.SharedFormulaRecord.class
            r1[r0] = r2
            r0 = 95
            java.lang.Class<org.apache.poi.hssf.record.SSTRecord> r2 = org.apache.poi.hssf.record.SSTRecord.class
            r1[r0] = r2
            r0 = 96
            java.lang.Class<org.apache.poi.hssf.record.StringRecord> r2 = org.apache.poi.hssf.record.StringRecord.class
            r1[r0] = r2
            r0 = 97
            java.lang.Class<org.apache.poi.hssf.record.StyleRecord> r2 = org.apache.poi.hssf.record.StyleRecord.class
            r1[r0] = r2
            r0 = 98
            java.lang.Class<org.apache.poi.hssf.record.SupBookRecord> r2 = org.apache.poi.hssf.record.SupBookRecord.class
            r1[r0] = r2
            r0 = 99
            java.lang.Class<org.apache.poi.hssf.record.TabIdRecord> r2 = org.apache.poi.hssf.record.TabIdRecord.class
            r1[r0] = r2
            r0 = 100
            java.lang.Class<org.apache.poi.hssf.record.TableRecord> r2 = org.apache.poi.hssf.record.TableRecord.class
            r1[r0] = r2
            r0 = 101(0x65, float:1.42E-43)
            java.lang.Class<org.apache.poi.hssf.record.TableStylesRecord> r2 = org.apache.poi.hssf.record.TableStylesRecord.class
            r1[r0] = r2
            r0 = 102(0x66, float:1.43E-43)
            java.lang.Class<org.apache.poi.hssf.record.TextObjectRecord> r2 = org.apache.poi.hssf.record.TextObjectRecord.class
            r1[r0] = r2
            r0 = 103(0x67, float:1.44E-43)
            java.lang.Class<org.apache.poi.hssf.record.TopMarginRecord> r2 = org.apache.poi.hssf.record.TopMarginRecord.class
            r1[r0] = r2
            r0 = 104(0x68, float:1.46E-43)
            java.lang.Class<org.apache.poi.hssf.record.UncalcedRecord> r2 = org.apache.poi.hssf.record.UncalcedRecord.class
            r1[r0] = r2
            r0 = 105(0x69, float:1.47E-43)
            java.lang.Class<org.apache.poi.hssf.record.UseSelFSRecord> r2 = org.apache.poi.hssf.record.UseSelFSRecord.class
            r1[r0] = r2
            r0 = 106(0x6a, float:1.49E-43)
            java.lang.Class<org.apache.poi.hssf.record.UserSViewBegin> r2 = org.apache.poi.hssf.record.UserSViewBegin.class
            r1[r0] = r2
            r0 = 107(0x6b, float:1.5E-43)
            java.lang.Class<org.apache.poi.hssf.record.UserSViewEnd> r2 = org.apache.poi.hssf.record.UserSViewEnd.class
            r1[r0] = r2
            r0 = 108(0x6c, float:1.51E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.ValueRangeRecord> r2 = org.apache.poi.hssf.record.chart.ValueRangeRecord.class
            r1[r0] = r2
            r0 = 109(0x6d, float:1.53E-43)
            java.lang.Class<org.apache.poi.hssf.record.VCenterRecord> r2 = org.apache.poi.hssf.record.VCenterRecord.class
            r1[r0] = r2
            r0 = 110(0x6e, float:1.54E-43)
            java.lang.Class<org.apache.poi.hssf.record.VerticalPageBreakRecord> r2 = org.apache.poi.hssf.record.VerticalPageBreakRecord.class
            r1[r0] = r2
            r0 = 111(0x6f, float:1.56E-43)
            java.lang.Class<org.apache.poi.hssf.record.WindowOneRecord> r2 = org.apache.poi.hssf.record.WindowOneRecord.class
            r1[r0] = r2
            r0 = 112(0x70, float:1.57E-43)
            java.lang.Class<org.apache.poi.hssf.record.WindowProtectRecord> r2 = org.apache.poi.hssf.record.WindowProtectRecord.class
            r1[r0] = r2
            r0 = 113(0x71, float:1.58E-43)
            java.lang.Class<org.apache.poi.hssf.record.WindowTwoRecord> r2 = org.apache.poi.hssf.record.WindowTwoRecord.class
            r1[r0] = r2
            r0 = 114(0x72, float:1.6E-43)
            java.lang.Class<org.apache.poi.hssf.record.WriteAccessRecord> r2 = org.apache.poi.hssf.record.WriteAccessRecord.class
            r1[r0] = r2
            r0 = 115(0x73, float:1.61E-43)
            java.lang.Class<org.apache.poi.hssf.record.WriteProtectRecord> r2 = org.apache.poi.hssf.record.WriteProtectRecord.class
            r1[r0] = r2
            r0 = 116(0x74, float:1.63E-43)
            java.lang.Class<org.apache.poi.hssf.record.WSBoolRecord> r2 = org.apache.poi.hssf.record.WSBoolRecord.class
            r1[r0] = r2
            r0 = 117(0x75, float:1.64E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.BeginRecord> r2 = org.apache.poi.hssf.record.chart.BeginRecord.class
            r1[r0] = r2
            r0 = 118(0x76, float:1.65E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartFRTInfoRecord> r2 = org.apache.poi.hssf.record.chart.ChartFRTInfoRecord.class
            r1[r0] = r2
            r0 = 119(0x77, float:1.67E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartStartBlockRecord> r2 = org.apache.poi.hssf.record.chart.ChartStartBlockRecord.class
            r1[r0] = r2
            r0 = 120(0x78, float:1.68E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartEndBlockRecord> r2 = org.apache.poi.hssf.record.chart.ChartEndBlockRecord.class
            r1[r0] = r2
            r0 = 121(0x79, float:1.7E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartStartObjectRecord> r2 = org.apache.poi.hssf.record.chart.ChartStartObjectRecord.class
            r1[r0] = r2
            r0 = 122(0x7a, float:1.71E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.ChartEndObjectRecord> r2 = org.apache.poi.hssf.record.chart.ChartEndObjectRecord.class
            r1[r0] = r2
            r0 = 123(0x7b, float:1.72E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.CatLabRecord> r2 = org.apache.poi.hssf.record.chart.CatLabRecord.class
            r1[r0] = r2
            r0 = 124(0x7c, float:1.74E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.DataFormatRecord> r2 = org.apache.poi.hssf.record.chart.DataFormatRecord.class
            r1[r0] = r2
            r0 = 125(0x7d, float:1.75E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.EndRecord> r2 = org.apache.poi.hssf.record.chart.EndRecord.class
            r1[r0] = r2
            r0 = 126(0x7e, float:1.77E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.LinkedDataRecord> r2 = org.apache.poi.hssf.record.chart.LinkedDataRecord.class
            r1[r0] = r2
            r0 = 127(0x7f, float:1.78E-43)
            java.lang.Class<org.apache.poi.hssf.record.chart.SeriesToChartGroupRecord> r2 = org.apache.poi.hssf.record.chart.SeriesToChartGroupRecord.class
            r1[r0] = r2
            r0 = 128(0x80, float:1.794E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.DataItemRecord> r2 = org.apache.poi.hssf.record.pivottable.DataItemRecord.class
            r1[r0] = r2
            r0 = 129(0x81, float:1.81E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord> r2 = org.apache.poi.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord.class
            r1[r0] = r2
            r0 = 130(0x82, float:1.82E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.PageItemRecord> r2 = org.apache.poi.hssf.record.pivottable.PageItemRecord.class
            r1[r0] = r2
            r0 = 131(0x83, float:1.84E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.StreamIDRecord> r2 = org.apache.poi.hssf.record.pivottable.StreamIDRecord.class
            r1[r0] = r2
            r0 = 132(0x84, float:1.85E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.ViewDefinitionRecord> r2 = org.apache.poi.hssf.record.pivottable.ViewDefinitionRecord.class
            r1[r0] = r2
            r0 = 133(0x85, float:1.86E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.ViewFieldsRecord> r2 = org.apache.poi.hssf.record.pivottable.ViewFieldsRecord.class
            r1[r0] = r2
            r0 = 134(0x86, float:1.88E-43)
            java.lang.Class<org.apache.poi.hssf.record.pivottable.ViewSourceRecord> r2 = org.apache.poi.hssf.record.pivottable.ViewSourceRecord.class
            r1[r0] = r2
            recordClasses = r1
            java.util.Map r0 = recordsToMap(r1)
            _recordCreatorsById = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.record.RecordFactory.<clinit>():void");
    }

    public static Class<? extends Record> getRecordClass(int sid) {
        I_RecordCreator rc = _recordCreatorsById.get(Integer.valueOf(sid));
        if (rc == null) {
            return null;
        }
        return rc.getRecordClass();
    }

    public static Record[] createRecord(RecordInputStream in) {
        Record record = createSingleRecord(in);
        if (record instanceof DBCellRecord) {
            return new Record[]{null};
        } else if (record instanceof RKRecord) {
            return new Record[]{convertToNumberRecord((RKRecord) record)};
        } else if (record instanceof MulRKRecord) {
            return convertRKRecords((MulRKRecord) record);
        } else {
            return new Record[]{record};
        }
    }

    public static Record createSingleRecord(RecordInputStream in) {
        I_RecordCreator constructor = _recordCreatorsById.get(Integer.valueOf(in.getSid()));
        if (constructor == null) {
            return new UnknownRecord(in);
        }
        return constructor.create(in);
    }

    public static NumberRecord convertToNumberRecord(RKRecord rk) {
        NumberRecord num = new NumberRecord();
        num.setColumn(rk.getColumn());
        num.setRow(rk.getRow());
        num.setXFIndex(rk.getXFIndex());
        num.setValue(rk.getRKNumber());
        return num;
    }

    public static NumberRecord[] convertRKRecords(MulRKRecord mrk) {
        NumberRecord[] mulRecs = new NumberRecord[mrk.getNumColumns()];
        for (int k = 0; k < mrk.getNumColumns(); k++) {
            NumberRecord nr = new NumberRecord();
            nr.setColumn((short) (mrk.getFirstColumn() + k));
            nr.setRow(mrk.getRow());
            nr.setXFIndex(mrk.getXFAt(k));
            nr.setValue(mrk.getRKNumberAt(k));
            mulRecs[k] = nr;
        }
        return mulRecs;
    }

    public static BlankRecord[] convertBlankRecords(MulBlankRecord mbk) {
        BlankRecord[] mulRecs = new BlankRecord[mbk.getNumColumns()];
        for (int k = 0; k < mbk.getNumColumns(); k++) {
            BlankRecord br = new BlankRecord();
            br.setColumn((short) (mbk.getFirstColumn() + k));
            br.setRow(mbk.getRow());
            br.setXFIndex(mbk.getXFAt(k));
            mulRecs[k] = br;
        }
        return mulRecs;
    }

    public static short[] getAllKnownRecordSIDs() {
        if (_allKnownRecordSIDs == null) {
            Map<Integer, I_RecordCreator> map = _recordCreatorsById;
            short[] results = new short[map.size()];
            int i = 0;
            for (Integer sid : map.keySet()) {
                results[i] = sid.shortValue();
                i++;
            }
            Arrays.sort(results);
            _allKnownRecordSIDs = results;
        }
        return (short[]) _allKnownRecordSIDs.clone();
    }

    private static Map<Integer, I_RecordCreator> recordsToMap(Class<? extends Record>[] records) {
        Map<Integer, I_RecordCreator> result = new HashMap<>();
        Set<Class<?>> uniqueRecClasses = new HashSet<>((records.length * 3) / 2);
        int i = 0;
        while (i < records.length) {
            Class<? extends Record> recClass = records[i];
            if (!Record.class.isAssignableFrom(recClass)) {
                throw new RuntimeException("Invalid record sub-class (" + recClass.getName() + ")");
            } else if (Modifier.isAbstract(recClass.getModifiers())) {
                throw new RuntimeException("Invalid record class (" + recClass.getName() + ") - must not be abstract");
            } else if (uniqueRecClasses.add(recClass)) {
                try {
                    int sid = recClass.getField("sid").getShort((Object) null);
                    Integer key = Integer.valueOf(sid);
                    if (!result.containsKey(key)) {
                        result.put(key, getRecordCreator(recClass));
                        i++;
                    } else {
                        Class<? extends Record> recordClass = result.get(key).getRecordClass();
                        throw new RuntimeException("duplicate record sid 0x" + Integer.toHexString(sid).toUpperCase() + " for classes (" + recClass.getName() + ") and (" + recordClass.getName() + ")");
                    }
                } catch (Exception e) {
                    throw new RecordFormatException("Unable to determine record types");
                }
            } else {
                throw new RuntimeException("duplicate record class (" + recClass.getName() + ")");
            }
        }
        return result;
    }

    private static I_RecordCreator getRecordCreator(Class<? extends Record> recClass) {
        try {
            return new ReflectionConstructorRecordCreator(recClass.getConstructor(CONSTRUCTOR_ARGS));
        } catch (NoSuchMethodException e) {
            try {
                return new ReflectionMethodRecordCreator(recClass.getDeclaredMethod("create", CONSTRUCTOR_ARGS));
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException("Failed to find constructor or create method for (" + recClass.getName() + ").");
            }
        }
    }

    public static List<Record> createRecords(InputStream in) throws RecordFormatException {
        List<Record> records = new ArrayList<>(512);
        RecordFactoryInputStream recStream = new RecordFactoryInputStream(in, true);
        while (true) {
            Record nextRecord = recStream.nextRecord();
            Record record = nextRecord;
            if (nextRecord == null) {
                return records;
            }
            records.add(record);
        }
    }
}
