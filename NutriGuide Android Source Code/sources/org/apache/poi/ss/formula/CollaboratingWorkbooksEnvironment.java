package org.apache.poi.ss.formula;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class CollaboratingWorkbooksEnvironment {
    public static final CollaboratingWorkbooksEnvironment EMPTY = new CollaboratingWorkbooksEnvironment();
    private final WorkbookEvaluator[] _evaluators;
    private final Map<String, WorkbookEvaluator> _evaluatorsByName;
    private boolean _unhooked;

    public static final class WorkbookNotFoundException extends Exception {
        WorkbookNotFoundException(String msg) {
            super(msg);
        }
    }

    private CollaboratingWorkbooksEnvironment() {
        this._evaluatorsByName = Collections.emptyMap();
        this._evaluators = new WorkbookEvaluator[0];
    }

    public static void setup(String[] workbookNames, WorkbookEvaluator[] evaluators) {
        int nItems = workbookNames.length;
        if (evaluators.length != nItems) {
            throw new IllegalArgumentException("Number of workbook names is " + nItems + " but number of evaluators is " + evaluators.length);
        } else if (nItems >= 1) {
            new CollaboratingWorkbooksEnvironment(workbookNames, evaluators, nItems);
        } else {
            throw new IllegalArgumentException("Must provide at least one collaborating worbook");
        }
    }

    private CollaboratingWorkbooksEnvironment(String[] workbookNames, WorkbookEvaluator[] evaluators, int nItems) {
        Map<String, WorkbookEvaluator> m = new HashMap<>((nItems * 3) / 2);
        IdentityHashMap<WorkbookEvaluator, String> uniqueEvals = new IdentityHashMap<>((nItems * 3) / 2);
        int i = 0;
        while (i < nItems) {
            String wbName = workbookNames[i];
            WorkbookEvaluator wbEval = evaluators[i];
            if (m.containsKey(wbName)) {
                throw new IllegalArgumentException("Duplicate workbook name '" + wbName + "'");
            } else if (!uniqueEvals.containsKey(wbEval)) {
                uniqueEvals.put(wbEval, wbName);
                m.put(wbName, wbEval);
                i++;
            } else {
                throw new IllegalArgumentException("Attempted to register same workbook under names '" + uniqueEvals.get(wbEval) + "' and '" + wbName + "'");
            }
        }
        unhookOldEnvironments(evaluators);
        hookNewEnvironment(evaluators, this);
        this._unhooked = false;
        this._evaluators = evaluators;
        this._evaluatorsByName = m;
    }

    private static void hookNewEnvironment(WorkbookEvaluator[] evaluators, CollaboratingWorkbooksEnvironment env) {
        int nItems = evaluators.length;
        IEvaluationListener evalListener = evaluators[0].getEvaluationListener();
        int i = 0;
        while (i < nItems) {
            if (evalListener == evaluators[i].getEvaluationListener()) {
                i++;
            } else {
                throw new RuntimeException("Workbook evaluators must all have the same evaluation listener");
            }
        }
        EvaluationCache cache = new EvaluationCache(evalListener);
        for (int i2 = 0; i2 < nItems; i2++) {
            evaluators[i2].attachToEnvironment(env, cache, i2);
        }
    }

    private void unhookOldEnvironments(WorkbookEvaluator[] evaluators) {
        Set<CollaboratingWorkbooksEnvironment> oldEnvs = new HashSet<>();
        for (WorkbookEvaluator environment : evaluators) {
            oldEnvs.add(environment.getEnvironment());
        }
        CollaboratingWorkbooksEnvironment[] oldCWEs = new CollaboratingWorkbooksEnvironment[oldEnvs.size()];
        oldEnvs.toArray(oldCWEs);
        for (CollaboratingWorkbooksEnvironment unhook : oldCWEs) {
            unhook.unhook();
        }
    }

    private void unhook() {
        if (this._evaluators.length >= 1) {
            int i = 0;
            while (true) {
                WorkbookEvaluator[] workbookEvaluatorArr = this._evaluators;
                if (i < workbookEvaluatorArr.length) {
                    workbookEvaluatorArr[i].detachFromEnvironment();
                    i++;
                } else {
                    this._unhooked = true;
                    return;
                }
            }
        }
    }

    public WorkbookEvaluator getWorkbookEvaluator(String workbookName) throws WorkbookNotFoundException {
        if (!this._unhooked) {
            WorkbookEvaluator result = this._evaluatorsByName.get(workbookName);
            if (result != null) {
                return result;
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("Could not resolve external workbook name '");
            sb.append(workbookName);
            sb.append("'.");
            if (this._evaluators.length >= 1) {
                sb.append(" The following workbook names are valid: (");
                int count = 0;
                for (String append : this._evaluatorsByName.keySet()) {
                    int count2 = count + 1;
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append("'");
                    sb.append(append);
                    sb.append("'");
                    count = count2;
                }
                sb.append(")");
            } else {
                sb.append(" Workbook environment has not been set up.");
            }
            throw new WorkbookNotFoundException(sb.toString());
        }
        throw new IllegalStateException("This environment has been unhooked");
    }
}
