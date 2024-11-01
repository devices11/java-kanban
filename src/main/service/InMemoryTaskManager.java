package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusModel;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;
    protected final HashMap<Integer, Task> taskStorage;
    protected final HashMap<Integer, Epic> epicStorage;
    protected final HashMap<Integer, Subtask> subtaskStorage;
    protected int newId = 1;
    protected final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.taskStorage = new HashMap<>();
        this.epicStorage = new HashMap<>();
        this.subtaskStorage = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    //Создать задачу
    @Override
    public Task createTask(Task task) {
        if (isTasksOverlap(task))
            return null;
        task.setId(newId);
        taskStorage.put(newId, task);
        newId++;
        updatePrioritizedTasks(task);
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
        if (isTasksOverlap(subtask))
            return null;
        subtask.setId(newId);
        subtaskStorage.put(newId, subtask);
        Epic epic = epicStorage.get(subtask.getEpicId());
        epic.setSubtasks(newId);
        checkUpdateEpic(epic);
        epicStorage.put(subtask.getEpicId(), epic);
        newId++;
        updatePrioritizedTasks(subtask);
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
    public Task updateTask(Task task) {
        if (isTasksOverlap(task))
            return null;

        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
            updatePrioritizedTasks(task);
            return task;
        } else return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            epicStorage.put(epic.getId(), epic);
            checkUpdateEpic(epic);
            return epic;
        } else return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (isTasksOverlap(subtask))
            return null;

        if (subtaskStorage.containsKey(subtask.getId())) {
            subtaskStorage.put(subtask.getId(), subtask);
            Epic epic = epicStorage.get(subtask.getEpicId());
            checkUpdateEpic(epic);
            updatePrioritizedTasks(subtask);
            return subtask;
        } else return null;
    }

    //Удалить все задачи
    @Override
    public void deleteAllTask() {
        taskStorage.keySet().forEach(taskId -> {
            historyManager.remove(taskId);
            removePrioritizedTasks(taskStorage.get(taskId));
        });
        taskStorage.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicStorage.keySet().forEach(historyManager::remove);
        subtaskStorage.keySet().forEach(historyManager::remove);
        epicStorage.clear();
        subtaskStorage.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtaskStorage.keySet().forEach(subtaskId -> {
            historyManager.remove(subtaskId);
            removePrioritizedTasks(subtaskStorage.get(subtaskId));
        });
        subtaskStorage.clear();

        epicStorage.values().forEach(epic -> {
            epic.deleteAllSubtask();
            checkUpdateEpic(epic);
        });
    }

    //Удалить задачу по id
    @Override
    public void deleteTaskById(int id) {
        removePrioritizedTasks(taskStorage.get(id));
        taskStorage.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicStorage.get(id);
        if (epic != null) {
            ArrayList<Integer> subtasks = epic.getSubtasks();
            epicStorage.remove(id);
            subtasks.forEach(subtaskId -> {
                subtaskStorage.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskStorage.get(id);
        Epic epic = epicStorage.get(subtask.getEpicId());
        if (epic != null) {
            epic.deleteSubtaskId(id);
            checkUpdateEpic(epic);
        }
        removePrioritizedTasks(subtask);
        subtaskStorage.remove(id);
        historyManager.remove(id);
    }

    //Получить список всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> getAllSubtaskInEpic(Integer epicId) {
        return epicStorage.get(epicId).getSubtasks().stream()
                .map(subtaskStorage::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //Получить историю просмотра задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Вернуть список задач в порядке приоритета
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updatePrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }
    }

    private void removePrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    protected void checkUpdateEpic(Epic epic) {
        checkUpdateEpicStatus(epic);
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        updateEpicEndTime(epic);
    }

    //    Проверить и обновить статус эпика в зависимости от статуса подзадач. Пользователю не доступен.
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

    //Обновить продолжительность эпика
    private void updateEpicDuration(Epic epic) {
        Duration duration = getAllSubtaskInEpic(epic.getId()).stream()
                .filter(subtask -> subtask.getDuration() != null && !subtask.getDuration().isZero())
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(duration);
    }

//    Обновить дату взятия эпика в работу
    private void updateEpicStartTime(Epic epic) {
        Optional<Subtask> startTime = getAllSubtaskInEpic(epic.getId()).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Task::getStartTime));

        startTime.ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));
    }

    //Обновить дату окончания эпика
    protected void updateEpicEndTime(Epic epic) {
        Optional<Subtask> endTime = getAllSubtaskInEpic(epic.getId()).stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .max(Comparator.comparing(Task::getStartTime));

        endTime.ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
    }

    //Проверить пересечение задач
    private boolean isTasksOverlap(Task task) {
        if (task == null || task.getStartTime() == null) {
            return false;
        }

        Optional<Task> overlap =
                getPrioritizedTasks().stream()
                        .filter(task1 -> task1.getId() != task.getId())
                        .filter(task1 -> task.getStartTime().isBefore(task1.getEndTime()) &&
                                task.getEndTime().isAfter(task1.getStartTime()))
                        .findFirst();
        return overlap.isPresent();
    }
}