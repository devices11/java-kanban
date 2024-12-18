package main.models;

import main.util.StatusModel;
import main.util.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static main.util.StatusModel.NEW;

public class Task {
    private String title;
    private String description;
    private int id;
    private StatusModel status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String title, String description) {
        this(title, description, null, 0);
    }

    public Task(String title, String description, LocalDateTime startTime, int durationOfMinutes) {
        this.status = NEW;
        this.title = title;
        this.description = description;
        this.duration = Duration.ofMinutes(durationOfMinutes);
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusModel getStatus() {
        return status;
    }

    public void setStatus(StatusModel status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = Objects.requireNonNullElse(duration, Duration.ZERO);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
