package main.models;

import main.util.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        this(title, description, null, 0);
    }

    public Epic(String title, String description, LocalDateTime startTime, int durationOfMinutes) {
        super(title, description, startTime, durationOfMinutes);
        this.subtasks = new ArrayList<>();
        this.endTime = null;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtasks=" + subtasks +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                '}';
    }
}
