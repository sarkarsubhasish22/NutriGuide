package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.NullEscherSerializationListener;
import org.apache.poi.hssf.util.LazilyConcatenatedByteArray;
import org.apache.poi.util.LittleEndian;

public abstract class AbstractEscherHolderRecord extends Record {
    private static boolean DESERIALISE;
    private List<EscherRecord> escherRecords = new ArrayList();
    private LazilyConcatenatedByteArray rawDataContainer = new LazilyConcatenatedByteArray();

    /* access modifiers changed from: protected */
    public abstract String getRecordName();

    public abstract short getSid();

    static {
        try {
            DESERIALISE = System.getProperty("poi.deserialize.escher") != null;
        } catch (SecurityException e) {
            DESERIALISE = false;
        }
    }

    public AbstractEscherHolderRecord() {
    }

    public AbstractEscherHolderRecord(RecordInputStream in) {
        if (!DESERIALISE) {
            this.rawDataContainer.concatenate(in.readRemainder());
            return;
        }
        byte[] data = in.readAllContinuedRemainder();
        convertToEscherRecords(0, data.length, data);
    }

    /* access modifiers changed from: protected */
    public void convertRawBytesToEscherRecords() {
        byte[] rawData = getRawData();
        convertToEscherRecords(0, rawData.length, rawData);
    }

    private void convertToEscherRecords(int offset, int size, byte[] data) {
        EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int pos = offset;
        while (pos < offset + size) {
            EscherRecord r = recordFactory.createRecord(data, pos);
            int bytesRead = r.fillFields(data, pos, recordFactory);
            this.escherRecords.add(r);
            pos += bytesRead;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        String nl = System.getProperty("line.separator");
        buffer.append('[' + getRecordName() + ']' + nl);
        if (this.escherRecords.size() == 0) {
            buffer.append("No Escher Records Decoded" + nl);
        }
        for (EscherRecord r : this.escherRecords) {
            buffer.append(r.toString());
        }
        buffer.append("[/" + getRecordName() + ']' + nl);
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data) {
        LittleEndian.putShort(data, offset + 0, getSid());
        LittleEndian.putShort(data, offset + 2, (short) (getRecordSize() - 4));
        byte[] rawData = getRawData();
        if (this.escherRecords.size() != 0 || rawData == null) {
            LittleEndian.putShort(data, offset + 0, getSid());
            LittleEndian.putShort(data, offset + 2, (short) (getRecordSize() - 4));
            int pos = offset + 4;
            for (EscherRecord r : this.escherRecords) {
                pos += r.serialize(pos, data, new NullEscherSerializationListener());
            }
            return getRecordSize();
        }
        LittleEndian.putShort(data, offset + 0, getSid());
        LittleEndian.putShort(data, offset + 2, (short) (getRecordSize() - 4));
        System.arraycopy(rawData, 0, data, offset + 4, rawData.length);
        return rawData.length + 4;
    }

    public int getRecordSize() {
        byte[] rawData = getRawData();
        if (this.escherRecords.size() == 0 && rawData != null) {
            return rawData.length;
        }
        int size = 0;
        for (EscherRecord r : this.escherRecords) {
            size += r.getRecordSize();
        }
        return size;
    }

    public Object clone() {
        return cloneViaReserialise();
    }

    public void addEscherRecord(int index, EscherRecord element) {
        this.escherRecords.add(index, element);
    }

    public boolean addEscherRecord(EscherRecord element) {
        return this.escherRecords.add(element);
    }

    public List<EscherRecord> getEscherRecords() {
        return this.escherRecords;
    }

    public void clearEscherRecords() {
        this.escherRecords.clear();
    }

    public EscherContainerRecord getEscherContainer() {
        for (EscherRecord er : this.escherRecords) {
            if (er instanceof EscherContainerRecord) {
                return (EscherContainerRecord) er;
            }
        }
        return null;
    }

    public EscherRecord findFirstWithId(short id) {
        return findFirstWithId(id, getEscherRecords());
    }

    private EscherRecord findFirstWithId(short id, List<EscherRecord> records) {
        EscherRecord found;
        for (EscherRecord r : records) {
            if (r.getRecordId() == id) {
                return r;
            }
        }
        for (EscherRecord r2 : records) {
            if (r2.isContainerRecord() && (found = findFirstWithId(id, r2.getChildRecords())) != null) {
                return found;
            }
        }
        return null;
    }

    public EscherRecord getEscherRecord(int index) {
        return this.escherRecords.get(index);
    }

    public void join(AbstractEscherHolderRecord record) {
        this.rawDataContainer.concatenate(record.getRawData());
    }

    public void processContinueRecord(byte[] record) {
        this.rawDataContainer.concatenate(record);
    }

    public byte[] getRawData() {
        return this.rawDataContainer.toArray();
    }

    public void setRawData(byte[] rawData) {
        this.rawDataContainer.clear();
        this.rawDataContainer.concatenate(rawData);
    }

    public void decode() {
        byte[] rawData = getRawData();
        convertToEscherRecords(0, rawData.length, rawData);
    }
}
