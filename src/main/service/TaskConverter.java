package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.util.StatusModel;
import main.util.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;

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
                task.getStartTime() + "," +
                task.getDuration() + "," +
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
        LocalDateTime startTime = !splitValue[5].isEmpty() ? LocalDateTime.parse(splitValue[5]) : null;
        Duration duration = !splitValue[6].isEmpty() ? Duration.parse(splitValue[6]) : null;
        if (type.equals(SUBTASK)) {
            epicId = Integer.parseInt(splitValue[7]);
        }

        if (type == TASK) {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(status);
            task.setStartTime(startTime);
            task.setDuration(duration);
            return task;
        } else if (type == EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            epic.setStartTime(startTime);
            epic.setDuration(duration);
            return epic;
        } else {
            Subtask subtask = new Subtask(name, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            subtask.setStartTime(startTime);
            subtask.setDuration(duration);
            return subtask;
        }
    }

}
