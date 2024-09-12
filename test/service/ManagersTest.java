package service;

import main.service.InMemoryHistoryManager;
import main.service.InMemoryTaskManager;
import main.service.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        assertNotNull(Managers.getDefault());
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    void getDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}