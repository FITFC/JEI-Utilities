package com.github.vfyjxf.jeiutilities.helper;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ReflectionUtils {

    private static final Map<FieldDescriptor, Field> FIELD_CACHE = new HashMap<>();
    private static final Map<MethodDescriptor, Method> METHOD_CACHE = new HashMap<>();

    public static <T, E> T getField(@Nonnull Class<? extends E> clazz, @Nonnull E object, @Nonnull String fieldName) {
        return getField(object, new FieldDescriptor(clazz, fieldName, false));
    }

    public static <T> T getStaticField(@Nonnull Class<?> clazz, @Nonnull String fieldName) {
        return getField(null, new FieldDescriptor(clazz, fieldName, true));
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(@Nullable Object obj, @Nonnull FieldDescriptor fieldDescriptor) {
        try {
            Field field = FIELD_CACHE.get(fieldDescriptor);
            if (field == null) {
                field = ObfuscationReflectionHelper.findField(fieldDescriptor.clazz, fieldDescriptor.fieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                FIELD_CACHE.put(fieldDescriptor, field);
            }
            return (T) field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, Class<?> returnType, Class<?>... parameterTypes) {
        MethodDescriptor methodDescriptor = new MethodDescriptor(clazz, methodName, parameterTypes);
        Method method = METHOD_CACHE.get(methodDescriptor);
        if (method == null) {
            method = ObfuscationReflectionHelper.findMethod(clazz, methodName, returnType, parameterTypes);
            METHOD_CACHE.put(methodDescriptor, method);
        }
        return method;
    }

    private static class FieldDescriptor {
        @Nonnull
        private final Class<?> clazz;
        @Nonnull
        private final String fieldName;
        private final boolean isStatic;

        public FieldDescriptor(@Nonnull Class<?> clazz, @Nonnull String fieldName, boolean isStatic) {
            this.clazz = clazz;
            this.fieldName = fieldName;
            this.isStatic = isStatic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldDescriptor that = (FieldDescriptor) o;
            return isStatic == that.isStatic && clazz.equals(that.clazz) && fieldName.equals(that.fieldName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, fieldName, isStatic);
        }

    }

    private static class MethodDescriptor {
        @Nonnull
        private final Class<?> clazz;
        @Nonnull
        private final String methodName;
        @Nonnull
        private final Class<?>[] parameterTypes;

        public MethodDescriptor(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nonnull Class<?>[] parameterTypes) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodDescriptor that = (MethodDescriptor) o;
            return clazz.equals(that.clazz) &&
                    methodName.equals(that.methodName) &&
                    Arrays.equals(parameterTypes, that.parameterTypes);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(clazz, methodName);
            result = 31 * result + Arrays.hashCode(parameterTypes);
            return result;
        }

    }

}
