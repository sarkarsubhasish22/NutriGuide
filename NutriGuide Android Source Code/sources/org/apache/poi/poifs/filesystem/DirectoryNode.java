package org.apache.poi.poifs.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.property.Property;

public class DirectoryNode extends EntryNode implements DirectoryEntry, POIFSViewable, Iterable<Entry> {
    private Map<String, Entry> _byname;
    private ArrayList<Entry> _entries;
    private POIFSFileSystem _filesystem;
    private POIFSDocumentPath _path;

    DirectoryNode(DirectoryProperty property, POIFSFileSystem filesystem, DirectoryNode parent) {
        super(property, parent);
        Entry entry;
        if (parent == null) {
            this._path = new POIFSDocumentPath();
        } else {
            this._path = new POIFSDocumentPath(parent._path, new String[]{property.getName()});
        }
        this._filesystem = filesystem;
        this._byname = new HashMap();
        this._entries = new ArrayList<>();
        Iterator<Property> iter = property.getChildren();
        while (iter.hasNext()) {
            Property child = iter.next();
            if (child.isDirectory()) {
                entry = new DirectoryNode((DirectoryProperty) child, this._filesystem, this);
            } else {
                entry = new DocumentNode((DocumentProperty) child, this);
            }
            Entry childNode = entry;
            this._entries.add(childNode);
            this._byname.put(childNode.getName(), childNode);
        }
    }

    public POIFSDocumentPath getPath() {
        return this._path;
    }

    public POIFSFileSystem getFileSystem() {
        return this._filesystem;
    }

    public DocumentInputStream createDocumentInputStream(String documentName) throws IOException {
        Entry document = getEntry(documentName);
        if (document.isDocumentEntry()) {
            return new DocumentInputStream((DocumentEntry) document);
        }
        throw new IOException("Entry '" + documentName + "' is not a DocumentEntry");
    }

    /* access modifiers changed from: package-private */
    public DocumentEntry createDocument(POIFSDocument document) throws IOException {
        DocumentProperty property = document.getDocumentProperty();
        DocumentNode rval = new DocumentNode(property, this);
        ((DirectoryProperty) getProperty()).addChild(property);
        this._filesystem.addDocument(document);
        this._entries.add(rval);
        this._byname.put(property.getName(), rval);
        return rval;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000b, code lost:
        r0 = ((org.apache.poi.poifs.property.DirectoryProperty) getProperty()).changeName(r1.getProperty(), r6);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean changeName(java.lang.String r5, java.lang.String r6) {
        /*
            r4 = this;
            r0 = 0
            java.util.Map<java.lang.String, org.apache.poi.poifs.filesystem.Entry> r1 = r4._byname
            java.lang.Object r1 = r1.get(r5)
            org.apache.poi.poifs.filesystem.EntryNode r1 = (org.apache.poi.poifs.filesystem.EntryNode) r1
            if (r1 == 0) goto L_0x002d
            org.apache.poi.poifs.property.Property r2 = r4.getProperty()
            org.apache.poi.poifs.property.DirectoryProperty r2 = (org.apache.poi.poifs.property.DirectoryProperty) r2
            org.apache.poi.poifs.property.Property r3 = r1.getProperty()
            boolean r0 = r2.changeName(r3, r6)
            if (r0 == 0) goto L_0x002d
            java.util.Map<java.lang.String, org.apache.poi.poifs.filesystem.Entry> r2 = r4._byname
            r2.remove(r5)
            java.util.Map<java.lang.String, org.apache.poi.poifs.filesystem.Entry> r2 = r4._byname
            org.apache.poi.poifs.property.Property r3 = r1.getProperty()
            java.lang.String r3 = r3.getName()
            r2.put(r3, r1)
        L_0x002d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.poifs.filesystem.DirectoryNode.changeName(java.lang.String, java.lang.String):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean deleteEntry(EntryNode entry) {
        boolean rval = ((DirectoryProperty) getProperty()).deleteChild(entry.getProperty());
        if (rval) {
            this._entries.remove(entry);
            this._byname.remove(entry.getName());
            this._filesystem.remove(entry);
        }
        return rval;
    }

    public Iterator<Entry> getEntries() {
        return this._entries.iterator();
    }

    public boolean isEmpty() {
        return this._entries.isEmpty();
    }

    public int getEntryCount() {
        return this._entries.size();
    }

    public Entry getEntry(String name) throws FileNotFoundException {
        Entry rval = null;
        if (name != null) {
            rval = this._byname.get(name);
        }
        if (rval != null) {
            return rval;
        }
        throw new FileNotFoundException("no such entry: \"" + name + "\"");
    }

    public DocumentEntry createDocument(String name, InputStream stream) throws IOException {
        return createDocument(new POIFSDocument(name, stream));
    }

    public DocumentEntry createDocument(String name, int size, POIFSWriterListener writer) throws IOException {
        return createDocument(new POIFSDocument(name, size, this._path, writer));
    }

    public DirectoryEntry createDirectory(String name) throws IOException {
        DirectoryProperty property = new DirectoryProperty(name);
        DirectoryNode rval = new DirectoryNode(property, this._filesystem, this);
        ((DirectoryProperty) getProperty()).addChild(property);
        this._filesystem.addDirectory(property);
        this._entries.add(rval);
        this._byname.put(name, rval);
        return rval;
    }

    public ClassID getStorageClsid() {
        return getProperty().getStorageClsid();
    }

    public void setStorageClsid(ClassID clsidStorage) {
        getProperty().setStorageClsid(clsidStorage);
    }

    public boolean isDirectoryEntry() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isDeleteOK() {
        return isEmpty();
    }

    public Object[] getViewableArray() {
        return new Object[0];
    }

    public Iterator getViewableIterator() {
        List components = new ArrayList();
        components.add(getProperty());
        Iterator<Entry> iter = this._entries.iterator();
        while (iter.hasNext()) {
            components.add(iter.next());
        }
        return components.iterator();
    }

    public boolean preferArray() {
        return false;
    }

    public String getShortDescription() {
        return getName();
    }

    public Iterator<Entry> iterator() {
        return getEntries();
    }
}
