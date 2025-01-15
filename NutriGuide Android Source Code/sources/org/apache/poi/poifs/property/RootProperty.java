package org.apache.poi.poifs.property;

import org.apache.poi.poifs.storage.SmallDocumentBlock;

public final class RootProperty extends DirectoryProperty {
    RootProperty() {
        super("Root Entry");
        setNodeColor((byte) 1);
        setPropertyType((byte) 5);
        setStartBlock(-2);
    }

    protected RootProperty(int index, byte[] array, int offset) {
        super(index, array, offset);
    }

    public void setSize(int size) {
        super.setSize(SmallDocumentBlock.calcSize(size));
    }
}
