package test.models;

import main.models.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private Epic epic1;

    @BeforeEach
    void beforeEach() {
        epic1 = new Epic("Название эпика 1", "Описание эпика 1");
    }

    @Test
    void setSubtasks() {
        epic1.setSubtasks(11);
        assertEquals(1, epic1.getSubtasks().size());
        assertEquals(11, epic1.getSubtasks().getFirst());
    }

    @Test
    void getSubtasks() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(12);
        list.add(13);
        list.add(14);
        epic1.setSubtasks(12);
        epic1.setSubtasks(13);
        epic1.setSubtasks(14);
        assertArrayEquals(list.toArray(), epic1.getSubtasks().toArray());
    }

    @Test
    void deleteSubtaskId() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(12);
        list.add(14);
        epic1.setSubtasks(12);
        epic1.setSubtasks(13);
        epic1.setSubtasks(14);
        epic1.deleteSubtaskId(13);
        assertEquals(list, epic1.getSubtasks());
    }

    @Test
    void deleteAllSubtask() {
        epic1.setSubtasks(12);
        epic1.setSubtasks(13);
        epic1.setSubtasks(14);
        epic1.deleteAllSubtask();
        assertEquals(0, epic1.getSubtasks().size());
    }

    @Test
    void twoEpicWithOneIdEquals () {
        epic1.setId(100);
        Epic epic2 = new Epic("Название эпика 1", "Описание эпика 1");
        epic2.setId(100);
        assertEquals(epic1, epic2);
    }
}