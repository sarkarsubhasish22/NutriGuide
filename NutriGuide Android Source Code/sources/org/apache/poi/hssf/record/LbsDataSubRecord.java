package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public class LbsDataSubRecord extends SubRecord {
    public static final int sid = 19;
    private boolean[] _bsels;
    private int _cLines;
    private int _cbFContinued;
    private LbsDropData _dropData;
    private int _flags;
    private int _iSel;
    private int _idEdit;
    private Ptg _linkPtg;
    private String[] _rgLines;
    private Byte _unknownPostFormulaByte;
    private int _unknownPreFormulaInt;

    public LbsDataSubRecord(LittleEndianInput in, int cbFContinued, int cmoOt) {
        this._cbFContinued = cbFContinued;
        int encodedTokenLen = in.readUShort();
        if (encodedTokenLen > 0) {
            int formulaSize = in.readUShort();
            this._unknownPreFormulaInt = in.readInt();
            Ptg[] ptgs = Ptg.readTokens(formulaSize, in);
            if (ptgs.length == 1) {
                this._linkPtg = ptgs[0];
                int i = (encodedTokenLen - formulaSize) - 6;
                if (i == 0) {
                    this._unknownPostFormulaByte = null;
                } else if (i == 1) {
                    this._unknownPostFormulaByte = Byte.valueOf(in.readByte());
                } else {
                    throw new RecordFormatException("Unexpected leftover bytes");
                }
            } else {
                throw new RecordFormatException("Read " + ptgs.length + " tokens but expected exactly 1");
            }
        }
        this._cLines = in.readUShort();
        this._iSel = in.readUShort();
        this._flags = in.readUShort();
        this._idEdit = in.readUShort();
        if (cmoOt == 20) {
            this._dropData = new LbsDropData(in);
        }
        if ((this._flags & 2) != 0) {
            this._rgLines = new String[this._cLines];
            for (int i2 = 0; i2 < this._cLines; i2++) {
                this._rgLines[i2] = StringUtil.readUnicodeString(in);
            }
        }
        if (((this._flags >> 4) & 2) != 0) {
            this._bsels = new boolean[this._cLines];
            for (int i3 = 0; i3 < this._cLines; i3++) {
                this._bsels[i3] = in.readByte() == 1;
            }
        }
    }

    LbsDataSubRecord() {
    }

    public static LbsDataSubRecord newAutoFilterInstance() {
        LbsDataSubRecord lbs = new LbsDataSubRecord();
        lbs._cbFContinued = 8174;
        lbs._iSel = 0;
        lbs._flags = 769;
        LbsDropData lbsDropData = new LbsDropData();
        lbs._dropData = lbsDropData;
        int unused = lbsDropData._wStyle = LbsDropData.STYLE_COMBO_SIMPLE_DROPDOWN;
        int unused2 = lbs._dropData._cLine = 8;
        return lbs;
    }

    public boolean isTerminating() {
        return true;
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        int result = 2;
        Ptg ptg = this._linkPtg;
        if (ptg != null) {
            result = 2 + 2 + 4 + ptg.getSize();
            if (this._unknownPostFormulaByte != null) {
                result++;
            }
        }
        int result2 = result + 8;
        LbsDropData lbsDropData = this._dropData;
        if (lbsDropData != null) {
            result2 += lbsDropData.getDataSize();
        }
        if (this._rgLines != null) {
            for (String str : this._rgLines) {
                result2 += StringUtil.getEncodedSize(str);
            }
        }
        boolean[] zArr = this._bsels;
        if (zArr != null) {
            return result2 + zArr.length;
        }
        return result2;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(19);
        out.writeShort(this._cbFContinued);
        Ptg ptg = this._linkPtg;
        if (ptg == null) {
            out.writeShort(0);
        } else {
            int formulaSize = ptg.getSize();
            int linkSize = formulaSize + 6;
            if (this._unknownPostFormulaByte != null) {
                linkSize++;
            }
            out.writeShort(linkSize);
            out.writeShort(formulaSize);
            out.writeInt(this._unknownPreFormulaInt);
            this._linkPtg.write(out);
            Byte b = this._unknownPostFormulaByte;
            if (b != null) {
                out.writeByte(b.intValue());
            }
        }
        out.writeShort(this._cLines);
        out.writeShort(this._iSel);
        out.writeShort(this._flags);
        out.writeShort(this._idEdit);
        LbsDropData lbsDropData = this._dropData;
        if (lbsDropData != null) {
            lbsDropData.serialize(out);
        }
        if (this._rgLines != null) {
            for (String str : this._rgLines) {
                StringUtil.writeUnicodeString(out, str);
            }
        }
        if (this._bsels != null) {
            for (boolean val : this._bsels) {
                out.writeByte(val);
            }
        }
    }

    public Object clone() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(256);
        sb.append("[ftLbsData]\n");
        sb.append("    .unknownShort1 =");
        sb.append(HexDump.shortToHex(this._cbFContinued));
        sb.append("\n");
        sb.append("    .formula        = ");
        sb.append(10);
        Ptg ptg = this._linkPtg;
        if (ptg != null) {
            sb.append(ptg.toString());
            sb.append(this._linkPtg.getRVAType());
            sb.append(10);
        }
        sb.append("    .nEntryCount   =");
        sb.append(HexDump.shortToHex(this._cLines));
        sb.append("\n");
        sb.append("    .selEntryIx    =");
        sb.append(HexDump.shortToHex(this._iSel));
        sb.append("\n");
        sb.append("    .style         =");
        sb.append(HexDump.shortToHex(this._flags));
        sb.append("\n");
        sb.append("    .unknownShort10=");
        sb.append(HexDump.shortToHex(this._idEdit));
        sb.append("\n");
        if (this._dropData != null) {
            sb.append(10);
            sb.append(this._dropData.toString());
        }
        sb.append("[/ftLbsData]\n");
        return sb.toString();
    }

    public Ptg getFormula() {
        return this._linkPtg;
    }

    public int getNumberOfItems() {
        return this._cLines;
    }

    public static class LbsDropData {
        public static int STYLE_COMBO_DROPDOWN = 0;
        public static int STYLE_COMBO_EDIT_DROPDOWN = 1;
        public static int STYLE_COMBO_SIMPLE_DROPDOWN = 2;
        /* access modifiers changed from: private */
        public int _cLine;
        private int _dxMin;
        private String _str;
        private Byte _unused;
        /* access modifiers changed from: private */
        public int _wStyle;

        public LbsDropData() {
            this._str = "";
            this._unused = (byte) 0;
        }

        public LbsDropData(LittleEndianInput in) {
            this._wStyle = in.readUShort();
            this._cLine = in.readUShort();
            this._dxMin = in.readUShort();
            String readUnicodeString = StringUtil.readUnicodeString(in);
            this._str = readUnicodeString;
            if (StringUtil.getEncodedSize(readUnicodeString) % 2 != 0) {
                this._unused = Byte.valueOf(in.readByte());
            }
        }

        public void setStyle(int style) {
            this._wStyle = style;
        }

        public void setNumLines(int num) {
            this._cLine = num;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._wStyle);
            out.writeShort(this._cLine);
            out.writeShort(this._dxMin);
            StringUtil.writeUnicodeString(out, this._str);
            Byte b = this._unused;
            if (b != null) {
                out.writeByte(b.byteValue());
            }
        }

        public int getDataSize() {
            return 6 + StringUtil.getEncodedSize(this._str) + this._unused.byteValue();
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[LbsDropData]\n");
            sb.append("  ._wStyle:  ");
            sb.append(this._wStyle);
            sb.append(10);
            sb.append("  ._cLine:  ");
            sb.append(this._cLine);
            sb.append(10);
            sb.append("  ._dxMin:  ");
            sb.append(this._dxMin);
            sb.append(10);
            sb.append("  ._str:  ");
            sb.append(this._str);
            sb.append(10);
            if (this._unused != null) {
                sb.append("  ._unused:  ");
                sb.append(this._unused);
                sb.append(10);
            }
            sb.append("[/LbsDropData]\n");
            return sb.toString();
        }
    }
}
