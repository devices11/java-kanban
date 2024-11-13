package main;

import main.server.HttpTaskServer;
import main.service.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefault());

        try {
            taskServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
