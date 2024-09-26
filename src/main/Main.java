package main;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.service.Managers;
import main.service.TaskManager;

import java.util.List;

public class Main {
    static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        Task task1 = new Task("Название таски 1", "Описание таски 1");
        task1.setId(1);
        taskManager.createTask(task1);
        Task task2 = new Task("Название таски 2", "Описание таски 2");
        task2.setId(2);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        epic1.setId(3);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Название сабтаски 1", "Описание сабтаски 2", 3);
        subtask1.setId(4);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Название сабтаски 2", "Описание сабтаски 2", 3);
        subtask2.setId(5);
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Название эпика 2", "Описание эпика 2");
        epic2.setId(6);
        taskManager.createEpic(epic2);

        List<Task> history;
        System.out.println("-----------------");
        System.out.println("Первая запись в истории самая свежая!");
        System.out.println("-----------------");

        System.out.println("История пустая");
        history = taskManager.getHistory();
        System.out.println("Размер истории " + history.size());

        System.out.println("-----------------");

        taskManager.getTaskById(1);
        System.out.println("История просмотра, одна запись");
        history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println("id " + task.getId());
        }

        System.out.println("-----------------");
        System.out.println("История просмотра, несколько записей");
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(11); //Несуществующая таска
        taskManager.getEpicById(3);
        taskManager.getEpicById(31); //Несуществующая таска
        taskManager.getEpicById(6);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(41); //Несуществующая таска
        taskManager.getSubtaskById(5);
        taskManager.getTaskById(1);

        history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println("id " + task.getId());
        }

        System.out.println("-----------------");
        System.out.println("Удалили таску с id 2");
        taskManager.deleteTaskById(2);
        history = taskManager.getHistory();
        System.out.println(" ");
        for (Task task : history) {
            System.out.println("id " + task.getId());
        }

        System.out.println("-----------------");
        System.out.println("Удалили эпик 3 с тасками 4, 5");
        taskManager.deleteEpicById(3);
        history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println("id " + task.getId());
        }

        System.out.println("-----------------");
        System.out.println("Удалили все таски");
        taskManager.deleteAllTask();
        history = taskManager.getHistory();
        for (Task task : history) {
            System.out.println("id " + task.getId());
        }

        System.out.println("-----------------");
        System.out.println("Удалили все эпики");
        taskManager.deleteAllEpic();
        history = taskManager.getHistory();
        System.out.println("Размер истории " + history.size());

        System.out.println("-----------------");
    }

}
