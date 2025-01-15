package org.apache.poi.hssf.record;

import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.cf.BorderFormatting;
import org.apache.poi.hssf.record.cf.FontFormatting;
import org.apache.poi.hssf.record.cf.PatternFormatting;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;

public final class CFRuleRecord extends StandardRecord {
    public static final byte CONDITION_TYPE_CELL_VALUE_IS = 1;
    public static final byte CONDITION_TYPE_FORMULA = 2;
    private static final BitField align = bf(134217728);
    private static final BitField alignHor = bf(1);
    private static final BitField alignIndent = bf(32);
    private static final BitField alignJustLast = bf(16);
    private static final BitField alignRot = bf(8);
    private static final BitField alignShrin = bf(64);
    private static final BitField alignTextDir = bf(Integer.MIN_VALUE);
    private static final BitField alignVer = bf(2);
    private static final BitField alignWrap = bf(4);
    private static final BitField bord = bf(268435456);
    private static final BitField bordBlTr = bf(32768);
    private static final BitField bordBot = bf(8192);
    private static final BitField bordLeft = bf(1024);
    private static final BitField bordRight = bf(2048);
    private static final BitField bordTlBr = bf(16384);
    private static final BitField bordTop = bf(4096);
    private static final BitField fmtBlockBits = bf(2080374784);
    private static final BitField font = bf(67108864);
    private static final BitField modificationBits = bf(4194303);
    private static final BitField notUsed1 = bf(128);
    private static final BitField notUsed2 = bf(3670016);
    private static final BitField patt = bf(536870912);
    private static final BitField pattBgCol = bf(262144);
    private static final BitField pattCol = bf(131072);
    private static final BitField pattStyle = bf(65536);
    private static final BitField prot = bf(BasicMeasure.EXACTLY);
    private static final BitField protHidden = bf(512);
    private static final BitField protLocked = bf(256);
    public static final short sid = 433;
    private static final BitField undocumented = bf(62914560);
    private BorderFormatting _borderFormatting;
    private FontFormatting _fontFormatting;
    private PatternFormatting _patternFormatting;
    private Formula field_17_formula1;
    private Formula field_18_formula2;
    private byte field_1_condition_type;
    private byte field_2_comparison_operator;
    private int field_5_options;
    private short field_6_not_used;

    public static final class ComparisonOperator {
        public static final byte BETWEEN = 1;
        public static final byte EQUAL = 3;
        public static final byte GE = 7;
        public static final byte GT = 5;
        public static final byte LE = 8;
        public static final byte LT = 6;
        public static final byte NOT_BETWEEN = 2;
        public static final byte NOT_EQUAL = 4;
        public static final byte NO_COMPARISON = 0;
    }

    private static BitField bf(int i) {
        return BitFieldFactory.getInstance(i);
    }

    private CFRuleRecord(byte conditionType, byte comparisonOperation) {
        this.field_1_condition_type = conditionType;
        this.field_2_comparison_operator = comparisonOperation;
        int value = modificationBits.setValue(this.field_5_options, -1);
        this.field_5_options = value;
        int value2 = fmtBlockBits.setValue(value, 0);
        this.field_5_options = value2;
        this.field_5_options = undocumented.clear(value2);
        this.field_6_not_used = -32766;
        this._fontFormatting = null;
        this._borderFormatting = null;
        this._patternFormatting = null;
        this.field_17_formula1 = Formula.create(Ptg.EMPTY_PTG_ARRAY);
        this.field_18_formula2 = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }

    private CFRuleRecord(byte conditionType, byte comparisonOperation, Ptg[] formula1, Ptg[] formula2) {
        this(conditionType, comparisonOperation);
        this.field_17_formula1 = Formula.create(formula1);
        this.field_18_formula2 = Formula.create(formula2);
    }

    public static CFRuleRecord create(HSSFSheet sheet, String formulaText) {
        return new CFRuleRecord((byte) 2, (byte) 0, parseFormula(formulaText, sheet), (Ptg[]) null);
    }

    public static CFRuleRecord create(HSSFSheet sheet, byte comparisonOperation, String formulaText1, String formulaText2) {
        return new CFRuleRecord((byte) 1, comparisonOperation, parseFormula(formulaText1, sheet), parseFormula(formulaText2, sheet));
    }

