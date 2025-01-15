package org.apache.poi.util;

public class ShortList {
    private static final int _default_size = 128;
    private short[] _array;
    private int _limit;

    public ShortList() {
        this(128);
    }

    public ShortList(ShortList list) {
        this(list._array.length);
        short[] sArr = list._array;
        short[] sArr2 = this._array;
        System.arraycopy(sArr, 0, sArr2, 0, sArr2.length);
        this._limit = list._limit;
    }

    public ShortList(int initialCapacity) {
        this._array = new short[initialCapacity];
        this._limit = 0;
    }

    public void add(int index, short value) {
        int i = this._limit;
        if (index > i) {
            throw new IndexOutOfBoundsException();
        } else if (index == i) {
            add(value);
        } else {
            if (i == this._array.length) {
                growArray(i * 2);
            }
            short[] sArr = this._array;
            System.arraycopy(sArr, index, sArr, index + 1, this._limit - index);
            this._array[index] = value;
            this._limit++;
        }
    }

    public boolean add(short value) {
        int i = this._limit;
        if (i == this._array.length) {
            growArray(i * 2);
        }
        short[] sArr = this._array;
        int i2 = this._limit;
        this._limit = i2 + 1;
        sArr[i2] = value;
        return true;
    }

    public boolean addAll(ShortList c) {
        int i = c._limit;
        if (i == 0) {
            return true;
        }
        int i2 = this._limit;
        if (i2 + i > this._array.length) {
            growArray(i2 + i);
        }
        System.arraycopy(c._array, 0, this._array, this._limit, c._limit);
        this._limit += c._limit;
        return true;
    }

    public boolean addAll(int index, ShortList c) {
        int i = this._limit;
        if (index <= i) {
            int i2 = c._limit;
            if (i2 == 0) {
                return true;
            }
            if (i + i2 > this._array.length) {
                growArray(i + i2);
            }
            short[] sArr = this._array;
            System.arraycopy(sArr, index, sArr, c._limit + index, this._limit - index);
            System.arraycopy(c._array, 0, this._array, index, c._limit);
            this._limit += c._limit;
            return true;
        }
        throw new IndexOutOfBoundsException();
    }

    public void clear() {
        this._limit = 0;
    }

    public boolean contains(short o) {
        boolean rval = false;
        int j = 0;
        while (!rval && j < this._limit) {
            if (this._array[j] == o) {
                rval = true;
            }
            j++;
        }
        return rval;
    }

    public boolean containsAll(ShortList c) {
        boolean rval = true;
        if (this != c) {
            int j = 0;
            while (rval && j < c._limit) {
                if (!contains(c._array[j])) {
                    rval = false;
                }
                j++;
            }
        }
        return rval;
    }

    public boolean equals(Object o) {
        boolean rval = this == o;
        if (!rval && o != null && o.getClass() == getClass()) {
            ShortList other = (ShortList) o;
            if (other._limit == this._limit) {
                boolean rval2 = true;
                int j = 0;
                while (rval && j < this._limit) {
                    rval2 = this._array[j] == other._array[j];
                    j++;
                }
            }
        }
        return rval;
    }

    public short get(int index) {
        if (index < this._limit) {
            return this._array[index];
        }
        throw new IndexOutOfBoundsException();
    }

    public int hashCode() {
        int hash = 0;
        for (int j = 0; j < this._limit; j++) {
            hash = (hash * 31) + this._array[j];
        }
        return hash;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0011 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int indexOf(short r4) {
        /*
            r3 = this;
            r0 = 0
        L_0x0001:
            int r1 = r3._limit
            if (r0 >= r1) goto L_0x000f
            short[] r2 = r3._array
            short r2 = r2[r0]
            if (r4 != r2) goto L_0x000c
            goto L_0x000f
        L_0x000c:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x000f:
            if (r0 != r1) goto L_0x0012
            r0 = -1
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.util.ShortList.indexOf(short):int");
    }

    public boolean isEmpty() {
        return this._limit == 0;
    }

    public int lastIndexOf(short o) {
        int rval = this._limit - 1;
        while (rval >= 0 && o != this._array[rval]) {
            rval--;
        }
        return rval;
    }

    public short remove(int index) {
        int i = this._limit;
        if (index < i) {
            short[] sArr = this._array;
            short rval = sArr[index];
            System.arraycopy(sArr, index + 1, sArr, index, i - index);
            this._limit--;
            return rval;
        }
        throw new IndexOutOfBoundsException();
    }

    public boolean removeValue(short o) {
        boolean rval = false;
        int j = 0;
        while (!rval) {
            int i = this._limit;
            if (j >= i) {
                break;
            }
            short[] sArr = this._array;
            if (o == sArr[j]) {
                System.arraycopy(sArr, j + 1, sArr, j, i - j);
                this._limit--;
                rval = true;
            }
            j++;
        }
        return rval;
    }

    public boolean removeAll(ShortList c) {
        boolean rval = false;
        for (int j = 0; j < c._limit; j++) {
            if (removeValue(c._array[j])) {
                rval = true;
            }
        }
        return rval;
    }

    public boolean retainAll(ShortList c) {
        boolean rval = false;
        int j = 0;
        while (j < this._limit) {
            if (!c.contains(this._array[j])) {
                remove(j);
                rval = true;
            } else {
                j++;
            }
        }
        return rval;
    }

    public short set(int index, short element) {
        if (index < this._limit) {
            short[] sArr = this._array;
            short rval = sArr[index];
            sArr[index] = element;
            return rval;
        }
        throw new IndexOutOfBoundsException();
    }

    public int size() {
        return this._limit;
    }

    public short[] toArray() {
        int i = this._limit;
        short[] rval = new short[i];
        System.arraycopy(this._array, 0, rval, 0, i);
        return rval;
    }

    public short[] toArray(short[] a) {
        int length = a.length;
        int i = this._limit;
        if (length != i) {
            return toArray();
        }
        System.arraycopy(this._array, 0, a, 0, i);
        return a;
    }

    private void growArray(int new_size) {
        short[] sArr = this._array;
        short[] new_array = new short[(new_size == sArr.length ? new_size + 1 : new_size)];
        System.arraycopy(sArr, 0, new_array, 0, this._limit);
        this._array = new_array;
    }
}
