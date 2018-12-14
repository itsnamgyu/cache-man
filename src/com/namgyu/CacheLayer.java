package com.namgyu;

public abstract class CacheLayer<K, V> implements Database<K, V> {
    /*
    A caching layer to provide fast read/write access to a database.

    Rationale: why abstract class?
    There may be different ways to implement a caching layer between the user
    and the underlying database (origin). Such a caching layer would be
    beneficial if access to origin is very slow.
     */

    /*
    The original database (whose access performance we want to enhance).
     */
    Database<K, V> origin;

    public CacheLayer(Database<K, V> origin) {
        this.origin = origin;
    }

    /*
    Get value from some caching mechanism. If it is not available, this would require
    access from the original database.
     */
    public abstract V get_value(K key);

    /*
    Set value of some value in the database. The immediate flag requires that the
    original database be updated immediately. Otherwise, a lazy update method may be
    used where the value is only updated within the cache and written back to the
    database afterwards.

    Returns success.
     */
    public abstract void set_value(K key, V value, boolean immediate);

    /*
    Use lazy update by default
     */
    @Override
    public void set_value(K key, V value) {
        set_value(key, value, false);
    }

    /*
    Perform all lazy updates (mentioned in set_value) immediately.
     */
    public abstract void flush();
}
