package org.apache.poi.ddf;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ddf.EscherRecord;

public class DefaultEscherRecordFactory implements EscherRecordFactory {
    private static Class<?>[] escherRecordClasses;
    private static Map<Short, Constructor<? extends EscherRecord>> recordsMap;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Class<?>[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            r0 = 11
            java.lang.Class[] r0 = new java.lang.Class[r0]
            r1 = 0
            java.lang.Class<org.apache.poi.ddf.EscherBSERecord> r2 = org.apache.poi.ddf.EscherBSERecord.class
            r0[r1] = r2
            r1 = 1
            java.lang.Class<org.apache.poi.ddf.EscherOptRecord> r2 = org.apache.poi.ddf.EscherOptRecord.class
            r0[r1] = r2
            r1 = 2
            java.lang.Class<org.apache.poi.ddf.EscherClientAnchorRecord> r2 = org.apache.poi.ddf.EscherClientAnchorRecord.class
            r0[r1] = r2
            r1 = 3
            java.lang.Class<org.apache.poi.ddf.EscherDgRecord> r2 = org.apache.poi.ddf.EscherDgRecord.class
            r0[r1] = r2
            r1 = 4
            java.lang.Class<org.apache.poi.ddf.EscherSpgrRecord> r2 = org.apache.poi.ddf.EscherSpgrRecord.class
            r0[r1] = r2
            r1 = 5
            java.lang.Class<org.apache.poi.ddf.EscherSpRecord> r2 = org.apache.poi.ddf.EscherSpRecord.class
            r0[r1] = r2
            r1 = 6
            java.lang.Class<org.apache.poi.ddf.EscherClientDataRecord> r2 = org.apache.poi.ddf.EscherClientDataRecord.class
            r0[r1] = r2
            r1 = 7
            java.lang.Class<org.apache.poi.ddf.EscherDggRecord> r2 = org.apache.poi.ddf.EscherDggRecord.class
            r0[r1] = r2
            r1 = 8
            java.lang.Class<org.apache.poi.ddf.EscherSplitMenuColorsRecord> r2 = org.apache.poi.ddf.EscherSplitMenuColorsRecord.class
            r0[r1] = r2
            r1 = 9
            java.lang.Class<org.apache.poi.ddf.EscherChildAnchorRecord> r2 = org.apache.poi.ddf.EscherChildAnchorRecord.class
            r0[r1] = r2
            r1 = 10
            java.lang.Class<org.apache.poi.ddf.EscherTextboxRecord> r2 = org.apache.poi.ddf.EscherTextboxRecord.class
            r0[r1] = r2
            escherRecordClasses = r0
            java.util.Map r0 = recordsToMap(r0)
            recordsMap = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ddf.DefaultEscherRecordFactory.<clinit>():void");
    }

    public EscherRecord createRecord(byte[] data, int offset) {
        EscherBlipRecord r;
        EscherRecord.EscherRecordHeader header = EscherRecord.EscherRecordHeader.readHeader(data, offset);
        if ((header.getOptions() & 15) == 15 && header.getRecordId() != -4083) {
            EscherContainerRecord r2 = new EscherContainerRecord();
            r2.setRecordId(header.getRecordId());
            r2.setOptions(header.getOptions());
            return r2;
        } else if (header.getRecordId() < -4072 || header.getRecordId() > -3817) {
            Constructor<? extends EscherRecord> recordConstructor = recordsMap.get(Short.valueOf(header.getRecordId()));
            if (recordConstructor == null) {
                return new UnknownEscherRecord();
            }
            try {
                EscherRecord escherRecord = (EscherRecord) recordConstructor.newInstance(new Object[0]);
                escherRecord.setRecordId(header.getRecordId());
                escherRecord.setOptions(header.getOptions());
                return escherRecord;
            } catch (Exception e) {
                return new UnknownEscherRecord();
            }
        } else {
            if (header.getRecordId() == -4065 || header.getRecordId() == -4067 || header.getRecordId() == -4066) {
                r = new EscherBitmapBlip();
            } else if (header.getRecordId() == -4070 || header.getRecordId() == -4069 || header.getRecordId() == -4068) {
                r = new EscherMetafileBlip();
            } else {
                r = new EscherBlipRecord();
            }
            r.setRecordId(header.getRecordId());
            r.setOptions(header.getOptions());
            return r;
        }
    }

    private static Map<Short, Constructor<? extends EscherRecord>> recordsToMap(Class<?>[] recClasses) {
        Map<Short, Constructor<? extends EscherRecord>> result = new HashMap<>();
        Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
        int i = 0;
        while (i < recClasses.length) {
            Class<? extends EscherRecord> recCls = recClasses[i];
            try {
                short sid = recCls.getField("RECORD_ID").getShort((Object) null);
                try {
                    result.put(Short.valueOf(sid), recCls.getConstructor(EMPTY_CLASS_ARRAY));
                    i++;
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } catch (IllegalArgumentException e2) {
                throw new RuntimeException(e2);
            } catch (IllegalAccessException e3) {
                throw new RuntimeException(e3);
            } catch (NoSuchFieldException e4) {
                throw new RuntimeException(e4);
            }
        }
        return result;
    }
}
