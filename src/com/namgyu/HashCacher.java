package com.namgyu;

import java.util.Vector;

public class HashCacher<K, V> extends CacheLayer<K, V> {
    /*
    Hash table based cache layer with a manually implement hash table.
    */

    private int table_size;
    private Vector<K> keys;
    private Vector<V> values;
    private Vector<Boolean> dirty;

    public HashCacher(Database<K, V> origin, int table_size) {
        super(origin);
        this.table_size = table_size;
        keys = new Vector<>(table_size);
        values = new Vector<>(table_size);

        /*
        Marks if a value in the cache has been modified, a not yet updated in
        the original database
         */
        dirty = new Vector<>(table_size);

        for (int i = 0; i < table_size; ++i) {
            keys.add(null);
            values.add(null);
            dirty.add(false);
        }
    }

    public HashCacher(Database<K, V> origin) {
        this(origin, 256);
    }

    @Override
    public V getValue(K key) {
        int h = key.hashCode() % table_size;
        if (keys.get(h) == key)
            return values.get(h);
        else {
            // lazy update
            if (dirty.get(h))
                origin.setValue(keys.get(h), values.get(h));
            dirty.set(h, false);

            // update cache
            V value = origin.getValue(key);
            keys.set(h, key);
            values.set(h, value);
            return value;
        }
    }

    @Override
    public void setValue(K key, V value, boolean immediate) {
        int h = key.hashCode() % table_size;
        if (immediate) {
            // update origin
            origin.setValue(key, value);

            // update cache if key exists in cache
            if (keys.get(h) == key) {
                values.set(h, value);
                dirty.set(h, false);
            }
        } else {
            if (keys.get(h) == key) {
                //update just the cache if key exists in cache
                values.set(h, value);
                dirty.set(h, true);
            } else {
                // update origin if key doesn't exist in cache
                origin.setValue(key, value);
            }
        }
    }

    @Override
    public void flush() {
        for (int i = 0; i < table_size; ++i)
            if (dirty.get(i))
                origin.setValue(keys.get(i), values.get(i));
    }
}
