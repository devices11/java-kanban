package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        task1.setId(1);
        epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        epic1.setId(2);
        subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1", 2);
        subtask1.setId(3);
    }

    @Test
    @DisplayName("Добавление единственной записи в историю")
    public void add1TaskInHistory() {
        historyManager.add(task1);

        assertEquals(1, historyManager.getAll().size(), "В истории не 1 объект");
        assertEquals(historyManager.getAll().getFirst().getTitle(), task1.getTitle(),
                "Запись в историю не добавлена");
    }

    @Test
    @DisplayName("Добавление записей в историю")
    public void addTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        assertEquals(List.of(subtask1, epic1, task1), historyManager.getAll(), "История некорректна");
    }

    @Test
    @DisplayName("Отсутствие в историю дублей")
    public void addTaskInHistoryDuplicateEntry() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task1);

        assertEquals(List.of(task1, subtask1, epic1), historyManager.getAll(), "История некорректна");
    }

    @Test
    @DisplayName("Просмотр истории с 1 млн записей")
    public void get1MillionTaskInHistory() {
        for (int i = 1; i <= 1_000_000; i++) {
            Task task = new Task(("Название таски " + i), ("Описание таски " + i));
            task.setId(i);
            historyManager.add(task);
        }

        long startTime = System.nanoTime();
        assertEquals(historyManager.getAll().size(), 1_000_000);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Получили историю за " + duration * 0.000_000_001 + " сек.");
    }

    @Test
    @DisplayName("Удаление записи в истории с 1 млн записей")
    public void removeTaskInBigHistory() {
        for (int i = 1; i <= 1_000_000; i++) {
            Task task = new Task(("Название таски " + i), ("Описание таски " + i));
            task.setId(i);
            historyManager.add(task);
        }
        int num = 965_001;
        long startTime = System.nanoTime();
        historyManager.remove(num);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Task taskInHistory = null;
        for (Task task : historyManager.getAll()) {
            if (task.getId() == num) {
                taskInHistory = task;
            }
        }

        assertNull(taskInHistory, "Запись не удалена из истории");
        System.out.println("Удалили запись " + num + " за " + duration + " наносекунд");
    }

    @Test
    @DisplayName("Удаление первой записи в истории")
    public void removeFirstTaskInHistory() {
        for (int i = 1; i <= 3; i++) {
            Task task = new Task(("Название таски " + i), ("Описание таски " + i));
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.remove(1);

        assertEquals(historyManager.getAll().size(), 2, "История некорректна");
        assertEquals(historyManager.getAll().getFirst().getId(), 3, "История некорректна");
        assertEquals(historyManager.getAll().getLast().getId(), 2, "История некорректна");
    }

    @Test
    @DisplayName("Удаление записи из середины в истории")
    public void removeMiddleTaskInHistory() {
        for (int i = 1; i <= 3; i++) {
            Task task = new Task(("Название таски " + i), ("Описание таски " + i));
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.remove(2);

        assertEquals(historyManager.getAll().size(), 2, "История некорректна");
        assertEquals(historyManager.getAll().getFirst().getId(), 3, "История некорректна");
        assertEquals(historyManager.getAll().getLast().getId(), 1, "История некорректна");
    }

    @Test
    @DisplayName("Удаление последней записи в истории")
    public void removeTailTaskInHistory() {
        for (int i = 1; i <= 3; i++) {
            Task task = new Task(("Название таски " + i), ("Описание таски " + i));
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.remove(3);
        
        assertEquals(historyManager.getAll().size(), 2, "История некорректна");
        assertEquals(historyManager.getAll().getFirst().getId(), 2, "История некорректна");
        assertEquals(historyManager.getAll().getLast().getId(), 1, "История некорректна");
    }

    @Test
    @DisplayName("Удаление единственной записи в истории")
    public void removeTaskInHistoryContainedOneObject() {
        historyManager.add(task1);
        historyManager.remove(1);

        assertEquals(historyManager.getAll().size(), 0, "История не пустая");
    }
}
