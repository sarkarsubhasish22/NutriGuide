package org.apache.poi.util;

import java.io.File;
import java.util.Random;

public final class TempFile {
    private static File dir;
    private static final Random rnd = new Random();

    public static File createTempFile(String prefix, String suffix) {
        if (dir == null) {
            File file = new File(System.getProperty("java.io.tmpdir"), "poifiles");
            dir = file;
            file.mkdir();
            if (System.getProperty("poi.keep.tmp.files") == null) {
                dir.deleteOnExit();
            }
        }
        File file2 = dir;
        File newFile = new File(file2, prefix + rnd.nextInt() + suffix);
        if (System.getProperty("poi.keep.tmp.files") == null) {
            newFile.deleteOnExit();
        }
        return newFile;
    }
}
