package com.dfined.omnipatcher.v2.format.mapper;

import com.dfined.omnipatcher.v2.DFUtil;
import com.dfined.omnipatcher.v2.format.mapper.annotation.MapperField;
import com.dfined.omnipatcher.v2.format.raw.VDFObjectNode;
import com.google.common.base.CaseFormat;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;

@RequiredArgsConstructor
public class AutoMapper<T> extends Mapper<T> {
    public final Class<T> type;

    @Override
    public T map(VDFObjectNode node) {
        try {
            var obj = type.getConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                var fieldName = field.getName();
                var fieldMapped = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
                String value = node.getValueOrDefault(fieldMapped, null);
                var fieldAnnotations = field.getAnnotationsByType(MapperField.class);
                var annotation = fieldAnnotations.length > 0 ? fieldAnnotations[0] : null;
                if (value == null) {
                    if (annotation != null) {
                        value = annotation.defaultValue();
                    } else {
                        if (field.getType().isPrimitive()) {
                            throw new IllegalStateException("Automapping error: field " + field.getName() + "has no default value and is not in node " + node);
                        }
                    }
                }
                field.setAccessible(true);
                if (field.getType().isEnum()) {
//                    if (annotation == null) {
//                        throw new IllegalStateException("Automapping error: field " + field.getName() + " enum fields must be annotated with type");
//                    }
                    field.set(obj, DFUtil.unsafeGetEnum(value, (Class<? extends Enum>) field.getType()));
                } else {
                    field.set(obj, value);
                }
            }
            return obj;
        } catch (Exception e) {
            System.out.println("Got an exception trying to automap node: " + node.toString());
        }
        return null;
    }

    public static <T> List<T> lookup(Class<T> type, VDFObjectNode node, String paths, List<String> blacklistNames){
        return new AutoMapper<>(type).lookup(node, paths, blacklistNames);
    }
}
