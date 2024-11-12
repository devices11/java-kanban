package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.server.adapter.DurationAdapter;
import main.server.adapter.LocalDateTimeAdapter;
import main.service.TaskManager;
import main.util.Endpoint;
import main.util.Exception.NotFoundException;
import main.util.TypeTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson = getGson();

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            if (pathParts[1].equals("tasks") && requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            } else if (pathParts[1].equals("tasks") && requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            } else if (pathParts[1].equals("tasks") && requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_ALL_TASK;
            } else if (pathParts[1].equals("subtasks") && requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            } else if (pathParts[1].equals("subtasks") && requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASK;
            } else if (pathParts[1].equals("subtasks") && requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_ALL_SUBTASK;
            } else if (pathParts[1].equals("epics") && requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            } else if (pathParts[1].equals("epics") && requestMethod.equals("POST")) {
                return Endpoint.POST_EPIC;
            } else if (pathParts[1].equals("epics") && requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_ALL_EPIC;
            } else if (pathParts[1].equals("history") && requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            } else if (pathParts[1].equals("prioritized") && requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            }
        } else if (pathParts.length == 3) {
            if (pathParts[1].equals("tasks") && requestMethod.equals("GET")) {
                return Endpoint.GET_TASK_BY_ID;
            } else if (pathParts[1].equals("tasks") && requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            } else if (pathParts[1].equals("subtasks") && requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK_BY_ID;
            } else if (pathParts[1].equals("subtasks") && requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            } else if (pathParts[1].equals("epics") && requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC_BY_ID;
            } else if (pathParts[1].equals("epics") && requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        }

        if (pathParts.length == 4
                && pathParts[1].equals("epics")
                && pathParts[3].equals("subtasks")
                && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }

        return Endpoint.UNKNOWN;
    }

    protected void getTask(HttpExchange exchange, TypeTask typeTask) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = switch (typeTask) {
                case TASK -> gson.toJson(taskManager.getAllTask());
                case SUBTASK -> gson.toJson(taskManager.getAllSubtask());
                case EPIC -> gson.toJson(taskManager.getAllEpic());
            };

            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, json);
            System.out.println("Body: " + json);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    protected void getTaskById(HttpExchange exchange, TypeTask typeTask) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        try {
            int id = getId(exchange);
            String json = switch (typeTask) {
                case TASK -> gson.toJson(taskManager.getTaskById(id));
                case SUBTASK -> gson.toJson(taskManager.getSubtaskById(id));
                case EPIC -> gson.toJson(taskManager.getEpicById(id));
            };
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, json);
            System.out.println("Body: " + json);

        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    protected void deleteTask(HttpExchange exchange, TypeTask typeTask) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        try {
            int id = getId(exchange);
            switch (typeTask) {
                case TASK -> taskManager.deleteTaskById(id);
                case SUBTASK -> taskManager.deleteSubtaskById(id);
                case EPIC -> taskManager.deleteEpicById(id);
            }
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendOk(exchange);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    protected void deleteAllTask(HttpExchange exchange, TypeTask typeTask) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        try {
            switch (typeTask) {
                case TASK -> taskManager.deleteAllTask();
                case SUBTASK -> taskManager.deleteAllSubtask();
                case EPIC -> taskManager.deleteAllEpic();
            }
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendOk(exchange);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    //Отправить общий ответ в случае успеха;
    protected void sendText(HttpExchange h, String text) throws IOException {
        int statusCode = 200;
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
        System.out.println("HTTP code: " + statusCode);
        System.out.println(h.getResponseHeaders());
    }

    //Отправить ответ без тела в случае успеха
    protected void sendOk(HttpExchange h) throws IOException {
        int statusCode = 204;
        h.sendResponseHeaders(statusCode, -1);
        h.close();
        System.out.println("HTTP code: " + statusCode);
        System.out.println(h.getResponseHeaders());
    }

    //Отправить ответ в случае, если запрос некорректный
    protected void sendBadRequest(HttpExchange h) throws IOException {
        System.out.println(LocalDateTime.now() + " Некорректный запрос");
        h.sendResponseHeaders(400, 0);
        System.out.println(h.getResponseHeaders());
        h.close();

    }

    //Отправить ответ в случае, если объект не был найден;
    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    //Отправить ответ, если при создании или обновлении задача пересекается с уже существующими.
    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.sendResponseHeaders(406, 0);
        h.close();
    }

    //Отправить ответ ошибка на стороне сервера
    protected void sendError(HttpExchange h) throws IOException {
        int statusCode = 500;
        h.sendResponseHeaders(statusCode, 0);
        System.out.println(LocalDateTime.now() + " Отправлен  statusCode: " + statusCode);
        System.out.println(h.getResponseHeaders());
        h.close();
    }

    //Получить id задачи
    protected int getId(HttpExchange h) throws IOException {
        String[] pathParts = h.getRequestURI().getPath().split("/");

        try {
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException exception) {
            throw new NotFoundException(LocalDateTime.now() + " Некорректный id задачи");
        }
    }

    protected String readBodyRequest(HttpExchange exchange) throws IOException {
        String body;
        try (InputStream inputStream = exchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        }
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI() + "\nBody: " + body);

        if (body.isEmpty()) {
            sendBadRequest(exchange);
            return "";
        }
        return body;
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
