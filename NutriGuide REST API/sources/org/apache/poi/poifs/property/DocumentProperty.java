package org.apache.poi.poifs.property;

import org.apache.poi.poifs.filesystem.POIFSDocument;

public class DocumentProperty extends Property {
    private POIFSDocument _document = null;

    public DocumentProperty(String name, int size) {
        setName(name);
        setSize(size);
        setNodeColor((byte) 1);
        setPropertyType((byte) 2);
    }

    protected DocumentProperty(int index, byte[] array, int offset) {
        super(index, array, offset);
    }

    public void setDocument(POIFSDocument doc) {
        this._document = doc;
    }

    public POIFSDocument getDocument() {
        return this._document;
    }

    public boolean shouldUseSmallBlocks() {
        return super.shouldUseSmallBlocks();
    }

    public boolean isDirectory() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void preWrite() {
    }
}
