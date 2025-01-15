package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.function.FunctionMetadata;
import org.apache.poi.hssf.record.formula.function.FunctionMetadataRegistry;
import org.apache.poi.hssf.record.formula.functions.AggregateFunction;
import org.apache.poi.hssf.record.formula.functions.BooleanFunction;
import org.apache.poi.hssf.record.formula.functions.CalendarFieldFunction;
import org.apache.poi.hssf.record.formula.functions.Choose;
import org.apache.poi.hssf.record.formula.functions.Column;
import org.apache.poi.hssf.record.formula.functions.Columns;
import org.apache.poi.hssf.record.formula.functions.Count;
import org.apache.poi.hssf.record.formula.functions.Counta;
import org.apache.poi.hssf.record.formula.functions.Countblank;
import org.apache.poi.hssf.record.formula.functions.Countif;
import org.apache.poi.hssf.record.formula.functions.DateFunc;
import org.apache.poi.hssf.record.formula.functions.Days360;
import org.apache.poi.hssf.record.formula.functions.Errortype;
import org.apache.poi.hssf.record.formula.functions.Even;
import org.apache.poi.hssf.record.formula.functions.FinanceFunction;
import org.apache.poi.hssf.record.formula.functions.Function;
import org.apache.poi.hssf.record.formula.functions.Hlookup;
import org.apache.poi.hssf.record.formula.functions.Hyperlink;
import org.apache.poi.hssf.record.formula.functions.IfFunc;
import org.apache.poi.hssf.record.formula.functions.Index;
import org.apache.poi.hssf.record.formula.functions.LogicalFunction;
import org.apache.poi.hssf.record.formula.functions.Lookup;
import org.apache.poi.hssf.record.formula.functions.Match;
import org.apache.poi.hssf.record.formula.functions.MinaMaxa;
import org.apache.poi.hssf.record.formula.functions.Mode;
import org.apache.poi.hssf.record.formula.functions.Na;
import org.apache.poi.hssf.record.formula.functions.NotImplementedFunction;
import org.apache.poi.hssf.record.formula.functions.Now;
import org.apache.poi.hssf.record.formula.functions.Npv;
import org.apache.poi.hssf.record.formula.functions.NumericFunction;
import org.apache.poi.hssf.record.formula.functions.Odd;
import org.apache.poi.hssf.record.formula.functions.Offset;
import org.apache.poi.hssf.record.formula.functions.Replace;
import org.apache.poi.hssf.record.formula.functions.RowFunc;
import org.apache.poi.hssf.record.formula.functions.Rows;
import org.apache.poi.hssf.record.formula.functions.Substitute;
import org.apache.poi.hssf.record.formula.functions.Subtotal;
import org.apache.poi.hssf.record.formula.functions.Sumif;
import org.apache.poi.hssf.record.formula.functions.Sumproduct;
import org.apache.poi.hssf.record.formula.functions.Sumx2my2;
import org.apache.poi.hssf.record.formula.functions.Sumx2py2;
import org.apache.poi.hssf.record.formula.functions.Sumxmy2;
import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.record.formula.functions.TextFunction;
import org.apache.poi.hssf.record.formula.functions.TimeFunc;
import org.apache.poi.hssf.record.formula.functions.Today;
import org.apache.poi.hssf.record.formula.functions.Value;
import org.apache.poi.hssf.record.formula.functions.Vlookup;
import org.apache.poi.ss.formula.eval.NotImplementedException;

public final class FunctionEval {
    private static final FunctionID ID = null;
    protected static final Function[] functions = produceFunctions();

    private static final class FunctionID {
        public static final int CHOOSE = 100;
        public static final int EXTERNAL_FUNC = 255;
        public static final int IF = 1;
        public static final int INDIRECT = 148;
        public static final int OFFSET = 78;
        public static final int SUM = 4;

        private FunctionID() {
        }
    }

