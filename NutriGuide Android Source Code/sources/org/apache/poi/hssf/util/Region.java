package org.apache.poi.hssf.util;

public class Region extends org.apache.poi.ss.util.Region {
    public Region() {
    }

    public Region(int rowFrom, short colFrom, int rowTo, short colTo) {
        super(rowFrom, colFrom, rowTo, colTo);
    }

    public Region(String ref) {
        super(ref);
    }
}
