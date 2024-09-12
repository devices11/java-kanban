package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.Managers;
import main.service.TaskManager;
import main.util.StatusModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    Task task1;
    Epic epic1;
    Subtask subtask1;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();

        task1 = new Task("Название таски 1", "Описание таски 1");   //id==1
        taskManager.createTask(task1);

        epic1 = new Epic("Название эпика 1", "Описание эпика 1");   //id==2
        taskManager.createEpic(epic1);

        subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 1", 2);    //id==3
        taskManager.createSubtask(subtask1);
    }

    @Test
    void createTask() {
        assertNotNull(taskManager.getAllTask(), "Хранилище тасок пустое");
    }

    @Test
    void createEpic() {
        assertNotNull(taskManager.getAllEpic(), "Хранилище эпиков пустое");
    }

    @Test
    void createSubtask() {
        assertNotNull(taskManager.getAllSubtask(), "Хранилище сабтасок пустое");
    }

    @Test
    void getTaskById() {
        assertEquals(task1, taskManager.getTaskById(1), "Полученная таска != положенной");
    }

    @Test
    void getEpicById() {
        assertEquals(epic1, taskManager.getEpicById(2), "Полученный эпик != положенному");
    }

    @Test
    void getSubtaskById() {
        assertEquals(subtask1, taskManager.getSubtaskById(3), "Полученная сабтаска != положенной");
    }

    @Test
    void getAllTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task1);
        assertEquals(3, taskManager.getAllTask().size(), "Количество сохраненных задач не совпадает");
    }

    @Test
    void getAllEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic1);
        assertEquals(3, taskManager.getAllEpic().size(), "Количество сохраненных задач не совпадает");
    }

    @Test
    void getAllSubtask() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask1);
        assertEquals(3, taskManager.getAllSubtask().size(),
                "Количество сохраненных задач не совпадает");
    }

    @Test
    void updateTask() {
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(1);
        taskManager.updateTask(taskForUpdate);
        assertEquals(taskForUpdate, taskManager.getTaskById(1), "Задача не обновлена");
    }

    @Test
    void updateTaskIdNotExist() {
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(2);
        taskManager.updateTask(taskForUpdate);
        assertEquals(task1, taskManager.getTaskById(1), "Задача не должна быть изменена");
        assertEquals(1, taskManager.getAllTask().size(), "Неверное количество задач");
    }

    @Test
    void updateEpic() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        taskManager.updateEpic(epicForUpdate);
        assertEquals(epicForUpdate, taskManager.getEpicById(2), "Задача не обновлена");
        assertEquals(StatusModel.NEW, taskManager.getEpicById(2).getStatus(), "Статус не должен меняться");
    }

    @Test
    void updateEpicStatusIfSubtaskStatusFromNewToDone() {
        Subtask subtask2 = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);    //id==4
        subtask2.setStatus(StatusModel.DONE);
        taskManager.createSubtask(subtask2);
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        epicForUpdate.setSubtasks(4);
        taskManager.updateEpic(epicForUpdate);
        assertEquals(epicForUpdate, taskManager.getEpicById(2), "Задача не обновлена");
        assertEquals(StatusModel.IN_PROGRESS, taskManager.getEpicById(2).getStatus(), "Статус некорректный");
    }

    @Test
    void notUpdateEpicStatus() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        epicForUpdate.setStatus(StatusModel.DONE);
        taskManager.updateEpic(epicForUpdate);
        assertEquals(epicForUpdate, taskManager.getEpicById(2), "Задача не обновлена");
        assertEquals(StatusModel.NEW, taskManager.getEpicById(2).getStatus(), "Статус не должен меняться");
    }

    @Test
    void updateEpicStatusAllSubtaskClosed() {
        Subtask subtaskForUpdate = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);
        subtaskForUpdate.setId(3);
        subtaskForUpdate.setStatus(StatusModel.DONE);
        taskManager.updateSubtask(subtaskForUpdate);
        assertEquals(subtaskForUpdate, taskManager.getSubtaskById(3), "Задача не обновлена");
        assertEquals(StatusModel.DONE, taskManager.getSubtaskById(3).getStatus(),
                "Статус сабтаски не изменился");
        assertEquals(StatusModel.DONE, taskManager.getEpicById(2).getStatus(), "Статус эпика не изменился");
    }

    @Test
    void updateSubtask() {
        Subtask subtaskForUpdate = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);
        subtaskForUpdate.setId(3);
        taskManager.updateSubtask(subtaskForUpdate);
        assertEquals(subtaskForUpdate, taskManager.getSubtaskById(3), "Задача не обновлена");
    }

    @Test
    void updateSubtaskStatusAndUpdateEpicStatus() {
        Subtask subtaskForUpdate = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);
        subtaskForUpdate.setId(3);
        subtaskForUpdate.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateSubtask(subtaskForUpdate);
        assertEquals(subtaskForUpdate, taskManager.getSubtaskById(3), "Задача не обновлена");
        assertEquals(StatusModel.IN_PROGRESS, taskManager.getSubtaskById(3).getStatus(),
                "Статус сабтаски не изменился");
        assertEquals(StatusModel.IN_PROGRESS, taskManager.getEpicById(2).getStatus(),
                "Статус эпика не изменился");
    }

    @Test
    void deleteAllTask() {
        taskManager.deleteAllTask();
        assertEquals(0, taskManager.getAllTask().size(), "Список задач не пустой");
    }

    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getAllEpic().size(), "Список эпиков не пустой");
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
    }

    @Test
    void deleteAllSubtaskAndUpdateEpicStatus() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        epicForUpdate.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateEpic(epicForUpdate);
        taskManager.deleteAllSubtask();
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
        assertEquals(StatusModel.NEW, taskManager.getEpicById(2).getStatus(), "Статус эпика не изменился");
    }

    @Test
    void deleteAllSubtaskNotUpdateEpicStatusDone() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        epicForUpdate.setStatus(StatusModel.DONE);
        subtask1.setStatus(StatusModel.DONE);
        taskManager.updateEpic(epicForUpdate);
        taskManager.deleteAllSubtask();
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
        assertEquals(StatusModel.DONE, taskManager.getEpicById(2).getStatus(), "Статус эпика не изменился");
    }

    @Test
    void deleteTaskById() {
        taskManager.deleteTaskById(1);
        assertEquals(0, taskManager.getAllTask().size(), "Список задач не пустой");
    }

    @Test
    void deleteEpicById() {
        taskManager.deleteEpicById(2);
        assertEquals(0, taskManager.getAllEpic().size(), "Список эпиков не пустой");
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
    }

    @Test
    void deleteSubtaskByIdAndUpdateEpicStatus() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        epicForUpdate.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateEpic(epicForUpdate);
        taskManager.deleteSubtaskById(3);
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
        assertEquals(StatusModel.NEW, taskManager.getEpicById(2).getStatus(), "Статус эпика не изменился");
    }

    @Test
    void getAllSubtaskInEpic() {
        assertEquals(1, taskManager.getAllSubtaskInEpic(epic1.getId()).size(),
                "Количество подзадач не сопадает");
    }
}