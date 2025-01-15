package org.apache.poi.poifs.crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;

public class Decryptor {
    public static final String DEFAULT_PASSWORD = "VelvetSweatshop";
    private final EncryptionInfo info;
    private byte[] passwordHash;

    public Decryptor(EncryptionInfo info2) {
        this.info = info2;
    }

    private void generatePasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try {
            byte[] passwordBytes = password.getBytes("UTF-16LE");
            sha1.update(this.info.getVerifier().getSalt());
            byte[] hash = sha1.digest(passwordBytes);
            byte[] iterator = new byte[4];
            for (int i = 0; i < 50000; i++) {
                sha1.reset();
                LittleEndian.putInt(iterator, i);
                sha1.update(iterator);
                hash = sha1.digest(hash);
            }
            this.passwordHash = hash;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Your JVM is broken - UTF16 not found!");
        }
    }

    private byte[] generateKey(int block) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(this.passwordHash);
        byte[] blockValue = new byte[4];
        LittleEndian.putInt(blockValue, block);
        byte[] finalHash = sha1.digest(blockValue);
        int requiredKeyLength = this.info.getHeader().getKeySize() / 8;
        byte[] buff = new byte[64];
        Arrays.fill(buff, (byte) 54);
        for (int i = 0; i < finalHash.length; i++) {
            buff[i] = (byte) (buff[i] ^ finalHash[i]);
        }
        sha1.reset();
        byte[] x1 = sha1.digest(buff);
        Arrays.fill(buff, (byte) 92);
        for (int i2 = 0; i2 < finalHash.length; i2++) {
            buff[i2] = (byte) (buff[i2] ^ finalHash[i2]);
        }
        sha1.reset();
        byte[] x2 = sha1.digest(buff);
        byte[] x3 = new byte[(x1.length + x2.length)];
        System.arraycopy(x1, 0, x3, 0, x1.length);
        System.arraycopy(x2, 0, x3, x1.length, x2.length);
        return truncateOrPad(x3, requiredKeyLength);
    }

    public boolean verifyPassword(String password) throws GeneralSecurityException {
        generatePasswordHash(password);
        Cipher cipher = getCipher();
        byte[] calcVerifierHash = MessageDigest.getInstance("SHA-1").digest(cipher.doFinal(this.info.getVerifier().getVerifier()));
        return Arrays.equals(calcVerifierHash, truncateOrPad(cipher.doFinal(this.info.getVerifier().getVerifierHash()), calcVerifierHash.length));
    }

    private byte[] truncateOrPad(byte[] source, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, 0, result, 0, Math.min(length, source.length));
        if (length > source.length) {
            for (int i = source.length; i < length; i++) {
                result[i] = 0;
            }
        }
        return result;
    }

    private Cipher getCipher() throws GeneralSecurityException {
        byte[] key = generateKey(0);
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(2, new SecretKeySpec(key, "AES"));
        return cipher;
    }

    public InputStream getDataStream(POIFSFileSystem fs) throws IOException, GeneralSecurityException {
        DocumentInputStream dis = fs.createDocumentInputStream("EncryptedPackage");
        long readLong = dis.readLong();
        return new CipherInputStream(dis, getCipher());
    }
}
