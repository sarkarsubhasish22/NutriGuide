package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;

public final class ColumnInfoRecordsAggregate extends RecordAggregate {
    private final List records;

    private static final class CIRComparator implements Comparator {
        public static final Comparator instance = new CIRComparator();

        private CIRComparator() {
        }

        public int compare(Object a, Object b) {
            return compareColInfos((ColumnInfoRecord) a, (ColumnInfoRecord) b);
        }

        public static int compareColInfos(ColumnInfoRecord a, ColumnInfoRecord b) {
            return a.getFirstColumn() - b.getFirstColumn();
        }
    }

    public ColumnInfoRecordsAggregate() {
        this.records = new ArrayList();
    }

    public ColumnInfoRecordsAggregate(RecordStream rs) {
        this();
        boolean isInOrder = true;
        ColumnInfoRecord cirPrev = null;
        while (rs.peekNextClass() == ColumnInfoRecord.class) {
            ColumnInfoRecord cir = (ColumnInfoRecord) rs.getNext();
            this.records.add(cir);
            if (cirPrev != null && CIRComparator.compareColInfos(cirPrev, cir) > 0) {
                isInOrder = false;
            }
            cirPrev = cir;
        }
        if (this.records.size() < 1) {
            throw new RuntimeException("No column info records found");
        } else if (!isInOrder) {
            Collections.sort(this.records, CIRComparator.instance);
        }
    }

    public Object clone() {
        ColumnInfoRecordsAggregate rec = new ColumnInfoRecordsAggregate();
        for (int k = 0; k < this.records.size(); k++) {
            rec.records.add(((ColumnInfoRecord) this.records.get(k)).clone());
        }
        return rec;
    }

    public void insertColumn(ColumnInfoRecord col) {
        this.records.add(col);
        Collections.sort(this.records, CIRComparator.instance);
    }

    private void insertColumn(int idx, ColumnInfoRecord col) {
        this.records.add(idx, col);
    }

