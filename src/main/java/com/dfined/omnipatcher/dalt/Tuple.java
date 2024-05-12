package com.dfined.omnipatcher.dalt;

public class Tuple<K, V> {
    K Key;
    V Value;

    public Tuple(K key, V value) {
        Key = key;
        Value = value;
    }

    public K getKey() {
        return Key;
    }

    public void setKey(K key) {
        Key = key;
    }

    public V getValue() {
        return Value;
    }

    public void setValue(V value) {
        Value = value;
    }
}
