package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.ss.usermodel.Hyperlink;

public class HSSFHyperlink implements Hyperlink {
    public static final int LINK_DOCUMENT = 2;
    public static final int LINK_EMAIL = 3;
    public static final int LINK_FILE = 4;
    public static final int LINK_URL = 1;
    protected int link_type;
    protected HyperlinkRecord record = null;

    public HSSFHyperlink(int type) {
        this.link_type = type;
        HyperlinkRecord hyperlinkRecord = new HyperlinkRecord();
        this.record = hyperlinkRecord;
        if (type != 1) {
            if (type == 2) {
                hyperlinkRecord.newDocumentLink();
                return;
            } else if (type != 3) {
                if (type == 4) {
                    hyperlinkRecord.newFileLink();
                    return;
                }
                return;
            }
        }
        hyperlinkRecord.newUrlLink();
    }

    protected HSSFHyperlink(HyperlinkRecord record2) {
        this.record = record2;
        if (record2.isFileLink()) {
            this.link_type = 4;
        } else if (record2.isDocumentLink()) {
            this.link_type = 2;
        } else if (record2.getAddress() == null || !record2.getAddress().startsWith("mailto:")) {
            this.link_type = 1;
        } else {
            this.link_type = 3;
        }
    }

    public int getFirstRow() {
        return this.record.getFirstRow();
    }

    public void setFirstRow(int row) {
        this.record.setFirstRow(row);
    }

    public int getLastRow() {
        return this.record.getLastRow();
    }

    public void setLastRow(int row) {
        this.record.setLastRow(row);
    }

    public int getFirstColumn() {
        return this.record.getFirstColumn();
    }

    public void setFirstColumn(int col) {
        this.record.setFirstColumn((short) col);
    }

    public int getLastColumn() {
        return this.record.getLastColumn();
    }

    public void setLastColumn(int col) {
        this.record.setLastColumn((short) col);
    }

    public String getAddress() {
        return this.record.getAddress();
    }

    public String getTextMark() {
        return this.record.getTextMark();
    }

    public void setTextMark(String textMark) {
        this.record.setTextMark(textMark);
    }

    public String getShortFilename() {
        return this.record.getShortFilename();
    }

    public void setShortFilename(String shortFilename) {
        this.record.setShortFilename(shortFilename);
    }

    public void setAddress(String address) {
        this.record.setAddress(address);
    }

    public String getLabel() {
        return this.record.getLabel();
    }

    public void setLabel(String label) {
        this.record.setLabel(label);
    }

    public int getType() {
        return this.link_type;
    }
}
