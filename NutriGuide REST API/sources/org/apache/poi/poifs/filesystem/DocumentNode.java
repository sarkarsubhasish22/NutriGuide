package org.apache.poi.poifs.filesystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DocumentProperty;

public class DocumentNode extends EntryNode implements DocumentEntry, POIFSViewable {
    private POIFSDocument _document;

    DocumentNode(DocumentProperty property, DirectoryNode parent) {
        super(property, parent);
        this._document = property.getDocument();
    }

    /* access modifiers changed from: package-private */
    public POIFSDocument getDocument() {
        return this._document;
    }

    public int getSize() {
        return getProperty().getSize();
    }

    public boolean isDocumentEntry() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isDeleteOK() {
        return true;
    }

    public Object[] getViewableArray() {
        return new Object[0];
    }

    public Iterator getViewableIterator() {
        List components = new ArrayList();
        components.add(getProperty());
        components.add(this._document);
        return components.iterator();
    }

    public boolean preferArray() {
        return false;
    }

    public String getShortDescription() {
        return getName();
    }
}
