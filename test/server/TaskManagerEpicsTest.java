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

public class TaskManagerEpicsTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = BaseHttpHandler.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTask();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("POST /epics. Успешное создание эпика")
    @Test
    public void createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название эпика 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("POST /epics. Успешное обновление эпика")
    @Test
    public void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(1);
        taskJson = gson.toJson(epicForUpdate);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Название эпика 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @DisplayName("POST /epics. Создание эпика. Некорректный запрос")
    @Test
    public void createEpicBadRequest() throws IOException, InterruptedException {
        String taskJson = "";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        assertEquals(0, tasksFromManager.size(), "Задача не должна быть создана");
    }

    @DisplayName("GET /epics. Успешное получение всех эпиков")
    @Test
    public void getEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(tasksFromManager.getFirst().getId(), jsonObject.get("id").getAsInt());
        assertEquals(tasksFromManager.getFirst().getTitle(), jsonObject.get("title").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), jsonObject.get("description").getAsString());
        assertEquals("NEW", jsonObject.get("status").getAsString());
    }

    @DisplayName("GET /epics/{id}. Успешное получение эпика по id")
    @Test
    public void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/epics/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        assertEquals(tasksFromManager.getFirst().getId(), jsonObject.get("id").getAsInt());
        assertEquals(tasksFromManager.getFirst().getTitle(), jsonObject.get("title").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), jsonObject.get("description").getAsString());
        assertEquals("NEW", jsonObject.get("status").getAsString());
    }

    @DisplayName("GET /epics/{id}. Получение эпика по id. Эпик не найден")
    @Test
    public void getEpicByIdNoEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный HTTP статус код");
    }

    @DisplayName("DELETE /epics/{id}. Успешное удаление эпика по id")
    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/epics/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("DELETE /epics/{id}. Удаление эпика по несуществующему id")
    @Test
    public void deleteEpicNoTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный HTTP статус код");
    }

    @DisplayName("DELETE /epics. Успешное удаление всех эпиков")
    @Test
    public void deleteAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllEpic());
        assertEquals(0, tasksFromManager.size(), "Задача не удалена");
    }

    @DisplayName("GET /epics/{id}/subtasks. Успешное получение подзадач эпика по id")
    @Test
    public void getEpicSubtasksById() throws IOException, InterruptedException {
        Epic epic = new Epic("Название эпика 1", "Описание эпика 1");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = new Subtask("Название подзадачи 1", "Описание подзадачи 1", 1,
                LocalDateTime.now(), 5);
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/epics/1/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(manager.getAllSubtask());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(tasksFromManager.getFirst().getId(), jsonObject.get("id").getAsInt());
    }
}
