package com.dfined.omnipatcher.dalt;

import java.lang.reflect.Field;
import java.util.Locale;

public interface ALTBackedObject {
    ALTNode getBackingObject();

    static <T extends ALTBackedObject> void baseDeserialize(T obj, ALTNode node) {
        var fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            var annotations = field.getAnnotationsByType(ALTMapping.class);
            if (annotations.length > 0) {
                var val = node.tryGetValue(annotations[0].value());
                if (val != null) {
                    try {
                        field.setAccessible(true);
                        if (field.getType().isEnum()) {
                            try {
                                field.set(obj, Enum.valueOf((Class<Enum>) field.getType(), val.toUpperCase(Locale.ROOT)));
                            } catch (IllegalArgumentException e) {
                                //We expect exceptions here, when we get unknown enum values. This is fine
                            }
                        } else if (field.getType().equals(String.class)) {
                            field.set(obj, val);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to reflectively set field " + field.getName(), e);
                    }
                }
            }
        }
    }
}

