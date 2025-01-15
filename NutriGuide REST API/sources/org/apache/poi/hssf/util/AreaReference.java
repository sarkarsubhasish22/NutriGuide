package org.apache.poi.hssf.util;

public final class AreaReference extends org.apache.poi.ss.util.AreaReference {
    public AreaReference(String reference) {
        super(reference);
    }

    public AreaReference(CellReference topLeft, CellReference botRight) {
        super(topLeft, botRight);
    }
}