    public CFRuleRecord(RecordInputStream in) {
        this.field_1_condition_type = in.readByte();
        this.field_2_comparison_operator = in.readByte();
        int field_3_formula1_len = in.readUShort();
        int field_4_formula2_len = in.readUShort();
        this.field_5_options = in.readInt();
        this.field_6_not_used = in.readShort();
        if (containsFontFormattingBlock()) {
            this._fontFormatting = new FontFormatting(in);
        }
        if (containsBorderFormattingBlock()) {
            this._borderFormatting = new BorderFormatting(in);
        }
        if (containsPatternFormattingBlock()) {
            this._patternFormatting = new PatternFormatting(in);
        }
        this.field_17_formula1 = Formula.read(field_3_formula1_len, in);
        this.field_18_formula2 = Formula.read(field_4_formula2_len, in);
    }

    public byte getConditionType() {
        return this.field_1_condition_type;
    }

    public boolean containsFontFormattingBlock() {
        return getOptionFlag(font);
    }

    public void setFontFormatting(FontFormatting fontFormatting) {
        this._fontFormatting = fontFormatting;
        setOptionFlag(fontFormatting != null, font);
    }

    public FontFormatting getFontFormatting() {
        if (containsFontFormattingBlock()) {
            return this._fontFormatting;
        }
        return null;
    }

    public boolean containsAlignFormattingBlock() {
        return getOptionFlag(align);
    }

    public void setAlignFormattingUnchanged() {
        setOptionFlag(false, align);
    }

    public boolean containsBorderFormattingBlock() {
        return getOptionFlag(bord);
    }

    public void setBorderFormatting(BorderFormatting borderFormatting) {
        this._borderFormatting = borderFormatting;
        setOptionFlag(borderFormatting != null, bord);
    }

    public BorderFormatting getBorderFormatting() {
        if (containsBorderFormattingBlock()) {
            return this._borderFormatting;
        }
        return null;
    }

    public boolean containsPatternFormattingBlock() {
        return getOptionFlag(patt);
    }

    public void setPatternFormatting(PatternFormatting patternFormatting) {
        this._patternFormatting = patternFormatting;
        setOptionFlag(patternFormatting != null, patt);
    }

    public PatternFormatting getPatternFormatting() {
        if (containsPatternFormattingBlock()) {
            return this._patternFormatting;
        }
        return null;
    }

    public boolean containsProtectionFormattingBlock() {
        return getOptionFlag(prot);
    }

    public void setProtectionFormattingUnchanged() {
        setOptionFlag(false, prot);
    }

    public void setComparisonOperation(byte operation) {
        this.field_2_comparison_operator = operation;
    }

    public byte getComparisonOperation() {
        return this.field_2_comparison_operator;
    }

    public int getOptions() {
        return this.field_5_options;
    }

    private boolean isModified(BitField field) {
        return !field.isSet(this.field_5_options);
    }

    private void setModified(boolean modified, BitField field) {
        this.field_5_options = field.setBoolean(this.field_5_options, !modified);
    }

    public boolean isLeftBorderModified() {
        return isModified(bordLeft);
    }

    public void setLeftBorderModified(boolean modified) {
        setModified(modified, bordLeft);
    }

    public boolean isRightBorderModified() {
        return isModified(bordRight);
    }

    public void setRightBorderModified(boolean modified) {
        setModified(modified, bordRight);
    }

    public boolean isTopBorderModified() {
        return isModified(bordTop);
    }

    public void setTopBorderModified(boolean modified) {
        setModified(modified, bordTop);
    }

    public boolean isBottomBorderModified() {
        return isModified(bordBot);
    }

    public void setBottomBorderModified(boolean modified) {
        setModified(modified, bordBot);
    }

    public boolean isTopLeftBottomRightBorderModified() {
        return isModified(bordTlBr);
    }

    public void setTopLeftBottomRightBorderModified(boolean modified) {
        setModified(modified, bordTlBr);
    }

    public boolean isBottomLeftTopRightBorderModified() {
        return isModified(bordBlTr);
    }

