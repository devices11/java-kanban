package test.service;

import main.service.InMemoryHistoryManager;
import main.service.InMemoryTaskManager;
import main.service.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        assertTrue(Managers.getDefault() != null);
        assertTrue(Managers.getDefault() instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistory() {
        assertTrue(Managers.getDefaultHistory() != null);
        assertTrue(Managers.getDefaultHistory() instanceof InMemoryHistoryManager);
    }
}