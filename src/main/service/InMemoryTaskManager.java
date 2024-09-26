package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private final HashMap<Integer, Task> taskStorage;
    private final HashMap<Integer, Epic> epicStorage;
    private final HashMap<Integer, Subtask> subtaskStorage;
    private int newId = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.taskStorage = new HashMap<>();
        this.epicStorage = new HashMap<>();
        this.subtaskStorage = new HashMap<>();
    }

    //Создать задачу
    @Override
    public Task createTask(Task task) {
        task.setId(newId);
        taskStorage.put(newId, task);
        newId++;
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(newId);
        epicStorage.put(newId, epic);
        newId++;
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(newId);
        subtaskStorage.put(newId, subtask);
        Epic epic = epicStorage.get(subtask.getEpicId());
        epic.setSubtasks(newId);
        epicStorage.put(subtask.getEpicId(), epic);
        newId++;
        return subtask;
    }

    //Получить задачу по id
    @Override
    public Task getTaskById(int id) {
        historyManager.add(taskStorage.get(id));
        return taskStorage.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicStorage.get(id));
        return epicStorage.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtaskStorage.get(id));
        return subtaskStorage.get(id);
    }

    //Получить весь список задач
    @Override
    public Collection<Task> getAllTask() {
        return taskStorage.values();
    }

    @Override
    public Collection<Epic> getAllEpic() {
        return epicStorage.values();
    }

    @Override
    public Collection<Subtask> getAllSubtask() {
        return subtaskStorage.values();
    }

    //Обновить задачу
    @Override
    public void updateTask(Task task) {
        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            epicStorage.put(epic.getId(), epic);
            checkUpdateEpicStatus(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskStorage.containsKey(subtask.getId())) {
            subtaskStorage.put(subtask.getId(), subtask);
            checkUpdateEpicStatus(epicStorage.get(subtask.getEpicId()));
        }
    }

    //Удалить все задачи
    @Override
    public void deleteAllTask() {
        for (int taskId : taskStorage.keySet()) {
            historyManager.remove(taskId);
        }
        taskStorage.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (int epicId : epicStorage.keySet()) {
            historyManager.remove(epicId);
        }
        for (int subtaskId : subtaskStorage.keySet()) {
            historyManager.remove(subtaskId);
        }
        epicStorage.clear();
        subtaskStorage.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (int subtaskId : subtaskStorage.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtaskStorage.clear();

        for (int epicId : epicStorage.keySet()) {
            Epic epic = epicStorage.get(epicId);
            epic.deleteAllSubtask();
            checkUpdateEpicStatus(epic);
        }
    }

    //Удалить задачу по id
    @Override
    public void deleteTaskById(int id) {
        taskStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicStorage.get(id);
        if (epic != null) {
            ArrayList<Integer> subtasks = epic.getSubtasks();
            epicStorage.remove(id);
            for (int subtaskId : subtasks) {
                subtaskStorage.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Epic epic = epicStorage.get(subtaskStorage.get(id).getEpicId());
        subtaskStorage.remove(id);
        if (epic != null) {
            epic.deleteSubtaskId(id);
            checkUpdateEpicStatus(epic);
        }
        historyManager.remove(id);
    }

    //Получить список всех подзадач определённого эпика.
    @Override
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
        boolean isDone = true;

        for (Subtask subtask : subtasks) {
            StatusModel status = subtask.getStatus();
            if (status.equals(StatusModel.NEW)) {
                epic.setStatus(StatusModel.NEW);
                isDone = false;
            } else if (status.equals(StatusModel.IN_PROGRESS)) {
                epic.setStatus(StatusModel.IN_PROGRESS);
                return;
            } else if (status.equals(StatusModel.DONE)) {
                epic.setStatus(StatusModel.IN_PROGRESS);
            }
        }

        if (isDone) {
            epic.setStatus(StatusModel.DONE);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
