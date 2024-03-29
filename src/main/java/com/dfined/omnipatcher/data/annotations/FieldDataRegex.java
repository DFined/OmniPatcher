package com.dfined.omnipatcher.data.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldDataRegex {
    public String regex() default "";
    public boolean getKeys() default false;
    public String defaultValue() default "";
}
