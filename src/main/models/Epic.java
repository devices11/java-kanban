package main.models;

import main.util.TypeTask;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    public void setSubtasks(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void deleteSubtaskId(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void deleteAllSubtask() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}
