package org.apache.poi.ss.usermodel;

public interface Picture {
    ClientAnchor getPreferredSize();

    void resize();

    void resize(double d);
}
