package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    int generateId();

    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Subtask> getAllSubtasks();
    HashMap<Integer, Epic> getAllEpics();

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    tracker.model.Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    int addNewTask(tracker.model.Task task);

    int addNewSubtask(Subtask subtask);

    int addNewEpic(Epic epic);

    void updateTask(tracker.model.Task updatedTask);

    void removeTaskById(int taskId);

    void updateSubtask(Subtask updatedSubtask);

    void updateEpic(Epic updatedEpic);

    void removeEpicById(int epicId);

    void removeSubtaskById(int subtaskId);

    ArrayList<tracker.model.Task> getSubtasksForEpic(int epicId);

    void calculateEpicStatus(Epic epic);

    boolean allStatusesAre(ArrayList<Status> statuses, Status targetStatus);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Task> getTasks();
}
