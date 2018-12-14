package com.namgyu;

public interface Database<K, V> {
    /*
    A database interface the provides read/write access via key-value pairs.
    Can be used along with a CacheLayer to enhance performance of high-latency
    mediums such as hard drive disk access, network access, DB queries etc.

    Ideally, this would extend the Map Collection, but I think that's outside the scope
    of this small project.
     */

    V get_value(K key);

    void set_value(K key, V value);
}
