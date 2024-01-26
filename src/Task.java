import java.util.Objects;
import java.util.ArrayList;

public class Task {
    String name;
    String description;
    int id;
    Status status;
    ArrayList<Task> subtasks = new ArrayList<>();
    Epic parentEpic;


    public Task(String name, String description, int id, Status status, Epic parentEpic) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.parentEpic = parentEpic;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", name='" + name + "', status=" + status + "}";
    }

}
