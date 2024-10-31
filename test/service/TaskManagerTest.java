package service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.TaskManager;
import main.util.StatusModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {
    TaskManager taskManager;
    Task task1;
    Epic epic1;
    Subtask subtask1;

    @DisplayName("Создание задачи")
    @Test
    void createTask() {
        assertNotNull(taskManager.getAllTask(), "Хранилище тасок пустое");
        assertEquals(task1.getId(), taskManager.getTaskById(1).getId(), "id не совпадает");
        assertEquals(task1.getTitle(), taskManager.getTaskById(1).getTitle(), "Название не совпадает");
        assertEquals(task1.getDescription(), taskManager.getTaskById(1).getDescription(), "Описание не совпадает");
        assertEquals(task1.getStatus(), taskManager.getTaskById(1).getStatus(), "Статус не совпадает");
        assertEquals(task1.getStartTime(), taskManager.getTaskById(1).getStartTime(), "Старт задачи не совпадает");
        assertEquals(task1.getDuration(), taskManager.getTaskById(1).getDuration(), "Продолжительность не совпадает");
    }

    @DisplayName("Создание эпика")
    @Test
    void createEpic() {
        assertNotNull(taskManager.getAllEpic(), "Хранилище эпиков пустое");
        assertEquals(epic1.getId(), taskManager.getEpicById(2).getId(), "id не совпадает");
        assertEquals(epic1.getTitle(), taskManager.getEpicById(2).getTitle(), "Название не совпадает");
        assertEquals(epic1.getDescription(), taskManager.getEpicById(2).getDescription(), "Описание не совпадает");
        assertEquals(epic1.getStatus(), taskManager.getEpicById(2).getStatus(), "Статус не совпадает");
        assertEquals(epic1.getStartTime(), taskManager.getEpicById(2).getStartTime(), "Старт задачи не совпадает");
        assertEquals(epic1.getDuration(), taskManager.getEpicById(2).getDuration(), "Продолжительность не совпадает");
        assertEquals(epic1.getSubtasks(), taskManager.getEpicById(2).getSubtasks(), "Список подзадач не совпадает");
        assertEquals(epic1.getEndTime(), taskManager.getEpicById(2).getEndTime(), "Дата окончания задачи не совпадает");
    }

    @DisplayName("Создание подзадачи")
    @Test
    void createSubtask() {
        assertNotNull(taskManager.getAllSubtask(), "Хранилище сабтасок пустое");
        assertEquals(subtask1.getId(), taskManager.getSubtaskById(3).getId(), "id не совпадает");
        assertEquals(subtask1.getTitle(), taskManager.getSubtaskById(3).getTitle(), "Название не совпадает");
        assertEquals(subtask1.getDescription(), taskManager.getSubtaskById(3).getDescription(), "Описание не совпадает");
        assertEquals(subtask1.getStatus(), taskManager.getSubtaskById(3).getStatus(), "Статус не совпадает");
        assertEquals(subtask1.getEpicId(), taskManager.getSubtaskById(3).getEpicId(), "id эпика не совпадает");
        assertEquals(subtask1.getStartTime(), taskManager.getSubtaskById(3).getStartTime(), "Старт задачи не совпадает");
        assertEquals(subtask1.getDuration(), taskManager.getSubtaskById(3).getDuration(), "Продолжительность не совпадает");
    }

    @DisplayName("Получение задачи по id")
    @Test
    void getTaskById() {
        assertEquals(task1, taskManager.getTaskById(1), "Полученная таска != положенной");
    }

    @DisplayName("Получение эпика по id")
    @Test
    void getEpicById() {
        assertEquals(epic1, taskManager.getEpicById(2), "Полученный эпик != положенному");
    }

    @DisplayName("Получение подзадачи по id")
    @Test
    void getSubtaskById() {
        assertEquals(subtask1, taskManager.getSubtaskById(3), "Полученная сабтаска != положенной");
    }

    @DisplayName("Получение всех задач")
    @Test
    void getAllTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task1);
        assertEquals(3, taskManager.getAllTask().size(), "Количество сохраненных задач не совпадает");
    }

    @DisplayName("Получение всех эпиков")
    @Test
    void getAllEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic1);
        assertEquals(3, taskManager.getAllEpic().size(), "Количество сохраненных задач не совпадает");
    }

    @DisplayName("Получение всех подзадач")
    @Test
    void getAllSubtask() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask1);
        assertEquals(3, taskManager.getAllSubtask().size(),
                "Количество сохраненных задач не совпадает");
    }

    @DisplayName("Обновление задачи")
    @Test
    void updateTask() {
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(1);
        taskManager.updateTask(taskForUpdate);

        assertEquals(taskForUpdate.getTitle(), taskManager.getTaskById(1).getTitle(), "Задача не обновлена");
        assertEquals(taskForUpdate.getDescription(), taskManager.getTaskById(1).getDescription(), "Задача не обновлена");
    }

    @DisplayName("Обновление задачи, id не существует")
    @Test
    void updateTaskIdNotExist() {
        Task taskForUpdate = new Task("Название таски 2", "Описание таски 2");
        taskForUpdate.setId(2);
        taskManager.updateTask(taskForUpdate);

        assertEquals(task1.getTitle(), taskManager.getTaskById(1).getTitle(), "Задача не должна быть изменена");
        assertEquals(task1.getDescription(), taskManager.getTaskById(1).getDescription(), "Задача не должна быть изменена");
        assertEquals(1, taskManager.getAllTask().size(), "Неверное количество задач");
    }

    @DisplayName("Обновление эпика")
    @Test
    void updateEpic() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        taskManager.updateEpic(epicForUpdate);

        assertEquals(epicForUpdate.getTitle(), taskManager.getEpicById(2).getTitle(), "Задача не обновлена");
        assertEquals(epicForUpdate.getDescription(), taskManager.getEpicById(2).getDescription(), "Задача не обновлена");
        assertEquals(StatusModel.NEW, taskManager.getEpicById(2).getStatus(), "Статус не должен меняться");
    }

    @DisplayName("Обновление статуса эпика")
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

        assertEquals(epicForUpdate.getTitle(), taskManager.getEpicById(2).getTitle(), "Задача не обновлена");
        assertEquals(epicForUpdate.getDescription(), taskManager.getEpicById(2).getDescription(), "Задача не обновлена");
        assertEquals(StatusModel.IN_PROGRESS, taskManager.getEpicById(2).getStatus(), "Статус некорректный");
    }

    @DisplayName("Обновление статуса эпика напрямую запрещено")
    @Test
    void notUpdateEpicStatus() {
        Epic epicForUpdate = new Epic("Название эпика 2", "Описание эпика 2");
        epicForUpdate.setId(2);
        epicForUpdate.setSubtasks(3);
        epicForUpdate.setStatus(StatusModel.DONE);
        taskManager.updateEpic(epicForUpdate);

        assertEquals(epicForUpdate.getTitle(), taskManager.getEpicById(2).getTitle(), "Задача не обновлена");
        assertEquals(epicForUpdate.getDescription(), taskManager.getEpicById(2).getDescription(), "Задача не обновлена");
        assertEquals(StatusModel.NEW, taskManager.getEpicById(2).getStatus(), "Статус не должен меняться");
    }

    @DisplayName("Обновление статуса эпика, все задачи закрыты")
    @Test
    void updateEpicStatusAllSubtaskClosed() {
        Subtask subtaskForUpdate = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);
        subtaskForUpdate.setId(3);
        subtaskForUpdate.setStatus(StatusModel.DONE);
        taskManager.updateSubtask(subtaskForUpdate);

        assertEquals(subtaskForUpdate.getTitle(), taskManager.getSubtaskById(3).getTitle(), "Задача не обновлена");
        assertEquals(subtaskForUpdate.getDescription(), taskManager.getSubtaskById(3).getDescription(), "Задача не обновлена");
        assertEquals(StatusModel.DONE, taskManager.getSubtaskById(3).getStatus(),
                "Статус сабтаски не изменился");
        assertEquals(StatusModel.DONE, taskManager.getEpicById(2).getStatus(), "Статус эпика не изменился");
    }

    @DisplayName("Обновление подзадачи")
    @Test
    void updateSubtask() {
        Subtask subtaskForUpdate = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 2);
        subtaskForUpdate.setId(3);
        taskManager.updateSubtask(subtaskForUpdate);

        assertEquals(subtaskForUpdate.getTitle(), taskManager.getSubtaskById(3).getTitle(), "Задача не обновлена");
        assertEquals(subtaskForUpdate.getDescription(), taskManager.getSubtaskById(3).getDescription(), "Задача не обновлена");
    }

    @DisplayName("Обновление параметров эпика при обновлении подзадачи")
    @Test
    void updateSubtaskStatusAndUpdateEpic() {
        Subtask subtaskForUpdate = new Subtask("Название сабтаски 2", "Описание сабтаски 2", epic1.getId(),
                LocalDateTime.parse("2024-10-21T21:00:00"), 24 * 60);
        subtaskForUpdate.setId(3);
        subtaskForUpdate.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateSubtask(subtaskForUpdate);

        assertEquals(subtaskForUpdate.getTitle(),
                taskManager.getSubtaskById(3).getTitle(), "Задача не обновлена");
        assertEquals(subtaskForUpdate.getDescription(),
                taskManager.getSubtaskById(3).getDescription(), "Задача не обновлена");
        assertEquals(StatusModel.IN_PROGRESS, taskManager.getSubtaskById(3).getStatus(),
                "Статус сабтаски не изменился");
        assertEquals(StatusModel.IN_PROGRESS, taskManager.getEpicById(2).getStatus(),
                "Статус эпика не изменился");
        assertEquals(subtaskForUpdate.getStartTime(), taskManager.getEpicById(2).getStartTime(),
                "Дата начала выполнения эпика не изменился");
        assertEquals(subtaskForUpdate.getDuration(), taskManager.getEpicById(2).getDuration(),
                "Срок выполнения эпика не изменился");
        assertEquals(subtaskForUpdate.getEndTime(),
                taskManager.getEpicById(2).getStartTime().plus(taskManager.getEpicById(2).getDuration()),
                "Дата окончания эпика не изменился");

    }

    @DisplayName("Обновление списка задач/подзадач в порядке приоритета")
    @Test
    void updatePrioritizedTasks() {
        Subtask subtask2 = new Subtask("Название подзадачи 2", "Описание подзадачи 2", epic1.getId(),
                null, 0);
        taskManager.createSubtask(subtask2);

        assertEquals(2,
                taskManager.getPrioritizedTasks().size(), "Количество задач не соответствует");
        assertEquals(subtask1,
                taskManager.getPrioritizedTasks().getFirst(), "Первая задача некорректна");
        assertEquals(task1,
                taskManager.getPrioritizedTasks().getLast(), "Последняя задача некорректна");

    }

    @DisplayName("Удаление всех задач")
    @Test
    void deleteAllTask() {
        taskManager.deleteAllTask();

        assertEquals(0, taskManager.getAllTask().size(), "Список задач не пустой");
    }

    @DisplayName("Удаление всех эпиков")
    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();

        assertEquals(0, taskManager.getAllEpic().size(), "Список эпиков не пустой");
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
    }

    @DisplayName("Удаление всех подзадач с обновлением статуса эпика")
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

    @DisplayName("Удаление всех подзадач в закрытом эпике")
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

    @DisplayName("Удаление задачи по id")
    @Test
    void deleteTaskById() {
        taskManager.deleteTaskById(1);

        assertEquals(0, taskManager.getAllTask().size(), "Список задач не пустой");
    }

    @DisplayName("Удаление эпика' по id")
    @Test
    void deleteEpicById() {
        taskManager.deleteEpicById(2);

        assertEquals(0, taskManager.getAllEpic().size(), "Список эпиков не пустой");
        assertEquals(0, taskManager.getAllSubtask().size(), "Список подзадач не пустой");
    }

    @DisplayName("Удаление подзадачи по id с обновлением статуса эпика")
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

    @DisplayName("Получение подзадач в эпике")
    @Test
    void getAllSubtaskInEpic() {
        Subtask subtask2 = new Subtask("Название подзадачи 2", "Описание подзадачи 2", epic1.getId(),
                null, 0);
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Название эпика 2", "Описание эпика 2");   //id==2
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Название подзадачи 3", "Описание подзадачи 3", epic2.getId(),
                null, 0);
        taskManager.createSubtask(subtask3);

        assertEquals(2, taskManager.getAllSubtaskInEpic(epic1.getId()).size(),
                "Количество подзадач не совпадает");
    }

    @DisplayName("Создание задачи с пересечением времени выполнения")
    @Test
    void creatingTasksWithIntersection() {
        Task task2 = new Task("Название задачи 2", "Описание задачи 2",
                LocalDateTime.parse("2024-10-25T20:00:01"), 12 * 60);
        taskManager.createTask(task2);
        Task task3 = new Task("Название задачи 3", "Описание задачи 3",
                LocalDateTime.parse("2024-10-20T19:00:01"), 12 * 60);
        taskManager.createTask(task3);

        assertEquals(1, taskManager.getAllTask().size(), "Задача не одна");
        assertEquals(task1, taskManager.getTaskById(1), "Список задач некорректен");
    }


}
