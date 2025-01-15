package org.apache.poi.hssf.record.formula;

public final class FormulaShifter {
    private final int _amountToMove;
    private final int _externSheetIndex;
    private final int _firstMovedIndex;
    private final int _lastMovedIndex;

    private FormulaShifter(int externSheetIndex, int firstMovedIndex, int lastMovedIndex, int amountToMove) {
        if (amountToMove == 0) {
            throw new IllegalArgumentException("amountToMove must not be zero");
        } else if (firstMovedIndex <= lastMovedIndex) {
            this._externSheetIndex = externSheetIndex;
            this._firstMovedIndex = firstMovedIndex;
            this._lastMovedIndex = lastMovedIndex;
            this._amountToMove = amountToMove;
        } else {
            throw new IllegalArgumentException("firstMovedIndex, lastMovedIndex out of order");
        }
    }

    public static FormulaShifter createForRowShift(int externSheetIndex, int firstMovedRowIndex, int lastMovedRowIndex, int numberOfRowsToMove) {
        return new FormulaShifter(externSheetIndex, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(this._firstMovedIndex);
        sb.append(this._lastMovedIndex);
        sb.append(this._amountToMove);
        return sb.toString();
    }

    public boolean adjustFormula(Ptg[] ptgs, int currentExternSheetIx) {
        boolean refsWereChanged = false;
        for (int i = 0; i < ptgs.length; i++) {
            Ptg newPtg = adjustPtg(ptgs[i], currentExternSheetIx);
            if (newPtg != null) {
                refsWereChanged = true;
                ptgs[i] = newPtg;
            }
        }
        return refsWereChanged;
    }

    private Ptg adjustPtg(Ptg ptg, int currentExternSheetIx) {
        return adjustPtgDueToRowMove(ptg, currentExternSheetIx);
    }

    private Ptg adjustPtgDueToRowMove(Ptg ptg, int currentExternSheetIx) {
        if (ptg instanceof RefPtg) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return null;
            }
            return rowMoveRefPtg((RefPtg) ptg);
        } else if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg = (Ref3DPtg) ptg;
            if (this._externSheetIndex != rptg.getExternSheetIndex()) {
                return null;
            }
            return rowMoveRefPtg(rptg);
        } else if (ptg instanceof Area2DPtgBase) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return ptg;
            }
            return rowMoveAreaPtg((Area2DPtgBase) ptg);
        } else if (!(ptg instanceof Area3DPtg)) {
            return null;
        } else {
            Area3DPtg aptg = (Area3DPtg) ptg;
            if (this._externSheetIndex != aptg.getExternSheetIndex()) {
                return null;
            }
            return rowMoveAreaPtg(aptg);
        }
    }

    private Ptg rowMoveRefPtg(RefPtgBase rptg) {
        int refRow = rptg.getRow();
        int i = this._firstMovedIndex;
        if (i > refRow || refRow > this._lastMovedIndex) {
            int i2 = this._amountToMove;
            int destFirstRowIndex = i + i2;
            int destLastRowIndex = this._lastMovedIndex + i2;
            if (destLastRowIndex < refRow || refRow < destFirstRowIndex) {
                return null;
            }
            if (destFirstRowIndex <= refRow && refRow <= destLastRowIndex) {
                return createDeletedRef(rptg);
            }
            throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + refRow + ", " + refRow + ")");
        }
        rptg.setRow(this._amountToMove + refRow);
        return rptg;
    }

    private Ptg rowMoveAreaPtg(AreaPtgBase aptg) {
        int aFirstRow = aptg.getFirstRow();
        int aLastRow = aptg.getLastRow();
        int i = this._firstMovedIndex;
        if (i > aFirstRow || aLastRow > this._lastMovedIndex) {
            int i2 = this._amountToMove;
            int destFirstRowIndex = i + i2;
            int i3 = this._lastMovedIndex;
            int destLastRowIndex = i3 + i2;
            if (aFirstRow >= i || i3 >= aLastRow) {
                if (i > aFirstRow || aFirstRow > i3) {
                    if (i > aLastRow || aLastRow > i3) {
                        if (destLastRowIndex < aFirstRow || aLastRow < destFirstRowIndex) {
                            return null;
                        }
                        if (destFirstRowIndex <= aFirstRow && aLastRow <= destLastRowIndex) {
                            return createDeletedRef(aptg);
                        }
                        if (aFirstRow <= destFirstRowIndex && destLastRowIndex <= aLastRow) {
                            return null;
                        }
                        if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                            aptg.setFirstRow(destLastRowIndex + 1);
                            return aptg;
                        } else if (destFirstRowIndex >= aLastRow || aLastRow > destLastRowIndex) {
                            throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + aFirstRow + ", " + aLastRow + ")");
                        } else {
                            aptg.setLastRow(destFirstRowIndex - 1);
                            return aptg;
                        }
                    } else if (i2 > 0) {
                        aptg.setLastRow(i2 + aLastRow);
                        return aptg;
                    } else if (destLastRowIndex < aFirstRow) {
                        return null;
                    } else {
                        int newLastRowIx = i2 + aLastRow;
                        if (destFirstRowIndex > aFirstRow) {
                            aptg.setLastRow(newLastRowIx);
                            return aptg;
                        }
                        int areaRemainingBottomRowIx = i - 1;
                        if (destLastRowIndex < areaRemainingBottomRowIx) {
                            newLastRowIx = areaRemainingBottomRowIx;
                        }
                        aptg.setFirstRow(Math.min(aFirstRow, destFirstRowIndex));
                        aptg.setLastRow(newLastRowIx);
                        return aptg;
                    }
                } else if (i2 < 0) {
                    aptg.setFirstRow(i2 + aFirstRow);
                    return aptg;
                } else if (destFirstRowIndex > aLastRow) {
                    return null;
                } else {
                    int newFirstRowIx = i2 + aFirstRow;
                    if (destLastRowIndex < aLastRow) {
                        aptg.setFirstRow(newFirstRowIx);
                        return aptg;
                    }
                    int areaRemainingTopRowIx = i3 + 1;
                    if (destFirstRowIndex > areaRemainingTopRowIx) {
                        newFirstRowIx = areaRemainingTopRowIx;
                    }
                    aptg.setFirstRow(newFirstRowIx);
                    aptg.setLastRow(Math.max(aLastRow, destLastRowIndex));
                    return aptg;
                }
            } else if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            } else if (destFirstRowIndex > aLastRow || aLastRow >= destLastRowIndex) {
                return null;
            } else {
                aptg.setLastRow(destFirstRowIndex - 1);
                return aptg;
            }
        } else {
            aptg.setFirstRow(this._amountToMove + aFirstRow);
            aptg.setLastRow(this._amountToMove + aLastRow);
            return aptg;
        }
    }

    private static Ptg createDeletedRef(Ptg ptg) {
        if (ptg instanceof RefPtg) {
            return new RefErrorPtg();
        }
        if (ptg instanceof Ref3DPtg) {
            return new DeletedRef3DPtg(((Ref3DPtg) ptg).getExternSheetIndex());
        }
        if (ptg instanceof AreaPtg) {
            return new AreaErrPtg();
        }
        if (ptg instanceof Area3DPtg) {
            return new DeletedArea3DPtg(((Area3DPtg) ptg).getExternSheetIndex());
        }
        throw new IllegalArgumentException("Unexpected ref ptg class (" + ptg.getClass().getName() + ")");
    }
}
