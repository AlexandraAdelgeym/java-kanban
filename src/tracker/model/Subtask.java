package tracker.model;

public class Subtask extends Task {
    private final Epic parentEpic;

    public Epic getParentEpic() {
        return parentEpic;
    }

    public Subtask(String name, String description, Status status, Epic parentEpic) {
        super(name, description, status);
        this.parentEpic = parentEpic;

    }
    @Override
    public String toString() {
        return "model.Subtask{id=" + getId() + ", name='" + getName() + "', status=" + getStatus() + ", parentEpic=" + parentEpic.getName() + "}";
    }
}