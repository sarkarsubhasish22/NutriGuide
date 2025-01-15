package org.apache.poi.poifs.filesystem;

import java.io.File;

public class POIFSDocumentPath {
    private String[] components;
    private int hashcode;

    public POIFSDocumentPath(String[] components2) throws IllegalArgumentException {
        this.hashcode = 0;
        if (components2 == null) {
            this.components = new String[0];
            return;
        }
        this.components = new String[components2.length];
        for (int j = 0; j < components2.length; j++) {
            if (components2[j] == null || components2[j].length() == 0) {
                throw new IllegalArgumentException("components cannot contain null or empty strings");
            }
            this.components[j] = components2[j];
        }
    }

    public POIFSDocumentPath() {
        this.hashcode = 0;
        this.components = new String[0];
    }

    public POIFSDocumentPath(POIFSDocumentPath path, String[] components2) throws IllegalArgumentException {
        this.hashcode = 0;
        if (components2 == null) {
            this.components = new String[path.components.length];
        } else {
            this.components = new String[(path.components.length + components2.length)];
        }
        int j = 0;
        while (true) {
            String[] strArr = path.components;
            if (j >= strArr.length) {
                break;
            }
            this.components[j] = strArr[j];
            j++;
        }
        if (components2 != null) {
            for (int j2 = 0; j2 < components2.length; j2++) {
                if (components2[j2] == null || components2[j2].length() == 0) {
                    throw new IllegalArgumentException("components cannot contain null or empty strings");
                }
                this.components[path.components.length + j2] = components2[j2];
            }
        }
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (this == o) {
            return true;
        }
        POIFSDocumentPath path = (POIFSDocumentPath) o;
        if (path.components.length != this.components.length) {
            return false;
        }
        int j = 0;
        while (true) {
            String[] strArr = this.components;
            if (j >= strArr.length) {
                return true;
            }
            if (!path.components[j].equals(strArr[j])) {
                return false;
            }
            j++;
        }
    }

    public int hashCode() {
        if (this.hashcode == 0) {
            int j = 0;
            while (true) {
                String[] strArr = this.components;
                if (j >= strArr.length) {
                    break;
                }
                this.hashcode += strArr[j].hashCode();
                j++;
            }
        }
        return this.hashcode;
    }

    public int length() {
        return this.components.length;
    }

    public String getComponent(int n) throws ArrayIndexOutOfBoundsException {
        return this.components[n];
    }

    public POIFSDocumentPath getParent() {
        int length = this.components.length - 1;
        if (length < 0) {
            return null;
        }
        POIFSDocumentPath parent = new POIFSDocumentPath((String[]) null);
        String[] strArr = new String[length];
        parent.components = strArr;
        System.arraycopy(this.components, 0, strArr, 0, length);
        return parent;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        int l = length();
        b.append(File.separatorChar);
        for (int i = 0; i < l; i++) {
            b.append(getComponent(i));
            if (i < l - 1) {
                b.append(File.separatorChar);
            }
        }
        return b.toString();
    }
}
