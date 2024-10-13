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
import java.util.ArrayList;
import java.util.List;

import static main.util.StatusModel.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private TaskManager emptyTaskManager;
    private File file;


    @BeforeEach
    public void beforeEach() {
        try {
            file = File.createTempFile("storage", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(Managers.getDefaultHistory(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Task task1 = new Task("Название таски 1", "Описание таски 1");   //id==1
        taskManager.createTask(task1);

        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");   //id==2
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1", 2);    //id==3
        taskManager.createSubtask(subtask1);
    }

    @AfterEach
    public void afterEach() {
        file.deleteOnExit();
    }

    @DisplayName("Cохранение пустого хранилища в файл")
    @Test
    void saveEmptyStorage() {
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
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }

        assertEquals(1, allLinesInFile.size(), "Файл не пустой");
        assertEquals("id,type,name,status,description,epic", allLinesInFile.getFirst(),
                "Первая строка некорректна");
    }

    @DisplayName("Создание задач и сохранение в файл")
    @Test
    void createTaskAndSave() {
        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
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

    @DisplayName("Обновление задачи и сохранение в файл")
    @Test
    void updateTaskAndSave() {
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(1);
        taskForUpdate.setStatus(IN_PROGRESS);
        taskManager.updateTask(taskForUpdate);
        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }

        assertEquals("1,TASK,Название таски 2,IN_PROGRESS,Описание таски 2,", allLinesInFile.get(1),
                "Задача обновлена некорректно");
    }

    @DisplayName("Обновление подзадачи, статуса эпика и сохранение в файл")
    @Test
    void updateSubtaskAndSave() {
        Subtask subtask = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);    //id==4
        subtask.setId(3);
        subtask.setStatus(IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }

        assertEquals("2,EPIC,Название эпика 1,IN_PROGRESS,Описание эпика 1,", allLinesInFile.get(2),
                "Эпик обновлен некорректно");
        assertEquals("3,SUBTASK,Название сабтаски 2,IN_PROGRESS,Описание сабтаски 2,2", allLinesInFile.get(3),
                "Подзадача обновлена некорректно");
    }

    @DisplayName("Удаление всех подзадач и сохранение в файл")
    @Test
    void deleteAllSubtaskAndSave() {
        taskManager.deleteAllSubtask();
        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }

        assertEquals(3, allLinesInFile.size(), "Количество задач некорректно");
        assertEquals("2,EPIC,Название эпика 1,NEW,Описание эпика 1,", allLinesInFile.get(2),
                "Файл некорректный");

    }

    @DisplayName("Удаление эпика и сохранение в файл")
    @Test
    void deleteEpicByIdAndSave() {
        taskManager.deleteEpicById(2);
        List<String> allLinesInFile = new ArrayList<>();
        try (Reader reader = new FileReader(file.getCanonicalFile(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                allLinesInFile.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }

        assertEquals(2, allLinesInFile.size(), "Количество задач некорректно");
        assertEquals("1,TASK,Название таски 1,NEW,Описание таски 1,", allLinesInFile.get(1),
                "Файл некорректный");

    }
}
