package org.apache.poi.ss.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.hssf.record.constant.ErrorConstant;
import org.apache.poi.hssf.record.formula.AbstractFunctionPtg;
import org.apache.poi.hssf.record.formula.AddPtg;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.AreaPtg;
import org.apache.poi.hssf.record.formula.ArrayPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.BoolPtg;
import org.apache.poi.hssf.record.formula.ConcatPtg;
import org.apache.poi.hssf.record.formula.DividePtg;
import org.apache.poi.hssf.record.formula.EqualPtg;
import org.apache.poi.hssf.record.formula.ErrPtg;
import org.apache.poi.hssf.record.formula.FuncPtg;
import org.apache.poi.hssf.record.formula.FuncVarPtg;
import org.apache.poi.hssf.record.formula.GreaterEqualPtg;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.LessEqualPtg;
import org.apache.poi.hssf.record.formula.LessThanPtg;
import org.apache.poi.hssf.record.formula.MemAreaPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.MultiplyPtg;
import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.NotEqualPtg;
import org.apache.poi.hssf.record.formula.NumberPtg;
import org.apache.poi.hssf.record.formula.OperandPtg;
import org.apache.poi.hssf.record.formula.OperationPtg;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.record.formula.PercentPtg;
import org.apache.poi.hssf.record.formula.PowerPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.RangePtg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.RefPtg;
import org.apache.poi.hssf.record.formula.StringPtg;
import org.apache.poi.hssf.record.formula.SubtractPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.poi.hssf.record.formula.UnionPtg;
import org.apache.poi.hssf.record.formula.ValueOperatorPtg;
import org.apache.poi.hssf.record.formula.function.FunctionMetadata;
import org.apache.poi.hssf.record.formula.function.FunctionMetadataRegistry;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

