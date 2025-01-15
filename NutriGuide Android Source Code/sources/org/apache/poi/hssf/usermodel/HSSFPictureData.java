package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ss.usermodel.PictureData;

public class HSSFPictureData implements PictureData {
    public static final short FORMAT_MASK = -16;
    public static final short MSOBI_DIB = 31360;
    public static final short MSOBI_EMF = 15680;
    public static final short MSOBI_JPEG = 18080;
    public static final short MSOBI_PICT = 21536;
    public static final short MSOBI_PNG = 28160;
    public static final short MSOBI_WMF = 8544;
    private EscherBlipRecord blip;

    public HSSFPictureData(EscherBlipRecord blip2) {
        this.blip = blip2;
    }

    public byte[] getData() {
        return this.blip.getPicturedata();
    }

    public int getFormat() {
        return this.blip.getRecordId() + 4072;
    }

    public String suggestFileExtension() {
        switch (this.blip.getRecordId()) {
            case -4070:
                return "emf";
            case -4069:
                return "wmf";
            case -4068:
                return "pict";
            case -4067:
                return "jpeg";
            case -4066:
                return "png";
            case -4065:
                return "dib";
            default:
                return "";
        }
    }

    public String getMimeType() {
        switch (this.blip.getRecordId()) {
            case -4070:
                return "image/x-emf";
            case -4069:
                return "image/x-wmf";
            case -4068:
                return "image/x-pict";
            case -4067:
                return "image/jpeg";
            case -4066:
                return "image/png";
            case -4065:
                return "image/bmp";
            default:
                return "image/unknown";
        }
    }
}
