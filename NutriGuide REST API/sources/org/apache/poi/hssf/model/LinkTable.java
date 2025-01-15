package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.record.CRNCountRecord;
import org.apache.poi.hssf.record.CRNRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.ExternalNameRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.record.formula.NameXPtg;

final class LinkTable {
    private final List<NameRecord> _definedNames;
    private final ExternSheetRecord _externSheetRecord;
    private final ExternalBookBlock[] _externalBookBlocks;
    private final int _recordCount;
    private final WorkbookRecordList _workbookRecordList;

    private static final class CRNBlock {
        private final CRNCountRecord _countRecord;
        private final CRNRecord[] _crns;

        public CRNBlock(RecordStream rs) {
            CRNCountRecord cRNCountRecord = (CRNCountRecord) rs.getNext();
            this._countRecord = cRNCountRecord;
            CRNRecord[] crns = new CRNRecord[cRNCountRecord.getNumberOfCRNs()];
            for (int i = 0; i < crns.length; i++) {
                crns[i] = (CRNRecord) rs.getNext();
            }
            this._crns = crns;
        }

        public CRNRecord[] getCrns() {
            return (CRNRecord[]) this._crns.clone();
        }
    }

    private static final class ExternalBookBlock {
        private final CRNBlock[] _crnBlocks;
        private final SupBookRecord _externalBookRecord;
        private final ExternalNameRecord[] _externalNameRecords;

        public ExternalBookBlock(RecordStream rs) {
            this._externalBookRecord = (SupBookRecord) rs.getNext();
            List<Object> temp = new ArrayList<>();
            while (rs.peekNextClass() == ExternalNameRecord.class) {
                temp.add(rs.getNext());
            }
            ExternalNameRecord[] externalNameRecordArr = new ExternalNameRecord[temp.size()];
            this._externalNameRecords = externalNameRecordArr;
            temp.toArray(externalNameRecordArr);
            temp.clear();
            while (rs.peekNextClass() == CRNCountRecord.class) {
                temp.add(new CRNBlock(rs));
            }
            CRNBlock[] cRNBlockArr = new CRNBlock[temp.size()];
            this._crnBlocks = cRNBlockArr;
            temp.toArray(cRNBlockArr);
        }

        public ExternalBookBlock(int numberOfSheets) {
            this._externalBookRecord = SupBookRecord.createInternalReferences((short) numberOfSheets);
            this._externalNameRecords = new ExternalNameRecord[0];
            this._crnBlocks = new CRNBlock[0];
        }

        public SupBookRecord getExternalBookRecord() {
            return this._externalBookRecord;
        }

        public String getNameText(int definedNameIndex) {
            return this._externalNameRecords[definedNameIndex].getText();
        }

        public int getNameIx(int definedNameIndex) {
            return this._externalNameRecords[definedNameIndex].getIx();
        }

        public int getIndexOfName(String name) {
            int i = 0;
            while (true) {
                ExternalNameRecord[] externalNameRecordArr = this._externalNameRecords;
                if (i >= externalNameRecordArr.length) {
                    return -1;
                }
                if (externalNameRecordArr[i].getText().equalsIgnoreCase(name)) {
                    return i;
                }
                i++;
            }
        }
    }

    public LinkTable(List inputList, int startIndex, WorkbookRecordList workbookRecordList, Map<String, NameCommentRecord> commentRecords) {
        this._workbookRecordList = workbookRecordList;
        RecordStream rs = new RecordStream(inputList, startIndex);
        List<ExternalBookBlock> temp = new ArrayList<>();
        while (rs.peekNextClass() == SupBookRecord.class) {
            temp.add(new ExternalBookBlock(rs));
        }
        ExternalBookBlock[] externalBookBlockArr = new ExternalBookBlock[temp.size()];
        this._externalBookBlocks = externalBookBlockArr;
        temp.toArray(externalBookBlockArr);
        temp.clear();
        if (externalBookBlockArr.length <= 0) {
            this._externSheetRecord = null;
        } else if (rs.peekNextClass() != ExternSheetRecord.class) {
            this._externSheetRecord = null;
        } else {
            this._externSheetRecord = readExtSheetRecord(rs);
        }
        this._definedNames = new ArrayList();
        while (true) {
            Class nextClass = rs.peekNextClass();
            if (nextClass == NameRecord.class) {
                this._definedNames.add((NameRecord) rs.getNext());
            } else if (nextClass == NameCommentRecord.class) {
                NameCommentRecord ncr = (NameCommentRecord) rs.getNext();
                commentRecords.put(ncr.getNameText(), ncr);
            } else {
                int countRead = rs.getCountRead();
                this._recordCount = countRead;
                this._workbookRecordList.getRecords().addAll(inputList.subList(startIndex, countRead + startIndex));
                return;
            }
        }
    }

