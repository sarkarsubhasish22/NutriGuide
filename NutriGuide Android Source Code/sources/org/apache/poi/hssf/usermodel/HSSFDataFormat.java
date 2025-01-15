package org.apache.poi.hssf.usermodel;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormat;

public final class HSSFDataFormat implements DataFormat {
    private static final String[] _builtinFormats = BuiltinFormats.getAll();
    private final Vector<String> _formats = new Vector<>();
    private boolean _movedBuiltins = false;
    private final InternalWorkbook _workbook;

    HSSFDataFormat(InternalWorkbook workbook) {
        this._workbook = workbook;
        for (FormatRecord r : workbook.getFormats()) {
            if (this._formats.size() < r.getIndexCode() + 1) {
                this._formats.setSize(r.getIndexCode() + 1);
            }
            this._formats.set(r.getIndexCode(), r.getFormatString());
        }
    }

    public static List<String> getBuiltinFormats() {
        return Arrays.asList(_builtinFormats);
    }

    public static short getBuiltinFormat(String format) {
        return (short) BuiltinFormats.getBuiltinFormat(format);
    }

    public short getFormat(String pFormat) {
        String format;
        if (pFormat.toUpperCase().equals("TEXT")) {
            format = "@";
        } else {
            format = pFormat;
        }
        if (!this._movedBuiltins) {
            int i = 0;
            while (true) {
                String[] strArr = _builtinFormats;
                if (i >= strArr.length) {
                    break;
                }
                if (this._formats.size() < i + 1) {
                    this._formats.setSize(i + 1);
                }
                this._formats.set(i, strArr[i]);
                i++;
            }
            this._movedBuiltins = true;
        }
        ListIterator<String> i2 = this._formats.listIterator();
        while (i2.hasNext()) {
            int ind = i2.nextIndex();
            if (format.equals(i2.next())) {
                return (short) ind;
            }
        }
        int ind2 = this._workbook.getFormat(format, true);
        if (this._formats.size() <= ind2) {
            this._formats.setSize(ind2 + 1);
        }
        this._formats.set(ind2, format);
        return (short) ind2;
    }

    public String getFormat(short index) {
        if (this._movedBuiltins) {
            return this._formats.get(index);
        }
        String[] strArr = _builtinFormats;
        if (strArr.length <= index || strArr[index] == null) {
            return this._formats.get(index);
        }
        return strArr[index];
    }

    public static String getBuiltinFormat(short index) {
        return BuiltinFormats.getBuiltinFormat((int) index);
    }

    public static int getNumberOfBuiltinBuiltinFormats() {
        return _builtinFormats.length;
    }
}
