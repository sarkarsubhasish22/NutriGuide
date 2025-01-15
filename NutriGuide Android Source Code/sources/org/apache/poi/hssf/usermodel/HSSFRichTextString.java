package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

public final class HSSFRichTextString implements Comparable<HSSFRichTextString>, RichTextString {
    public static final short NO_FONT = 0;
    private InternalWorkbook _book;
    private LabelSSTRecord _record;
    private UnicodeString _string;

    public HSSFRichTextString() {
        this("");
    }

    public HSSFRichTextString(String string) {
        if (string == null) {
            this._string = new UnicodeString("");
        } else {
            this._string = new UnicodeString(string);
        }
    }

    HSSFRichTextString(InternalWorkbook book, LabelSSTRecord record) {
        setWorkbookReferences(book, record);
        this._string = book.getSSTString(record.getSSTIndex());
    }

    /* access modifiers changed from: package-private */
    public void setWorkbookReferences(InternalWorkbook book, LabelSSTRecord record) {
        this._book = book;
        this._record = record;
    }

    private UnicodeString cloneStringIfRequired() {
        if (this._book == null) {
            return this._string;
        }
        return (UnicodeString) this._string.clone();
    }

    private void addToSSTIfRequired() {
        InternalWorkbook internalWorkbook = this._book;
        if (internalWorkbook != null) {
            int index = internalWorkbook.addSSTString(this._string);
            this._record.setSSTIndex(index);
            this._string = this._book.getSSTString(index);
        }
    }

    public void applyFont(int startIndex, int endIndex, short fontIndex) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index must be less than end index.");
        } else if (startIndex < 0 || endIndex > length()) {
            throw new IllegalArgumentException("Start and end index not in range.");
        } else if (startIndex != endIndex) {
            short currentFont = 0;
            if (endIndex != length()) {
                currentFont = getFontAtIndex(endIndex);
            }
            UnicodeString cloneStringIfRequired = cloneStringIfRequired();
            this._string = cloneStringIfRequired;
            Iterator formatting = cloneStringIfRequired.formatIterator();
            if (formatting != null) {
                while (formatting.hasNext()) {
                    UnicodeString.FormatRun r = formatting.next();
                    if (r.getCharacterPos() >= startIndex && r.getCharacterPos() < endIndex) {
                        formatting.remove();
                    }
                }
            }
            this._string.addFormatRun(new UnicodeString.FormatRun((short) startIndex, fontIndex));
            if (endIndex != length()) {
                this._string.addFormatRun(new UnicodeString.FormatRun((short) endIndex, currentFont));
            }
            addToSSTIfRequired();
        }
    }

    public void applyFont(int startIndex, int endIndex, Font font) {
        applyFont(startIndex, endIndex, ((HSSFFont) font).getIndex());
    }

    public void applyFont(Font font) {
        applyFont(0, this._string.getCharCount(), font);
    }

    public void clearFormatting() {
        UnicodeString cloneStringIfRequired = cloneStringIfRequired();
        this._string = cloneStringIfRequired;
        cloneStringIfRequired.clearFormatting();
        addToSSTIfRequired();
    }

    public String getString() {
        return this._string.getString();
    }

    /* access modifiers changed from: package-private */
    public UnicodeString getUnicodeString() {
        return cloneStringIfRequired();
    }

    /* access modifiers changed from: package-private */
    public UnicodeString getRawUnicodeString() {
        return this._string;
    }

    /* access modifiers changed from: package-private */
    public void setUnicodeString(UnicodeString str) {
        this._string = str;
    }

    public int length() {
        return this._string.getCharCount();
    }

    public short getFontAtIndex(int index) {
        int size = this._string.getFormatRunCount();
        UnicodeString.FormatRun currentRun = null;
        for (int i = 0; i < size; i++) {
            UnicodeString.FormatRun r = this._string.getFormatRun(i);
            if (r.getCharacterPos() > index) {
                break;
            }
            currentRun = r;
        }
        if (currentRun == null) {
            return 0;
        }
        return currentRun.getFontIndex();
    }

    public int numFormattingRuns() {
        return this._string.getFormatRunCount();
    }

    public int getIndexOfFormattingRun(int index) {
        return this._string.getFormatRun(index).getCharacterPos();
    }

    public short getFontOfFormattingRun(int index) {
        return this._string.getFormatRun(index).getFontIndex();
    }

    public int compareTo(HSSFRichTextString r) {
        return this._string.compareTo(r._string);
    }

    public boolean equals(Object o) {
        if (o instanceof HSSFRichTextString) {
            return this._string.equals(((HSSFRichTextString) o)._string);
        }
        return false;
    }

    public String toString() {
        return this._string.toString();
    }

    public void applyFont(short fontIndex) {
        applyFont(0, this._string.getCharCount(), fontIndex);
    }
}
