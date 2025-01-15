package org.apache.poi.poifs.crypt;

import java.io.IOException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class EncryptionInfo {
    private final int encryptionFlags;
    private final EncryptionHeader header;
    private final EncryptionVerifier verifier;
    private final int versionMajor;
    private final int versionMinor;

    public EncryptionInfo(POIFSFileSystem fs) throws IOException {
        DocumentInputStream dis = fs.createDocumentInputStream("EncryptionInfo");
        this.versionMajor = dis.readShort();
        this.versionMinor = dis.readShort();
        this.encryptionFlags = dis.readInt();
        int readInt = dis.readInt();
        EncryptionHeader encryptionHeader = new EncryptionHeader(dis);
        this.header = encryptionHeader;
        if (encryptionHeader.getAlgorithm() == 26625) {
            this.verifier = new EncryptionVerifier(dis, 20);
        } else {
            this.verifier = new EncryptionVerifier(dis, 32);
        }
    }

    public int getVersionMajor() {
        return this.versionMajor;
    }

    public int getVersionMinor() {
        return this.versionMinor;
    }

    public int getEncryptionFlags() {
        return this.encryptionFlags;
    }

    public EncryptionHeader getHeader() {
        return this.header;
    }

    public EncryptionVerifier getVerifier() {
        return this.verifier;
    }
}
