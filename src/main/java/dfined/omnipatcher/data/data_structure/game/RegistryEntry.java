package dfined.omnipatcher.data.data_structure.game;

import dfined.omnipatcher.data.Registries;
import dfined.omnipatcher.data.annotations.PrimaryRegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface RegistryEntry {
    Logger log = LogManager.getLogger(RegistryEntry.class);
    default void register(){
        Field[] fields = this.getClass().getDeclaredFields();
        Class tClass = this.getClass();
        Set<Object> primaryKeys = new HashSet<>();
        Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(PrimaryRegistryKey.class))
                .forEach(field -> {
                    try {
                        primaryKeys.add(field.get(this));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        Registries.register(this.getClass(), primaryKeys, this);
    }
}
