package dfined.omnipatcher.data;

import java.util.HashMap;
import java.util.Set;

public abstract class Registries {
    private static final HashMap<Class<?>, HashMap<Set<Object>,Object>> registries = new HashMap<>();
    public static void register(Class<?> registryClass, Set<Object> primaryKey, Object value){
                registries.putIfAbsent(registryClass, new HashMap<>());
                registries.get(registryClass).put(primaryKey, value);
    }

    public static <T> T get(Class<T> registry, Set<Object> primaryKey){
        return (T) registries.get(registry).get(primaryKey);
    }
}
