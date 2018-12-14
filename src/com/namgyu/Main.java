package com.namgyu;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        writer.write("Hello World\n");
        writer.write("For now, I'll just repeat what you say until you say stop.\n");
        writer.flush();

        while (true) {
            writer.write("> ");
            writer.flush();
            String s = reader.readLine();
            if (s.equals("stop")) {
                writer.write("Goodbye...\n");
                writer.flush();
                return;
            } else {
                writer.write(s);
                writer.newLine();
                writer.flush();
            }
        }
    }
}
