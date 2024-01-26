import java.util.HashMap;
import java.util.ArrayList;
public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    static int counter = 0;

     static int generateId(){
        return counter += 1;
    }

    ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());

    }
    void removeAllTasks() {
        tasks.clear();
    }
    Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    Task createTask(String name, String description, int id, Status status) {
        Task task = new Task(name, description, id, status, null);
        tasks.put(task.id, task);
        return task;
    }
    Subtask createSubtask(String name, String description, int id, Epic parentEpic, Status status) {
        if (parentEpic != null) {
            Subtask subtask = new Subtask(name, description, id, status, parentEpic);
            parentEpic.subtasks.add(subtask);
            tasks.put(subtask.id, subtask);
            return subtask;
        } else {
            return null;
        }
    }

    Epic createEpic(String name, String description, Status status) {
        Epic epic = new Epic(name, description, generateId(), status);
        tasks.put(epic.id, epic);
        epics.put(epic.id, epic);
        return epic;
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.id, updatedTask);
    }
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }
    ArrayList<Task> getSubtasksForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            ArrayList<Task> subtasksForEpic = new ArrayList<>();

            for (Task task : tasks.values()) {
                if (task.parentEpic == epic) {
                    subtasksForEpic.add(task);
                }
            }

            return subtasksForEpic;
        } else {
            return new ArrayList<>();
        }
    }
    void calculateEpicStatus(Epic epic) {
        ArrayList<Status> subtaskStatuses = new ArrayList<>();

        for (Task subtask : epic.subtasks) {
            subtaskStatuses.add(subtask.getStatus());
        }

        if (subtaskStatuses.isEmpty() || allStatusesAre(subtaskStatuses, Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (allStatusesAre(subtaskStatuses, Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean allStatusesAre(ArrayList<Status> statuses, Status targetStatus) {
        for (Status status : statuses) {
            if (status != targetStatus) {
                return false;
            }
        }
        return true;
    }

}
