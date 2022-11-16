package com.dfined.omnipatcher.v2;

import java.util.HashMap;

public class HashTree<T> extends HashMap<String, HashTree<T>> {
    T value;
}
