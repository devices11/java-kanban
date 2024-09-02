package test.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.HistoryManager;
import main.service.InMemoryTaskManager;
import main.service.Managers;
import main.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = ((InMemoryTaskManager) taskManager).getHistoryManager();

        Task task1 = new Task("Название таски 1", "Описание таски 1");
        taskManager.createTask(task1);

        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1", 2);
        taskManager.createSubtask(subtask1);
    }

    @Test
    public void add1TaskInHistory() {
        taskManager.getTaskById(1);
        assertEquals(1, historyManager.getHistory().size(), "В истории не 1 объект");
    }

    @Test
    public void taskInHistoryAllVersion() {
        taskManager.getTaskById(1);
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(1);
        taskManager.updateTask(taskForUpdate);
        taskManager.getTaskById(1);
        assertEquals(2, historyManager.getHistory().size(), "В истории не 2 объекта");
        assertEquals("Название таски 1", historyManager.getHistory().getFirst().getTitle());
        assertEquals("Описание таски 1", historyManager.getHistory().getFirst().getDescription());
        assertEquals("Название таски 2", historyManager.getHistory().get(1).getTitle());
        assertEquals("Описание таски 2", historyManager.getHistory().get(1).getDescription());
    }

    @Test
    public void add10TaskInHistory() {
        //Посмотрели 10 объектов
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getEpicById(2);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(3);

        assertEquals(10, historyManager.getHistory().size(), "В истории не 10 объектов");
    }

    @Test
    public void checkLimit10TaskInHistory() {
        //Посмотрели 11 объектов
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);

        assertEquals(10, historyManager.getHistory().size(),
                "В истории не 10 объектов");
        assertEquals(1, historyManager.getHistory().getFirst().getId(),
                "Первый не с id = 1");
        assertEquals(3, historyManager.getHistory().get(9).getId(),
                "Десятый объект в истории не с id = 3");
    }
}
