package com.dfined.omnipatcher.v2.format.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapperField {
    String value() default "";
    String defaultValue() default "";
    Class<? extends Enum<?>> enumType();
}
