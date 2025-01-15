package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.storage.ListManagedBlock;

class PropertyFactory {
    private PropertyFactory() {
    }

    static List convertToProperties(ListManagedBlock[] blocks) throws IOException {
        List properties = new ArrayList();
        for (ListManagedBlock data : blocks) {
            byte[] data2 = data.getData();
            int property_count = data2.length / 128;
            int offset = 0;
            for (int k = 0; k < property_count; k++) {
                byte b = data2[offset + 66];
                if (b == 1) {
                    properties.add(new DirectoryProperty(properties.size(), data2, offset));
                } else if (b == 2) {
                    properties.add(new DocumentProperty(properties.size(), data2, offset));
                } else if (b != 5) {
                    properties.add((Object) null);
                } else {
                    properties.add(new RootProperty(properties.size(), data2, offset));
                }
                offset += 128;
            }
        }
        return properties;
    }
}
