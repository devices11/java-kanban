package main.server;

import com.sun.net.httpserver.HttpServer;
import main.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager manager;
    private static HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        manager = taskManager;
    }

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(manager));
            httpServer.createContext("/epics", new EpicHandler(manager));
            httpServer.createContext("/subtasks", new SubtaskHandler(manager));
            httpServer.createContext("/history", new HistoryHandler(manager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
            httpServer.start();

            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        httpServer.stop(0);
    }
}
