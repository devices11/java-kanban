package main.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.service.TaskManager;
import main.util.Endpoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (Objects.requireNonNull(endpoint) == Endpoint.GET_PRIORITIZED) {
            handleGetPrioritized(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        String json = gson.toJson(taskManager.getPrioritizedTasks());
        System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
        sendText(exchange, json);
        System.out.println("Body: " + json);
    }
}
