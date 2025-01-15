package org.apache.poi.ss.formula;

import org.apache.poi.hssf.record.formula.ArrayPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.FuncVarPtg;
import org.apache.poi.hssf.record.formula.MemAreaPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.Ptg;

final class ParseNode {
    public static final ParseNode[] EMPTY_ARRAY = new ParseNode[0];
    private final ParseNode[] _children;
    private boolean _isIf;
    private final Ptg _token;
    private final int _tokenCount;

    public ParseNode(Ptg token, ParseNode[] children) {
        if (token != null) {
            this._token = token;
            this._children = children;
            this._isIf = isIf(token);
            int tokenCount = 1;
            for (ParseNode tokenCount2 : children) {
                tokenCount += tokenCount2.getTokenCount();
            }
            this._tokenCount = this._isIf != 0 ? tokenCount + children.length : tokenCount;
            return;
        }
        throw new IllegalArgumentException("token must not be null");
    }

    public ParseNode(Ptg token) {
        this(token, EMPTY_ARRAY);
    }

    public ParseNode(Ptg token, ParseNode child0) {
        this(token, new ParseNode[]{child0});
    }

    public ParseNode(Ptg token, ParseNode child0, ParseNode child1) {
        this(token, new ParseNode[]{child0, child1});
    }

    private int getTokenCount() {
        return this._tokenCount;
    }

    public int getEncodedSize() {
        Ptg ptg = this._token;
        int result = ptg instanceof ArrayPtg ? 8 : ptg.getSize();
        int i = 0;
        while (true) {
            ParseNode[] parseNodeArr = this._children;
            if (i >= parseNodeArr.length) {
                return result;
            }
            result += parseNodeArr[i].getEncodedSize();
            i++;
        }
    }

    public static Ptg[] toTokenArray(ParseNode rootNode) {
        TokenCollector temp = new TokenCollector(rootNode.getTokenCount());
        rootNode.collectPtgs(temp);
        return temp.getResult();
    }

    private void collectPtgs(TokenCollector temp) {
        if (isIf(this._token)) {
            collectIfPtgs(temp);
            return;
        }
        Ptg ptg = this._token;
        boolean isPreFixOperator = (ptg instanceof MemFuncPtg) || (ptg instanceof MemAreaPtg);
        if (isPreFixOperator) {
            temp.add(ptg);
        }
        for (ParseNode collectPtgs : getChildren()) {
            collectPtgs.collectPtgs(temp);
        }
        if (!isPreFixOperator) {
            temp.add(this._token);
        }
    }

    private void collectIfPtgs(TokenCollector temp) {
        getChildren()[0].collectPtgs(temp);
        int ifAttrIndex = temp.createPlaceholder();
        getChildren()[1].collectPtgs(temp);
        int skipAfterTrueParamIndex = temp.createPlaceholder();
        AttrPtg attrIf = AttrPtg.createIf(temp.sumTokenSizes(ifAttrIndex + 1, skipAfterTrueParamIndex) + 4);
        if (getChildren().length > 2) {
            getChildren()[2].collectPtgs(temp);
            int skipAfterFalseParamIndex = temp.createPlaceholder();
            AttrPtg attrSkipAfterTrue = AttrPtg.createSkip(((temp.sumTokenSizes(skipAfterTrueParamIndex + 1, skipAfterFalseParamIndex) + 4) + 4) - 1);
            AttrPtg attrSkipAfterFalse = AttrPtg.createSkip(3);
            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue);
            temp.setPlaceholder(skipAfterFalseParamIndex, attrSkipAfterFalse);
        } else {
            AttrPtg attrSkipAfterTrue2 = AttrPtg.createSkip(3);
            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue2);
        }
        temp.add(this._token);
    }

    private static boolean isIf(Ptg token) {
        if (!(token instanceof FuncVarPtg) || !"IF".equals(((FuncVarPtg) token).getName())) {
            return false;
        }
        return true;
    }

    public Ptg getToken() {
        return this._token;
    }

    public ParseNode[] getChildren() {
        return this._children;
    }

    private static final class TokenCollector {
        private int _offset = 0;
        private final Ptg[] _ptgs;

        public TokenCollector(int tokenCount) {
            this._ptgs = new Ptg[tokenCount];
        }

        public int sumTokenSizes(int fromIx, int toIx) {
            int result = 0;
            for (int i = fromIx; i < toIx; i++) {
                result += this._ptgs[i].getSize();
            }
            return result;
        }

        public int createPlaceholder() {
            int i = this._offset;
            this._offset = i + 1;
            return i;
        }

        public void add(Ptg token) {
            if (token != null) {
                Ptg[] ptgArr = this._ptgs;
                int i = this._offset;
                ptgArr[i] = token;
                this._offset = i + 1;
                return;
            }
            throw new IllegalArgumentException("token must not be null");
        }

        public void setPlaceholder(int index, Ptg token) {
            Ptg[] ptgArr = this._ptgs;
            if (ptgArr[index] == null) {
                ptgArr[index] = token;
                return;
            }
            throw new IllegalStateException("Invalid placeholder index (" + index + ")");
        }

        public Ptg[] getResult() {
            return this._ptgs;
        }
    }
}
