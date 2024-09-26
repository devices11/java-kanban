package main.service;

import main.models.Node;
import main.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap;
    private Node last;

    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            Node node = historyMap.get(task.getId());
            if (node != null) {
                if (node.equals(last)) {
                    return;
                }
                removeNode(node);
            }
            linkLast(task);
            historyMap.put(task.getId(), last);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(oldLast, task, null);
        last = newNode;
        if (oldLast != null) {
            oldLast.next = newNode;
            historyMap.put(oldLast.task.getId(), oldLast);
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node lastNode = this.last;
        while (!historyMap.isEmpty()) {
            if (lastNode != null) {
                tasks.add(lastNode.task);
                lastNode = lastNode.prev;
            } else {
                break;
            }
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node prev = node.prev;
            Node next = node.next;

            if (next == null && prev == null) {
                historyMap.remove(node.task.getId());
                return;
            } else if (prev == null) {
                next.prev = null;
            } else if (next == null) {
                prev.next = null;
                last = prev;
            } else {
                prev.next = next;
                next.prev = prev;
            }

            historyMap.remove(node.task.getId());
        }
    }

    public Map<Integer, Node> getHistoryMap() {
        return historyMap;
    }
}
