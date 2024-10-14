package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusModel;
import main.util.TypeTask;

import static main.util.TypeTask.*;
import static main.util.TypeTask.EPIC;

public class TaskConverter {

    private TaskConverter() {
    }

    //Преобразовать объект задачи в строку
    protected static String toString(Task task) {
        String epicId = "";
        if (task.getType().equals(SUBTASK))
            epicId = String.valueOf(((Subtask) task).getEpicId());

        return task.getId() + "," +
                task.getType() + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                epicId + "\n";
    }

    //Преобразовать строку в объект задачи
    protected static Task fromString(String value) {
        String[] splitValue = value.split(",");
        int id = Integer.parseInt(splitValue[0]);
        TypeTask type = valueOf(splitValue[1]);
        String name = splitValue[2];
        StatusModel status = StatusModel.valueOf(splitValue[3]);
        String description = splitValue[4];
        int epicId = 0;
        if (type.equals(SUBTASK)) {
            epicId = Integer.parseInt(splitValue[5]);
        }

        if (type == TASK) {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(status);
            return task;
        } else if (type == EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else {
            Subtask subtask = new Subtask(name, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        }
    }

}
