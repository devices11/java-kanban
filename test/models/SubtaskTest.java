package models;

import main.models.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
   private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask("Название таски 1", "Описание таски 1", 1);
    }
    @Test
    void getEpicId() {
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void twoSubtaskWithOneIdEquals () {
        subtask.setId(100);
        Subtask subtask2 = new Subtask("Название таски 1", "Описание таски 1", 1);
        subtask2.setId(100);
        assertEquals(subtask, subtask2);
    }
}