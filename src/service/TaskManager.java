package service;

import models.Epic;
import models.Subtask;
import models.Task;
import util.StatusModel;

import java.util.ArrayList;
import java.util.Collection;
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
    public Collection<Task> getAllTask() {
        return taskStorage.values();
    }

    public Collection<Epic> getAllEpic() {
        return epicStorage.values();
    }

    public Collection<Subtask> getAllSubtask() {
        return subtaskStorage.values();
    }

    //Обновить задачу
    public void updateTask(Task task) {
        if  (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            checkUpdateEpicStatus(epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskStorage.containsKey(subtask.getId())) {
            subtaskStorage.put(subtask.getId(), subtask);
            checkUpdateEpicStatus(getEpicById(subtask.getEpicId()));
        }
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
            epic.deleteAllSubtask();
            checkUpdateEpicStatus(epic);
        }
    }

    //Удалить задачу по id
    public void deleteTaskById(int id) {
        taskStorage.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epicStorage.get(id);
        if (epic != null) {
            ArrayList<Integer> subtasks = epic.getSubtasks();
            epicStorage.remove(id);
            for (int subtaskId : subtasks) {
                subtaskStorage.remove(subtaskId);
            }
        }
    }

    public void deleteSubtaskById(int id) {
        Epic epic = getEpicById(subtaskStorage.get(id).getEpicId());
        subtaskStorage.remove(id);
        if (epic != null) {
            epic.deleteSubtaskId(id);
            checkUpdateEpicStatus(epic);
        }
    }

    //Получить список всех подзадач определённого эпика.
    public ArrayList<Subtask> getAllSubtaskInEpic(Integer epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = epicStorage.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                subtasks.add(getSubtaskById(subtaskId));
            }
        }
        return subtasks;
    }

    //Проверить и обновить статус эпика в зависимости от статуса подзадач. Пользователю не доступен.
    private void checkUpdateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty() && !epic.getStatus().equals(StatusModel.DONE)) {
            epic.setStatus(StatusModel.NEW);
            return;
        }

        ArrayList<Subtask> subtasks = getAllSubtaskInEpic(epic.getId());

        for (Subtask subtask : subtasks) {
            StatusModel status = subtaskStorage.get(subtask.getId()).getStatus();
            if (status.equals(StatusModel.NEW)) {
                epic.setStatus(StatusModel.NEW);
            } else if (status.equals(StatusModel.IN_PROGRESS)) {
                epic.setStatus(StatusModel.IN_PROGRESS);
                return;
            }
        }

        epic.setStatus(StatusModel.DONE);
    }

}
