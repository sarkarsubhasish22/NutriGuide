package org.apache.poi.ss.usermodel;

public interface Drawing {
    Comment createCellComment(ClientAnchor clientAnchor);

    Picture createPicture(ClientAnchor clientAnchor, int i);
}
