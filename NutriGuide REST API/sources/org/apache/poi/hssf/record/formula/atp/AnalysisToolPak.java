package org.apache.poi.hssf.record.formula.atp;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;
import org.apache.poi.hssf.record.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.NotImplementedException;

public final class AnalysisToolPak implements UDFFinder {
    public static final UDFFinder instance = new AnalysisToolPak();
    private final Map<String, FreeRefFunction> _functionsByName = createFunctionsMap();

    private static final class NotImplemented implements FreeRefFunction {
        private final String _functionName;

        public NotImplemented(String functionName) {
            this._functionName = functionName;
        }

        public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
            throw new NotImplementedException(this._functionName);
        }
    }

    private AnalysisToolPak() {
    }

    public FreeRefFunction findFunction(String name) {
        return this._functionsByName.get(name);
    }

    private Map<String, FreeRefFunction> createFunctionsMap() {
        Map<String, FreeRefFunction> m = new HashMap<>(100);
        r(m, "ACCRINT", (FreeRefFunction) null);
        r(m, "ACCRINTM", (FreeRefFunction) null);
        r(m, "AMORDEGRC", (FreeRefFunction) null);
        r(m, "AMORLINC", (FreeRefFunction) null);
        r(m, "BESSELI", (FreeRefFunction) null);
        r(m, "BESSELJ", (FreeRefFunction) null);
        r(m, "BESSELK", (FreeRefFunction) null);
        r(m, "BESSELY", (FreeRefFunction) null);
        r(m, "BIN2DEC", (FreeRefFunction) null);
        r(m, "BIN2HEX", (FreeRefFunction) null);
        r(m, "BIN2OCT", (FreeRefFunction) null);
        r(m, "CO MPLEX", (FreeRefFunction) null);
        r(m, "CONVERT", (FreeRefFunction) null);
        r(m, "COUPDAYBS", (FreeRefFunction) null);
        r(m, "COUPDAYS", (FreeRefFunction) null);
        r(m, "COUPDAYSNC", (FreeRefFunction) null);
        r(m, "COUPNCD", (FreeRefFunction) null);
        r(m, "COUPNUM", (FreeRefFunction) null);
        r(m, "COUPPCD", (FreeRefFunction) null);
        r(m, "CUMIPMT", (FreeRefFunction) null);
        r(m, "CUMPRINC", (FreeRefFunction) null);
        r(m, "DEC2BIN", (FreeRefFunction) null);
        r(m, "DEC2HEX", (FreeRefFunction) null);
        r(m, "DEC2OCT", (FreeRefFunction) null);
        r(m, "DELTA", (FreeRefFunction) null);
        r(m, "DISC", (FreeRefFunction) null);
        r(m, "DOLLARDE", (FreeRefFunction) null);
        r(m, "DOLLARFR", (FreeRefFunction) null);
        r(m, "DURATION", (FreeRefFunction) null);
        r(m, "EDATE", (FreeRefFunction) null);
        r(m, "EFFECT", (FreeRefFunction) null);
        r(m, "EOMONTH", (FreeRefFunction) null);
        r(m, "ERF", (FreeRefFunction) null);
        r(m, "ERFC", (FreeRefFunction) null);
        r(m, "FACTDOUBLE", (FreeRefFunction) null);
        r(m, "FVSCHEDULE", (FreeRefFunction) null);
        r(m, "GCD", (FreeRefFunction) null);
        r(m, "GESTEP", (FreeRefFunction) null);
        r(m, "HEX2BIN", (FreeRefFunction) null);
        r(m, "HEX2DEC", (FreeRefFunction) null);
        r(m, "HEX2OCT", (FreeRefFunction) null);
        r(m, "IMABS", (FreeRefFunction) null);
        r(m, "IMAGINARY", (FreeRefFunction) null);
        r(m, "IMARGUMENT", (FreeRefFunction) null);
        r(m, "IMCONJUGATE", (FreeRefFunction) null);
        r(m, "IMCOS", (FreeRefFunction) null);
        r(m, "IMDIV", (FreeRefFunction) null);
        r(m, "IMEXP", (FreeRefFunction) null);
        r(m, "IMLN", (FreeRefFunction) null);
        r(m, "IMLOG10", (FreeRefFunction) null);
        r(m, "IMLOG2", (FreeRefFunction) null);
        r(m, "IMPOWER", (FreeRefFunction) null);
        r(m, "IMPRODUCT", (FreeRefFunction) null);
        r(m, "IMREAL", (FreeRefFunction) null);
        r(m, "IMSIN", (FreeRefFunction) null);
        r(m, "IMSQRT", (FreeRefFunction) null);
        r(m, "IMSUB", (FreeRefFunction) null);
        r(m, "IMSUM", (FreeRefFunction) null);
        r(m, "INTRATE", (FreeRefFunction) null);
        r(m, "ISEVEN", ParityFunction.IS_EVEN);
        r(m, "ISODD", ParityFunction.IS_ODD);
        r(m, "LCM", (FreeRefFunction) null);
        r(m, "MDURATION", (FreeRefFunction) null);
        r(m, "MROUND", (FreeRefFunction) null);
        r(m, "MULTINOMIAL", (FreeRefFunction) null);
        r(m, "NETWORKDAYS", (FreeRefFunction) null);
        r(m, "NOMINAL", (FreeRefFunction) null);
        r(m, "OCT2BIN", (FreeRefFunction) null);
        r(m, "OCT2DEC", (FreeRefFunction) null);
        r(m, "OCT2HEX", (FreeRefFunction) null);
        r(m, "ODDFPRICE", (FreeRefFunction) null);
        r(m, "ODDFYIELD", (FreeRefFunction) null);
        r(m, "ODDLPRICE", (FreeRefFunction) null);
        r(m, "ODDLYIELD", (FreeRefFunction) null);
        r(m, "PRICE", (FreeRefFunction) null);
        r(m, "PRICEDISC", (FreeRefFunction) null);
        r(m, "PRICEMAT", (FreeRefFunction) null);
        r(m, "QUOTIENT", (FreeRefFunction) null);
        r(m, "RANDBETWEEN", RandBetween.instance);
        r(m, "RECEIVED", (FreeRefFunction) null);
        r(m, "SERIESSUM", (FreeRefFunction) null);
        r(m, "SQRTPI", (FreeRefFunction) null);
        r(m, "TBILLEQ", (FreeRefFunction) null);
        r(m, "TBILLPRICE", (FreeRefFunction) null);
        r(m, "TBILLYIELD", (FreeRefFunction) null);
        r(m, "WEEKNUM", (FreeRefFunction) null);
        r(m, "WORKDAY", (FreeRefFunction) null);
        r(m, "XIRR", (FreeRefFunction) null);
        r(m, "XNPV", (FreeRefFunction) null);
        r(m, "YEARFRAC", YearFrac.instance);
        r(m, "YIELD", (FreeRefFunction) null);
        r(m, "YIELDDISC", (FreeRefFunction) null);
        r(m, "YIELDMAT", (FreeRefFunction) null);
        return m;
    }

    private static void r(Map<String, FreeRefFunction> m, String functionName, FreeRefFunction pFunc) {
        m.put(functionName, pFunc == null ? new NotImplemented(functionName) : pFunc);
    }
}
