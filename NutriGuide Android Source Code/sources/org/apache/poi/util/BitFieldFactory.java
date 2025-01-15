package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

public class BitFieldFactory {
    private static Map instances = new HashMap();

    public static BitField getInstance(int mask) {
        BitField f = (BitField) instances.get(Integer.valueOf(mask));
        if (f != null) {
            return f;
        }
        BitField f2 = new BitField(mask);
        instances.put(Integer.valueOf(mask), f2);
        return f2;
    }
}
