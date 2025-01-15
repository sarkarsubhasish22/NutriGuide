package org.apache.poi.poifs.filesystem;

public class DocumentDescriptor {
    private int hashcode = 0;
    private String name;
    private POIFSDocumentPath path;

    public DocumentDescriptor(POIFSDocumentPath path2, String name2) {
        if (path2 == null) {
            throw new NullPointerException("path must not be null");
        } else if (name2 == null) {
            throw new NullPointerException("name must not be null");
        } else if (name2.length() != 0) {
            this.path = path2;
            this.name = name2;
        } else {
            throw new IllegalArgumentException("name cannot be empty");
        }
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (this == o) {
            return true;
        }
        DocumentDescriptor descriptor = (DocumentDescriptor) o;
        return this.path.equals(descriptor.path) && this.name.equals(descriptor.name);
    }

    public int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = this.path.hashCode() ^ this.name.hashCode();
        }
        return this.hashcode;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer((this.path.length() + 1) * 40);
        for (int j = 0; j < this.path.length(); j++) {
            buffer.append(this.path.getComponent(j));
            buffer.append("/");
        }
        buffer.append(this.name);
        return buffer.toString();
    }
}
