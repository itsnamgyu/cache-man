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
        HashCacher<String, String> cached = new HashCacher<>(db);

        System.out.println("Testing with cache");
        start = System.currentTimeMillis();
        mockAccess(cached);
        cached.flush();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Time elapsed: " + elapsed + "ms");
        System.out.println();
    }

    private static void mockAccess(Database<String, String> db) {
        db.set_value("a", "Hello World!");
        assert(db.get_value("a").equals("Hello World!"));
        assert(db.get_value("b") == null);
        db.set_value("b", "Alohamora!");
        assert(db.get_value("c") == null);
        assert(db.get_value("b").equals("Alohamora!"));
        assert(db.get_value("a").equals("Hello World!"));
        assert(db.get_value("c") == null);
        db.set_value("b", "Wingardium Leviosa!");
        assert(db.get_value("b").equals("Wingardium Leviosa!"));
        assert(db.get_value("a").equals("Hello World!"));
        assert(db.get_value("b").equals("Wingardium Leviosa!"));
    }
}
