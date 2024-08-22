import service.TaskManager;
import models.Epic;
import models.Subtask;
import util.StatusModel;
import models.Task;

public class Main {

    public static void main(String[] args) {
        //-----------ДЛЯ ТЕСТИРОВАНИЯ---------
        TaskManager taskManager = new TaskManager();

        System.out.println("\n--- TEST-1 ---- Создание таски");
        Task task1 = new Task("Позавтракать", "Описание к завтраку");
        System.out.println(taskManager.createTask(task1)); //Создание. Сам объект должен передаваться в качестве параметра.
        System.out.println(taskManager.getAllTask());

        System.out.println("\n---TEST-2----Создание пустой таски");
        Task task2 = new Task(null, null);
        System.out.println(taskManager.createTask(task2));
        System.out.println(taskManager.getAllTask());

        System.out.println("\n---TEST-3----Создание эпика с сабтасками");
        Epic epic1 = new Epic("Построить дом", "Описание дома мечты");
        Subtask subtask1 = new Subtask("Купить участок", "Описание где купить участок", 3);
        Subtask subtask2 = new Subtask("Разработать проект", "Описание проекта", 3);
        System.out.println(taskManager.createEpic(epic1));
        System.out.println(taskManager.createSubtask(subtask1));
        System.out.println(taskManager.createSubtask(subtask2));
        System.out.println(taskManager.getAllEpic());

        System.out.println("\n---TEST-4----Создание эпика с одной сабтаской с пустыми заголовком и описанием");
        Epic epic2 = new Epic(null, null);
        Subtask subtask3 = new Subtask(null, null, 6);
        System.out.println(taskManager.createEpic(epic2));
        System.out.println(taskManager.createSubtask(subtask3));
        System.out.println(taskManager.getAllEpic());

        System.out.println("\n---- TEST-5 --- Получение заявки по id");
        System.out.println("запрос таски 1 - " + taskManager.getTaskById(1));
        System.out.println("запрос эпика 3 - " + taskManager.getEpicById(3));
        System.out.println("запрос сабтаски 7 - " + taskManager.getSubtaskById(7));

        System.out.println("\n---- TEST-6 --- Получение всех заявок");
        System.out.println("Таски " + taskManager.getAllTask());
        System.out.println("Эпики " + taskManager.getAllEpic());
        System.out.println("Сабтаски" + taskManager.getAllSubtask());

        System.out.println("\n---- TEST-7 --- Обновление тасок");
        Task task2new = new Task(null, null);
        task2new.setId(2);
        task2new.setStatus(StatusModel.IN_PROGRESS);
        task2new.setTitle("Позавтракать222");
        task2new.setDescription("Описание к завтраку2222");
        taskManager.updateTask(task2new); // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        System.out.println(taskManager.getAllTask());

        System.out.println("\n---- TEST-8 --- Обновление сабтаски с обновлением статуса эпика");
        Subtask subtaskForUpdate = new Subtask("Нанять рабочих", "Бригада У", 3);
        subtaskForUpdate.setId(5);
        subtaskForUpdate.setStatus(StatusModel.IN_PROGRESS);
        System.out.println("До " + taskManager.getAllEpic());
        System.out.println("До " + taskManager.getAllSubtask());
        taskManager.updateSubtask(subtaskForUpdate);
        System.out.println("После " + taskManager.getAllEpic());
        System.out.println("После " + taskManager.getAllSubtask());

        System.out.println("\n---TEST-9----Попытка обновить статус эпика в DONE");
        epic1.setStatus(StatusModel.DONE);
        taskManager.updateEpic(epic1);
        System.out.println(taskManager.getAllEpic());

        System.out.println("\n---- TEST-10--- если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.");
        Subtask subtaskForUpdate2 = new Subtask("Нанять рабочих2", "Бригада У2", 3);
        subtaskForUpdate2.setId(4);
        subtaskForUpdate2.setStatus(StatusModel.DONE);
        taskManager.updateSubtask(subtaskForUpdate2);
        subtaskForUpdate2.setId(5);
        taskManager.updateSubtask(subtaskForUpdate2);
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        System.out.println("\n---TEST-11----Получение списка всех подзадач определённого эпика.");
        System.out.println("Epic id=3 " + taskManager.getAllSubtaskInEpic(3));
        System.out.println("Epic id=6 " + taskManager.getAllSubtaskInEpic(6));


        System.out.println("\n---TEST-12---Удаление таски по идентификатору 2.");
        System.out.println("До " + taskManager.getAllTask());
        taskManager.deleteTaskById(2);
        System.out.println("После " + taskManager.getAllTask());


        System.out.println("\n---TEST-13 ---Удаление всех подзадач. Апдейт эпиков в NEW не в статусе DONE т.к. тасок нет");
        subtask3.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);
        System.out.println("До " + taskManager.getAllEpic());
        System.out.println("До " + taskManager.getAllSubtask());
        taskManager.deleteAllSubtask();
        System.out.println("После " + taskManager.getAllEpic());
        System.out.println("После " + taskManager.getAllSubtask());

        System.out.println("\n---TEST-14---Удаление сабтаски по идентификатору ");
        Epic epic3 = new Epic(null, "Описание эпика");
        taskManager.createEpic(epic3);
        Subtask subtask4 = new Subtask(null, null, 8);
        taskManager.createSubtask(subtask4);
        subtask4.setStatus(StatusModel.IN_PROGRESS);
        taskManager.updateSubtask(subtask4);

        System.out.println("До " + taskManager.getAllEpic());
        System.out.println("До " + taskManager.getAllSubtask());
        taskManager.deleteSubtaskById(9);
        System.out.println("После " + taskManager.getAllEpic());
        System.out.println("После " + taskManager.getAllSubtask());

        System.out.println("\n---TEST-15---Удаление эпика по идентификатору ");
        Subtask subtask5 = new Subtask(null, null, 8);
        Subtask subtask6 = new Subtask("subtask6", null, 6);
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);
        System.out.println("До " + taskManager.getAllEpic());
        System.out.println("До " + taskManager.getAllSubtask());
        taskManager.deleteEpicById(8);
        System.out.println("После " + taskManager.getAllEpic());
        System.out.println("После " + taskManager.getAllSubtask());
    }
}
