package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor2) {
        this.constructorConstructor = constructorConstructor2;
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
        JsonAdapter annotation = (JsonAdapter) targetType.getRawType().getAnnotation(JsonAdapter.class);
        if (annotation == null) {
            return null;
        }
        return getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
    }

    static TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor2, Gson gson, TypeToken<?> fieldType, JsonAdapter annotation) {
        Class<?> value = annotation.value();
        if (TypeAdapter.class.isAssignableFrom(value)) {
            return (TypeAdapter) constructorConstructor2.get(TypeToken.get(value)).construct();
        }
        if (TypeAdapterFactory.class.isAssignableFrom(value)) {
            return ((TypeAdapterFactory) constructorConstructor2.get(TypeToken.get(value)).construct()).create(gson, fieldType);
        }
        throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
    }
}