    private static Function[] produceFunctions() {
        FunctionMetadata fm;
        Function[] retval = new Function[368];
        retval[0] = new Count();
        retval[1] = new IfFunc();
        retval[2] = LogicalFunction.ISNA;
        retval[3] = LogicalFunction.ISERROR;
        retval[4] = AggregateFunction.SUM;
        retval[5] = AggregateFunction.AVERAGE;
        retval[6] = AggregateFunction.MIN;
        retval[7] = AggregateFunction.MAX;
        retval[8] = new RowFunc();
        retval[9] = new Column();
        retval[10] = new Na();
        retval[11] = new Npv();
        retval[12] = AggregateFunction.STDEV;
        retval[13] = NumericFunction.DOLLAR;
        retval[15] = NumericFunction.SIN;
        retval[16] = NumericFunction.COS;
        retval[17] = NumericFunction.TAN;
        retval[18] = NumericFunction.ATAN;
        retval[19] = NumericFunction.PI;
        retval[20] = NumericFunction.SQRT;
        retval[21] = NumericFunction.EXP;
        retval[22] = NumericFunction.LN;
        retval[23] = NumericFunction.LOG10;
        retval[24] = NumericFunction.ABS;
        retval[25] = NumericFunction.INT;
        retval[26] = NumericFunction.SIGN;
        retval[27] = NumericFunction.ROUND;
        retval[28] = new Lookup();
        retval[29] = new Index();
        retval[31] = TextFunction.MID;
        retval[32] = TextFunction.LEN;
        retval[33] = new Value();
        retval[34] = BooleanFunction.TRUE;
        retval[35] = BooleanFunction.FALSE;
        retval[36] = BooleanFunction.AND;
        retval[37] = BooleanFunction.OR;
        retval[38] = BooleanFunction.NOT;
        retval[39] = NumericFunction.MOD;
        retval[48] = TextFunction.TEXT;
        retval[56] = FinanceFunction.PV;
        retval[57] = FinanceFunction.FV;
        retval[58] = FinanceFunction.NPER;
        retval[59] = FinanceFunction.PMT;
        retval[63] = NumericFunction.RAND;
        retval[64] = new Match();
        retval[65] = DateFunc.instance;
        retval[66] = new TimeFunc();
        retval[67] = CalendarFieldFunction.DAY;
        retval[68] = CalendarFieldFunction.MONTH;
        retval[69] = CalendarFieldFunction.YEAR;
        retval[74] = new Now();
        retval[76] = new Rows();
        retval[77] = new Columns();
        retval[82] = TextFunction.SEARCH;
        retval[78] = new Offset();
        retval[82] = TextFunction.SEARCH;
        retval[97] = NumericFunction.ATAN2;
        retval[98] = NumericFunction.ASIN;
        retval[99] = NumericFunction.ACOS;
        retval[100] = new Choose();
        retval[101] = new Hlookup();
        retval[102] = new Vlookup();
        retval[105] = LogicalFunction.ISREF;
        retval[109] = NumericFunction.LOG;
        retval[112] = TextFunction.LOWER;
        retval[113] = TextFunction.UPPER;
        retval[115] = TextFunction.LEFT;
        retval[116] = TextFunction.RIGHT;
        retval[117] = TextFunction.EXACT;
        retval[118] = TextFunction.TRIM;
        retval[119] = new Replace();
        retval[120] = new Substitute();
        retval[124] = TextFunction.FIND;
        retval[127] = LogicalFunction.ISTEXT;
        retval[128] = LogicalFunction.ISNUMBER;
        retval[129] = LogicalFunction.ISBLANK;
        retval[130] = new T();
        retval[148] = null;
        retval[169] = new Counta();
        retval[183] = AggregateFunction.PRODUCT;
        retval[184] = NumericFunction.FACT;
        retval[190] = LogicalFunction.ISNONTEXT;
        retval[197] = NumericFunction.TRUNC;
        retval[198] = LogicalFunction.ISLOGICAL;
        retval[212] = NumericFunction.ROUNDUP;
        retval[213] = NumericFunction.ROUNDDOWN;
        retval[220] = new Days360();
        retval[221] = new Today();
        retval[227] = AggregateFunction.MEDIAN;
        retval[228] = new Sumproduct();
        retval[229] = NumericFunction.SINH;
        retval[230] = NumericFunction.COSH;
        retval[231] = NumericFunction.TANH;
        retval[232] = NumericFunction.ASINH;
        retval[233] = NumericFunction.ACOSH;
        retval[234] = NumericFunction.ATANH;
        retval[255] = null;
        retval[261] = new Errortype();
        retval[269] = AggregateFunction.AVEDEV;
        retval[276] = NumericFunction.COMBIN;
        retval[279] = new Even();
        retval[285] = NumericFunction.FLOOR;
        retval[288] = NumericFunction.CEILING;
        retval[298] = new Odd();
        retval[300] = NumericFunction.POISSON;
        retval[303] = new Sumxmy2();
        retval[304] = new Sumx2my2();
        retval[305] = new Sumx2py2();
        retval[318] = AggregateFunction.DEVSQ;
        retval[321] = AggregateFunction.SUMSQ;
        retval[325] = AggregateFunction.LARGE;
        retval[326] = AggregateFunction.SMALL;
        retval[330] = new Mode();
        retval[336] = TextFunction.CONCATENATE;
        retval[337] = NumericFunction.POWER;
        retval[342] = NumericFunction.RADIANS;
        retval[343] = NumericFunction.DEGREES;
        retval[344] = new Subtotal();
        retval[345] = new Sumif();
        retval[346] = new Countif();
        retval[347] = new Countblank();
        retval[359] = new Hyperlink();
        retval[362] = MinaMaxa.MAXA;
        retval[363] = MinaMaxa.MINA;
        for (int i = 0; i < retval.length; i++) {
            if (retval[i] == null && (fm = FunctionMetadataRegistry.getFunctionByIndex(i)) != null) {
                retval[i] = new NotImplementedFunction(fm.getName());
            }
        }
        return retval;
    }

    public static Function getBasicFunction(int functionIndex) {
        if (functionIndex == 148 || functionIndex == 255) {
            return null;
        }
        Function result = functions[functionIndex];
        if (result != null) {
            return result;
        }
        throw new NotImplementedException("FuncIx=" + functionIndex);
    }
}
