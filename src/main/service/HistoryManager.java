package main.service;

import main.models.Task;

import java.util.List;

public interface HistoryManager {

    //пометить задачу как просмотренную
    void add(Task task);

    //Вернуть историю просмотренных задач
    List<Task> getHistory();

}
