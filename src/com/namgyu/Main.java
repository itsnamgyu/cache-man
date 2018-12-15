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

        System.out.println("Testing with cache...");
        start = System.currentTimeMillis();
        mockAccess(cached);
        cached.flush();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Time elapsed: " + elapsed + "ms");
        System.out.println();
    }

    private static void mockAccess(Database<String, String> db) {
        db.setValue("a", "Hello World!");
        assert(db.getValue("a").equals("Hello World!"));
        assert(db.getValue("b") == null);
        db.setValue("b", "Alohamora!");
        assert(db.getValue("c") == null);
        assert(db.getValue("b").equals("Alohamora!"));
        assert(db.getValue("a").equals("Hello World!"));
        assert(db.getValue("c") == null);
        db.setValue("b", "Wingardium Leviosa!");
        assert(db.getValue("b").equals("Wingardium Leviosa!"));
        assert(db.getValue("a").equals("Hello World!"));
        assert(db.getValue("b").equals("Wingardium Leviosa!"));
    }
}
