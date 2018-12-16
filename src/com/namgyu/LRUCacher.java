package com.namgyu;

import java.util.Vector;

public class LRUCacher<K, V> extends CacheLayer<K, V> {

    /*
    Whether a set operation should count as a recent reference
     */
    private final static boolean updateOnSet = true;

    /*
    An LRU cache mechanism with custom implementation of a linked-list.

    This involves a
     */

    private class BucketNode {
        private BucketNode next;
        private BucketNode last;
        private K key;
        private V value;

        private boolean dirty;

        void reset(K key, V value) {
            this.key = key;
            this.value = value;
        }

        void set(V value, boolean setDirty) {
            this.value = value;
            if (setDirty) this.dirty = true;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }

        boolean resetDirty() {
            boolean wasDirty = dirty;
            dirty = false;
            return wasDirty;
        }

        BucketNode searchBucket(K key) {
            if (this.key == key) return this;
            else if (next != null) return next.searchBucket(key);
            return null;
        }

        void removeFromBucket() {
            if (next != null) next.last = last;
            if (last != null) last.next = next;
        }

        /*
        Append to the front of the bucket.
         */
        void appendToBucket(BucketNode node) {
            if (next != null) next.last = node;
            node.next = next;
            next = node;
        }
    }

    private class StackNode extends BucketNode {
        private StackNode above;
        private StackNode below;

        void removeFromStack() {
            if (above != null) above.below = below;
            if (below != null) below.above = above;
        }

        StackNode popBelow() {
            StackNode toPop = below;
            if (toPop != null) {
                below = toPop.below;
                if (below != null) below.above = this;
            }
            return toPop;
        }

        void pushBelow(StackNode node) {
            // update new node
            node.below = below;
            node.above = this;

            // update list
            if (below != null) below.above = node;
            below = node;
        }
    }

    // The size of the hash table
    private int tableSize;

    // The nodes in the table should not hold any data itself
    private Vector<BucketNode> table;

    // The *empty* head node of the LRU stack. `head.next` should point to
    // the node to be replaced next.
    private StackNode head;

    // The tail node of the LRU stack. Used to move recently used elements
    // to the tail of the LRU stack.
    private StackNode tail;

    /*
    - slots: size of LRU stack. Hence, the number of cachable elements.
     */
    public LRUCacher(Database<K, V> origin, int tableSize, int slots) {
        super(origin);
        this.tableSize = tableSize;

        table = new Vector<>(tableSize);
        // populate head nodes of the hash table
        for (int i = 0; i < tableSize; ++i)
            table.add(new BucketNode());

        // pre-populate stack
        head = new StackNode();
        for (int i = 0; i < slots; ++i) {
            tail = new StackNode();
            head.pushBelow(tail);
        }
    }

    @Override
    public V getValue(K key) {
        StackNode node = retrieveValue(key, true);

        if (node == null) {
            // pop LRU stack
            node = head.popBelow();

            // repurpose node for new key/value
            if (node.resetDirty())
                origin.setValue(node.getKey(), node.getValue());
            node.reset(key, origin.getValue(key));

            // assign (BucketNode) node to new bucket
            node.removeFromBucket();
            getBucketHead(key).appendToBucket(node);

            // add back to LRU stack
            tail.pushBelow(node);
            tail = node;
        }

        return node.getValue();
    }

    @Override
    public void setValue(K key, V value, boolean immediate) {
        StackNode node = retrieveValue(key, updateOnSet);
        if (immediate || node == null)
            origin.setValue(key, value);
        if (node != null)
            node.set(value, !immediate);
    }

    @Override
    public void flush() {
        assert("Not Implemented" == null);
    }

    private BucketNode getBucketHead(K key) {
        int h = key.hashCode() % tableSize;
        BucketNode node = table.get(h);
        assert(node != null);
        return node;
    }

    private StackNode retrieveValue(K key, boolean makeRecent) {
        BucketNode head = getBucketHead(key);
        StackNode node = (StackNode) head.searchBucket(key);

        if (node != null && makeRecent) {
            node.removeFromStack();
            tail.pushBelow(node);
            tail = node;
        }

        return node;
    }
}
