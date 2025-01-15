package org.apache.poi.poifs.filesystem;

import org.apache.poi.poifs.property.Property;

public abstract class EntryNode implements Entry {
    private DirectoryNode _parent;
    private Property _property;

    /* access modifiers changed from: protected */
    public abstract boolean isDeleteOK();

    protected EntryNode(Property property, DirectoryNode parent) {
        this._property = property;
        this._parent = parent;
    }

    /* access modifiers changed from: protected */
    public Property getProperty() {
        return this._property;
    }

    /* access modifiers changed from: protected */
    public boolean isRoot() {
        return this._parent == null;
    }

    public String getName() {
        return this._property.getName();
    }

    public boolean isDirectoryEntry() {
        return false;
    }

    public boolean isDocumentEntry() {
        return false;
    }

    public DirectoryEntry getParent() {
        return this._parent;
    }

    public boolean delete() {
        if (isRoot() || !isDeleteOK()) {
            return false;
        }
        return this._parent.deleteEntry(this);
    }

    public boolean renameTo(String newName) {
        if (!isRoot()) {
            return this._parent.changeName(getName(), newName);
        }
        return false;
    }
}
