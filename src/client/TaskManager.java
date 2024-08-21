package client;

import models.Epic;
import models.Subtask;
import models.Task;
import util.StatusModel;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskStorage = new HashMap<>();
    private final HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();

    private int newId = 1;

    //Создать задачу
    public Task createTask(Task task) {
        task.setId(newId);
        taskStorage.put(newId, task);
        newId++;
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(newId);
        epicStorage.put(newId, epic);
        newId++;
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(newId);
        subtaskStorage.put(newId, subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.setSubtasks(newId);
        epicStorage.put(subtask.getEpicId(), epic);
        newId++;
        return subtask;
    }

    //Получить задачу по id
    public Task getTaskById(int id) {
        return taskStorage.get(id);
    }

    public Epic getEpicById(int id) {
        return epicStorage.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskStorage.get(id);
    }

    //Получить весь список задач
    public HashMap<Integer, Task> getAllTask() {
        return taskStorage;
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epicStorage;
    }

    public HashMap<Integer, Subtask> getAllSubtask() {
        return subtaskStorage;
    }

    //Обновить задачу
    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic updatedEpic = checkUpdateEpicStatus(epic);
        epicStorage.put(updatedEpic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskStorage.put(subtask.getId(), subtask);
        Epic updatedEpic = checkUpdateEpicStatus(getEpicById(subtask.getEpicId()));
        epicStorage.put(updatedEpic.getId(), updatedEpic);
    }

    //Удалить все задачи
    public void deleteAllTask() {
        taskStorage.clear();
    }

    public void deleteAllEpic() {
        epicStorage.clear();
        subtaskStorage.clear();
    }

    public void deleteAllSubtask() {
        subtaskStorage.clear();

        for (int epicId : epicStorage.keySet()) {
            Epic epic = getEpicById(epicId);
            if (!epicStorage.get(epicId).getStatus().equals(StatusModel.DONE)) {
                epic.setStatus(StatusModel.NEW);
                epic.deleteAllSubtask();
                epicStorage.put(epicId, epic);
            } else {
                epic.deleteAllSubtask();
            }
        }
    }


    //Удалить задачу по id
    public void deleteTaskById(int id) {
        taskStorage.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> subtaskIds = getAllSubtaskInEpic(id);
        epicStorage.remove(id);
        for (int subtaskId : subtaskIds) {
            deleteSubtaskById(subtaskId);
        }

    }

    public void deleteSubtaskById(int id) {
        Epic epic = getEpicById(subtaskStorage.get(id).getEpicId());
        subtaskStorage.remove(id);
        if (epic != null) {
            epic.deleteSubtaskId(id);
            Epic updatedEpic = checkUpdateEpicStatus(epic);
            epicStorage.put(updatedEpic.getId(), updatedEpic);
        }
    }

    //Получить список всех подзадач определённого эпика.
    public ArrayList<Integer> getAllSubtaskInEpic(Integer epicId) {
        return epicStorage.get(epicId).getSubtasks();
    }

    //Проверить и обновить статус эпика в зависимости от статуса подзадач. Пользователю не доступен.
    private Epic checkUpdateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty() && !epic.getStatus().equals(StatusModel.DONE)) {
            epic.setStatus(StatusModel.NEW);
            return epic;
        }

        ArrayList<Integer> subtasks = getAllSubtaskInEpic(epic.getId());

        for (Integer subtaskId : subtasks) {
            StatusModel status = subtaskStorage.get(subtaskId).getStatus();
            if (status.equals(StatusModel.NEW)) {
                epic.setStatus(StatusModel.NEW);
            } else if (status.equals(StatusModel.IN_PROGRESS)) {
                epic.setStatus(StatusModel.IN_PROGRESS);
                return epic;
            }
        }

        epic.setStatus(StatusModel.DONE);
        return epic;
    }

}