public final class FormulaParser {
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Za-z]+)?(\\$?[0-9]+)?");
    private static char TAB = 9;
    private FormulaParsingWorkbook _book;
    private final int _formulaLength;
    private final String _formulaString;
    private int _pointer = 0;
    private ParseNode _rootNode;
    private int _sheetIndex;
    private SpreadsheetVersion _ssVersion;
    private char look;

    private static final class Identifier {
        private final boolean _isQuoted;
        private final String _name;

        public Identifier(String name, boolean isQuoted) {
            this._name = name;
            this._isQuoted = isQuoted;
        }

        public String getName() {
            return this._name;
        }

        public boolean isQuoted() {
            return this._isQuoted;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            if (this._isQuoted) {
                sb.append("'");
                sb.append(this._name);
                sb.append("'");
            } else {
                sb.append(this._name);
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private static final class SheetIdentifier {
        private final String _bookName;
        private final Identifier _sheetIdentifier;

        public SheetIdentifier(String bookName, Identifier sheetIdentifier) {
            this._bookName = bookName;
            this._sheetIdentifier = sheetIdentifier;
        }

        public String getBookName() {
            return this._bookName;
        }

        public Identifier getSheetIdentifier() {
            return this._sheetIdentifier;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            if (this._bookName != null) {
                sb.append(" [");
                sb.append(this._sheetIdentifier.getName());
                sb.append("]");
            }
            if (this._sheetIdentifier.isQuoted()) {
                sb.append("'");
                sb.append(this._sheetIdentifier.getName());
                sb.append("'");
            } else {
                sb.append(this._sheetIdentifier.getName());
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private FormulaParser(String formula, FormulaParsingWorkbook book, int sheetIndex) {
        this._formulaString = formula;
        this._book = book;
        this._ssVersion = book == null ? SpreadsheetVersion.EXCEL97 : book.getSpreadsheetVersion();
        this._formulaLength = formula.length();
        this._sheetIndex = sheetIndex;
    }

    public static Ptg[] parse(String formula, FormulaParsingWorkbook workbook, int formulaType, int sheetIndex) {
        FormulaParser fp = new FormulaParser(formula, workbook, sheetIndex);
        fp.parse();
        return fp.getRPNPtg(formulaType);
    }

    private void GetChar() {
        int i = this._pointer;
        int i2 = this._formulaLength;
        if (i <= i2) {
            if (i < i2) {
                this.look = this._formulaString.charAt(i);
            } else {
                this.look = 0;
            }
            this._pointer++;
            return;
        }
        throw new RuntimeException("too far");
    }

    private void resetPointer(int ptr) {
        this._pointer = ptr;
        if (ptr <= this._formulaLength) {
            this.look = this._formulaString.charAt(ptr - 1);
        } else {
            this.look = 0;
        }
    }

    private RuntimeException expected(String s) {
        String msg;
        if (this.look != '=' || this._formulaString.substring(0, this._pointer - 1).trim().length() >= 1) {
            msg = "Parse error near char " + (this._pointer - 1) + " '" + this.look + "'" + " in specified formula '" + this._formulaString + "'. Expected " + s;
        } else {
            msg = "The specified formula '" + this._formulaString + "' starts with an equals sign which is not allowed.";
        }
        return new FormulaParseException(msg);
    }

    private static boolean IsAlpha(char c) {
        return Character.isLetter(c) || c == '$' || c == '_';
    }

    private static boolean IsDigit(char c) {
        return Character.isDigit(c);
    }

    private static boolean IsWhite(char c) {
        return c == ' ' || c == TAB;
    }

    private void SkipWhite() {
        while (IsWhite(this.look)) {
            GetChar();
        }
    }

    private void Match(char x) {
        if (this.look == x) {
            GetChar();
            return;
        }
        throw expected("'" + x + "'");
    }

    private String GetNum() {
        StringBuffer value = new StringBuffer();
        while (IsDigit(this.look)) {
            value.append(this.look);
            GetChar();
        }
        if (value.length() == 0) {
            return null;
        }
        return value.toString();
    }

    private ParseNode parseRangeExpression() {
        ParseNode result = parseRangeable();
        boolean hasRange = false;
        while (this.look == ':') {
            int pos = this._pointer;
            GetChar();
            ParseNode nextPart = parseRangeable();
            checkValidRangeOperand("LHS", pos, result);
            checkValidRangeOperand("RHS", pos, nextPart);
            result = new ParseNode((Ptg) RangePtg.instance, new ParseNode[]{result, nextPart});
            hasRange = true;
        }
        if (hasRange) {
            return augmentWithMemPtg(result);
        }
        return result;
    }

    private static ParseNode augmentWithMemPtg(ParseNode root) {
        Ptg memPtg;
        if (needsMemFunc(root)) {
            memPtg = new MemFuncPtg(root.getEncodedSize());
        } else {
            memPtg = new MemAreaPtg(root.getEncodedSize());
        }
        return new ParseNode(memPtg, root);
    }

    private static boolean needsMemFunc(ParseNode root) {
        Ptg token = root.getToken();
        if ((token instanceof AbstractFunctionPtg) || (token instanceof ExternSheetReferenceToken) || (token instanceof NamePtg) || (token instanceof NameXPtg)) {
            return true;
        }
        if ((token instanceof OperationPtg) || (token instanceof ParenthesisPtg)) {
            for (ParseNode child : root.getChildren()) {
                if (needsMemFunc(child)) {
                    return true;
                }
            }
            return false;
        } else if (!(token instanceof OperandPtg) && (token instanceof OperationPtg)) {
            return true;
        } else {
            return false;
        }
    }

    private static void checkValidRangeOperand(String sideName, int currentParsePosition, ParseNode pn) {
        if (!isValidRangeOperand(pn)) {
            throw new FormulaParseException("The " + sideName + " of the range operator ':' at position " + currentParsePosition + " is not a proper reference.");
        }
    }

    private static boolean isValidRangeOperand(ParseNode a) {
        Ptg tkn = a.getToken();
        if (tkn instanceof OperandPtg) {
            return true;
        }
        if (tkn instanceof AbstractFunctionPtg) {
            if (((AbstractFunctionPtg) tkn).getDefaultOperandClass() == 0) {
                return true;
            }
            return false;
        } else if (tkn instanceof ValueOperatorPtg) {
            return false;
        } else {
            if (tkn instanceof OperationPtg) {
                return true;
            }
            if (tkn instanceof ParenthesisPtg) {
                return isValidRangeOperand(a.getChildren()[0]);
            }
            if (tkn == ErrPtg.REF_INVALID) {
                return true;
            }
            return false;
        }
    }

    private ParseNode parseRangeable() {
        char c;
        String prefix;
        SkipWhite();
        int savePointer = this._pointer;
        SheetIdentifier sheetIden = parseSheetName();
        if (sheetIden == null) {
            resetPointer(savePointer);
        } else {
            SkipWhite();
            savePointer = this._pointer;
        }
        SimpleRangePart part1 = parseSimpleRangePart();
        if (part1 != null) {
            boolean whiteAfterPart1 = IsWhite(this.look);
            if (whiteAfterPart1) {
                SkipWhite();
            }
            char c2 = this.look;
            if (c2 == ':') {
                int colonPos = this._pointer;
                GetChar();
                SkipWhite();
                SimpleRangePart part2 = parseSimpleRangePart();
                if (part2 != null && !part1.isCompatibleForArea(part2)) {
                    part2 = null;
                }
                if (part2 != null) {
                    return createAreaRefParseNode(sheetIden, part1, part2);
                }
                resetPointer(colonPos);
                if (part1.isCell()) {
                    return createAreaRefParseNode(sheetIden, part1, part2);
                }
                if (sheetIden == null) {
                    prefix = "";
                } else {
                    prefix = "'" + sheetIden.getSheetIdentifier().getName() + '!';
                }
                throw new FormulaParseException(prefix + part1.getRep() + "' is not a proper reference.");
            } else if (c2 == '.') {
                GetChar();
                int dotCount = 1;
                while (true) {
                    c = this.look;
                    if (c != '.') {
                        break;
                    }
                    dotCount++;
                    GetChar();
                }
                boolean whiteBeforePart2 = IsWhite(c);
                SkipWhite();
                SimpleRangePart part22 = parseSimpleRangePart();
                String part1And2 = this._formulaString.substring(savePointer - 1, this._pointer - 1);
                if (part22 == null) {
                    if (sheetIden == null) {
                        return parseNonRange(savePointer);
                    }
                    throw new FormulaParseException("Complete area reference expected after sheet name at index " + this._pointer + ".");
                } else if (whiteAfterPart1 || whiteBeforePart2) {
                    if (!part1.isRowOrColumn() && !part22.isRowOrColumn()) {
                        return createAreaRefParseNode(sheetIden, part1, part22);
                    }
                    throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must not contain whitespace.");
                } else if (dotCount == 1 && part1.isRow() && part22.isRow()) {
                    return parseNonRange(savePointer);
                } else {
                    if ((!part1.isRowOrColumn() && !part22.isRowOrColumn()) || dotCount == 2) {
                        return createAreaRefParseNode(sheetIden, part1, part22);
                    }
                    throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must have exactly 2 dots.");
                }
            } else if (part1.isCell() != 0 && isValidCellReference(part1.getRep())) {
                return createAreaRefParseNode(sheetIden, part1, (SimpleRangePart) null);
            } else {
                if (sheetIden == null) {
                    return parseNonRange(savePointer);
                }
                throw new FormulaParseException("Second part of cell reference expected after sheet name at index " + this._pointer + ".");
            }
        } else if (sheetIden == null) {
            return parseNonRange(savePointer);
        } else {
            if (this.look == '#') {
                return new ParseNode(ErrPtg.valueOf(parseErrorLiteral()));
            }
            throw new FormulaParseException("Cell reference expected after sheet name at index " + this._pointer + ".");
        }
    }

    private ParseNode parseNonRange(int savePointer) {
        resetPointer(savePointer);
        if (Character.isDigit(this.look)) {
            return new ParseNode(parseNumber());
        }
        if (this.look == '\"') {
            return new ParseNode(new StringPtg(parseStringLiteral()));
        }
        StringBuilder sb = new StringBuilder();
        if (Character.isLetter(this.look) || this.look == '_') {
            while (isValidDefinedNameChar(this.look)) {
                sb.append(this.look);
                GetChar();
            }
            SkipWhite();
            String name = sb.toString();
            if (this.look == '(') {
                return function(name);
            }
            if (name.equalsIgnoreCase("TRUE") || name.equalsIgnoreCase("FALSE")) {
                return new ParseNode(BoolPtg.valueOf(name.equalsIgnoreCase("TRUE")));
            }
            FormulaParsingWorkbook formulaParsingWorkbook = this._book;
            if (formulaParsingWorkbook != null) {
                EvaluationName evalName = formulaParsingWorkbook.getName(name, this._sheetIndex);
                if (evalName == null) {
                    throw new FormulaParseException("Specified named range '" + name + "' does not exist in the current workbook.");
                } else if (evalName.isRange()) {
                    return new ParseNode(evalName.createPtg());
                } else {
                    throw new FormulaParseException("Specified name '" + name + "' is not a range as expected.");
                }
            } else {
                throw new IllegalStateException("Need book to evaluate name '" + name + "'");
            }
        } else {
            throw expected("number, string, or defined name");
        }
    }

    private static boolean isValidDefinedNameChar(char ch) {
        if (Character.isLetterOrDigit(ch) || ch == '.' || ch == '?' || ch == '\\' || ch == '_') {
            return true;
        }
        return false;
    }

    private ParseNode createAreaRefParseNode(SheetIdentifier sheetIden, SimpleRangePart part1, SimpleRangePart part2) throws FormulaParseException {
        int extIx;
        Ptg ptg;
        if (sheetIden == null) {
            extIx = Integer.MIN_VALUE;
        } else {
            String sName = sheetIden.getSheetIdentifier().getName();
            if (sheetIden.getBookName() == null) {
                extIx = this._book.getExternalSheetIndex(sName);
            } else {
                extIx = this._book.getExternalSheetIndex(sheetIden.getBookName(), sName);
            }
        }
        if (part2 == null) {
            CellReference cr = part1.getCellReference();
            if (sheetIden == null) {
                ptg = new RefPtg(cr);
            } else {
                ptg = new Ref3DPtg(cr, extIx);
            }
        } else {
            AreaReference areaRef = createAreaRef(part1, part2);
            if (sheetIden == null) {
                ptg = new AreaPtg(areaRef);
            } else {
                ptg = new Area3DPtg(areaRef, extIx);
            }
        }
        return new ParseNode(ptg);
    }

    private static AreaReference createAreaRef(SimpleRangePart part1, SimpleRangePart part2) {
        if (!part1.isCompatibleForArea(part2)) {
            throw new FormulaParseException("has incompatible parts: '" + part1.getRep() + "' and '" + part2.getRep() + "'.");
        } else if (part1.isRow()) {
            return AreaReference.getWholeRow(part1.getRep(), part2.getRep());
        } else {
            if (part1.isColumn()) {
                return AreaReference.getWholeColumn(part1.getRep(), part2.getRep());
            }
            return new AreaReference(part1.getCellReference(), part2.getCellReference());
        }
    }

    private SimpleRangePart parseSimpleRangePart() {
        int ptr = this._pointer - 1;
        boolean hasDigits = false;
        boolean hasLetters = false;
        while (ptr < this._formulaLength) {
            char ch = this._formulaString.charAt(ptr);
            if (!Character.isDigit(ch)) {
                if (!Character.isLetter(ch)) {
                    if (!(ch == '$' || ch == '_')) {
                        break;
                    }
                } else {
                    hasLetters = true;
                }
            } else {
                hasDigits = true;
            }
            ptr++;
        }
        int i = this._pointer;
        if (ptr <= i - 1) {
            return null;
        }
        String rep = this._formulaString.substring(i - 1, ptr);
        if (!CELL_REF_PATTERN.matcher(rep).matches()) {
            return null;
        }
        if (!hasLetters || !hasDigits) {
            if (hasLetters) {
                if (!CellReference.isColumnWithnRange(rep.replace("$", ""), this._ssVersion)) {
                    return null;
                }
            } else if (!hasDigits) {
                return null;
            } else {
                try {
                    int i2 = Integer.parseInt(rep.replace("$", ""));
                    if (i2 < 1 || i2 > 65536) {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else if (!isValidCellReference(rep)) {
            return null;
        }
        resetPointer(ptr + 1);
        return new SimpleRangePart(rep, hasLetters, hasDigits);
    }

    private static final class SimpleRangePart {
        private final String _rep;
        private final Type _type;

        private enum Type {
            CELL,
            ROW,
            COLUMN;

            public static Type get(boolean hasLetters, boolean hasDigits) {
                if (hasLetters) {
                    return hasDigits ? CELL : COLUMN;
                }
                if (hasDigits) {
                    return ROW;
                }
                throw new IllegalArgumentException("must have either letters or numbers");
            }
        }

        public SimpleRangePart(String rep, boolean hasLetters, boolean hasNumbers) {
            this._rep = rep;
            this._type = Type.get(hasLetters, hasNumbers);
        }

        public boolean isCell() {
            return this._type == Type.CELL;
        }

        public boolean isRowOrColumn() {
            return this._type != Type.CELL;
        }

        public CellReference getCellReference() {
            if (this._type == Type.CELL) {
                return new CellReference(this._rep);
            }
            throw new IllegalStateException("Not applicable to this type");
        }

        public boolean isColumn() {
            return this._type == Type.COLUMN;
        }

        public boolean isRow() {
            return this._type == Type.ROW;
        }

        public String getRep() {
            return this._rep;
        }

        public boolean isCompatibleForArea(SimpleRangePart part2) {
            return this._type == part2._type;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(getClass().getName());
            sb.append(" [");
            sb.append(this._rep);
            sb.append("]");
            return sb.toString();
        }
    }

    private SheetIdentifier parseSheetName() {
        String bookName;
        if (this.look == '[') {
            StringBuilder sb = new StringBuilder();
            GetChar();
            while (true) {
                char c = this.look;
                if (c == ']') {
                    break;
                }
                sb.append(c);
                GetChar();
            }
            GetChar();
            bookName = sb.toString();
        } else {
            bookName = null;
        }
        char c2 = this.look;
        if (c2 == '\'') {
            StringBuffer sb2 = new StringBuffer();
            Match('\'');
            boolean done = this.look == '\'';
            while (!done) {
                sb2.append(this.look);
                GetChar();
                if (this.look == '\'') {
                    Match('\'');
                    done = this.look != '\'';
                }
            }
            Identifier iden = new Identifier(sb2.toString(), true);
            SkipWhite();
            if (this.look != '!') {
                return null;
            }
            GetChar();
            return new SheetIdentifier(bookName, iden);
        } else if (c2 != '_' && !Character.isLetter(c2)) {
            return null;
        } else {
            StringBuilder sb3 = new StringBuilder();
            while (isUnquotedSheetNameChar(this.look)) {
                sb3.append(this.look);
                GetChar();
            }
            SkipWhite();
            if (this.look != '!') {
                return null;
            }
            GetChar();
            return new SheetIdentifier(bookName, new Identifier(sb3.toString(), false));
        }
    }

    private static boolean isUnquotedSheetNameChar(char ch) {
        if (Character.isLetterOrDigit(ch) || ch == '.' || ch == '_') {
            return true;
        }
        return false;
    }

    private boolean isValidCellReference(String str) {
        boolean z = true;
        boolean result = CellReference.classifyCellReference(str, this._ssVersion) == CellReference.NameType.CELL;
        if (!result) {
            return result;
        }
        if (!(FunctionMetadataRegistry.getFunctionByName(str.toUpperCase()) != null)) {
            return result;
        }
        int savePointer = this._pointer;
        resetPointer(this._pointer + str.length());
        SkipWhite();
        if (this.look == '(') {
            z = false;
        }
        boolean result2 = z;
        resetPointer(savePointer);
        return result2;
    }

    private ParseNode function(String name) {
        Ptg nameToken = null;
        if (!AbstractFunctionPtg.isBuiltInFunctionName(name)) {
            FormulaParsingWorkbook formulaParsingWorkbook = this._book;
            if (formulaParsingWorkbook != null) {
                EvaluationName hName = formulaParsingWorkbook.getName(name, this._sheetIndex);
                if (hName == null) {
                    nameToken = this._book.getNameXPtg(name);
                    if (nameToken == null) {
                        throw new FormulaParseException("Name '" + name + "' is completely unknown in the current workbook");
                    }
                } else if (hName.isFunctionName()) {
                    nameToken = hName.createPtg();
                } else {
                    throw new FormulaParseException("Attempt to use name '" + name + "' as a function, but defined name in workbook does not refer to a function");
                }
            } else {
                throw new IllegalStateException("Need book to evaluate name '" + name + "'");
            }
        }
        Match('(');
        ParseNode[] args = Arguments();
        Match(')');
        return getFunction(name, nameToken, args);
    }

    private ParseNode getFunction(String name, Ptg namePtg, ParseNode[] args) {
        AbstractFunctionPtg retval;
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByName(name.toUpperCase());
        int numArgs = args.length;
        if (fm == null) {
            if (namePtg != null) {
                ParseNode[] allArgs = new ParseNode[(numArgs + 1)];
                allArgs[0] = new ParseNode(namePtg);
                System.arraycopy(args, 0, allArgs, 1, numArgs);
                return new ParseNode((Ptg) FuncVarPtg.create(name, numArgs + 1), allArgs);
            }
            throw new IllegalStateException("NamePtg must be supplied for external functions");
        } else if (namePtg == null) {
            boolean isVarArgs = !fm.hasFixedArgsLength();
            int funcIx = fm.getIndex();
            if (funcIx == 4 && args.length == 1) {
                return new ParseNode((Ptg) AttrPtg.getSumSingle(), args);
            }
            validateNumArgs(args.length, fm);
            if (isVarArgs) {
                retval = FuncVarPtg.create(name, numArgs);
            } else {
                retval = FuncPtg.create(funcIx);
            }
            return new ParseNode((Ptg) retval, args);
        } else {
            throw new IllegalStateException("NamePtg no applicable to internal functions");
        }
    }

    private void validateNumArgs(int numArgs, FunctionMetadata fm) {
        int maxArgs;
        String msg;
        String msg2;
        if (numArgs < fm.getMinParams()) {
            String msg3 = "Too few arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg2 = msg3 + "Expected " + fm.getMinParams();
            } else {
                msg2 = msg3 + "At least " + fm.getMinParams() + " were expected";
            }
            throw new FormulaParseException(msg2 + " but got " + numArgs + ".");
        }
        if (fm.hasUnlimitedVarags()) {
            FormulaParsingWorkbook formulaParsingWorkbook = this._book;
            if (formulaParsingWorkbook != null) {
                maxArgs = formulaParsingWorkbook.getSpreadsheetVersion().getMaxFunctionArgs();
            } else {
                maxArgs = fm.getMaxParams();
            }
        } else {
            maxArgs = fm.getMaxParams();
        }
        if (numArgs > maxArgs) {
            String msg4 = "Too many arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg = msg4 + "Expected " + maxArgs;
            } else {
                msg = msg4 + "At most " + maxArgs + " were expected";
            }
            throw new FormulaParseException(msg + " but got " + numArgs + ".");
        }
    }

    private static boolean isArgumentDelimiter(char ch) {
        return ch == ',' || ch == ')';
    }

    private ParseNode[] Arguments() {
        List<ParseNode> temp = new ArrayList<>(2);
        SkipWhite();
        if (this.look == ')') {
            return ParseNode.EMPTY_ARRAY;
        }
        boolean missedPrevArg = true;
        int numArgs = 0;
        while (true) {
            SkipWhite();
            if (isArgumentDelimiter(this.look)) {
                if (missedPrevArg) {
                    temp.add(new ParseNode(MissingArgPtg.instance));
                    numArgs++;
                }
                if (this.look == ')') {
                    ParseNode[] result = new ParseNode[temp.size()];
                    temp.toArray(result);
                    return result;
                }
                Match(',');
                missedPrevArg = true;
            } else {
                temp.add(comparisonExpression());
                numArgs++;
                missedPrevArg = false;
                SkipWhite();
                if (!isArgumentDelimiter(this.look)) {
                    throw expected("',' or ')'");
                }
            }
        }
    }

    private ParseNode powerFactor() {
        ParseNode result = percentFactor();
        while (true) {
            SkipWhite();
            if (this.look != '^') {
                return result;
            }
            Match('^');
            result = new ParseNode(PowerPtg.instance, result, percentFactor());
        }
    }

    private ParseNode percentFactor() {
        ParseNode result = parseSimpleFactor();
        while (true) {
            SkipWhite();
            if (this.look != '%') {
                return result;
            }
            Match('%');
            result = new ParseNode((Ptg) PercentPtg.instance, result);
        }
    }

    private ParseNode parseSimpleFactor() {
        char c;
        SkipWhite();
        char c2 = this.look;
        if (c2 == '\"') {
            return new ParseNode(new StringPtg(parseStringLiteral()));
        }
        if (c2 == '#') {
            return new ParseNode(ErrPtg.valueOf(parseErrorLiteral()));
        }
        if (c2 == '(') {
            Match('(');
            ParseNode inside = comparisonExpression();
            Match(')');
            return new ParseNode((Ptg) ParenthesisPtg.instance, inside);
        } else if (c2 == '+') {
            Match('+');
            return parseUnary(true);
        } else if (c2 == '-') {
            Match('-');
            return parseUnary(false);
        } else if (c2 == '{') {
            Match('{');
            ParseNode arrayNode = parseArray();
            Match('}');
            return arrayNode;
        } else if (IsAlpha(c2) || Character.isDigit(this.look) || (c = this.look) == '\'' || c == '[') {
            return parseRangeExpression();
        } else {
            if (c == '.') {
                return new ParseNode(parseNumber());
            }
            throw expected("cell ref or constant literal");
        }
    }

    private ParseNode parseUnary(boolean isPlus) {
        boolean numberFollows = IsDigit(this.look) || this.look == '.';
        ParseNode factor = powerFactor();
        if (numberFollows) {
            Ptg token = factor.getToken();
            if (token instanceof NumberPtg) {
                if (isPlus) {
                    return factor;
                }
                return new ParseNode(new NumberPtg(-((NumberPtg) token).getValue()));
            } else if (token instanceof IntPtg) {
                if (isPlus) {
                    return factor;
                }
                return new ParseNode(new NumberPtg((double) (-((IntPtg) token).getValue())));
            }
        }
        return new ParseNode((Ptg) isPlus ? UnaryPlusPtg.instance : UnaryMinusPtg.instance, factor);
    }

    private ParseNode parseArray() {
        List<Object[]> rowsData = new ArrayList<>();
        while (true) {
            rowsData.add(parseArrayRow());
            char c = this.look;
            if (c == '}') {
                Object[][] values2d = new Object[rowsData.size()][];
                rowsData.toArray(values2d);
                checkRowLengths(values2d, values2d[0].length);
                return new ParseNode(new ArrayPtg(values2d));
            } else if (c == ';') {
                Match(';');
            } else {
                throw expected("'}' or ';'");
            }
        }
    }

    private void checkRowLengths(Object[][] values2d, int nColumns) {
        int i = 0;
        while (i < values2d.length) {
            int rowLen = values2d[i].length;
            if (rowLen == nColumns) {
                i++;
            } else {
                throw new FormulaParseException("Array row " + i + " has length " + rowLen + " but row 0 has length " + nColumns);
            }
        }
    }

    private Object[] parseArrayRow() {
        char c;
        List<Object> temp = new ArrayList<>();
        while (true) {
            temp.add(parseArrayItem());
            SkipWhite();
            c = this.look;
            if (c != ',') {
                break;
            }
            Match(',');
        }
        if (c == ';' || c == '}') {
            Object[] result = new Object[temp.size()];
            temp.toArray(result);
            return result;
        }
        throw expected("'}' or ','");
    }

    private Object parseArrayItem() {
        SkipWhite();
        char c = this.look;
        if (c == '\"') {
            return parseStringLiteral();
        }
        if (c == '#') {
            return ErrorConstant.valueOf(parseErrorLiteral());
        }
        if (c != '-') {
            return (c == 'F' || c == 'T' || c == 'f' || c == 't') ? parseBooleanLiteral() : convertArrayNumber(parseNumber(), true);
        }
        Match('-');
        SkipWhite();
        return convertArrayNumber(parseNumber(), false);
    }

    private Boolean parseBooleanLiteral() {
        String iden = parseUnquotedIdentifier();
        if ("TRUE".equalsIgnoreCase(iden)) {
            return Boolean.TRUE;
        }
        if ("FALSE".equalsIgnoreCase(iden)) {
            return Boolean.FALSE;
        }
        throw expected("'TRUE' or 'FALSE'");
    }

    private static Double convertArrayNumber(Ptg ptg, boolean isPositive) {
        double value;
        if (ptg instanceof IntPtg) {
            value = (double) ((IntPtg) ptg).getValue();
        } else if (ptg instanceof NumberPtg) {
            value = ((NumberPtg) ptg).getValue();
        } else {
            throw new RuntimeException("Unexpected ptg (" + ptg.getClass().getName() + ")");
        }
        if (!isPositive) {
            value = -value;
        }
        return new Double(value);
    }

    private Ptg parseNumber() {
        String number2 = null;
        String exponent = null;
        String number1 = GetNum();
        if (this.look == '.') {
            GetChar();
            number2 = GetNum();
        }
        if (this.look == 'E') {
            GetChar();
            String sign = "";
            char c = this.look;
            if (c == '+') {
                GetChar();
            } else if (c == '-') {
                GetChar();
                sign = "-";
            }
            String number = GetNum();
            if (number != null) {
                exponent = sign + number;
            } else {
                throw expected("Integer");
            }
        }
        if (number1 != null || number2 != null) {
            return getNumberPtgFromString(number1, number2, exponent);
        }
        throw expected("Integer");
    }

    private int parseErrorLiteral() {
        Match('#');
        String part1 = parseUnquotedIdentifier().toUpperCase();
        if (part1 != null) {
            char charAt = part1.charAt(0);
            if (charAt != 'D') {
                if (charAt != 'N') {
                    if (charAt != 'R') {
                        if (charAt != 'V') {
                            throw expected("#VALUE!, #REF!, #DIV/0!, #NAME?, #NUM!, #NULL! or #N/A");
                        } else if (part1.equals("VALUE")) {
                            Match('!');
                            return 15;
                        } else {
                            throw expected("#VALUE!");
                        }
                    } else if (part1.equals("REF")) {
                        Match('!');
                        return 23;
                    } else {
                        throw expected("#REF!");
                    }
                } else if (part1.equals("NAME")) {
                    Match('?');
                    return 29;
                } else if (part1.equals("NUM")) {
                    Match('!');
                    return 36;
                } else if (part1.equals("NULL")) {
                    Match('!');
                    return 0;
                } else if (part1.equals("N")) {
                    Match('/');
                    char c = this.look;
                    if (c == 'A' || c == 'a') {
                        Match(c);
                        return 42;
                    }
                    throw expected("#N/A");
                } else {
                    throw expected("#NAME?, #NUM!, #NULL! or #N/A");
                }
            } else if (part1.equals("DIV")) {
                Match('/');
                Match('0');
                Match('!');
                return 7;
            } else {
                throw expected("#DIV/0!");
            }
        } else {
            throw expected("remainder of error constant literal");
        }
    }

    private String parseUnquotedIdentifier() {
        if (this.look != '\'') {
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (!Character.isLetterOrDigit(this.look) && this.look != '.') {
                    break;
                }
                sb.append(this.look);
                GetChar();
            }
            if (sb.length() < 1) {
                return null;
            }
            return sb.toString();
        }
        throw expected("unquoted identifier");
    }

    private static Ptg getNumberPtgFromString(String number1, String number2, String exponent) {
        StringBuffer number = new StringBuffer();
        if (number2 == null) {
            number.append(number1);
            if (exponent != null) {
                number.append('E');
                number.append(exponent);
            }
            String numberStr = number.toString();
            try {
                int intVal = Integer.parseInt(numberStr);
                if (IntPtg.isInRange(intVal)) {
                    return new IntPtg(intVal);
                }
                return new NumberPtg(numberStr);
            } catch (NumberFormatException e) {
                return new NumberPtg(numberStr);
            }
        } else {
            if (number1 != null) {
                number.append(number1);
            }
            number.append('.');
            number.append(number2);
            if (exponent != null) {
                number.append('E');
                number.append(exponent);
            }
            return new NumberPtg(number.toString());
        }
    }

    private String parseStringLiteral() {
        Match('\"');
        StringBuffer token = new StringBuffer();
        while (true) {
            if (this.look == '\"') {
                GetChar();
                if (this.look != '\"') {
                    return token.toString();
                }
            }
            token.append(this.look);
            GetChar();
        }
    }

    private ParseNode Term() {
        Ptg operator;
        ParseNode result = powerFactor();
        while (true) {
            SkipWhite();
            char c = this.look;
            if (c == '*') {
                Match('*');
                operator = MultiplyPtg.instance;
            } else if (c != '/') {
                return result;
            } else {
                Match('/');
                operator = DividePtg.instance;
            }
            result = new ParseNode(operator, result, powerFactor());
        }
    }

    private ParseNode unionExpression() {
        ParseNode result = comparisonExpression();
        boolean hasUnions = false;
        while (true) {
            SkipWhite();
            if (this.look != ',') {
                break;
            }
            GetChar();
            hasUnions = true;
            result = new ParseNode(UnionPtg.instance, result, comparisonExpression());
        }
        if (hasUnions) {
            return augmentWithMemPtg(result);
        }
        return result;
    }

    private ParseNode comparisonExpression() {
        ParseNode result = concatExpression();
        while (true) {
            SkipWhite();
            switch (this.look) {
                case '<':
                case '=':
                case '>':
                    result = new ParseNode(getComparisonToken(), result, concatExpression());
                default:
                    return result;
            }
        }
    }

    private Ptg getComparisonToken() {
        char c = this.look;
        if (c == '=') {
            Match(c);
            return EqualPtg.instance;
        }
        boolean isGreater = c == '>';
        Match(c);
        if (!isGreater) {
            char c2 = this.look;
            if (c2 == '=') {
                Match('=');
                return LessEqualPtg.instance;
            } else if (c2 != '>') {
                return LessThanPtg.instance;
            } else {
                Match('>');
                return NotEqualPtg.instance;
            }
        } else if (this.look != '=') {
            return GreaterThanPtg.instance;
        } else {
            Match('=');
            return GreaterEqualPtg.instance;
        }
    }

    private ParseNode concatExpression() {
        ParseNode result = additiveExpression();
        while (true) {
            SkipWhite();
            if (this.look != '&') {
                return result;
            }
            Match('&');
            result = new ParseNode(ConcatPtg.instance, result, additiveExpression());
        }
    }

    private ParseNode additiveExpression() {
        Ptg operator;
        ParseNode result = Term();
        while (true) {
            SkipWhite();
            char c = this.look;
            if (c == '+') {
                Match('+');
                operator = AddPtg.instance;
            } else if (c != '-') {
                return result;
            } else {
                Match('-');
                operator = SubtractPtg.instance;
            }
            result = new ParseNode(operator, result, Term());
        }
    }

    private void parse() {
        this._pointer = 0;
        GetChar();
        this._rootNode = unionExpression();
        if (this._pointer <= this._formulaLength) {
            throw new FormulaParseException("Unused input [" + this._formulaString.substring(this._pointer - 1) + "] after attempting to parse the formula [" + this._formulaString + "]");
        }
    }

    private Ptg[] getRPNPtg(int formulaType) {
        new OperandClassTransformer(formulaType).transformFormula(this._rootNode);
        return ParseNode.toTokenArray(this._rootNode);
    }
}
