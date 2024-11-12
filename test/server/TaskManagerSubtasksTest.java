package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.server.BaseHttpHandler;
import main.server.HttpTaskServer;
import main.service.Managers;
import main.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerSubtasksTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();
    HttpClient client;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager.deleteAllTask();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        HttpTaskServer.start();

        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String epicJson = gson.toJson(epic);
        client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("POST /subtasks. Успешное создание подзадачи")
    @Test
    public void createSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название подзадачи 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("POST /subtasks. Успешное обновление подзадачи")
    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskForUpdate = new Subtask("Название подзадачи 2", "Описание подзадачи 2", 1,
                LocalDateTime.now(), 15);
        subtaskForUpdate.setId(2);
        taskJson = gson.toJson(subtaskForUpdate);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название подзадачи 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("POST /subtasks. Создание подзадачи. Некорректный запрос")
    @Test
    public void createSubtaskBadRequest() throws IOException, InterruptedException {
        String taskJson = "";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        assertEquals(0, tasksFromManager.size(), "Задача не должна быть создана");
    }

    @DisplayName("POST /subtasks. Создание подзадачи. Задача пересекается с существующей")
    @Test
    public void createSubtaskOverlap() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask2 = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        taskJson = gson.toJson(subtask2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название подзадачи 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("GET /subtasks. Успешное получение всех подзадач")
    @Test
    public void getSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(tasksFromManager.getFirst().getId(), jsonObject.get("id").getAsInt());
        assertEquals(tasksFromManager.getFirst().getTitle(), jsonObject.get("title").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), jsonObject.get("description").getAsString());
        assertEquals("NEW", jsonObject.get("status").getAsString());
        assertEquals(tasksFromManager.getFirst().getDuration().toMinutes(), jsonObject.get("duration").getAsInt());
        assertEquals(tasksFromManager.getFirst().getStartTime(),
                LocalDateTime.parse(jsonObject.get("startTime").getAsString()));

    }

    @DisplayName("GET /subtasks/{id}. Успешное получение подзадачи по id")
    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals(tasksFromManager.getFirst().getId(), jsonObject.get("id").getAsInt());
        assertEquals(tasksFromManager.getFirst().getTitle(), jsonObject.get("title").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), jsonObject.get("description").getAsString());
        assertEquals("NEW", jsonObject.get("status").getAsString());
        assertEquals(tasksFromManager.getFirst().getDuration().toMinutes(), jsonObject.get("duration").getAsInt());
        assertEquals(tasksFromManager.getFirst().getStartTime(),
                LocalDateTime.parse(jsonObject.get("startTime").getAsString()));
    }

    @DisplayName("GET /subtasks/{id}. Получение подзадачи по id. Задача не найдена")
    @Test
    public void getSubtaskByIdNoSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный HTTP статус код");
    }

    @DisplayName("DELETE /subtasks/{id}. Успешное удаление задачи по id")
    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("DELETE /subtasks/{id}. Удаление подзадачи по несуществующему id")
    @Test
    public void deleteSubtaskNoSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный HTTP статус код");
    }

    @DisplayName("DELETE /subtasks. Успешное удаление всех подзадач")
    @Test
    public void deleteAllSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }
}
