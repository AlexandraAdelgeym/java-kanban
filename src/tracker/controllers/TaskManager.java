package tracker.controllers;

import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Status;

import java.util.HashMap;
import java.util.ArrayList;
public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    private static int counter = 0;

     public int generateId(){
        return counter += 1;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());

    }
    private void deleteTasks() {
        tasks.clear();
    }

    private void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            calculateEpicStatus(epic);
        }
        allSubtasks.clear();
    }

    private void deleteEpics() {
        epics.clear();
        allSubtasks.clear();
    }

    private Task getTaskById(int taskId) {

         return tasks.get(taskId);
    }

    public int addNewTask(Task task) {
        final int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewSubtask(Subtask subtask) {
        final int id = generateId();
        subtask.setId(id);

        Epic parentEpic = subtask.getParentEpic();
        if (parentEpic != null) {
            parentEpic.getSubtasks().add(subtask);
        }
        allSubtasks.put(id, subtask);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

     private void updateTask(Task updatedTask) {
         tasks.put(updatedTask.getId(), updatedTask);
    }
     public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }
    private void updateSubtask(Subtask updatedSubtask) {
        Subtask existingSubtask = (Subtask) tasks.get(updatedSubtask.getId());
        if (existingSubtask != null) {
            tasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic parentEpic = existingSubtask.getParentEpic();
            if (parentEpic != null) {
                calculateEpicStatus(parentEpic);
            }
        }
    }
    private void updateEpic(Epic updatedEpic) {
        Epic existingEpic = (Epic) tasks.get(updatedEpic.getId());
        if (existingEpic != null){
            tasks.put(updatedEpic.getId(), updatedEpic);
        }
    }
    public void removeEpicById(int epicId) {
        Epic epicToRemove = (Epic) getTaskById(epicId);
        if (epicToRemove != null) {
            ArrayList<Task> subtasksToRemove = new ArrayList<>(((Epic) epicToRemove).getSubtasks());
            for (Task subtask : subtasksToRemove) {
                removeTaskById(subtask.getId());
            }
            removeTaskById(epicId);
        }
    }
    public void removeSubtaskById(int subtaskId) {
        Task subtaskToRemove = getTaskById(subtaskId);
        if (subtaskToRemove != null) {
            Subtask subtask = (Subtask) subtaskToRemove;
            Epic parentEpic = subtask.getParentEpic();
            if (parentEpic != null) {
                parentEpic.getSubtasks().remove(subtask);
                calculateEpicStatus(parentEpic);
            }
            removeTaskById(subtaskId);
        }
    }
    public ArrayList<Task> getSubtasksForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            ArrayList<Task> subtasksForEpic = new ArrayList<>();

            for (Task task : tasks.values()) {
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    if (subtask.getParentEpic() == epic) {
                        subtasksForEpic.add(task);
                    }
                }
            }

            return subtasksForEpic;
        } else {
            return new ArrayList<>();
        }
    }
    public void calculateEpicStatus(Epic epic) {
        ArrayList<Status> subtaskStatuses = new ArrayList<>();

        for (Task subtask : epic.getSubtasks()) {
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
