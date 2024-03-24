package tracker.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Task> subtasks;

    public ArrayList<Task> getSubtasks() {
        return subtasks;
    }

    public Epic(String name, String description, ArrayList<Task> subtasks) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
    }
    @Override
    public String toString() {
        return "model.Epic{id=" + getId() + ", name='" + getName() + "', status=" + getStatus() + ", subtasks=" + subtasks + "}";
    }
    public void cleanSubtaskIds() {
        for (Task subtask : subtasks) {
            subtask.setId(0);
        }
        subtasks.clear();
    }


}
