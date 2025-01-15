package org.apache.poi.poifs.crypt;

import org.apache.poi.poifs.filesystem.DocumentInputStream;

public class EncryptionVerifier {
    private final byte[] salt;
    private final byte[] verifier;
    private final byte[] verifierHash;
    private final int verifierHashSize;

    public EncryptionVerifier(DocumentInputStream is, int encryptedLength) {
        byte[] bArr = new byte[16];
        this.salt = bArr;
        byte[] bArr2 = new byte[16];
        this.verifier = bArr2;
        if (is.readInt() == 16) {
            is.readFully(bArr);
            is.readFully(bArr2);
            this.verifierHashSize = is.readInt();
            byte[] bArr3 = new byte[encryptedLength];
            this.verifierHash = bArr3;
            is.readFully(bArr3);
            return;
        }
        throw new RuntimeException("Salt size != 16 !?");
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public byte[] getVerifier() {
        return this.verifier;
    }

    public byte[] getVerifierHash() {
        return this.verifierHash;
    }
}
