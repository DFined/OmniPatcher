package com.dfined.omnipatcher.data.data_structure.game;

import com.dfined.omnipatcher.data.annotations.FieldDataRegex;
import com.dfined.omnipatcher.data.annotations.IgnoreDataMapping;
import com.google.common.base.CaseFormat;
import com.dfined.omnipatcher.data.annotations.DefaultValue;
import com.dfined.omnipatcher.data.data_structure.DataMap;
import com.dfined.omnipatcher.data.data_structure.HashDataMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

public interface HashDataMapBacked extends DataMap {
    Logger log = LogManager.getLogger(DataMap.class);

    default void init(HashDataMap dataMap) throws IllegalAccessException, InstantiationException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            String key = CaseFormat.LOWER_CAMEL.to(LOWER_UNDERSCORE, name);
            Class<?> type = field.getType();
            Annotation ignoreAnnotation = field.getAnnotation(IgnoreDataMapping.class);
            FieldDataRegex regexAnnotation = field.getAnnotation(FieldDataRegex.class);
            DefaultValue defaultValueAnnotation = field.getAnnotation(DefaultValue.class);
            boolean getKeys = false;
            if (regexAnnotation != null) {
                key = regexAnnotation.regex();
                getKeys = regexAnnotation.getKeys();
            }
            Object value = dataMap.getSingle(key, getKeys);
            if(value == null && defaultValueAnnotation != null){
                value = defaultValueAnnotation.value();
            }
            if (ignoreAnnotation != null || value == null) {
                continue;
            }
            if (value instanceof HashDataMap) {
                if (HashDataMapBacked.class.isAssignableFrom(type)) {
                    //Field is a HashDataMapBacked object;
                    HashDataMapBacked variable = (HashDataMapBacked) type.newInstance();
                    variable.init((HashDataMap) value);
                    field.set(this, variable);
                }
            } else if (type.equals(String.class)) {
                //Field is a String
                field.set(this, value);

            }
        }
    }

    @Override
    default Stream<Object> streamByRegex(String regex, boolean getKeys) {

        String[] paths = prepPath(regex);
        Stream<Object> values = Arrays.stream(this.getClass().getFields())
                .filter(field -> field.getName().matches(paths[0]))
                .map(field -> {
                            try {
                                return field.get(this);
                            } catch (IllegalAccessException e) {
                                log.error("Unable to acess fields in DataMap Object.", e);
                                return Stream.empty();
                            }
                        }
                );
        if (paths.length > 1) {
            return values
                    .filter(value -> value instanceof DataMap)
                    .flatMap(value -> ((DataMap) value).streamByRegex(paths[1], getKeys));
        }
        return values;
    }

}
