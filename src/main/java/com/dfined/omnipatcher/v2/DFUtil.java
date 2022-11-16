package com.dfined.omnipatcher.v2;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DFUtil {
    private static final Random random = new Random();

    public static <S, R> List<R> convert(Collection<S> collection, Function<S, R> mapper) {
        return StreamEx.of(collection).map(mapper).toList();
    }

    public static <T> List<T> filter(Collection<T> collection, Predicate<T> filter){
        return StreamEx.of(collection).filter(filter).toList();
    }

    public static <K,V> HashMap<K,V> filter(Map<K,V> map, Predicate<V> filter){
        return EntryStream.of(map).filterValues(filter).toCustomMap(HashMap::new);
    }

    public static <S, R> List<R> convertNonNull(Collection<S> collection, Function<S, R> mapper) {
        return StreamEx.of(collection).map(mapper).nonNull().toList();
    }

    public static <K, V> HashMap<K, V> mapify(Collection<V> vals, Function<V, K> keyMapper) {
        return StreamEx.of(vals).mapToEntry(keyMapper, Function.identity()).toCustomMap(HashMap::new);
    }

    public static long clamp(long min, long max, long value){
        return Math.min(max,Math.max(min,value));
    }

    public static <K, V> V getOrInit(Map<K, V> map, K key, Supplier<V> initializer) {
        if (!map.containsKey(key)) {
            map.put(key, initializer.get());
        }
        return map.get(key);
    }

    public static <K, V> Map<K, List<V>> groupMapify(Collection<V> vals, Function<V, K> keyMapper) {
        return StreamEx.of(vals).groupingBy(keyMapper);
    }

    public static <T extends Enum<T>> T safeGetEnum(String value, Class<T> type) {
        if (value == null) {
            return null;
        }
        value = value.toUpperCase();
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> T randomOfList(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        int ord = random.nextInt(list.size());
        return list.get(ord);
    }

    public static <T> StreamEx<T> streamExByType(Collection<?> objects, Class<T> type) {
        return StreamEx.of(objects)
                .filter(type::isInstance)
                .map(type::cast);
    }

    public static Object unsafeGetEnum(String value, Class<? extends Enum> type) {
        if (value == null) {
            return null;
        }
        value = value.toUpperCase();
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T extends Enum<T>> T getEnumWithDefault(String value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        value = value.toUpperCase();
        try {
            return Enum.valueOf(defaultValue.getDeclaringClass(), value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }

    }
}
