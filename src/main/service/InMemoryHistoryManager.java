package main.service;

import main.models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() > 9) {
            history.removeFirst();
        }
        if (task != null) {
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyCopy = new ArrayList<>();
        if (!history.isEmpty()) {
            historyCopy.addAll(history);
        }
        return historyCopy;
    }
}
