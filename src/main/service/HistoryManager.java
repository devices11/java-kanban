package main.service;

import main.models.Task;

import java.util.List;

public interface HistoryManager {

    //Добавить задачу в историю
    void add(Task task);

    //Вернуть историю просмотренных задач
    List<Task> getAll();

    //Удалить задачу из истории
    void remove(int id);
}
