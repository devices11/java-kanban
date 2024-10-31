package service;

import main.service.Managers;
import main.models.*;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest{

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getInMemoryTaskManager();

        task1 = new Task("Название таски 1", "Описание таски 1",
                LocalDateTime.parse("2024-10-24T21:00:00"), 24 * 60);   //id==1
        taskManager.createTask(task1);

        epic1 = new Epic("Название эпика 1", "Описание эпика 1");   //id==2
        taskManager.createEpic(epic1);

        subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1",
                epic1.getId(), LocalDateTime.parse("2024-10-20T20:00:00"), 24 * 60);    //id==3
        taskManager.createSubtask(subtask1);
    }
}