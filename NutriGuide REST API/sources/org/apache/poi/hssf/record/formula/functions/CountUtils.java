package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.RefEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.ss.formula.TwoDEval;

final class CountUtils {

    public interface I_MatchPredicate {
        boolean matches(ValueEval valueEval);
    }

    private CountUtils() {
    }

    public static int countMatchingCellsInArea(TwoDEval areaEval, I_MatchPredicate criteriaPredicate) {
        int result = 0;
        int height = areaEval.getHeight();
        int width = areaEval.getWidth();
        for (int rrIx = 0; rrIx < height; rrIx++) {
            for (int rcIx = 0; rcIx < width; rcIx++) {
                if (criteriaPredicate.matches(areaEval.getValue(rrIx, rcIx))) {
                    result++;
                }
            }
        }
        return result;
    }

    public static int countMatchingCell(RefEval refEval, I_MatchPredicate criteriaPredicate) {
        if (criteriaPredicate.matches(refEval.getInnerValueEval())) {
            return 1;
        }
        return 0;
    }

    public static int countArg(ValueEval eval, I_MatchPredicate criteriaPredicate) {
        if (eval == null) {
            throw new IllegalArgumentException("eval must not be null");
        } else if (eval instanceof TwoDEval) {
            return countMatchingCellsInArea((TwoDEval) eval, criteriaPredicate);
        } else {
            if (eval instanceof RefEval) {
                return countMatchingCell((RefEval) eval, criteriaPredicate);
            }
            return criteriaPredicate.matches(eval) ? 1 : 0;
        }
    }
}
