package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.LittleEndian;

public final class EscherPropertyFactory {
    public List<EscherProperty> createProperties(byte[] data, int offset, short numProperties) {
        List<EscherProperty> results = new ArrayList<>();
        int pos = offset;
        for (int i = 0; i < numProperties; i++) {
            short propId = LittleEndian.getShort(data, pos);
            int propData = LittleEndian.getInt(data, pos + 2);
            short propNumber = (short) (propId & 16383);
            boolean isComplex = (propId & Short.MIN_VALUE) != 0;
            if ((propId & 16384) != 0) {
            }
            byte propertyType = EscherProperties.getPropertyType(propNumber);
            if (propertyType == 1) {
                results.add(new EscherBoolProperty(propId, propData));
            } else if (propertyType == 2) {
                results.add(new EscherRGBProperty(propId, propData));
            } else if (propertyType == 3) {
                results.add(new EscherShapePathProperty(propId, propData));
            } else if (!isComplex) {
                results.add(new EscherSimpleProperty(propId, propData));
            } else if (propertyType == 5) {
                results.add(new EscherArrayProperty(propId, new byte[propData]));
            } else {
                results.add(new EscherComplexProperty(propId, new byte[propData]));
            }
            pos += 6;
        }
        for (EscherProperty p : results) {
            if (p instanceof EscherComplexProperty) {
                if (p instanceof EscherArrayProperty) {
                    pos += ((EscherArrayProperty) p).setArrayData(data, pos);
                } else {
                    byte[] complexData = ((EscherComplexProperty) p).getComplexData();
                    System.arraycopy(data, pos, complexData, 0, complexData.length);
                    pos += complexData.length;
                }
            }
        }
        return results;
    }
}
