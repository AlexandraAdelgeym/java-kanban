public class Subtask extends Task {

    public Subtask(String name, String description, int id, Status status, Epic parentEpic) {
        super(name, description, id, status, parentEpic);

    }
    @Override
    public String toString() {
        return "Subtask{id=" + id + ", name='" + name + "', status=" + status + ", parentEpic=" + parentEpic.getName() + "}";
    }
}