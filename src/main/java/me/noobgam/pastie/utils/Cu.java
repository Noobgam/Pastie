package me.noobgam.pastie.utils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.*;
import java.util.stream.Collectors;

public final class Cu {
    private Cu() {

    }

    public static List<Integer> intArraylist() {
        return new IntArrayList();
    }

    public static List<Long> longArrayList() {
        return new LongArrayList();
    }

    public static List<Object> arrayList() {
        return new ArrayList<>();
    }

    public static <K, V> Map<K, V> map() {
        return Collections.emptyMap();
    }

    public static <K, V> Map<K, V> map(K key, V value) {
        return Collections.singletonMap(key, value);
    }

    public static <K, V> Map<K, V> map(
            K key1, V value1,
            K key2, V value2
    ) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1);
        hashMap.put(key2, value2);
        return hashMap;
    }

    public static <K, V> Map<K, V> map(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3
    ) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1);
        hashMap.put(key2, value2);
        hashMap.put(key3, value3);
        return hashMap;
    }

    public static <K, V> Map<K, V> map(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4
    ) {
        HashMap<K, V> hashMap = new HashMap<>();
        hashMap.put(key1, value1);
        hashMap.put(key2, value2);
        hashMap.put(key3, value3);
        hashMap.put(key4, value4);
        return hashMap;
    }

    public static <K> Set<K> set() {
        return Collections.emptySet();
    }

    public static <K> Set<K> set(K key) {
        return Collections.singleton(key);
    }

    public static <K> Set<K> set(K... keys) {
        return Arrays.stream(keys).collect(Collectors.toSet());
    }
}
