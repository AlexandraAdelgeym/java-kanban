package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, List<Subtask> subtasks) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
        this.setDuration(Duration.ZERO);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void calculateDurationAndTimes() {
        Duration totalDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        this.setDuration(totalDuration);

        LocalDateTime earliestStartTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        this.setStartTime(earliestStartTime);

        LocalDateTime latestEndTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        this.endTime = latestEndTime;
    }

    @Override
    public String toString() {
        return String.format("Epic{id=%d, name='%s', description='%s', status=%s, subtasks=%s, duration=%s, startTime=%s, endTime=%s}",
                getId(), getName(), getDescription(), getStatus(), subtasks, getDuration(), getStartTime(), endTime);
    }

    public void cleanSubtaskIds() {
        subtasks.forEach(subtask -> subtask.setId(0));
        subtasks.clear();
    }
}
