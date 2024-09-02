package test.models;

import main.models.Task;
import main.util.StatusModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    private Task task1;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Название таски 1", "Описание таски 1");
    }

    @Test
    void getId() {
        task1.setId(10);
        assertEquals(10, task1.getId());
    }

    @Test
    void setId() {
        task1.setId(12);
        assertEquals(12, task1.getId());
        assertEquals("Название таски 1", task1.getTitle());
        assertEquals("Описание таски 1", task1.getDescription());
        assertEquals(StatusModel.NEW, task1.getStatus());
    }

    @Test
    void getTitle() {
        assertEquals("Название таски 1", task1.getTitle());
    }

    @Test
    void setTitle() {
        task1.setTitle("Название таски 2");
        assertEquals("Название таски 2", task1.getTitle());
        assertEquals("Описание таски 1", task1.getDescription());
        assertEquals(StatusModel.NEW, task1.getStatus());
    }

    @Test
    void getDescription() {
        assertEquals("Описание таски 1", task1.getDescription());
    }

    @Test
    void setDescription() {
        task1.setDescription("Описание таски 2");
        assertEquals("Название таски 1", task1.getTitle());
        assertEquals("Описание таски 2", task1.getDescription());
        assertEquals(StatusModel.NEW, task1.getStatus());
    }

    @Test
    void getStatus() {
        assertEquals(StatusModel.NEW, task1.getStatus());
    }

    @Test
    void setStatus() {
        task1.setStatus(StatusModel.IN_PROGRESS);
        assertEquals("Название таски 1", task1.getTitle());
        assertEquals("Описание таски 1", task1.getDescription());
        assertEquals(StatusModel.IN_PROGRESS, task1.getStatus());
    }

    @Test
    void twoTaskWithOneIdEquals () {
        task1.setId(100);
        Task task2 = new Task("Название таски 1", "Описание таски 1");
        task2.setId(100);
        assertEquals(task1, task2);
    }
}