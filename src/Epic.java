public class Epic extends Task {
    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status, null);
    }
    @Override
    public String toString() {
        return "Epic{id=" + id + ", name='" + name + "', status=" + status + ", subtasks=" + subtasks + "}";
    }

}
