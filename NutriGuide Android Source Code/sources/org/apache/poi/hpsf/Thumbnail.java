package org.apache.poi.hpsf;

import org.apache.poi.util.LittleEndian;

public final class Thumbnail {
    public static int CFTAG_FMTID = -3;
    public static int CFTAG_MACINTOSH = -2;
    public static int CFTAG_NODATA = 0;
    public static int CFTAG_WINDOWS = -1;
    public static int CF_BITMAP = 2;
    public static int CF_DIB = 8;
    public static int CF_ENHMETAFILE = 14;
    public static int CF_METAFILEPICT = 3;
    public static int OFFSET_CF = 8;
    public static int OFFSET_CFTAG = 4;
    public static int OFFSET_WMFDATA = 20;
    private byte[] _thumbnailData = null;

    public Thumbnail() {
    }

    public Thumbnail(byte[] thumbnailData) {
        this._thumbnailData = thumbnailData;
    }

    public byte[] getThumbnail() {
        return this._thumbnailData;
    }

    public void setThumbnail(byte[] thumbnail) {
        this._thumbnailData = thumbnail;
    }

    public long getClipboardFormatTag() {
        return LittleEndian.getUInt(getThumbnail(), OFFSET_CFTAG);
    }

    public long getClipboardFormat() throws HPSFException {
        if (getClipboardFormatTag() == ((long) CFTAG_WINDOWS)) {
            return LittleEndian.getUInt(getThumbnail(), OFFSET_CF);
        }
        throw new HPSFException("Clipboard Format Tag of Thumbnail must be CFTAG_WINDOWS.");
    }

    public byte[] getThumbnailAsWMF() throws HPSFException {
        if (getClipboardFormatTag() != ((long) CFTAG_WINDOWS)) {
            throw new HPSFException("Clipboard Format Tag of Thumbnail must be CFTAG_WINDOWS.");
        } else if (getClipboardFormat() == ((long) CF_METAFILEPICT)) {
            byte[] thumbnail = getThumbnail();
            int length = thumbnail.length;
            int i = OFFSET_WMFDATA;
            int wmfImageLength = length - i;
            byte[] wmfImage = new byte[wmfImageLength];
            System.arraycopy(thumbnail, i, wmfImage, 0, wmfImageLength);
            return wmfImage;
        } else {
            throw new HPSFException("Clipboard Format of Thumbnail must be CF_METAFILEPICT.");
        }
    }
}
