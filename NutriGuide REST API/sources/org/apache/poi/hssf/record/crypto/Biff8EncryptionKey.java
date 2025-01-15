package org.apache.poi.hssf.record.crypto;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutputStream;

public final class Biff8EncryptionKey {
    private static final int KEY_DIGEST_LENGTH = 5;
    private static final int PASSWORD_HASH_NUMBER_OF_BYTES_USED = 5;
    private static final ThreadLocal<String> _userPasswordTLS = new ThreadLocal<>();
    private final byte[] _keyDigest;

    public static Biff8EncryptionKey create(byte[] docId) {
        return new Biff8EncryptionKey(createKeyDigest(Decryptor.DEFAULT_PASSWORD, docId));
    }

    public static Biff8EncryptionKey create(String password, byte[] docIdData) {
        return new Biff8EncryptionKey(createKeyDigest(password, docIdData));
    }

    Biff8EncryptionKey(byte[] keyDigest) {
        if (keyDigest.length == 5) {
            this._keyDigest = keyDigest;
            return;
        }
        throw new IllegalArgumentException("Expected 5 byte key digest, but got " + HexDump.toHex(keyDigest));
    }

    static byte[] createKeyDigest(String str, byte[] bArr) {
        check16Bytes(bArr, "docId");
        int min = Math.min(str.length(), 16);
        byte[] bArr2 = new byte[(min * 2)];
        for (int i = 0; i < min; i++) {
            char charAt = str.charAt(i);
            int i2 = i * 2;
            bArr2[i2 + 0] = (byte) ((charAt << 0) & 255);
            bArr2[i2 + 1] = (byte) ((charAt << 8) & 255);
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr2);
            byte[] digest = instance.digest();
            instance.reset();
            for (int i3 = 0; i3 < 16; i3++) {
                instance.update(digest, 0, 5);
                instance.update(bArr, 0, bArr.length);
            }
            byte[] bArr3 = new byte[5];
            System.arraycopy(instance.digest(), 0, bArr3, 0, 5);
            return bArr3;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validate(byte[] saltData, byte[] saltHash) {
        check16Bytes(saltData, "saltData");
        check16Bytes(saltHash, "saltHash");
        RC4 rc4 = createRC4(0);
        byte[] saltDataPrime = (byte[]) saltData.clone();
        rc4.encrypt(saltDataPrime);
        byte[] saltHashPrime = (byte[]) saltHash.clone();
        rc4.encrypt(saltHashPrime);
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(saltDataPrime);
            return Arrays.equals(saltHashPrime, md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] c = new byte[a.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = (byte) (a[i] ^ b[i]);
        }
        return c;
    }

    private static void check16Bytes(byte[] data, String argName) {
        if (data.length != 16) {
            throw new IllegalArgumentException("Expected 16 byte " + argName + ", but got " + HexDump.toHex(data));
        }
    }

    /* access modifiers changed from: package-private */
    public RC4 createRC4(int keyBlockNo) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(this._keyDigest);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
            new LittleEndianOutputStream(baos).writeInt(keyBlockNo);
            md5.update(baos.toByteArray());
            return new RC4(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCurrentUserPassword(String password) {
        _userPasswordTLS.set(password);
    }

    public static String getCurrentUserPassword() {
        return _userPasswordTLS.get();
    }
}
