package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.FileBackedTaskManager;
import main.service.Managers;
import main.service.TaskManager;
import main.util.Exception.ManagerSaveException;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private TaskManager taskManagerFromFile;
    private File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("storage", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);
        createTasks();
    }

    @AfterEach
    public void afterEach() {
        file.deleteOnExit();
    }

    @DisplayName("Сохранение пустого хранилища в файл")
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
        assertEquals("id,type,name,status,description,startTime,duration,epic", allLinesInFile.getFirst(),
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
        assertEquals("id,type,name,status,description,startTime,duration,epic", allLinesInFile.getFirst(),
                "Первая строка некорректна");
        assertEquals("1,TASK,Название таски 1,NEW,Описание таски 1,"
                        + super.task1.getStartTime() + "," + super.task1.getDuration() + ",", allLinesInFile.get(1),
                "Задача создана некорректно");
        assertEquals("2,EPIC,Название эпика 1,NEW,Описание эпика 1,"
                        + super.epic1.getStartTime() + "," + super.epic1.getDuration() + ",", allLinesInFile.get(2),
                "Эпик создан некорректно");
        assertEquals("3,SUBTASK,Название сабтаски 1,NEW,Описание сабтаски 1,"
                        + super.subtask1.getStartTime() + "," + super.subtask1.getDuration() + ","
                        + super.subtask1.getEpicId(), allLinesInFile.get(3),
                "Подзадача создана некорректно");
        assertNotNull(taskManager.getAllTask(), "Хранилище тасок пустое");
    }

    @DisplayName("Чтение задач из файла")
    @Test
    void readFile() {
        taskManagerFromFile = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);
        List<Task> tasks = new ArrayList<>(taskManagerFromFile.getAllTask());
        List<Epic> epics = new ArrayList<>(taskManagerFromFile.getAllEpic());
        List<Subtask> subtasks = new ArrayList<>(taskManagerFromFile.getAllSubtask());

        assertEquals(3, tasks.size() + epics.size() + subtasks.size(), "Количество задач некорректно");

        assertArrayEquals(taskManager.getAllTask().toArray(), taskManagerFromFile.getAllTask().toArray());
        assertArrayEquals(taskManager.getAllEpic().toArray(), taskManagerFromFile.getAllEpic().toArray());
        assertArrayEquals(taskManager.getAllSubtask().toArray(), taskManagerFromFile.getAllSubtask().toArray());
        assertArrayEquals(taskManager.getPrioritizedTasks().toArray(), taskManagerFromFile.getPrioritizedTasks().toArray());
    }

    @DisplayName("Сохранение и чтение данных в файл")
    @Test
    void saveAndReadFile() {
        taskManagerFromFile = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);

        assertArrayEquals(taskManager.getAllTask().toArray(), taskManagerFromFile.getAllTask().toArray(), "Данные по задачам не совпадают");
        assertArrayEquals(taskManager.getAllEpic().toArray(), taskManagerFromFile.getAllEpic().toArray(), "Данные по эпикам не совпадают");
        assertArrayEquals(taskManager.getAllSubtask().toArray(), taskManagerFromFile.getAllSubtask().toArray(), "Данные по подзадачам не совпадают");
    }

    @DisplayName("Отсутствует файл при записи данных")
    @Test
    void saveFileException() {
        Task task = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.parse("2024-10-24T21:00:00"), 1440);   //id==1

        assertThrows(ManagerSaveException.class, () -> {
            file = Path.of("test").toFile();
            TaskManager taskManager2 = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
            taskManager2.createTask(task);
        }, "Отсутствие файла должно приводить к исключению");
    }

    @DisplayName("Отсутствует файл при чтении данных")
    @Test
    void openFileException() {
        assertThrows(ManagerSaveException.class, () -> {
            file = Path.of("test").toFile();
            taskManagerFromFile = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);
        }, "Отсутствие файла должно приводить к исключению");
    }


}
