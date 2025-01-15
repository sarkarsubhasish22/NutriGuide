package org.apache.poi.ss.usermodel;

public interface PictureData {
    byte[] getData();

    String getMimeType();

    String suggestFileExtension();
}
