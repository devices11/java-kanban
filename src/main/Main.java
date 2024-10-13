package main;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.Managers;
import main.service.TaskManager;
import main.util.StatusModel;

import java.util.List;

public class Main {
    static TaskManager taskManager = Managers.getFileBackedTaskManager();

    public static void main(String[] args) {
        System.out.println("--START--");
        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask() + "\n");

        Task task1 = new Task("Название таски 1", "Описание таски 1");
        taskManager.createTask(task1);

        Task task2 = new Task("Название таски 2", "Описание таски 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 2", 3);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 3);
        taskManager.createSubtask(subtask2);
        subtask2.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        Epic epic2 = new Epic("Название эпика 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        System.out.println("--END--");
        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());
    }

}
