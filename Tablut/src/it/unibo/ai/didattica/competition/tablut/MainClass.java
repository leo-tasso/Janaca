package it.unibo.ai.didattica.competition.tablut;

import it.unibo.ai.didattica.competition.tablut.janaca.JanacaBlackClient;
import it.unibo.ai.didattica.competition.tablut.janaca.JanacaWhiteClient;
import it.unibo.ai.didattica.competition.tablut.server.Server;

import java.io.IOException;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("Running MainClass");

        // Create threads for each EntryPoint class
        Thread thread1 = new Thread(() -> Server.main(args));
        Thread thread2 = new Thread(() -> {
            try {
                JanacaWhiteClient.main(args);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread thread3 = new Thread(() -> {
            try {
                JanacaBlackClient.main(args);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Start the threads
        thread1.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread2.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread3.start();
    }
}