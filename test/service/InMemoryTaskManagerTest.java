package service;

import main.service.InMemoryTaskManager;
import main.service.Managers;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = (InMemoryTaskManager) Managers.getInMemoryTaskManager();
        createTasks();
    }
}