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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerPrioritizedTest {
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

    @DisplayName("GET /history. Успешное получение приоретизированного списка задач")
    @Test
    public void getPrioritized() throws IOException, InterruptedException {
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
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = manager.getHistory();
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
}
