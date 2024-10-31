package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.util.Exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(HistoryManager historyManager, File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager, file);
        fileBackedTaskManager.loadFromFile(file);
        return fileBackedTaskManager;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    //Сохранить все задачи в файл
    private void save() {
        String header = "id,type,name,status,description,startTime,duration,epic\n";

        try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            bufferedWriter.write(header);

            for (Task task : getAllTask()) {
                bufferedWriter.write(TaskConverter.toString(task));
            }
            for (Task task : getAllEpic()) {
                bufferedWriter.write(TaskConverter.toString(task));
            }
            for (Task task : getAllSubtask()) {
                bufferedWriter.write(TaskConverter.toString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл");
        }
    }


    //Выгрузить данные из файла в manager
    private void loadFromFile(File file) {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            br.readLine();

            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) continue;
                Task task = TaskConverter.fromString(line);

                if (newId < task.getId()) {
                    newId = task.getId();
                }

                if (task.getClass().equals(Task.class)) {
                    taskStorage.put(task.getId(), task);
                    prioritizedTasks.add(task);
                } else if (task.getClass().equals(Epic.class)) {
                    Epic epic = (Epic) task;
                    epicStorage.put(epic.getId(), epic);
                } else if (task.getClass().equals(Subtask.class)) {
                    Subtask subtask = (Subtask) task;
                    subtaskStorage.put(subtask.getId(), subtask);
                    Epic epic = epicStorage.get(subtask.getEpicId());
                    epic.setSubtasks(subtask.getId());
                    super.checkUpdateEpic(epic);
                    prioritizedTasks.add(subtask);
                }
            }

            if (newId != 1) newId++;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }
    }

}
