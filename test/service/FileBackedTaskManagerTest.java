package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.FileBackedTaskManager;
import main.service.Managers;
import main.service.TaskManager;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static main.util.StatusModel.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private TaskManager taskManagerFromFile;
    private File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("storage", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);

        Task task1 = new Task("Название таски 1", "Описание таски 1");   //id==1
        taskManager.createTask(task1);

        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");   //id==2
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1", 2);    //id==3
        taskManager.createSubtask(subtask1);

        taskManagerFromFile = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        file.deleteOnExit();
    }

    @DisplayName("Cохранение пустого хранилища в файл")
    @Test
    void saveEmptyStorage() throws IOException {
        taskManager.deleteAllTask();
        taskManager.deleteAllSubtask();
        taskManager.deleteAllEpic();

        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        }

        assertEquals(1, allLinesInFile.size(), "Файл не пустой");
        assertEquals("id,type,name,status,description,epic", allLinesInFile.getFirst(),
                "Первая строка некорректна");
    }

    @DisplayName("Создание задач и сохранение в файл")
    @Test
    void createTaskAndSave() throws IOException {
        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        }

        assertEquals(4, allLinesInFile.size(), "Количество задач некорректно");
        assertEquals("id,type,name,status,description,epic", allLinesInFile.getFirst(),
                "Первая строка некорректна");
        assertEquals("1,TASK,Название таски 1,NEW,Описание таски 1,", allLinesInFile.get(1),
                "Задача создана некорректно");
        assertEquals("2,EPIC,Название эпика 1,NEW,Описание эпика 1,", allLinesInFile.get(2),
                "Эпик создан некорректно");
        assertEquals("3,SUBTASK,Название сабтаски 1,NEW,Описание сабтаски 1,2", allLinesInFile.get(3),
                "Подзадача создана некорректно");
        assertNotNull(taskManager.getAllTask(), "Хранилище тасок пустое");
    }

    @DisplayName("Чтение задач из файла")
    @Test
    void readFile() {
        List<Task> tasks = new ArrayList<>(taskManagerFromFile.getAllTask());
        List<Epic> epics = new ArrayList<>(taskManagerFromFile.getAllEpic());
        List<Subtask> subtasks = new ArrayList<>(taskManagerFromFile.getAllSubtask());

        assertEquals(3, tasks.size() + epics.size() + subtasks.size(), "Количество задач некорректно");

        assertEquals(tasks.getFirst().getId(), 1, "id задачи некорректен");
        assertEquals(tasks.getFirst().getTitle(), "Название таски 1", "Название задачи некорректно");
        assertEquals(tasks.getFirst().getStatus(), NEW, "Статус задачи некорректен");
        assertEquals(tasks.getFirst().getDescription(), "Описание таски 1", "Описание задачи некорректно");

        assertEquals(epics.getFirst().getId(), 2, "id эпика некорректен");
        assertEquals(epics.getFirst().getTitle(), "Название эпика 1", "Название эпика некорректно");
        assertEquals(epics.getFirst().getStatus(), NEW, "Статус эпика некорректен");
        assertEquals(epics.getFirst().getDescription(), "Описание эпика 1", "Описание эпика некорректно");

        assertEquals(subtasks.getFirst().getId(), 3, "id подзадачи некорректен");
        assertEquals(subtasks.getFirst().getTitle(), "Название сабтаски 1", "Название подзадачи некорректно");
        assertEquals(subtasks.getFirst().getStatus(), NEW, "Статус задачи некорректен");
        assertEquals(subtasks.getFirst().getDescription(), "Описание сабтаски 1", "Описание подзадачи некорректно");
        assertEquals(subtasks.getFirst().getEpicId(), 2, "id эпика некорректно");
    }
}
