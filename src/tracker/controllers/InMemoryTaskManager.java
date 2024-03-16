package tracker.controllers;

import org.w3c.dom.Node;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.controllers.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    private static int counter = 0;


    @Override
     public int generateId(){
        return counter += 1;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());

    }
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            epic.getSubtasks().clear();
            calculateEpicStatus(epic);
        }
        allSubtasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        allSubtasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }
    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = (Subtask) tasks.get(subtaskId);
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = (Epic) tasks.get(epicId);
        return epic;
    }

    @Override
    public int addNewTask(Task task) {
        final int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
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

    @Override
    public int addNewEpic(Epic epic) {
        final int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

     @Override
     public void updateTask(Task updatedTask) {

        tasks.put(updatedTask.getId(), updatedTask);
    }
     @Override
     public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);

    }
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask existingSubtask = (Subtask) tasks.get(updatedSubtask.getId());
        if (existingSubtask != null) {
            tasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic parentEpic = existingSubtask.getParentEpic();
            if (parentEpic != null) {
                calculateEpicStatus(parentEpic);
            }
        }
    }
    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = (Epic) tasks.get(updatedEpic.getId());
        if (existingEpic != null){
            tasks.put(updatedEpic.getId(), updatedEpic);
        }
    }
    @Override
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
    @Override
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
    @Override
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
    @Override
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

    @Override
    public boolean allStatusesAre(ArrayList<Status> statuses, Status targetStatus) {
        for (Status status : statuses) {
            if (status != targetStatus) {
                return false;
            }
        }
        return true;
    }


    @Override
    public List<Task> getHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager.getHistory();
    }

}
