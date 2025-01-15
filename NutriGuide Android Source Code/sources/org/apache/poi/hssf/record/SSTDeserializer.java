package org.apache.poi.hssf.record;

import java.io.PrintStream;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.util.IntMapper;

class SSTDeserializer {
    private IntMapper<UnicodeString> strings;

    public SSTDeserializer(IntMapper<UnicodeString> strings2) {
        this.strings = strings2;
    }

    public void manufactureStrings(int stringCount, RecordInputStream in) {
        UnicodeString str;
        for (int i = 0; i < stringCount; i++) {
            if (in.available() != 0 || in.hasNextRecord()) {
                str = new UnicodeString(in);
            } else {
                PrintStream printStream = System.err;
                printStream.println("Ran out of data before creating all the strings! String at index " + i + "");
                str = new UnicodeString("");
            }
            addToStringTable(this.strings, str);
        }
    }

    public static void addToStringTable(IntMapper<UnicodeString> strings2, UnicodeString string) {
        strings2.add(string);
    }
}
