package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;

import java.util.ArrayList;
import java.util.Collection;

public interface TaskManager {

    Task createTask(Task task);

    Task getTaskById(int id);

    Collection<Task> getAllTask();

    void updateTask(Task task);

    void deleteAllTask();

    void deleteTaskById(int id);

    Epic createEpic(Epic epic);

    Epic getEpicById(int id);

    ArrayList<Subtask> getAllSubtaskInEpic(Integer epicId);

    Collection<Epic> getAllEpic();

    void updateEpic(Epic epic);

    void deleteAllEpic();

    void deleteEpicById(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask getSubtaskById(int id);

    Collection<Subtask> getAllSubtask();

    void updateSubtask(Subtask subtask);

    void deleteAllSubtask();

    void deleteSubtaskById(int id);

}
