package org.apache.poi.hssf.record;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.HexRead;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class HyperlinkRecord extends StandardRecord {
    static final GUID FILE_MONIKER = GUID.parse("00000303-0000-0000-C000-000000000046");
    private static final byte[] FILE_TAIL;
    static final int HLINK_ABS = 2;
    static final int HLINK_LABEL = 20;
    static final int HLINK_PLACE = 8;
    private static final int HLINK_TARGET_FRAME = 128;
    private static final int HLINK_UNC_PATH = 256;
    static final int HLINK_URL = 1;
    static final GUID STD_MONIKER = GUID.parse("79EAC9D0-BAF9-11CE-8C82-00AA004BA90B");
    private static final int TAIL_SIZE;
    static final GUID URL_MONIKER = GUID.parse("79EAC9E0-BAF9-11CE-8C82-00AA004BA90B");
    private static final byte[] URL_TAIL = HexRead.readFromString("79 58 81 F4  3B 1D 7F 48   AF 2C 82 5D  C4 85 27 63   00 00 00 00  A5 AB 00 00");
    public static final short sid = 440;
    private String _address;
    private int _fileOpts;
    private GUID _guid;
    private String _label;
    private int _linkOpts;
    private GUID _moniker;
    private CellRangeAddress _range;
    private String _shortFilename;
    private String _targetFrame;
    private String _textMark;
    private byte[] _uninterpretedTail;

    static final class GUID {
        public static final int ENCODED_SIZE = 16;
        private static final int TEXT_FORMAT_LENGTH = 36;
        private final int _d1;
        private final int _d2;
        private final int _d3;
        private final long _d4;

        public GUID(LittleEndianInput in) {
            this(in.readInt(), in.readUShort(), in.readUShort(), in.readLong());
        }

        public GUID(int d1, int d2, int d3, long d4) {
            this._d1 = d1;
            this._d2 = d2;
            this._d3 = d3;
            this._d4 = d4;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeInt(this._d1);
            out.writeShort(this._d2);
            out.writeShort(this._d3);
            out.writeLong(this._d4);
        }

        public boolean equals(Object obj) {
            GUID other = (GUID) obj;
            if (obj != null && (obj instanceof GUID) && this._d1 == other._d1 && this._d2 == other._d2 && this._d3 == other._d3 && this._d4 == other._d4) {
                return true;
            }
            return false;
        }

        public int getD1() {
            return this._d1;
        }

        public int getD2() {
            return this._d2;
        }

        public int getD3() {
            return this._d3;
        }

        public long getD4() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
            try {
                new DataOutputStream(baos).writeLong(this._d4);
                return new LittleEndianByteArrayInputStream(baos.toByteArray()).readLong();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String formatAsString() {
            StringBuilder sb = new StringBuilder(36);
            int PREFIX_LEN = "0x".length();
            sb.append(HexDump.intToHex(this._d1), PREFIX_LEN, 8);
            sb.append("-");
            sb.append(HexDump.shortToHex(this._d2), PREFIX_LEN, 4);
            sb.append("-");
            sb.append(HexDump.shortToHex(this._d3), PREFIX_LEN, 4);
            sb.append("-");
            char[] d4Chars = HexDump.longToHex(getD4());
            sb.append(d4Chars, PREFIX_LEN, 4);
            sb.append("-");
            sb.append(d4Chars, PREFIX_LEN + 4, 12);
            return sb.toString();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(formatAsString());
            sb.append("]");
            return sb.toString();
        }

        public static GUID parse(String rep) {
            char[] cc = rep.toCharArray();
            if (cc.length == 36) {
                int d0 = (parseShort(cc, 0) << 16) + (parseShort(cc, 4) << 0);
                int d1 = parseShort(cc, 9);
                int d2 = parseShort(cc, 14);
                for (int i = 23; i > 19; i--) {
                    cc[i] = cc[i - 1];
                }
                return new GUID(d0, d1, d2, parseLELong(cc, 20));
            }
            throw new RecordFormatException("supplied text is the wrong length for a GUID");
        }

        private static long parseLELong(char[] cc, int startIndex) {
            long acc = 0;
            for (int i = startIndex + 14; i >= startIndex; i -= 2) {
                acc = (((acc << 4) + ((long) parseHexChar(cc[i + 0]))) << 4) + ((long) parseHexChar(cc[i + 1]));
            }
            return acc;
        }

        private static int parseShort(char[] cc, int startIndex) {
            int acc = 0;
            for (int i = 0; i < 4; i++) {
                acc = (acc << 4) + parseHexChar(cc[startIndex + i]);
            }
            return acc;
        }

        private static int parseHexChar(char c) {
            if (c >= '0' && c <= '9') {
                return c - '0';
            }
            if (c >= 'A' && c <= 'F') {
                return (c - 'A') + 10;
            }
            if (c >= 'a' && c <= 'f') {
                return (c - 'a') + 10;
            }
            throw new RecordFormatException("Bad hex char '" + c + "'");
        }
    }

    static {
        byte[] readFromString = HexRead.readFromString("FF FF AD DE  00 00 00 00   00 00 00 00  00 00 00 00   00 00 00 00  00 00 00 00");
        FILE_TAIL = readFromString;
        TAIL_SIZE = readFromString.length;
    }

    public HyperlinkRecord() {
    }

    public int getFirstColumn() {
        return this._range.getFirstColumn();
    }

    public void setFirstColumn(int col) {
        this._range.setFirstColumn(col);
    }

    public int getLastColumn() {
        return this._range.getLastColumn();
    }

    public void setLastColumn(int col) {
        this._range.setLastColumn(col);
    }

    public int getFirstRow() {
        return this._range.getFirstRow();
    }

    public void setFirstRow(int col) {
        this._range.setFirstRow(col);
    }

    public int getLastRow() {
        return this._range.getLastRow();
    }

    public void setLastRow(int col) {
        this._range.setLastRow(col);
    }

    /* access modifiers changed from: package-private */
    public GUID getGuid() {
        return this._guid;
    }

    /* access modifiers changed from: package-private */
    public GUID getMoniker() {
        return this._moniker;
    }

    private static String cleanString(String s) {
        if (s == null) {
            return null;
        }
        int idx = s.indexOf(0);
        if (idx < 0) {
            return s;
        }
        return s.substring(0, idx);
    }

    private static String appendNullTerm(String s) {
        if (s == null) {
            return null;
        }
        return s + 0;
    }

    public String getLabel() {
        return cleanString(this._label);
    }

    public void setLabel(String label) {
        this._label = appendNullTerm(label);
    }

    public String getTargetFrame() {
        return cleanString(this._targetFrame);
    }

    public String getAddress() {
        if ((this._linkOpts & 1) != 0 && FILE_MONIKER.equals(this._moniker)) {
            String str = this._address;
            if (str == null) {
                str = this._shortFilename;
            }
            return cleanString(str);
        } else if ((this._linkOpts & 8) != 0) {
            return cleanString(this._textMark);
        } else {
            return cleanString(this._address);
        }
    }

    public void setAddress(String address) {
        if ((this._linkOpts & 1) != 0 && FILE_MONIKER.equals(this._moniker)) {
            this._shortFilename = appendNullTerm(address);
        } else if ((this._linkOpts & 8) != 0) {
            this._textMark = appendNullTerm(address);
        } else {
            this._address = appendNullTerm(address);
        }
    }

    public String getShortFilename() {
        return cleanString(this._shortFilename);
    }

    public void setShortFilename(String shortFilename) {
        this._shortFilename = appendNullTerm(shortFilename);
    }

    public String getTextMark() {
        return cleanString(this._textMark);
    }

    public void setTextMark(String textMark) {
        this._textMark = appendNullTerm(textMark);
    }

    /* access modifiers changed from: package-private */
    public int getLinkOptions() {
        return this._linkOpts;
    }

    public int getLabelOptions() {
        return 2;
    }

    public int getFileOptions() {
        return this._fileOpts;
    }

    public HyperlinkRecord(RecordInputStream in) {
        this._range = new CellRangeAddress(in);
        this._guid = new GUID(in);
        int streamVersion = in.readInt();
        if (streamVersion == 2) {
            int readInt = in.readInt();
            this._linkOpts = readInt;
            if ((readInt & 20) != 0) {
                this._label = in.readUnicodeLEString(in.readInt());
            }
            if ((this._linkOpts & 128) != 0) {
                this._targetFrame = in.readUnicodeLEString(in.readInt());
            }
            int len = this._linkOpts;
            if (!((len & 1) == 0 || (len & 256) == 0)) {
                this._moniker = null;
                this._address = in.readUnicodeLEString(in.readInt());
            }
            int nChars = this._linkOpts;
            if ((nChars & 1) != 0 && (nChars & 256) == 0) {
                GUID guid = new GUID(in);
                this._moniker = guid;
                if (URL_MONIKER.equals(guid)) {
                    int length = in.readInt();
                    if (length == in.remaining()) {
                        this._address = in.readUnicodeLEString(length / 2);
                    } else {
                        this._address = in.readUnicodeLEString((length - TAIL_SIZE) / 2);
                        this._uninterpretedTail = readTail(URL_TAIL, in);
                    }
                } else if (FILE_MONIKER.equals(this._moniker)) {
                    this._fileOpts = in.readShort();
                    this._shortFilename = StringUtil.readCompressedUnicode(in, in.readInt());
                    this._uninterpretedTail = readTail(FILE_TAIL, in);
                    if (in.readInt() > 0) {
                        int charDataSize = in.readInt();
                        int optFlags = in.readUShort();
                        if (optFlags == 3) {
                            this._address = StringUtil.readUnicodeLE(in, charDataSize / 2);
                        } else {
                            throw new RecordFormatException("Expected 0x3 but found " + optFlags);
                        }
                    } else {
                        this._address = null;
                    }
                } else if (STD_MONIKER.equals(this._moniker)) {
                    this._fileOpts = in.readShort();
                    byte[] path_bytes = new byte[in.readInt()];
                    in.readFully(path_bytes);
                    this._address = new String(path_bytes);
                }
            }
            if ((this._linkOpts & 8) != 0) {
                this._textMark = in.readUnicodeLEString(in.readInt());
            }
            if (in.remaining() > 0) {
                System.out.println(HexDump.toHex(in.readRemainder()));
                return;
            }
            return;
        }
        throw new RecordFormatException("Stream Version must be 0x2 but found " + streamVersion);
    }

    public void serialize(LittleEndianOutput out) {
        this._range.serialize(out);
        this._guid.serialize(out);
        out.writeInt(2);
        out.writeInt(this._linkOpts);
        if ((this._linkOpts & 20) != 0) {
            out.writeInt(this._label.length());
            StringUtil.putUnicodeLE(this._label, out);
        }
        if ((this._linkOpts & 128) != 0) {
            out.writeInt(this._targetFrame.length());
            StringUtil.putUnicodeLE(this._targetFrame, out);
        }
        int i = this._linkOpts;
        if (!((i & 1) == 0 || (i & 256) == 0)) {
            out.writeInt(this._address.length());
            StringUtil.putUnicodeLE(this._address, out);
        }
        int i2 = this._linkOpts;
        if ((i2 & 1) != 0 && (i2 & 256) == 0) {
            this._moniker.serialize(out);
            if (URL_MONIKER.equals(this._moniker)) {
                if (this._uninterpretedTail == null) {
                    out.writeInt(this._address.length() * 2);
                    StringUtil.putUnicodeLE(this._address, out);
                } else {
                    out.writeInt((this._address.length() * 2) + TAIL_SIZE);
                    StringUtil.putUnicodeLE(this._address, out);
                    writeTail(this._uninterpretedTail, out);
                }
            } else if (FILE_MONIKER.equals(this._moniker)) {
                out.writeShort(this._fileOpts);
                out.writeInt(this._shortFilename.length());
                StringUtil.putCompressedUnicode(this._shortFilename, out);
                writeTail(this._uninterpretedTail, out);
                String str = this._address;
                if (str == null) {
                    out.writeInt(0);
                } else {
                    int addrLen = str.length() * 2;
                    out.writeInt(addrLen + 6);
                    out.writeInt(addrLen);
                    out.writeShort(3);
                    StringUtil.putUnicodeLE(this._address, out);
                }
            }
        }
        if ((this._linkOpts & 8) != 0) {
            out.writeInt(this._textMark.length());
            StringUtil.putUnicodeLE(this._textMark, out);
        }
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        int size = 0 + 8 + 16 + 4 + 4;
        if ((this._linkOpts & 20) != 0) {
            size = size + 4 + (this._label.length() * 2);
        }
        if ((this._linkOpts & 128) != 0) {
            size = size + 4 + (this._targetFrame.length() * 2);
        }
        int i = this._linkOpts;
        if (!((i & 1) == 0 || (i & 256) == 0)) {
            size = size + 4 + (this._address.length() * 2);
        }
        int i2 = this._linkOpts;
        if ((i2 & 1) != 0 && (i2 & 256) == 0) {
            size += 16;
            if (URL_MONIKER.equals(this._moniker)) {
                size = size + 4 + (this._address.length() * 2);
                if (this._uninterpretedTail != null) {
                    size += TAIL_SIZE;
                }
            } else if (FILE_MONIKER.equals(this._moniker)) {
                size = size + 2 + 4 + this._shortFilename.length() + TAIL_SIZE + 4;
                String str = this._address;
                if (str != null) {
                    size = size + 6 + (str.length() * 2);
                }
            }
        }
        if ((this._linkOpts & 8) != 0) {
            return size + 4 + (this._textMark.length() * 2);
        }
        return size;
    }

    private static byte[] readTail(byte[] expectedTail, LittleEndianInput in) {
        byte[] result = new byte[TAIL_SIZE];
        in.readFully(result);
        return result;
    }

    private static void writeTail(byte[] tail, LittleEndianOutput out) {
        out.write(tail);
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[HYPERLINK RECORD]\n");
        buffer.append("    .range   = ");
        buffer.append(this._range.formatAsString());
        buffer.append("\n");
        buffer.append("    .guid    = ");
        buffer.append(this._guid.formatAsString());
        buffer.append("\n");
        buffer.append("    .linkOpts= ");
        buffer.append(HexDump.intToHex(this._linkOpts));
        buffer.append("\n");
        buffer.append("    .label   = ");
        buffer.append(getLabel());
        buffer.append("\n");
        if ((this._linkOpts & 128) != 0) {
            buffer.append("    .targetFrame= ");
            buffer.append(getTargetFrame());
            buffer.append("\n");
        }
        if (!((this._linkOpts & 1) == 0 || this._moniker == null)) {
            buffer.append("    .moniker   = ");
            buffer.append(this._moniker.formatAsString());
            buffer.append("\n");
        }
        if ((this._linkOpts & 8) != 0) {
            buffer.append("    .textMark= ");
            buffer.append(getTextMark());
            buffer.append("\n");
        }
        buffer.append("    .address   = ");
        buffer.append(getAddress());
        buffer.append("\n");
        buffer.append("[/HYPERLINK RECORD]\n");
        return buffer.toString();
    }

    public boolean isUrlLink() {
        int i = this._linkOpts;
        return (i & 1) > 0 && (i & 2) > 0;
    }

    public boolean isFileLink() {
        int i = this._linkOpts;
        return (i & 1) > 0 && (i & 2) == 0;
    }

    public boolean isDocumentLink() {
        return (this._linkOpts & 8) > 0;
    }

    public void newUrlLink() {
        this._range = new CellRangeAddress(0, 0, 0, 0);
        this._guid = STD_MONIKER;
        this._linkOpts = 23;
        setLabel("");
        this._moniker = URL_MONIKER;
        setAddress("");
        this._uninterpretedTail = URL_TAIL;
    }

    public void newFileLink() {
        this._range = new CellRangeAddress(0, 0, 0, 0);
        this._guid = STD_MONIKER;
        this._linkOpts = 21;
        this._fileOpts = 0;
        setLabel("");
        this._moniker = FILE_MONIKER;
        setAddress((String) null);
        setShortFilename("");
        this._uninterpretedTail = FILE_TAIL;
    }

    public void newDocumentLink() {
        this._range = new CellRangeAddress(0, 0, 0, 0);
        this._guid = STD_MONIKER;
        this._linkOpts = 28;
        setLabel("");
        this._moniker = FILE_MONIKER;
        setAddress("");
        setTextMark("");
    }

    public Object clone() {
        HyperlinkRecord rec = new HyperlinkRecord();
        rec._range = this._range.copy();
        rec._guid = this._guid;
        rec._linkOpts = this._linkOpts;
        rec._fileOpts = this._fileOpts;
        rec._label = this._label;
        rec._address = this._address;
        rec._moniker = this._moniker;
        rec._shortFilename = this._shortFilename;
        rec._targetFrame = this._targetFrame;
        rec._textMark = this._textMark;
        rec._uninterpretedTail = this._uninterpretedTail;
        return rec;
    }
}
