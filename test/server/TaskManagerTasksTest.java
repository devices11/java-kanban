package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class TaskManagerTasksTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    @BeforeEach
    public void setUp() {
        manager.deleteAllTask();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("POST /tasks. Успешное создание задачи")
    @Test
    public void createTask() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название таски 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("POST /tasks. Успешное обновление задачи")
    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2",
                LocalDateTime.now(), 15);
        taskForUpdate.setId(1);
        taskJson = gson.toJson(taskForUpdate);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название таски 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("POST /tasks. Создание задачи. Некорректный запрос")
    @Test
    public void createTaskBadRequest() throws IOException, InterruptedException {
        String taskJson = "";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
        assertEquals(0, tasksFromManager.size(), "Задача не должна быть создана");
    }

    @DisplayName("POST /tasks. Создание задачи. Задача пересекается с существующей")
    @Test
    public void createTaskOverlap() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task2 = new Task("Название таски 2", "Описание таски 2",
                LocalDateTime.now(), 5);
        taskJson = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название таски 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("GET /tasks. Успешное получение всех задач")
    @Test
    public void getTask() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
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

    @DisplayName("GET /tasks/{id}. Успешное получение задачи по id")
    @Test
    public void getTaskById() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
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

    @DisplayName("GET /tasks/{id}. Получение задачи по id. Задача не найдена")
    @Test
    public void getTaskByIdNoTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный HTTP статус код");
    }

    @DisplayName("DELETE /tasks/{id}. Успешное удаление задачи по id")
    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("DELETE /tasks/{id}. Удаление задачи по несуществующему id")
    @Test
    public void deleteTaskNoTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный HTTP статус код");
    }

    @DisplayName("DELETE /tasks. Успешное удаление всех задач")
    @Test
    public void deleteAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.now(), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllTask());
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }
}
