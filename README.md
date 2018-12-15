# cache-man

A simple implementation of general purpose caching in Java.

## Main

Main.java will test the following three methods of database access
and report their running times. The test access pattern is defined
in the function, `mockAccess`.

1. Direct access
2. Basic hash based caching
3. Hash based LRU caching

To avoid complexity, we used a minimal hash table size of 10. The
mock function involves read/write using 3 different keys, 2 of which
collide within the 10-entry hash table.

Note that the mock function does not accurately represent real world
scenarios.

## Interface Modules

#### Database<K, V>

An interface for basic database access, with two functions: `get`, `set`.

#### CacheLayer<K, V> extends Database

A database wrapper interface. Acts as a wrapper around a barebone,
high-latency Database. Uses inhertance for polymorphism, but actual
implementation involves composition of the underlying Database. It also
provides a flush function for lazy writes, in addition to cached reads.
Both implementations of CacheLayer described below support lazy writes.

## Implementation Modules

#### ZenFileStore<String, String> extends Database

A basic file-based string database with simulated access latency.

#### HashCacher extends CacheLayer

A hash table based cache layer. Simply replaces the cache contents
when there is a hash collision. Note that the size of the hash
table defines the capacity of the cache, hence it may not be feasible
to increase the size of the hash table to minimize cache misses.

#### LRUCacher extends CacheLayer

A limited size cache with an LRU replacement policy. Uses a linked list
based stack to track the access history and a hash table to effectively
reference the nodes. To deal with hash collisions, the hash table uses
linked list based buckets. The nodes in the stack also serve as nodes in
the buckets.

TODO: needs further explanation

## Limitations

Since the caching occurs in dynamic memory managed by the JVM and the
system, we are effectivly adding uneeded overhead.
