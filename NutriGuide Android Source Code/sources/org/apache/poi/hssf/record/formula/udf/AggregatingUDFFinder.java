package org.apache.poi.hssf.record.formula.udf;

import org.apache.poi.hssf.record.formula.functions.FreeRefFunction;

public final class AggregatingUDFFinder implements UDFFinder {
    private final UDFFinder[] _usedToolPacks;

    public AggregatingUDFFinder(UDFFinder... usedToolPacks) {
        this._usedToolPacks = (UDFFinder[]) usedToolPacks.clone();
    }

    public FreeRefFunction findFunction(String name) {
        for (UDFFinder pack : this._usedToolPacks) {
            FreeRefFunction evaluatorForFunction = pack.findFunction(name);
            if (evaluatorForFunction != null) {
                return evaluatorForFunction;
            }
        }
        return null;
    }
}
