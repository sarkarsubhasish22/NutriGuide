package androidx.core.view;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.WindowInsets;
import androidx.core.graphics.Insets;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class WindowInsetsCompat {
    public static final WindowInsetsCompat CONSUMED = new Builder().build().consumeDisplayCutout().consumeStableInsets().consumeSystemWindowInsets();
    private static final String TAG = "WindowInsetsCompat";
    private final Impl mImpl;

    private WindowInsetsCompat(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= 29) {
            this.mImpl = new Impl29(this, insets);
        } else if (Build.VERSION.SDK_INT >= 28) {
            this.mImpl = new Impl28(this, insets);
        } else if (Build.VERSION.SDK_INT >= 21) {
            this.mImpl = new Impl21(this, insets);
        } else if (Build.VERSION.SDK_INT >= 20) {
            this.mImpl = new Impl20(this, insets);
        } else {
            this.mImpl = new Impl(this);
        }
    }

    public WindowInsetsCompat(WindowInsetsCompat src) {
        if (src != null) {
            Impl srcImpl = src.mImpl;
            if (Build.VERSION.SDK_INT >= 29 && (srcImpl instanceof Impl29)) {
                this.mImpl = new Impl29(this, (Impl29) srcImpl);
            } else if (Build.VERSION.SDK_INT >= 28 && (srcImpl instanceof Impl28)) {
                this.mImpl = new Impl28(this, (Impl28) srcImpl);
            } else if (Build.VERSION.SDK_INT >= 21 && (srcImpl instanceof Impl21)) {
                this.mImpl = new Impl21(this, (Impl21) srcImpl);
            } else if (Build.VERSION.SDK_INT < 20 || !(srcImpl instanceof Impl20)) {
                this.mImpl = new Impl(this);
            } else {
                this.mImpl = new Impl20(this, (Impl20) srcImpl);
            }
        } else {
            this.mImpl = new Impl(this);
        }
    }

    public static WindowInsetsCompat toWindowInsetsCompat(WindowInsets insets) {
        return new WindowInsetsCompat((WindowInsets) Preconditions.checkNotNull(insets));
    }

    public int getSystemWindowInsetLeft() {
        return getSystemWindowInsets().left;
    }

    public int getSystemWindowInsetTop() {
        return getSystemWindowInsets().top;
    }

    public int getSystemWindowInsetRight() {
        return getSystemWindowInsets().right;
    }

    public int getSystemWindowInsetBottom() {
        return getSystemWindowInsets().bottom;
    }

    public boolean hasSystemWindowInsets() {
        return !getSystemWindowInsets().equals(Insets.NONE);
    }

    public boolean hasInsets() {
        return hasSystemWindowInsets() || hasStableInsets() || getDisplayCutout() != null || !getSystemGestureInsets().equals(Insets.NONE) || !getMandatorySystemGestureInsets().equals(Insets.NONE) || !getTappableElementInsets().equals(Insets.NONE);
    }

    public boolean isConsumed() {
        return this.mImpl.isConsumed();
    }

    public boolean isRound() {
        return this.mImpl.isRound();
    }

    public WindowInsetsCompat consumeSystemWindowInsets() {
        return this.mImpl.consumeSystemWindowInsets();
    }

    @Deprecated
    public WindowInsetsCompat replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        return new Builder(this).setSystemWindowInsets(Insets.of(left, top, right, bottom)).build();
    }

    @Deprecated
    public WindowInsetsCompat replaceSystemWindowInsets(Rect systemWindowInsets) {
        return new Builder(this).setSystemWindowInsets(Insets.of(systemWindowInsets)).build();
    }

    public int getStableInsetTop() {
        return getStableInsets().top;
    }

    public int getStableInsetLeft() {
        return getStableInsets().left;
    }

    public int getStableInsetRight() {
        return getStableInsets().right;
    }

    public int getStableInsetBottom() {
        return getStableInsets().bottom;
    }

    public boolean hasStableInsets() {
        return !getStableInsets().equals(Insets.NONE);
    }

    public WindowInsetsCompat consumeStableInsets() {
        return this.mImpl.consumeStableInsets();
    }

    public DisplayCutoutCompat getDisplayCutout() {
        return this.mImpl.getDisplayCutout();
    }

    public WindowInsetsCompat consumeDisplayCutout() {
        return this.mImpl.consumeDisplayCutout();
    }

    public Insets getSystemWindowInsets() {
        return this.mImpl.getSystemWindowInsets();
    }

    public Insets getStableInsets() {
        return this.mImpl.getStableInsets();
    }

    public Insets getMandatorySystemGestureInsets() {
        return this.mImpl.getMandatorySystemGestureInsets();
    }

    public Insets getTappableElementInsets() {
        return this.mImpl.getTappableElementInsets();
    }

    public Insets getSystemGestureInsets() {
        return this.mImpl.getSystemGestureInsets();
    }

    public WindowInsetsCompat inset(Insets insets) {
        return inset(insets.left, insets.top, insets.right, insets.bottom);
    }

    public WindowInsetsCompat inset(int left, int top, int right, int bottom) {
        return this.mImpl.inset(left, top, right, bottom);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WindowInsetsCompat)) {
            return false;
        }
        return ObjectsCompat.equals(this.mImpl, ((WindowInsetsCompat) o).mImpl);
    }

    public int hashCode() {
        Impl impl = this.mImpl;
        if (impl == null) {
            return 0;
        }
        return impl.hashCode();
    }

    public WindowInsets toWindowInsets() {
        Impl impl = this.mImpl;
        if (impl instanceof Impl20) {
            return ((Impl20) impl).mPlatformInsets;
        }
        return null;
    }

    private static class Impl {
        final WindowInsetsCompat mHost;

        Impl(WindowInsetsCompat host) {
            this.mHost = host;
        }

        /* access modifiers changed from: package-private */
        public boolean isRound() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean isConsumed() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeSystemWindowInsets() {
            return this.mHost;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeStableInsets() {
            return this.mHost;
        }

        /* access modifiers changed from: package-private */
        public DisplayCutoutCompat getDisplayCutout() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeDisplayCutout() {
            return this.mHost;
        }

        /* access modifiers changed from: package-private */
        public Insets getSystemWindowInsets() {
            return Insets.NONE;
        }

        /* access modifiers changed from: package-private */
        public Insets getStableInsets() {
            return Insets.NONE;
        }

        /* access modifiers changed from: package-private */
        public Insets getSystemGestureInsets() {
            return getSystemWindowInsets();
        }

        /* access modifiers changed from: package-private */
        public Insets getMandatorySystemGestureInsets() {
            return getSystemWindowInsets();
        }

        /* access modifiers changed from: package-private */
        public Insets getTappableElementInsets() {
            return getSystemWindowInsets();
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat inset(int left, int top, int right, int bottom) {
            return WindowInsetsCompat.CONSUMED;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Impl)) {
                return false;
            }
            Impl impl = (Impl) o;
            if (isRound() != impl.isRound() || isConsumed() != impl.isConsumed() || !ObjectsCompat.equals(getSystemWindowInsets(), impl.getSystemWindowInsets()) || !ObjectsCompat.equals(getStableInsets(), impl.getStableInsets()) || !ObjectsCompat.equals(getDisplayCutout(), impl.getDisplayCutout())) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ObjectsCompat.hash(Boolean.valueOf(isRound()), Boolean.valueOf(isConsumed()), getSystemWindowInsets(), getStableInsets(), getDisplayCutout());
        }
    }

    private static class Impl20 extends Impl {
        final WindowInsets mPlatformInsets;
        private Insets mSystemWindowInsets;

        Impl20(WindowInsetsCompat host, WindowInsets insets) {
            super(host);
            this.mSystemWindowInsets = null;
            this.mPlatformInsets = insets;
        }

        Impl20(WindowInsetsCompat host, Impl20 other) {
            this(host, new WindowInsets(other.mPlatformInsets));
        }

        /* access modifiers changed from: package-private */
        public boolean isRound() {
            return this.mPlatformInsets.isRound();
        }

        /* access modifiers changed from: package-private */
        public final Insets getSystemWindowInsets() {
            if (this.mSystemWindowInsets == null) {
                this.mSystemWindowInsets = Insets.of(this.mPlatformInsets.getSystemWindowInsetLeft(), this.mPlatformInsets.getSystemWindowInsetTop(), this.mPlatformInsets.getSystemWindowInsetRight(), this.mPlatformInsets.getSystemWindowInsetBottom());
            }
            return this.mSystemWindowInsets;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat inset(int left, int top, int right, int bottom) {
            Builder b = new Builder(WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets));
            b.setSystemWindowInsets(WindowInsetsCompat.insetInsets(getSystemWindowInsets(), left, top, right, bottom));
            b.setStableInsets(WindowInsetsCompat.insetInsets(getStableInsets(), left, top, right, bottom));
            return b.build();
        }
    }

    private static class Impl21 extends Impl20 {
        private Insets mStableInsets = null;

        Impl21(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
        }

        Impl21(WindowInsetsCompat host, Impl21 other) {
            super(host, (Impl20) other);
        }

        /* access modifiers changed from: package-private */
        public boolean isConsumed() {
            return this.mPlatformInsets.isConsumed();
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeStableInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeStableInsets());
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeSystemWindowInsets() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeSystemWindowInsets());
        }

        /* access modifiers changed from: package-private */
        public final Insets getStableInsets() {
            if (this.mStableInsets == null) {
                this.mStableInsets = Insets.of(this.mPlatformInsets.getStableInsetLeft(), this.mPlatformInsets.getStableInsetTop(), this.mPlatformInsets.getStableInsetRight(), this.mPlatformInsets.getStableInsetBottom());
            }
            return this.mStableInsets;
        }
    }

    private static class Impl28 extends Impl21 {
        Impl28(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
        }

        Impl28(WindowInsetsCompat host, Impl28 other) {
            super(host, (Impl21) other);
        }

        /* access modifiers changed from: package-private */
        public DisplayCutoutCompat getDisplayCutout() {
            return DisplayCutoutCompat.wrap(this.mPlatformInsets.getDisplayCutout());
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat consumeDisplayCutout() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.consumeDisplayCutout());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Impl28)) {
                return false;
            }
            return C$r8$backportedMethods$utility$Objects$2$equals.equals(this.mPlatformInsets, ((Impl28) o).mPlatformInsets);
        }

        public int hashCode() {
            return this.mPlatformInsets.hashCode();
        }
    }

    private static class Impl29 extends Impl28 {
        private Insets mMandatorySystemGestureInsets = null;
        private Insets mSystemGestureInsets = null;
        private Insets mTappableElementInsets = null;

        Impl29(WindowInsetsCompat host, WindowInsets insets) {
            super(host, insets);
        }

        Impl29(WindowInsetsCompat host, Impl29 other) {
            super(host, (Impl28) other);
        }

        /* access modifiers changed from: package-private */
        public Insets getSystemGestureInsets() {
            if (this.mSystemGestureInsets == null) {
                this.mSystemGestureInsets = Insets.toCompatInsets(this.mPlatformInsets.getSystemGestureInsets());
            }
            return this.mSystemGestureInsets;
        }

        /* access modifiers changed from: package-private */
        public Insets getMandatorySystemGestureInsets() {
            if (this.mMandatorySystemGestureInsets == null) {
                this.mMandatorySystemGestureInsets = Insets.toCompatInsets(this.mPlatformInsets.getMandatorySystemGestureInsets());
            }
            return this.mMandatorySystemGestureInsets;
        }

        /* access modifiers changed from: package-private */
        public Insets getTappableElementInsets() {
            if (this.mTappableElementInsets == null) {
                this.mTappableElementInsets = Insets.toCompatInsets(this.mPlatformInsets.getTappableElementInsets());
            }
            return this.mTappableElementInsets;
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat inset(int left, int top, int right, int bottom) {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatformInsets.inset(left, top, right, bottom));
        }
    }

    static Insets insetInsets(Insets insets, int left, int top, int right, int bottom) {
        int newLeft = Math.max(0, insets.left - left);
        int newTop = Math.max(0, insets.top - top);
        int newRight = Math.max(0, insets.right - right);
        int newBottom = Math.max(0, insets.bottom - bottom);
        if (newLeft == left && newTop == top && newRight == right && newBottom == bottom) {
            return insets;
        }
        return Insets.of(newLeft, newTop, newRight, newBottom);
    }

    public static final class Builder {
        private final BuilderImpl mImpl;

        public Builder() {
            if (Build.VERSION.SDK_INT >= 29) {
                this.mImpl = new BuilderImpl29();
            } else if (Build.VERSION.SDK_INT >= 20) {
                this.mImpl = new BuilderImpl20();
            } else {
                this.mImpl = new BuilderImpl();
            }
        }

        public Builder(WindowInsetsCompat insets) {
            if (Build.VERSION.SDK_INT >= 29) {
                this.mImpl = new BuilderImpl29(insets);
            } else if (Build.VERSION.SDK_INT >= 20) {
                this.mImpl = new BuilderImpl20(insets);
            } else {
                this.mImpl = new BuilderImpl(insets);
            }
        }

        public Builder setSystemWindowInsets(Insets insets) {
            this.mImpl.setSystemWindowInsets(insets);
            return this;
        }

        public Builder setSystemGestureInsets(Insets insets) {
            this.mImpl.setSystemGestureInsets(insets);
            return this;
        }

        public Builder setMandatorySystemGestureInsets(Insets insets) {
            this.mImpl.setMandatorySystemGestureInsets(insets);
            return this;
        }

        public Builder setTappableElementInsets(Insets insets) {
            this.mImpl.setTappableElementInsets(insets);
            return this;
        }

        public Builder setStableInsets(Insets insets) {
            this.mImpl.setStableInsets(insets);
            return this;
        }

        public Builder setDisplayCutout(DisplayCutoutCompat displayCutout) {
            this.mImpl.setDisplayCutout(displayCutout);
            return this;
        }

        public WindowInsetsCompat build() {
            return this.mImpl.build();
        }
    }

    private static class BuilderImpl {
        private final WindowInsetsCompat mInsets;

        BuilderImpl() {
            this(new WindowInsetsCompat((WindowInsetsCompat) null));
        }

        BuilderImpl(WindowInsetsCompat insets) {
            this.mInsets = insets;
        }

        /* access modifiers changed from: package-private */
        public void setSystemWindowInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setSystemGestureInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setMandatorySystemGestureInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setTappableElementInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setStableInsets(Insets insets) {
        }

        /* access modifiers changed from: package-private */
        public void setDisplayCutout(DisplayCutoutCompat displayCutout) {
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat build() {
            return this.mInsets;
        }
    }

    private static class BuilderImpl20 extends BuilderImpl {
        private static Constructor<WindowInsets> sConstructor;
        private static boolean sConstructorFetched = false;
        private static Field sConsumedField;
        private static boolean sConsumedFieldFetched = false;
        private WindowInsets mInsets;

        BuilderImpl20() {
            this.mInsets = createWindowInsetsInstance();
        }

        BuilderImpl20(WindowInsetsCompat insets) {
            this.mInsets = insets.toWindowInsets();
        }

        /* access modifiers changed from: package-private */
        public void setSystemWindowInsets(Insets insets) {
            WindowInsets windowInsets = this.mInsets;
            if (windowInsets != null) {
                this.mInsets = windowInsets.replaceSystemWindowInsets(insets.left, insets.top, insets.right, insets.bottom);
            }
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat build() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mInsets);
        }

        private static WindowInsets createWindowInsetsInstance() {
            if (!sConsumedFieldFetched) {
                try {
                    sConsumedField = WindowInsets.class.getDeclaredField("CONSUMED");
                } catch (ReflectiveOperationException e) {
                    Log.i(WindowInsetsCompat.TAG, "Could not retrieve WindowInsets.CONSUMED field", e);
                }
                sConsumedFieldFetched = true;
            }
            Field field = sConsumedField;
            if (field != null) {
                try {
                    WindowInsets consumed = (WindowInsets) field.get((Object) null);
                    if (consumed != null) {
                        return new WindowInsets(consumed);
                    }
                } catch (ReflectiveOperationException e2) {
                    Log.i(WindowInsetsCompat.TAG, "Could not get value from WindowInsets.CONSUMED field", e2);
                }
            }
            if (!sConstructorFetched) {
                try {
                    sConstructor = WindowInsets.class.getConstructor(new Class[]{Rect.class});
                } catch (ReflectiveOperationException e3) {
                    Log.i(WindowInsetsCompat.TAG, "Could not retrieve WindowInsets(Rect) constructor", e3);
                }
                sConstructorFetched = true;
            }
            Constructor<WindowInsets> constructor = sConstructor;
            if (constructor != null) {
                try {
                    return constructor.newInstance(new Object[]{new Rect()});
                } catch (ReflectiveOperationException e4) {
                    Log.i(WindowInsetsCompat.TAG, "Could not invoke WindowInsets(Rect) constructor", e4);
                }
            }
            return null;
        }
    }

    private static class BuilderImpl29 extends BuilderImpl {
        final WindowInsets.Builder mPlatBuilder;

        BuilderImpl29() {
            this.mPlatBuilder = new WindowInsets.Builder();
        }

        BuilderImpl29(WindowInsetsCompat insets) {
            WindowInsets.Builder builder;
            WindowInsets platInsets = insets.toWindowInsets();
            if (platInsets != null) {
                builder = new WindowInsets.Builder(platInsets);
            } else {
                builder = new WindowInsets.Builder();
            }
            this.mPlatBuilder = builder;
        }

        /* access modifiers changed from: package-private */
        public void setSystemWindowInsets(Insets insets) {
            this.mPlatBuilder.setSystemWindowInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setSystemGestureInsets(Insets insets) {
            this.mPlatBuilder.setSystemGestureInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setMandatorySystemGestureInsets(Insets insets) {
            this.mPlatBuilder.setMandatorySystemGestureInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setTappableElementInsets(Insets insets) {
            this.mPlatBuilder.setTappableElementInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setStableInsets(Insets insets) {
            this.mPlatBuilder.setStableInsets(insets.toPlatformInsets());
        }

        /* access modifiers changed from: package-private */
        public void setDisplayCutout(DisplayCutoutCompat displayCutout) {
            this.mPlatBuilder.setDisplayCutout(displayCutout != null ? displayCutout.unwrap() : null);
        }

        /* access modifiers changed from: package-private */
        public WindowInsetsCompat build() {
            return WindowInsetsCompat.toWindowInsetsCompat(this.mPlatBuilder.build());
        }
    }
}
