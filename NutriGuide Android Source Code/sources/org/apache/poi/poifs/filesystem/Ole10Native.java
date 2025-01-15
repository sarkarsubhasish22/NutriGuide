package org.apache.poi.poifs.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public class Ole10Native {
    public static final String OLE10_NATIVE = "\u0001Ole10Native";
    private final String command;
    private final byte[] dataBuffer;
    private final int dataSize;
    private final String fileName;
    private short flags1;
    private short flags2;
    private short flags3;
    private final String label;
    private final int totalSize;
    private byte[] unknown1;
    private byte[] unknown2;

    public static Ole10Native createFromEmbeddedOleObject(POIFSFileSystem poifs) throws IOException, Ole10NativeException {
        boolean plain;
        try {
            poifs.getRoot().getEntry("\u0001Ole10ItemName");
            plain = true;
        } catch (FileNotFoundException e) {
            plain = false;
        }
        DocumentInputStream dis = poifs.createDocumentInputStream(OLE10_NATIVE);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(dis, bos);
        return new Ole10Native(bos.toByteArray(), 0, plain);
    }

    public Ole10Native(byte[] data, int offset) throws Ole10NativeException {
        this(data, offset, false);
    }

    public Ole10Native(byte[] data, int offset, boolean plain) throws Ole10NativeException {
        int ofs = offset;
        if (data.length >= offset + 2) {
            int i = LittleEndian.getInt(data, ofs);
            this.totalSize = i;
            int ofs2 = ofs + 4;
            if (plain) {
                byte[] bArr = new byte[(i - 4)];
                this.dataBuffer = bArr;
                System.arraycopy(data, 4, bArr, 0, bArr.length);
                this.dataSize = i - 4;
                byte[] oleLabel = new byte[8];
                System.arraycopy(bArr, 0, oleLabel, 0, Math.min(bArr.length, 8));
                String str = "ole-" + HexDump.toHex(oleLabel);
                this.label = str;
                this.fileName = str;
                this.command = str;
                return;
            }
            this.flags1 = LittleEndian.getShort(data, ofs2);
            int ofs3 = ofs2 + 2;
            int len = getStringLength(data, ofs3);
            this.label = StringUtil.getFromCompressedUnicode(data, ofs3, len - 1);
            int ofs4 = ofs3 + len;
            int len2 = getStringLength(data, ofs4);
            this.fileName = StringUtil.getFromCompressedUnicode(data, ofs4, len2 - 1);
            int ofs5 = ofs4 + len2;
            this.flags2 = LittleEndian.getShort(data, ofs5);
            int ofs6 = ofs5 + 2;
            int len3 = LittleEndian.getUnsignedByte(data, ofs6);
            this.unknown1 = new byte[len3];
            this.unknown2 = new byte[3];
            int ofs7 = ofs6 + len3 + 3;
            int len4 = getStringLength(data, ofs7);
            this.command = StringUtil.getFromCompressedUnicode(data, ofs7, len4 - 1);
            int ofs8 = ofs7 + len4;
            if ((i + 4) - ofs8 > 4) {
                int i2 = LittleEndian.getInt(data, ofs8);
                this.dataSize = i2;
                int ofs9 = ofs8 + 4;
                if (i2 > i || i2 < 0) {
                    throw new Ole10NativeException("Invalid Ole10Native");
                }
                byte[] bArr2 = new byte[i2];
                this.dataBuffer = bArr2;
                System.arraycopy(data, ofs9, bArr2, 0, i2);
                int ofs10 = ofs9 + i2;
                if (this.unknown1.length > 0) {
                    this.flags3 = LittleEndian.getShort(data, ofs10);
                    int ofs11 = ofs10 + 2;
                    return;
                }
                this.flags3 = 0;
                return;
            }
            throw new Ole10NativeException("Invalid Ole10Native");
        }
        throw new Ole10NativeException("data is too small");
    }

    private static int getStringLength(byte[] data, int ofs) {
        int len = 0;
        while (len + ofs < data.length && data[ofs + len] != 0) {
            len++;
        }
        return len + 1;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public short getFlags1() {
        return this.flags1;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFileName() {
        return this.fileName;
    }

    public short getFlags2() {
        return this.flags2;
    }

    public byte[] getUnknown1() {
        return this.unknown1;
    }

    public byte[] getUnknown2() {
        return this.unknown2;
    }

    public String getCommand() {
        return this.command;
    }

    public int getDataSize() {
        return this.dataSize;
    }

    public byte[] getDataBuffer() {
        return this.dataBuffer;
    }

    public short getFlags3() {
        return this.flags3;
    }
}
