package main.service;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import main.util.Exception.ManagerSaveException;
import main.util.StatusModel;
import main.util.TypeTask;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static main.util.TypeTask.*;

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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
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
        String header = "id,type,name,status,description,epic\n";

        try (Writer fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            bufferedWriter.write(header);

            for (Task task : getAllTask()) {
                bufferedWriter.write(toString(task));
            }
            for (Task task : getAllEpic()) {
                bufferedWriter.write(toString(task));
            }
            for (Task task : getAllSubtask()) {
                bufferedWriter.write(toString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл");
        }
    }

    //Преобразовать объект задачи в строку
    private String toString(Task task) {
        TypeTask type = null;
        String epicId = "";
        if (task.getClass().equals(Task.class)) {
            type = TASK;
        } else if (task.getClass().equals(Epic.class)) {
            type = EPIC;
        } else if (task.getClass().equals(Subtask.class)) {
            type = SUBTASK;
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return task.getId() + "," +
                type + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                epicId + "\n";
    }

    //Преобразовать строку в объект задачи
    private Task fromString(String value) {
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
        } else if (type == SUBTASK) {
            Subtask subtask = new Subtask(name, description, epicId);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        }

        return null;
    }

    //Выгрузить данные из файла в manager
    private void loadFromFile(File file) {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            br.readLine();

            while (br.ready()) {
                String line = br.readLine();
                Task task = fromString(line);
                assert task != null : "Задача пустая";

                if (newId < task.getId()) {
                    newId = task.getId();
                }

                if (task.getClass().equals(Task.class)) {
                    taskStorage.put(task.getId(), task);
                } else if (task.getClass().equals(Epic.class)) {
                    Epic epic = (Epic) task;
                    epicStorage.put(epic.getId(), epic);
                } else if (task.getClass().equals(Subtask.class)) {
                    Subtask subtask = (Subtask) task;
                    subtaskStorage.put(subtask.getId(), subtask);
                    Epic epic = epicStorage.get(subtask.getEpicId());
                    epic.setSubtasks(subtask.getId());
                    epicStorage.put(subtask.getEpicId(), epic);
                }
            }

            if (newId != 1) newId++;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла");
        }
    }

}