    private static ExternSheetRecord readExtSheetRecord(RecordStream rs) {
        List<ExternSheetRecord> temp = new ArrayList<>(2);
        while (rs.peekNextClass() == ExternSheetRecord.class) {
            temp.add((ExternSheetRecord) rs.getNext());
        }
        int nItems = temp.size();
        if (nItems < 1) {
            throw new RuntimeException("Expected an EXTERNSHEET record but got (" + rs.peekNextClass().getName() + ")");
        } else if (nItems == 1) {
            return temp.get(0);
        } else {
            ExternSheetRecord[] esrs = new ExternSheetRecord[nItems];
            temp.toArray(esrs);
            return ExternSheetRecord.combine(esrs);
        }
    }

    public LinkTable(int numberOfSheets, WorkbookRecordList workbookRecordList) {
        this._workbookRecordList = workbookRecordList;
        this._definedNames = new ArrayList();
        ExternalBookBlock[] externalBookBlockArr = {new ExternalBookBlock(numberOfSheets)};
        this._externalBookBlocks = externalBookBlockArr;
        ExternSheetRecord externSheetRecord = new ExternSheetRecord();
        this._externSheetRecord = externSheetRecord;
        this._recordCount = 2;
        SupBookRecord supbook = externalBookBlockArr[0].getExternalBookRecord();
        int idx = findFirstRecordLocBySid(140);
        if (idx >= 0) {
            workbookRecordList.add(idx + 1, externSheetRecord);
            workbookRecordList.add(idx + 1, supbook);
            return;
        }
        throw new RuntimeException("CountryRecord not found");
    }

    public int getRecordCount() {
        return this._recordCount;
    }

    public NameRecord getSpecificBuiltinRecord(byte builtInCode, int sheetNumber) {
        for (NameRecord record : this._definedNames) {
            if (record.getBuiltInName() == builtInCode && record.getSheetNumber() == sheetNumber) {
                return record;
            }
        }
        return null;
    }

    public void removeBuiltinRecord(byte name, int sheetIndex) {
        NameRecord record = getSpecificBuiltinRecord(name, sheetIndex);
        if (record != null) {
            this._definedNames.remove(record);
        }
    }

    public int getNumNames() {
        return this._definedNames.size();
    }

    public NameRecord getNameRecord(int index) {
        return this._definedNames.get(index);
    }

    public void addName(NameRecord name) {
        this._definedNames.add(name);
        int idx = findFirstRecordLocBySid(23);
        if (idx == -1) {
            idx = findFirstRecordLocBySid(SupBookRecord.sid);
        }
        if (idx == -1) {
            idx = findFirstRecordLocBySid(140);
        }
        this._workbookRecordList.add(idx + this._definedNames.size(), name);
    }

    public void removeName(int namenum) {
        this._definedNames.remove(namenum);
    }

