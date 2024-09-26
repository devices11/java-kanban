package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        assertEquals(1, historyManager.getHistoryMap().size(), "В истории не 1 объект");
        assertNull(historyManager.getHistoryMap().get(1).prev, "Ссылка на предыдущую ноду не null");
        assertNull(historyManager.getHistoryMap().get(1).next, "Ссылка на следующую ноду не null");
        assertEquals(historyManager.getHistoryMap().get(1).task.getTitle(), task1.getTitle(),
                "Запись в историю не добавлена");
    }

    @Test
    @DisplayName("Добавление записей в историю, обновление ссылок на ноды")
    public void addTaskInHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        assertEquals(3, historyManager.getHistoryMap().size(), "В истории не 3 объекта");
        assertNull(historyManager.getHistoryMap().get(1).prev, "Ссылка ноды 1 на предыдущую ноду не null");
        assertEquals(historyManager.getHistoryMap().get(1).next, historyManager.getHistoryMap().get(2),
                "Ссылка на следующую ноду некорректная");
        assertEquals(historyManager.getHistoryMap().get(2).prev, historyManager.getHistoryMap().get(1),
                "Ссылка на предыдущую ноду некорректная");
        assertEquals(historyManager.getHistoryMap().get(2).next, historyManager.getHistoryMap().get(3),
                "Ссылка на следующую ноду некорректная");
        assertNull(historyManager.getHistoryMap().get(3).next, "Ссылка ноды 2 на следующую ноду не null");
        assertEquals(historyManager.getHistoryMap().get(3).prev, historyManager.getHistoryMap().get(2),
                "Ссылка на предыдущую ноду некорректная");
    }

    @Test
    @DisplayName("Отсутствие в историю дублей")
    public void addTaskInHistoryDuplicateEntry() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(task1);

        assertEquals(historyManager.getHistoryMap().size(), 3, "Размер истории некорректный");
        assertNull(historyManager.getHistoryMap().get(1).next, "Запись не последняя в истории");
        assertNull(historyManager.getHistoryMap().get(2).prev, "Запись не первая в истории");
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
        assertEquals(historyManager.getHistory().size(), 1_000_000);
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
        assertNull(historyManager.getHistoryMap().get(num), "Запись не удалена из истории");
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
        assertNull(historyManager.getHistoryMap().get(1), "Запись не удалена из истории");
        assertNull(historyManager.getHistoryMap().get(2).prev, "Ссылка на предыдущую ноду не null");
        assertEquals(historyManager.getHistoryMap().get(2).next, historyManager.getHistoryMap().get(3),
                "Ссылка на следующую ноду некорректна");
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
        assertNull(historyManager.getHistoryMap().get(2).next, "Ссылка на следующую ноду не null");
        assertEquals(historyManager.getHistoryMap().get(2).prev, historyManager.getHistoryMap().get(1),
                "Ссылка на предыдущую ноду не null");
    }

    @Test
    @DisplayName("Удаление единственной записи в истории")
    public void removeTaskInHistoryContainedOneObject() {
        historyManager.add(task1);
        historyManager.remove(1);

        assertEquals(historyManager.getHistoryMap().size(), 0, "История не пустая");
    }
}
