package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.C$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    private final Excluder excluder;
    private final FieldNamingStrategy fieldNamingPolicy;

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor2, FieldNamingStrategy fieldNamingPolicy2, Excluder excluder2) {
        this.constructorConstructor = constructorConstructor2;
        this.fieldNamingPolicy = fieldNamingPolicy2;
        this.excluder = excluder2;
    }

    public boolean excludeField(Field f, boolean serialize) {
        return excludeField(f, serialize, this.excluder);
    }

    static boolean excludeField(Field f, boolean serialize, Excluder excluder2) {
        return !excluder2.excludeClass(f.getType(), serialize) && !excluder2.excludeField(f, serialize);
    }

    private List<String> getFieldNames(Field f) {
        return getFieldName(this.fieldNamingPolicy, f);
    }

    static List<String> getFieldName(FieldNamingStrategy fieldNamingPolicy2, Field f) {
        SerializedName serializedName = (SerializedName) f.getAnnotation(SerializedName.class);
        List<String> fieldNames = new LinkedList<>();
        if (serializedName == null) {
            fieldNames.add(fieldNamingPolicy2.translateName(f));
        } else {
            fieldNames.add(serializedName.value());
            for (String alternate : serializedName.alternate()) {
                fieldNames.add(alternate);
            }
        }
        return fieldNames;
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        return new Adapter(this.constructorConstructor.get(type), getBoundFields(gson, type, raw));
    }

    private BoundField createBoundField(Gson context, Field field, String name, TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
        return new BoundField(name, serialize, deserialize, context, field, fieldType, Primitives.isPrimitive(fieldType.getRawType())) {
            final TypeAdapter<?> typeAdapter;
            final /* synthetic */ Gson val$context;
            final /* synthetic */ Field val$field;
            final /* synthetic */ TypeToken val$fieldType;
            final /* synthetic */ boolean val$isPrimitive;

            {
                this.val$context = r5;
                this.val$field = r6;
                this.val$fieldType = r7;
                this.val$isPrimitive = r8;
                this.typeAdapter = ReflectiveTypeAdapterFactory.this.getFieldAdapter(r5, r6, r7);
            }

            /* access modifiers changed from: package-private */
            public void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
                new TypeAdapterRuntimeTypeWrapper(this.val$context, this.typeAdapter, this.val$fieldType.getType()).write(writer, this.val$field.get(value));
            }

            /* access modifiers changed from: package-private */
            public void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
                Object fieldValue = this.typeAdapter.read(reader);
                if (fieldValue != null || !this.val$isPrimitive) {
                    this.val$field.set(value, fieldValue);
                }
            }

            public boolean writeField(Object value) throws IOException, IllegalAccessException {
                if (this.serialized && this.val$field.get(value) != value) {
                    return true;
                }
                return false;
            }
        };
    }

    /* access modifiers changed from: private */
    public TypeAdapter<?> getFieldAdapter(Gson gson, Field field, TypeToken<?> fieldType) {
        TypeAdapter<?> adapter;
        JsonAdapter annotation = (JsonAdapter) field.getAnnotation(JsonAdapter.class);
        if (annotation == null || (adapter = JsonAdapterAnnotationTypeAdapterFactory.getTypeAdapter(this.constructorConstructor, gson, fieldType, annotation)) == null) {
            return gson.getAdapter(fieldType);
        }
        return adapter;
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
        ReflectiveTypeAdapterFactory reflectiveTypeAdapterFactory = this;
        Map<String, BoundField> result = new LinkedHashMap<>();
        if (raw.isInterface()) {
            return result;
        }
        Type declaredType = type.getType();
        TypeToken<?> type2 = type;
        Class<? super Object> cls = raw;
        while (cls != Object.class) {
            Field[] arr$ = cls.getDeclaredFields();
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                Field field = arr$[i$];
                boolean serialize = reflectiveTypeAdapterFactory.excludeField(field, true);
                boolean deserialize = reflectiveTypeAdapterFactory.excludeField(field, false);
                if (serialize || deserialize) {
                    field.setAccessible(true);
                    Type fieldType = C$Gson$Types.resolve(type2.getType(), cls, field.getGenericType());
                    List<String> fieldNames = reflectiveTypeAdapterFactory.getFieldNames(field);
                    BoundField previous = null;
                    int i = 0;
                    while (i < fieldNames.size()) {
                        String name = fieldNames.get(i);
                        boolean serialize2 = i != 0 ? false : serialize;
                        String name2 = name;
                        int i2 = i;
                        BoundField previous2 = previous;
                        List<String> fieldNames2 = fieldNames;
                        Field field2 = field;
                        previous = previous2 == null ? result.put(name2, createBoundField(context, field, name2, TypeToken.get(fieldType), serialize2, deserialize)) : previous2;
                        i = i2 + 1;
                        serialize = serialize2;
                        fieldNames = fieldNames2;
                        field = field2;
                    }
                    int i3 = i;
                    BoundField previous3 = previous;
                    List<String> list = fieldNames;
                    Field field3 = field;
                    if (previous3 != null) {
                        throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous3.name);
                    }
                }
                i$++;
                reflectiveTypeAdapterFactory = this;
            }
            type2 = TypeToken.get(C$Gson$Types.resolve(type2.getType(), cls, cls.getGenericSuperclass()));
            cls = type2.getRawType();
            reflectiveTypeAdapterFactory = this;
        }
        return result;
    }

    static abstract class BoundField {
        final boolean deserialized;
        final String name;
        final boolean serialized;

        /* access modifiers changed from: package-private */
        public abstract void read(JsonReader jsonReader, Object obj) throws IOException, IllegalAccessException;

        /* access modifiers changed from: package-private */
        public abstract void write(JsonWriter jsonWriter, Object obj) throws IOException, IllegalAccessException;

        /* access modifiers changed from: package-private */
        public abstract boolean writeField(Object obj) throws IOException, IllegalAccessException;

        protected BoundField(String name2, boolean serialized2, boolean deserialized2) {
            this.name = name2;
            this.serialized = serialized2;
            this.deserialized = deserialized2;
        }
    }

    public static final class Adapter<T> extends TypeAdapter<T> {
        private final Map<String, BoundField> boundFields;
        private final ObjectConstructor<T> constructor;

        private Adapter(ObjectConstructor<T> constructor2, Map<String, BoundField> boundFields2) {
            this.constructor = constructor2;
            this.boundFields = boundFields2;
        }

        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            T instance = this.constructor.construct();
            try {
                in.beginObject();
                while (in.hasNext()) {
                    BoundField field = this.boundFields.get(in.nextName());
                    if (field != null) {
                        if (field.deserialized) {
                            field.read(in, instance);
                        }
                    }
                    in.skipValue();
                }
                in.endObject();
                return instance;
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException((Throwable) e);
            } catch (IllegalAccessException e2) {
                throw new AssertionError(e2);
            }
        }

        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            try {
                for (BoundField boundField : this.boundFields.values()) {
                    if (boundField.writeField(value)) {
                        out.name(boundField.name);
                        boundField.write(out, value);
                    }
                }
                out.endObject();
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }
    }
}
