package tech.ydb.yoj.databind.schema.reflect;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KCallable;
import kotlin.reflect.jvm.ReflectJvmMapping;
import tech.ydb.yoj.databind.FieldValueType;
import tech.ydb.yoj.databind.schema.Column;
import tech.ydb.yoj.databind.schema.FieldValueException;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Represents a Kotlin data class component for the purposes of YOJ data-binding.
 */
public final class KotlinDataClassComponent implements ReflectField {
    private final KCallable<?> callable;

    private final String name;
    private final Type genericType;
    private final Class<?> type;
    private final FieldValueType valueType;
    private final Column column;
    
    private final ReflectType<?> reflectType;

    public KotlinDataClassComponent(Reflector reflector, String name, KCallable<?> callable) {
        this.callable = callable;

        this.name = name;

        var kReturnType = callable.getReturnType();
        this.genericType = ReflectJvmMapping.getJavaType(kReturnType);
        this.type = JvmClassMappingKt.getJavaClass(kReturnType);
        this.column = type.getAnnotation(Column.class);
        this.valueType = FieldValueType.forJavaType(genericType, column);
        this.reflectType = reflector.reflectFieldType(genericType, valueType);
    }

    @Nullable
    @Override
    public Object getValue(Object containingObject) {
        try {
            return callable.call(containingObject);
        } catch (Exception e) {
            throw new FieldValueException(e, getName(), containingObject);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getGenericType() {
        return genericType;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public FieldValueType getValueType() {
        return valueType;
    }

    @Override
    public Column getColumn() {
        return column;
    }

    @Override
    public ReflectType<?> getReflectType() {
        return reflectType;
    }

    @Override
    public String toString() {
        return "KotlinDataClassComponent[val " + name + ": " + genericType.getTypeName() + "]";
    }
}