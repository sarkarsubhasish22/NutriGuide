package androidx.core.util;

import android.os.Build;
import java.util.Arrays;

public class ObjectsCompat {
    private ObjectsCompat() {
    }

    public static boolean equals(Object a, Object b) {
        if (Build.VERSION.SDK_INT >= 19) {
            return C$r8$backportedMethods$utility$Objects$2$equals.equals(a, b);
        }
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object o) {
        if (o != null) {
            return o.hashCode();
        }
        return 0;
    }

    public static int hash(Object... values) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Arrays.hashCode(values);
        }
        return Arrays.hashCode(values);
    }
}
