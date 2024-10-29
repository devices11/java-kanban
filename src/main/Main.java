package main;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.Managers;
import main.service.TaskManager;

import java.time.LocalDateTime;

public class Main {
    static TaskManager taskManager = Managers.getInMemoryTaskManager();

    public static void main(String[] args) {
        System.out.println("--createTask--");
        Task task1 = new Task("Название таски 1", "Описание таски 1", LocalDateTime.parse("2024-10-22T21:00:00"), 24*60);
        if (taskManager.createTask(task1) == null)
            System.out.printf("Задача %s не создана\n", task1.getTitle());


        Task task2 = new Task("Название таски 2", "Описание таски 2", LocalDateTime.parse("2024-10-21T21:01:00"), 24*60);
        if (taskManager.createTask(task2) == null)
            System.out.printf("Задача %s не создана\n", task2.getTitle());

        Task task3= new Task("Название таски 3", "Описание таски 3", null, 10);
        if (taskManager.createTask(task3) == null)
            System.out.printf("Задача %s не создана\n", task3.getTitle());

        Task task4 = new Task("Название таски 4", "Описание таски 4", LocalDateTime.parse("2024-10-26T22:00:00"), 24*60);
        if (taskManager.createTask(task4) == null)
            System.out.printf("Задача %s не создана\n", task4.getTitle());

        System.out.println(taskManager.getPrioritizedTasks().toString());


        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        taskManager.createEpic(epic1);


        System.out.println("--createSubtask--");
        Subtask subtask1 = new Subtask("Название сабтаски 1111", "Описание сабтаски 1", epic1.getId(), LocalDateTime.parse("2024-10-24T22:00:00"), 24*60);
        if (taskManager.createSubtask(subtask1) == null)
            System.out.printf("Задача %s не создана\n", subtask1.getTitle());

        Subtask subtask2 = new Subtask("Название сабтаски 2111", "Описание сабтаски 2", epic1.getId(), LocalDateTime.parse("2024-10-26T23:00:00"), 24*60);
        if (taskManager.createSubtask(subtask2) == null)
            System.out.printf("Задача %s не создана\n", subtask2.getTitle());

        taskManager.getPrioritizedTasks().forEach(System.out::println);

    }

}