    public void setBottomLeftTopRightBorderModified(boolean modified) {
        setModified(modified, bordBlTr);
    }

    public boolean isPatternStyleModified() {
        return isModified(pattStyle);
    }

    public void setPatternStyleModified(boolean modified) {
        setModified(modified, pattStyle);
    }

    public boolean isPatternColorModified() {
        return isModified(pattCol);
    }

    public void setPatternColorModified(boolean modified) {
        setModified(modified, pattCol);
    }

    public boolean isPatternBackgroundColorModified() {
        return isModified(pattBgCol);
    }

    public void setPatternBackgroundColorModified(boolean modified) {
        setModified(modified, pattBgCol);
    }

    private boolean getOptionFlag(BitField field) {
        return field.isSet(this.field_5_options);
    }

    private void setOptionFlag(boolean flag, BitField field) {
        this.field_5_options = field.setBoolean(this.field_5_options, flag);
    }

    public Ptg[] getParsedExpression1() {
        return this.field_17_formula1.getTokens();
    }

    public void setParsedExpression1(Ptg[] ptgs) {
        this.field_17_formula1 = Formula.create(ptgs);
    }

    public Ptg[] getParsedExpression2() {
        return Formula.getTokens(this.field_18_formula2);
    }

    public void setParsedExpression2(Ptg[] ptgs) {
        this.field_18_formula2 = Formula.create(ptgs);
    }

    public short getSid() {
        return sid;
    }

    private static int getFormulaSize(Formula formula) {
        return formula.getEncodedTokenSize();
    }

    public void serialize(LittleEndianOutput out) {
        int formula1Len = getFormulaSize(this.field_17_formula1);
        int formula2Len = getFormulaSize(this.field_18_formula2);
        out.writeByte(this.field_1_condition_type);
        out.writeByte(this.field_2_comparison_operator);
        out.writeShort(formula1Len);
        out.writeShort(formula2Len);
        out.writeInt(this.field_5_options);
        out.writeShort(this.field_6_not_used);
        if (containsFontFormattingBlock()) {
            out.write(this._fontFormatting.getRawRecord());
        }
        if (containsBorderFormattingBlock()) {
            this._borderFormatting.serialize(out);
        }
        if (containsPatternFormattingBlock()) {
            this._patternFormatting.serialize(out);
        }
        this.field_17_formula1.serializeTokens(out);
        this.field_18_formula2.serializeTokens(out);
    }

    /* access modifiers changed from: protected */
    public int getDataSize() {
        int i = 0;
        int length = (containsFontFormattingBlock() ? this._fontFormatting.getRawRecord().length : 0) + 12 + (containsBorderFormattingBlock() ? 8 : 0);
        if (containsPatternFormattingBlock()) {
            i = 4;
        }
        return length + i + getFormulaSize(this.field_17_formula1) + getFormulaSize(this.field_18_formula2);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CFRULE]\n");
        buffer.append("    .condition_type   =" + this.field_1_condition_type);
        buffer.append("    OPTION FLAGS=0x" + Integer.toHexString(getOptions()));
        return buffer.toString();
    }

    public Object clone() {
        CFRuleRecord rec = new CFRuleRecord(this.field_1_condition_type, this.field_2_comparison_operator);
        rec.field_5_options = this.field_5_options;
        rec.field_6_not_used = this.field_6_not_used;
        if (containsFontFormattingBlock()) {
            rec._fontFormatting = (FontFormatting) this._fontFormatting.clone();
        }
        if (containsBorderFormattingBlock()) {
            rec._borderFormatting = (BorderFormatting) this._borderFormatting.clone();
        }
        if (containsPatternFormattingBlock()) {
            rec._patternFormatting = (PatternFormatting) this._patternFormatting.clone();
        }
        rec.field_17_formula1 = this.field_17_formula1.copy();
        rec.field_18_formula2 = this.field_17_formula1.copy();
        return rec;
    }

    private static Ptg[] parseFormula(String formula, HSSFSheet sheet) {
        if (formula == null) {
            return null;
        }
        return HSSFFormulaParser.parse(formula, sheet.getWorkbook(), 0, sheet.getWorkbook().getSheetIndex((Sheet) sheet));
    }
}