    public boolean nameAlreadyExists(NameRecord name) {
        for (int i = getNumNames() - 1; i >= 0; i--) {
            NameRecord rec = getNameRecord(i);
            if (rec != name && isDuplicatedNames(name, rec)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDuplicatedNames(NameRecord firstName, NameRecord lastName) {
        return lastName.getNameText().equalsIgnoreCase(firstName.getNameText()) && isSameSheetNames(firstName, lastName);
    }

    private static boolean isSameSheetNames(NameRecord firstName, NameRecord lastName) {
        return lastName.getSheetNumber() == firstName.getSheetNumber();
    }

    public String[] getExternalBookAndSheetName(int extRefIndex) {
        SupBookRecord ebr = this._externalBookBlocks[this._externSheetRecord.getExtbookIndexFromRefIndex(extRefIndex)].getExternalBookRecord();
        if (!ebr.isExternalReferences()) {
            return null;
        }
        int shIx = this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
        String usSheetName = null;
        if (shIx >= 0) {
            usSheetName = ebr.getSheetNames()[shIx];
        }
        return new String[]{ebr.getURL(), usSheetName};
    }

    public int getExternalSheetIndex(String workbookName, String sheetName) {
        SupBookRecord ebrTarget = null;
        int externalBookIndex = -1;
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i >= externalBookBlockArr.length) {
                break;
            }
            SupBookRecord ebr = externalBookBlockArr[i].getExternalBookRecord();
            if (ebr.isExternalReferences() && workbookName.equals(ebr.getURL())) {
                ebrTarget = ebr;
                externalBookIndex = i;
                break;
            }
            i++;
        }
        if (ebrTarget != null) {
            int sheetIndex = getSheetIndex(ebrTarget.getSheetNames(), sheetName);
            int result = this._externSheetRecord.getRefIxForSheet(externalBookIndex, sheetIndex);
            if (result >= 0) {
                return result;
            }
            throw new RuntimeException("ExternSheetRecord does not contain combination (" + externalBookIndex + ", " + sheetIndex + ")");
        }
        throw new RuntimeException("No external workbook with name '" + workbookName + "'");
    }

    private static int getSheetIndex(String[] sheetNames, String sheetName) {
        for (int i = 0; i < sheetNames.length; i++) {
            if (sheetNames[i].equals(sheetName)) {
                return i;
            }
        }
        throw new RuntimeException("External workbook does not contain sheet '" + sheetName + "'");
    }

    public int getIndexToInternalSheet(int extRefIndex) {
        return this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
    }

    public int getSheetIndexFromExternSheetIndex(int extRefIndex) {
        if (extRefIndex >= this._externSheetRecord.getNumOfRefs()) {
            return -1;
        }
        return this._externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
    }

    public int checkExternSheet(int sheetIndex) {
        int thisWbIndex = -1;
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i >= externalBookBlockArr.length) {
                break;
            } else if (externalBookBlockArr[i].getExternalBookRecord().isInternalReferences()) {
                thisWbIndex = i;
                break;
            } else {
                i++;
            }
        }
        if (thisWbIndex >= 0) {
            int i2 = this._externSheetRecord.getRefIxForSheet(thisWbIndex, sheetIndex);
            if (i2 >= 0) {
                return i2;
            }
            return this._externSheetRecord.addRef(thisWbIndex, sheetIndex, sheetIndex);
        }
        throw new RuntimeException("Could not find 'internal references' EXTERNALBOOK");
    }

    private int findFirstRecordLocBySid(short sid) {
        int index = 0;
        Iterator iterator = this._workbookRecordList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getSid() == sid) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public String resolveNameXText(int refIndex, int definedNameIndex) {
        return this._externalBookBlocks[this._externSheetRecord.getExtbookIndexFromRefIndex(refIndex)].getNameText(definedNameIndex);
    }

    public int resolveNameXIx(int refIndex, int definedNameIndex) {
        return this._externalBookBlocks[this._externSheetRecord.getExtbookIndexFromRefIndex(refIndex)].getNameIx(definedNameIndex);
    }

    public NameXPtg getNameXPtg(String name) {
        int sheetRefIndex;
        int i = 0;
        while (true) {
            ExternalBookBlock[] externalBookBlockArr = this._externalBookBlocks;
            if (i >= externalBookBlockArr.length) {
                return null;
            }
            int definedNameIndex = externalBookBlockArr[i].getIndexOfName(name);
            if (definedNameIndex >= 0 && (sheetRefIndex = findRefIndexFromExtBookIndex(i)) >= 0) {
                return new NameXPtg(sheetRefIndex, definedNameIndex);
            }
            i++;
        }
    }

    private int findRefIndexFromExtBookIndex(int extBookIndex) {
        return this._externSheetRecord.findRefIndexFromExtBookIndex(extBookIndex);
    }
}
