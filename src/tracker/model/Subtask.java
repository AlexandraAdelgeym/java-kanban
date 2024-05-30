package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Epic parentEpic;

    public Epic getParentEpic() {
        return parentEpic;
    }

    public Subtask(String name, String description, Status status, Epic parentEpic) {
        super(name, description, status);
        this.parentEpic = parentEpic;

    }
    public Subtask(String name, String description, Status status, Epic parentEpic, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.parentEpic = parentEpic;
    }
    @Override
    public String toString() {
        return String.format("Subtask{id=%d, name='%s', description='%s', status=%s, duration=%s, startTime=%s, parentEpic='%s'}",
                getId(), getName(), getDescription(), getStatus(), getDuration(), getStartTime(), parentEpic.getName());
    }
}