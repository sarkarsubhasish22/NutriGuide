package androidx.core.content.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.text.TextUtils;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ShortcutInfoCompat {
    private static final String EXTRA_LONG_LIVED = "extraLongLived";
    private static final String EXTRA_PERSON_ = "extraPerson_";
    private static final String EXTRA_PERSON_COUNT = "extraPersonCount";
    ComponentName mActivity;
    Set<String> mCategories;
    Context mContext;
    CharSequence mDisabledMessage;
    IconCompat mIcon;
    String mId;
    Intent[] mIntents;
    boolean mIsAlwaysBadged;
    boolean mIsLongLived;
    CharSequence mLabel;
    CharSequence mLongLabel;
    Person[] mPersons;
    int mRank;

    ShortcutInfoCompat() {
    }

    public ShortcutInfo toShortcutInfo() {
        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(this.mContext, this.mId).setShortLabel(this.mLabel).setIntents(this.mIntents);
        IconCompat iconCompat = this.mIcon;
        if (iconCompat != null) {
            builder.setIcon(iconCompat.toIcon(this.mContext));
        }
        if (!TextUtils.isEmpty(this.mLongLabel)) {
            builder.setLongLabel(this.mLongLabel);
        }
        if (!TextUtils.isEmpty(this.mDisabledMessage)) {
            builder.setDisabledMessage(this.mDisabledMessage);
        }
        ComponentName componentName = this.mActivity;
        if (componentName != null) {
            builder.setActivity(componentName);
        }
        Set<String> set = this.mCategories;
        if (set != null) {
            builder.setCategories(set);
        }
        builder.setRank(this.mRank);
        if (Build.VERSION.SDK_INT >= 29) {
            Person[] personArr = this.mPersons;
            if (personArr != null && personArr.length > 0) {
                android.app.Person[] persons = new android.app.Person[personArr.length];
                for (int i = 0; i < persons.length; i++) {
                    persons[i] = this.mPersons[i].toAndroidPerson();
                }
                builder.setPersons(persons);
            }
            builder.setLongLived(this.mIsLongLived);
        } else {
            builder.setExtras(buildLegacyExtrasBundle());
        }
        return builder.build();
    }

    private PersistableBundle buildLegacyExtrasBundle() {
        PersistableBundle bundle = new PersistableBundle();
        Person[] personArr = this.mPersons;
        if (personArr != null && personArr.length > 0) {
            bundle.putInt(EXTRA_PERSON_COUNT, personArr.length);
            for (int i = 0; i < this.mPersons.length; i++) {
                bundle.putPersistableBundle(EXTRA_PERSON_ + (i + 1), this.mPersons[i].toPersistableBundle());
            }
        }
        bundle.putBoolean(EXTRA_LONG_LIVED, this.mIsLongLived);
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public Intent addToIntent(Intent outIntent) {
        Intent[] intentArr = this.mIntents;
        outIntent.putExtra("android.intent.extra.shortcut.INTENT", intentArr[intentArr.length - 1]).putExtra("android.intent.extra.shortcut.NAME", this.mLabel.toString());
        if (this.mIcon != null) {
            Drawable badge = null;
            if (this.mIsAlwaysBadged) {
                PackageManager pm = this.mContext.getPackageManager();
                ComponentName componentName = this.mActivity;
                if (componentName != null) {
                    try {
                        badge = pm.getActivityIcon(componentName);
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }
                if (badge == null) {
                    badge = this.mContext.getApplicationInfo().loadIcon(pm);
                }
            }
            this.mIcon.addToShortcutIntent(outIntent, badge, this.mContext);
        }
        return outIntent;
    }

    public String getId() {
        return this.mId;
    }

    public ComponentName getActivity() {
        return this.mActivity;
    }

    public CharSequence getShortLabel() {
        return this.mLabel;
    }

    public CharSequence getLongLabel() {
        return this.mLongLabel;
    }

    public CharSequence getDisabledMessage() {
        return this.mDisabledMessage;
    }

    public Intent getIntent() {
        Intent[] intentArr = this.mIntents;
        return intentArr[intentArr.length - 1];
    }

    public Intent[] getIntents() {
        Intent[] intentArr = this.mIntents;
        return (Intent[]) Arrays.copyOf(intentArr, intentArr.length);
    }

    public Set<String> getCategories() {
        return this.mCategories;
    }

    public int getRank() {
        return this.mRank;
    }

    public IconCompat getIcon() {
        return this.mIcon;
    }

    static Person[] getPersonsFromExtra(PersistableBundle bundle) {
        if (bundle == null || !bundle.containsKey(EXTRA_PERSON_COUNT)) {
            return null;
        }
        int personsLength = bundle.getInt(EXTRA_PERSON_COUNT);
        Person[] persons = new Person[personsLength];
        for (int i = 0; i < personsLength; i++) {
            persons[i] = Person.fromPersistableBundle(bundle.getPersistableBundle(EXTRA_PERSON_ + (i + 1)));
        }
        return persons;
    }

    static boolean getLongLivedFromExtra(PersistableBundle bundle) {
        if (bundle == null || !bundle.containsKey(EXTRA_LONG_LIVED)) {
            return false;
        }
        return bundle.getBoolean(EXTRA_LONG_LIVED);
    }

    public static class Builder {
        private final ShortcutInfoCompat mInfo;

        public Builder(Context context, String id) {
            ShortcutInfoCompat shortcutInfoCompat = new ShortcutInfoCompat();
            this.mInfo = shortcutInfoCompat;
            shortcutInfoCompat.mContext = context;
            shortcutInfoCompat.mId = id;
        }

        public Builder(ShortcutInfoCompat shortcutInfo) {
            ShortcutInfoCompat shortcutInfoCompat = new ShortcutInfoCompat();
            this.mInfo = shortcutInfoCompat;
            shortcutInfoCompat.mContext = shortcutInfo.mContext;
            shortcutInfoCompat.mId = shortcutInfo.mId;
            shortcutInfoCompat.mIntents = (Intent[]) Arrays.copyOf(shortcutInfo.mIntents, shortcutInfo.mIntents.length);
            shortcutInfoCompat.mActivity = shortcutInfo.mActivity;
            shortcutInfoCompat.mLabel = shortcutInfo.mLabel;
            shortcutInfoCompat.mLongLabel = shortcutInfo.mLongLabel;
            shortcutInfoCompat.mDisabledMessage = shortcutInfo.mDisabledMessage;
            shortcutInfoCompat.mIcon = shortcutInfo.mIcon;
            shortcutInfoCompat.mIsAlwaysBadged = shortcutInfo.mIsAlwaysBadged;
            shortcutInfoCompat.mIsLongLived = shortcutInfo.mIsLongLived;
            shortcutInfoCompat.mRank = shortcutInfo.mRank;
            if (shortcutInfo.mPersons != null) {
                shortcutInfoCompat.mPersons = (Person[]) Arrays.copyOf(shortcutInfo.mPersons, shortcutInfo.mPersons.length);
            }
            if (shortcutInfo.mCategories != null) {
                shortcutInfoCompat.mCategories = new HashSet(shortcutInfo.mCategories);
            }
        }

        public Builder(Context context, ShortcutInfo shortcutInfo) {
            ShortcutInfoCompat shortcutInfoCompat = new ShortcutInfoCompat();
            this.mInfo = shortcutInfoCompat;
            shortcutInfoCompat.mContext = context;
            shortcutInfoCompat.mId = shortcutInfo.getId();
            Intent[] intents = shortcutInfo.getIntents();
            shortcutInfoCompat.mIntents = (Intent[]) Arrays.copyOf(intents, intents.length);
            shortcutInfoCompat.mActivity = shortcutInfo.getActivity();
            shortcutInfoCompat.mLabel = shortcutInfo.getShortLabel();
            shortcutInfoCompat.mLongLabel = shortcutInfo.getLongLabel();
            shortcutInfoCompat.mDisabledMessage = shortcutInfo.getDisabledMessage();
            shortcutInfoCompat.mCategories = shortcutInfo.getCategories();
            shortcutInfoCompat.mPersons = ShortcutInfoCompat.getPersonsFromExtra(shortcutInfo.getExtras());
            shortcutInfoCompat.mRank = shortcutInfo.getRank();
        }

        public Builder setShortLabel(CharSequence shortLabel) {
            this.mInfo.mLabel = shortLabel;
            return this;
        }

        public Builder setLongLabel(CharSequence longLabel) {
            this.mInfo.mLongLabel = longLabel;
            return this;
        }

        public Builder setDisabledMessage(CharSequence disabledMessage) {
            this.mInfo.mDisabledMessage = disabledMessage;
            return this;
        }

        public Builder setIntent(Intent intent) {
            return setIntents(new Intent[]{intent});
        }

        public Builder setIntents(Intent[] intents) {
            this.mInfo.mIntents = intents;
            return this;
        }

        public Builder setIcon(IconCompat icon) {
            this.mInfo.mIcon = icon;
            return this;
        }

        public Builder setActivity(ComponentName activity) {
            this.mInfo.mActivity = activity;
            return this;
        }

        public Builder setAlwaysBadged() {
            this.mInfo.mIsAlwaysBadged = true;
            return this;
        }

        public Builder setPerson(Person person) {
            return setPersons(new Person[]{person});
        }

        public Builder setPersons(Person[] persons) {
            this.mInfo.mPersons = persons;
            return this;
        }

        public Builder setCategories(Set<String> categories) {
            this.mInfo.mCategories = categories;
            return this;
        }

        @Deprecated
        public Builder setLongLived() {
            this.mInfo.mIsLongLived = true;
            return this;
        }

        public Builder setLongLived(boolean longLived) {
            this.mInfo.mIsLongLived = longLived;
            return this;
        }

        public Builder setRank(int rank) {
            this.mInfo.mRank = rank;
            return this;
        }

        public ShortcutInfoCompat build() {
            if (TextUtils.isEmpty(this.mInfo.mLabel)) {
                throw new IllegalArgumentException("Shortcut must have a non-empty label");
            } else if (this.mInfo.mIntents != null && this.mInfo.mIntents.length != 0) {
                return this.mInfo;
            } else {
                throw new IllegalArgumentException("Shortcut must have an intent");
            }
        }
    }
}
