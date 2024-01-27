package tracker.model;

public class Subtask extends Task {
    private final Epic parentEpic;

    public Epic getParentEpic() {
        return parentEpic;
    }

    public Subtask(String name, String description, int id, Status status, Epic parentEpic) {
        super(name, description, id, status);
        this.parentEpic = parentEpic;

    }
    @Override
    public String toString() {
        return "model.Subtask{id=" + getId() + ", name='" + getName() + "', status=" + getStatus() + ", parentEpic=" + parentEpic.getName() + "}";
    }
}