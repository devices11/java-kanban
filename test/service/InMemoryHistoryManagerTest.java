package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    Task task1;
    Epic epic1;
    Subtask subtask1;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();

        task1 = new Task("Название таски 1", "Описание таски 1");
        epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1", 2);
    }

    @Test
    public void add1TaskInHistory() {
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size(), "В истории не 1 объект");
    }

    @Test
    public void taskInHistoryAllVersion() {
        historyManager.add(task1);
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(1);
        historyManager.add(taskForUpdate);
        assertEquals(2, historyManager.getHistory().size(), "В истории не 2 объекта");
        assertEquals("Название таски 1", historyManager.getHistory().getFirst().getTitle());
        assertEquals("Описание таски 1", historyManager.getHistory().getFirst().getDescription());
        assertEquals("Название таски 2", historyManager.getHistory().get(1).getTitle());
        assertEquals("Описание таски 2", historyManager.getHistory().get(1).getDescription());
    }

        @Test
    public void add10TaskInHistory() {
        //Посмотрели 10 объектов
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        assertEquals(10, historyManager.getHistory().size(), "В истории не 10 объектов");
    }

    @Test
    public void checkLimit10TaskInHistory() {
        //Посмотрели 11 объектов
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task1);

        assertEquals(10, historyManager.getHistory().size(),"В истории не 10 объектов");
        assertInstanceOf(Epic.class, historyManager.getHistory().getFirst(), "Первый объект не Epic");
        assertInstanceOf(Task.class, historyManager.getHistory().get(9),"Десятый объект не Task");
    }

    @Test
    public void checkLimit10TaskInHistoryViewTaskNull() {
        Task task = null;
        //Посмотрели 11 объектов
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task);

        assertEquals(10, historyManager.getHistory().size(),"В истории не 10 объектов");
        assertInstanceOf(Task.class, historyManager.getHistory().getFirst(), "Первый объект не Task");
        assertInstanceOf(Subtask.class, historyManager.getHistory().get(9),"Десятый объект не Subtask");
    }
}
