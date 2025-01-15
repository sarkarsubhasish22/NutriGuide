package org.apache.poi.poifs.dev;

import java.util.Iterator;

public interface POIFSViewable {
    String getShortDescription();

    Object[] getViewableArray();

    Iterator getViewableIterator();

    boolean preferArray();
}
