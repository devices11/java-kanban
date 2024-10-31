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

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private TaskManager taskManagerFromFile;
    private File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("storage", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);

        task1 = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.parse("2024-10-24T21:00:00"), 1440);   //id==1
        taskManager.createTask(task1);

        epic1 = new Epic("Название эпика 1", "Описание эпика 1");   //id==2
        taskManager.createEpic(epic1);

        subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1",
                epic1.getId(), LocalDateTime.parse("2024-10-20T20:00:00"), 55);    //id==3
        taskManager.createSubtask(subtask1);
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

        assertEquals(super.task1.getId(), tasks.getFirst().getId(), "id задачи некорректен");
        assertEquals(super.task1.getTitle(), tasks.getFirst().getTitle(), "Название задачи некорректно");
        assertEquals(super.task1.getStatus(), tasks.getFirst().getStatus(), "Статус задачи некорректен");
        assertEquals(super.task1.getDescription(), tasks.getFirst().getDescription(), "Описание задачи некорректно");
        assertEquals(super.task1.getStartTime(), tasks.getFirst().getStartTime(), "Дата старта некорректна");
        assertEquals(super.task1.getDuration(), tasks.getFirst().getDuration(), "Срок выполнения некорректен");

        assertEquals(super.epic1.getId(), epics.getFirst().getId(), "id эпика некорректен");
        assertEquals(super.epic1.getTitle(), epics.getFirst().getTitle(), "Название эпика некорректно");
        assertEquals(super.epic1.getStatus(), epics.getFirst().getStatus(), "Статус эпика некорректен");
        assertEquals(super.epic1.getDescription(), epics.getFirst().getDescription(), "Описание эпика некорректно");
        assertEquals(super.epic1.getStartTime(), epics.getFirst().getStartTime(), "Дата старта некорректна");
        assertEquals(super.epic1.getDuration(), epics.getFirst().getDuration(), "Срок выполнения некорректен");
        assertEquals(super.epic1.getEndTime(), epics.getFirst().getEndTime(), "Дата окончания некорректна");

        assertEquals(super.subtask1.getId(), subtasks.getFirst().getId(), "id подзадачи некорректен");
        assertEquals(super.subtask1.getTitle(), subtasks.getFirst().getTitle(), "Название подзадачи некорректно");
        assertEquals(super.subtask1.getStatus(), subtasks.getFirst().getStatus(), "Статус задачи некорректен");
        assertEquals(super.subtask1.getDescription(), subtasks.getFirst().getDescription(), "Описание подзадачи некорректно");
        assertEquals(super.subtask1.getEpicId(), subtasks.getFirst().getEpicId(), "id эпика некорректно");
        assertEquals(super.subtask1.getStartTime(), subtasks.getFirst().getStartTime(), "Дата старта некорректна");
        assertEquals(super.subtask1.getDuration(), subtasks.getFirst().getDuration(), "Срок выполнения некорректен");

        assertEquals(2, taskManagerFromFile.getPrioritizedTasks().size(), "Список приоритетных задач некорректен");
        assertEquals(3, taskManagerFromFile.getPrioritizedTasks().getFirst().getId(), "Список приоритетных задач некорректен");
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
