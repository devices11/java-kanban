package main;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.Managers;
import main.service.TaskManager;
import main.util.StatusModel;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    static TaskManager taskManager = Managers.getInMemoryTaskManager();

    public static void main(String[] args) {
        System.out.println("--createTask--");
        Task task1 = new Task("Название таски 1", "Описание таски 1", null, 0);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofDays(1).plus(Duration.ofHours(1)));
        taskManager.createTask(task1);
        System.out.println(taskManager.getAllTask());
        task1.setStartTime(LocalDateTime.now().plusDays(2));
        task1.setDuration(Duration.ofDays(3).plus(Duration.ofHours(1)));
        taskManager.updateTask(task1);
        System.out.println(taskManager.getAllTask());
        task1.setStartTime(null);
        task1.setDuration(null);
        taskManager.updateTask(task1);
        System.out.println(taskManager.getAllTask());

        Task task2 = new Task("Название таски 2", "Описание таски 2", null, 0);
        taskManager.createTask(task2);

        System.out.println("--createEpic--");
        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        System.out.println(taskManager.getAllEpic());

        System.out.println("--createSubtask--");
        Subtask subtask1 = new Subtask("Название сабтаски 1111", "Описание сабтаски 1", 3);
        taskManager.createSubtask(subtask1);
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());
        System.out.println("--------");
        Subtask subtask2 = new Subtask("Название сабтаски 2111", "Описание сабтаски 2", 3);
        subtask2.setStartTime(LocalDateTime.now());
        subtask2.setDuration(Duration.ofDays(1).plus(Duration.ofHours(1)));
        taskManager.createSubtask(subtask2);
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        System.out.println("--updateSubtask--");
        subtask2.setStatus(StatusModel.IN_PROGRESS);
        subtask1.setStartTime(LocalDateTime.now().minusDays(1));
        subtask1.setDuration(Duration.ofDays(1).plus(Duration.ofHours(1)));
        taskManager.updateSubtask(subtask2);
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());
        System.out.println("--------");
//        subtask1.setStartTime(null);
//        subtask1.setDuration(null);
        taskManager.updateSubtask(subtask1);
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        Epic epic2 = new Epic("Название эпика 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        System.out.println("--getEndTime--");
        System.out.println("    --Task");
        System.out.println(taskManager.getAllTask());
        taskManager.getAllTask().stream()
                .map(Task::getEndTime)
                .peek(System.out::println)
                .toList();
        System.out.println("    --Epic");
        System.out.println(taskManager.getAllEpic());
        taskManager.getAllEpic().stream()
                .map(Task::getEndTime)
                .peek(System.out::println)
                .toList();
        System.out.println("    --Subtask");
        System.out.println(taskManager.getAllSubtask());
        taskManager.getAllSubtask().stream()
                .map(Task::getEndTime)
                .peek(System.out::println)
                .toList();;
    }

}
