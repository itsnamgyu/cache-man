package com.namgyu;

public class Main {
    public static void main(String[] args) throws ZenFileStore.NotEnoughZenException {
        long start;
        long elapsed;
        int delay = 500;

        System.err.println("Make sure to run with assertions enabled");

        // Non-cached version
        // 500 ms per access
        System.out.println("Setting access delay of " + delay + "ms");
        System.out.println();
        ZenFileStore db = new ZenFileStore(delay);
        db.reset();

        System.out.println("Testing without cache...");
        start = System.currentTimeMillis();
        mockAccess(db);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Time elapsed: " + elapsed + "ms");
        System.out.println();


        // Cached Version
        db.reset();
        CacheLayer<String, String> hash = new HashCacher<>(db, 10);

        System.out.println("Testing with hash cache...");
        start = System.currentTimeMillis();
        mockAccess(hash);
        hash.flush();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Time elapsed: " + elapsed + "ms");
        System.out.println();

        // Cached Version
        db.reset();
        CacheLayer<String, String> lru = new LRUCacher<>(db, 256, 10);
        System.out.println("Testing with LRU cache...");
        start = System.currentTimeMillis();
        mockAccess(lru);
        // lru.flush();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Time elapsed: " + elapsed + "ms");
        System.out.println();
    }

    private static void mockAccess(Database<String, String> db) {
        db.setValue("a", "Hello World!");
        assert (db.getValue("a").equals("Hello World!"));
        assert (db.getValue("b") == null);
        db.setValue("b", "Alohamora!");
        assert (db.getValue("k") == null);
        assert (db.getValue("b").equals("Alohamora!"));
        assert (db.getValue("a").equals("Hello World!"));
        assert (db.getValue("k") == null);
        db.setValue("b", "Wingardium Leviosa!");
        assert (db.getValue("b").equals("Wingardium Leviosa!"));
        assert (db.getValue("a").equals("Hello World!"));
        assert (db.getValue("k") == null);
        db.setValue("k", "Much Thanks!");
        assert (db.getValue("b").equals("Wingardium Leviosa!"));
        assert (db.getValue("k").equals("Much Thanks!"));
        assert (db.getValue("b").equals("Wingardium Leviosa!"));
        assert (db.getValue("a").equals("Hello World!"));
        assert (db.getValue("k").equals("Much Thanks!"));
    }
}
