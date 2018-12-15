package com.namgyu;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class ZenFileStore implements Database<String, String> {

    private final static String directory = "ZenFileStore";
    /*
    ZenFileStore, aka. SleepingFileStore

    A simple file store interface for reading/writing strings to text
    text files. The text files are stored in the directory, ZenFileStore.
    Concurrent access and other exceptions are not considered.

    - key: the name of the text file
    - value: the contents of the text file

    The creator of this database believes that people need to rediscover their
    inner zen—a concept long lost within our fast-paced society. To do so, he
    added a time delay for every single read/write access to this file store.
    This delay can be configured via the constructor, but the creator insists
    a delay of at least 100ms, for optimal zen.

    TL;DR: this database sleeps on access.
     */

    public class NotEnoughZenException extends Exception {
    }

    /*
    access delay in milliseconds
     */
    private int delay;

    public ZenFileStore(int delay) throws NotEnoughZenException {
        if (delay < 500)
            throw new NotEnoughZenException();
        this.delay = delay;
    }

    @Override
    public String getValue(String key) {
        System.out.println("Getting: " + key);
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Path base = get_base_directory();

        if (base != null) {
            Path file = base.resolve(key);
            if (file.toFile().exists()) {
                try {
                    byte[] encoded = Files.readAllBytes(base.resolve(Paths.get(key)));
                    return new String(encoded, Charset.defaultCharset());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        } else {
            System.err.println("issue with ZenFileStore directory");
        }
        return null;
    }

    @Override
    public void setValue(String key, String value) {
        System.out.println("Setting: " + key);

        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Path base = get_base_directory();

        if (base != null) {
            File f = base.resolve(key).toFile();
            try {
                Writer writer = new BufferedWriter(new FileWriter(f));
                writer.write(value);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("issue with reset value");
            }
        }
    }

    public void reset() {
        try {
            Files.walk(get_base_directory())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path get_base_directory() {
        File f = new File(directory);
        if (!f.exists()) f.mkdir();

        if (f.exists() && !f.isDirectory()) {
            System.err.println("The base directory of ZenFileStore could not be created");
        }

        return Paths.get(directory);
    }
}