    /* access modifiers changed from: package-private */
    public int getNumColumns() {
        return this.records.size();
    }

    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        int nItems = this.records.size();
        if (nItems >= 1) {
            ColumnInfoRecord cirPrev = null;
            int i = 0;
            while (i < nItems) {
                ColumnInfoRecord cir = (ColumnInfoRecord) this.records.get(i);
                rv.visitRecord(cir);
                if (cirPrev == null || CIRComparator.compareColInfos(cirPrev, cir) <= 0) {
                    cirPrev = cir;
                    i++;
                } else {
                    throw new RuntimeException("Column info records are out of order");
                }
            }
        }
    }

    private int findStartOfColumnOutlineGroup(int pIdx) {
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) this.records.get(pIdx);
        int level = columnInfo.getOutlineLevel();
        int idx = pIdx;
        while (idx != 0) {
            ColumnInfoRecord prevColumnInfo = (ColumnInfoRecord) this.records.get(idx - 1);
            if (!prevColumnInfo.isAdjacentBefore(columnInfo) || prevColumnInfo.getOutlineLevel() < level) {
                break;
            }
            idx--;
            columnInfo = prevColumnInfo;
        }
        return idx;
    }

    private int findEndOfColumnOutlineGroup(int colInfoIndex) {
        ColumnInfoRecord columnInfo = (ColumnInfoRecord) this.records.get(colInfoIndex);
        int level = columnInfo.getOutlineLevel();
        int idx = colInfoIndex;
        while (idx < this.records.size() - 1) {
            ColumnInfoRecord nextColumnInfo = (ColumnInfoRecord) this.records.get(idx + 1);
            if (!columnInfo.isAdjacentBefore(nextColumnInfo) || nextColumnInfo.getOutlineLevel() < level) {
                break;
            }
            idx++;
            columnInfo = nextColumnInfo;
        }
        return idx;
    }

    private ColumnInfoRecord getColInfo(int idx) {
        return (ColumnInfoRecord) this.records.get(idx);
    }

    private boolean isColumnGroupCollapsed(int idx) {
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup(idx);
        int nextColInfoIx = endOfOutlineGroupIdx + 1;
        if (nextColInfoIx >= this.records.size()) {
            return false;
        }
        ColumnInfoRecord nextColInfo = getColInfo(nextColInfoIx);
        if (!getColInfo(endOfOutlineGroupIdx).isAdjacentBefore(nextColInfo)) {
            return false;
        }
        return nextColInfo.getCollapsed();
    }

    private boolean isColumnGroupHiddenByParent(int idx) {
        int endLevel = 0;
        boolean endHidden = false;
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup(idx);
        if (endOfOutlineGroupIdx < this.records.size()) {
            ColumnInfoRecord nextInfo = getColInfo(endOfOutlineGroupIdx + 1);
            if (getColInfo(endOfOutlineGroupIdx).isAdjacentBefore(nextInfo)) {
                endLevel = nextInfo.getOutlineLevel();
                endHidden = nextInfo.getHidden();
            }
        }
        int startLevel = 0;
        boolean startHidden = false;
        int startOfOutlineGroupIdx = findStartOfColumnOutlineGroup(idx);
        if (startOfOutlineGroupIdx > 0) {
            ColumnInfoRecord prevInfo = getColInfo(startOfOutlineGroupIdx - 1);
            if (prevInfo.isAdjacentBefore(getColInfo(startOfOutlineGroupIdx))) {
                startLevel = prevInfo.getOutlineLevel();
                startHidden = prevInfo.getHidden();
            }
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }

    public void collapseColumn(int columnIndex) {
        int colInfoIx = findColInfoIdx(columnIndex, 0);
        if (colInfoIx != -1) {
            int groupStartColInfoIx = findStartOfColumnOutlineGroup(colInfoIx);
            setColumn(setGroupHidden(groupStartColInfoIx, getColInfo(groupStartColInfoIx).getOutlineLevel(), true) + 1, (Short) null, (Integer) null, (Integer) null, (Boolean) null, Boolean.TRUE);
        }
    }

    private int setGroupHidden(int pIdx, int level, boolean hidden) {
        int idx = pIdx;
        ColumnInfoRecord columnInfo = getColInfo(idx);
        while (idx < this.records.size()) {
            columnInfo.setHidden(hidden);
            if (idx + 1 < this.records.size()) {
                ColumnInfoRecord nextColumnInfo = getColInfo(idx + 1);
                if (!columnInfo.isAdjacentBefore(nextColumnInfo) || nextColumnInfo.getOutlineLevel() < level) {
                    break;
                }
                columnInfo = nextColumnInfo;
            }
            idx++;
        }
        return columnInfo.getLastColumn();
    }

    public void expandColumn(int columnIndex) {
        int idx = findColInfoIdx(columnIndex, 0);
        if (idx != -1 && isColumnGroupCollapsed(idx)) {
            int startIdx = findStartOfColumnOutlineGroup(idx);
            int endIdx = findEndOfColumnOutlineGroup(idx);
            ColumnInfoRecord columnInfo = getColInfo(endIdx);
            if (!isColumnGroupHiddenByParent(idx)) {
                int outlineLevel = columnInfo.getOutlineLevel();
                for (int i = startIdx; i <= endIdx; i++) {
                    ColumnInfoRecord ci = getColInfo(i);
                    if (outlineLevel == ci.getOutlineLevel()) {
                        ci.setHidden(false);
                    }
                }
            }
            setColumn(columnInfo.getLastColumn() + 1, (Short) null, (Integer) null, (Integer) null, (Boolean) null, Boolean.FALSE);
        }
    }

    private static ColumnInfoRecord copyColInfo(ColumnInfoRecord ci) {
        return (ColumnInfoRecord) ci.clone();
    }

    public void setColumn(int targetColumnIx, Short xfIndex, Integer width, Integer level, Boolean hidden, Boolean collapsed) {
        int i = targetColumnIx;
        ColumnInfoRecord ci = null;
        int k = 0;
        while (true) {
            if (k >= this.records.size()) {
                break;
            }
            ColumnInfoRecord tci = (ColumnInfoRecord) this.records.get(k);
            if (tci.containsColumn(i)) {
                ci = tci;
                break;
            } else if (tci.getFirstColumn() > i) {
                break;
            } else {
                k++;
            }
        }
        if (ci == null) {
            ColumnInfoRecord nci = new ColumnInfoRecord();
            nci.setFirstColumn(i);
            nci.setLastColumn(i);
            setColumnInfoFields(nci, xfIndex, width, level, hidden, collapsed);
            insertColumn(k, nci);
            attemptMergeColInfoRecords(k);
            return;
        }
        boolean columnChanged = false;
        boolean styleChanged = (xfIndex == null || ci.getXFIndex() == xfIndex.shortValue()) ? false : true;
        boolean widthChanged = (width == null || ci.getColumnWidth() == width.shortValue()) ? false : true;
        boolean levelChanged = (level == null || ci.getOutlineLevel() == level.intValue()) ? false : true;
        boolean hiddenChanged = (hidden == null || ci.getHidden() == hidden.booleanValue()) ? false : true;
        boolean collapsedChanged = (collapsed == null || ci.getCollapsed() == collapsed.booleanValue()) ? false : true;
        if (styleChanged || widthChanged || levelChanged || hiddenChanged || collapsedChanged) {
            columnChanged = true;
        }
        if (columnChanged) {
            if (ci.getFirstColumn() == i && ci.getLastColumn() == i) {
                setColumnInfoFields(ci, xfIndex, width, level, hidden, collapsed);
                attemptMergeColInfoRecords(k);
            } else if (ci.getFirstColumn() == i || ci.getLastColumn() == i) {
                if (ci.getFirstColumn() == i) {
                    ci.setFirstColumn(i + 1);
                } else {
                    ci.setLastColumn(i - 1);
                    k++;
                }
                ColumnInfoRecord nci2 = copyColInfo(ci);
                nci2.setFirstColumn(i);
                nci2.setLastColumn(i);
                setColumnInfoFields(nci2, xfIndex, width, level, hidden, collapsed);
                insertColumn(k, nci2);
                attemptMergeColInfoRecords(k);
            } else {
                ColumnInfoRecord ciMid = copyColInfo(ci);
                ColumnInfoRecord ciEnd = copyColInfo(ci);
                int lastcolumn = ci.getLastColumn();
                ci.setLastColumn(i - 1);
                ciMid.setFirstColumn(i);
                ciMid.setLastColumn(i);
                setColumnInfoFields(ciMid, xfIndex, width, level, hidden, collapsed);
                int k2 = k + 1;
                insertColumn(k2, ciMid);
                ColumnInfoRecord ciEnd2 = ciEnd;
                ciEnd2.setFirstColumn(i + 1);
                ciEnd2.setLastColumn(lastcolumn);
                insertColumn(k2 + 1, ciEnd2);
            }
        }
    }

    private static void setColumnInfoFields(ColumnInfoRecord ci, Short xfStyle, Integer width, Integer level, Boolean hidden, Boolean collapsed) {
        if (xfStyle != null) {
            ci.setXFIndex(xfStyle.shortValue());
        }
        if (width != null) {
            ci.setColumnWidth(width.intValue());
        }
        if (level != null) {
            ci.setOutlineLevel(level.shortValue());
        }
        if (hidden != null) {
            ci.setHidden(hidden.booleanValue());
        }
        if (collapsed != null) {
            ci.setCollapsed(collapsed.booleanValue());
        }
    }

    private int findColInfoIdx(int columnIx, int fromColInfoIdx) {
        if (columnIx < 0) {
            throw new IllegalArgumentException("column parameter out of range: " + columnIx);
        } else if (fromColInfoIdx >= 0) {
            for (int k = fromColInfoIdx; k < this.records.size(); k++) {
                ColumnInfoRecord ci = getColInfo(k);
                if (ci.containsColumn(columnIx)) {
                    return k;
                }
                if (ci.getFirstColumn() > columnIx) {
                    return -1;
                }
            }
            return -1;
        } else {
            throw new IllegalArgumentException("fromIdx parameter out of range: " + fromColInfoIdx);
        }
    }

    private void attemptMergeColInfoRecords(int colInfoIx) {
        int nRecords = this.records.size();
        if (colInfoIx < 0 || colInfoIx >= nRecords) {
            StringBuilder sb = new StringBuilder();
            sb.append("colInfoIx ");
            sb.append(colInfoIx);
            sb.append(" is out of range (0..");
            sb.append(nRecords - 1);
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        }
        ColumnInfoRecord currentCol = getColInfo(colInfoIx);
        int nextIx = colInfoIx + 1;
        if (nextIx < nRecords && mergeColInfoRecords(currentCol, getColInfo(nextIx))) {
            this.records.remove(nextIx);
        }
        if (colInfoIx > 0 && mergeColInfoRecords(getColInfo(colInfoIx - 1), currentCol)) {
            this.records.remove(colInfoIx);
        }
    }

    private static boolean mergeColInfoRecords(ColumnInfoRecord ciA, ColumnInfoRecord ciB) {
        if (!ciA.isAdjacentBefore(ciB) || !ciA.formatMatches(ciB)) {
            return false;
        }
        ciA.setLastColumn(ciB.getLastColumn());
        return true;
    }

    public void groupColumnRange(int fromColumnIx, int toColumnIx, boolean indent) {
        int level;
        int colInfoSearchStartIdx = 0;
        for (int i = fromColumnIx; i <= toColumnIx; i++) {
            int level2 = 1;
            int colInfoIdx = findColInfoIdx(i, colInfoSearchStartIdx);
            if (colInfoIdx != -1) {
                int level3 = getColInfo(colInfoIdx).getOutlineLevel();
                if (indent) {
                    level = level3 + 1;
                } else {
                    level = level3 - 1;
                }
                level2 = Math.min(7, Math.max(0, level));
                colInfoSearchStartIdx = Math.max(0, colInfoIdx - 1);
            }
            setColumn(i, (Short) null, (Integer) null, Integer.valueOf(level2), (Boolean) null, (Boolean) null);
        }
    }

    public ColumnInfoRecord findColumnInfo(int columnIndex) {
        int nInfos = this.records.size();
        for (int i = 0; i < nInfos; i++) {
            ColumnInfoRecord ci = getColInfo(i);
            if (ci.containsColumn(columnIndex)) {
                return ci;
            }
        }
        return null;
    }

    public int getMaxOutlineLevel() {
        int result = 0;
        int count = this.records.size();
        for (int i = 0; i < count; i++) {
            result = Math.max(getColInfo(i).getOutlineLevel(), result);
        }
        return result;
    }
}
