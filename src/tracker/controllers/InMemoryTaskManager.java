package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getStartTime();
        if (startTime1 == null && startTime2 == null) return 0;
        if (startTime1 == null) return 1;
        if (startTime2 == null) return -1;
        return startTime1.compareTo(startTime2);
    });

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private static int counter = 0;

    @Override
    public int generateId() {
        return ++counter;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
        prioritizedTasks.removeIf(task -> !(task instanceof Subtask));
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            epic.getSubtasks().clear();
            calculateEpicStatus(epic);
        }
        allSubtasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        allSubtasks.clear();
        prioritizedTasks.clear();
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
        return allSubtasks.get(subtaskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    @Override
    public int addNewTask(Task task) {
        final int id = generateId();
        task.setId(id);
        if (isTimeSlotAvailable(task)) {
            tasks.put(id, task);
            prioritizedTasks.add(task);
            return id;
        }
        return -1;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        final int id = generateId();
        subtask.setId(id);

        Epic parentEpic = subtask.getParentEpic();
        if (parentEpic != null) {
            parentEpic.getSubtasks().add(subtask);
        }
        if (isTimeSlotAvailable(subtask)) {
            allSubtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            return id;
        }
        return -1;
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
        Task existingTask = tasks.get(updatedTask.getId());
        if (existingTask != null) {
            prioritizedTasks.remove(existingTask);
            if (isTimeSlotAvailable(updatedTask)) {
                tasks.put(updatedTask.getId(), updatedTask);
                prioritizedTasks.add(updatedTask);
            }
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask existingSubtask = allSubtasks.get(updatedSubtask.getId());
        if (existingSubtask != null) {
            prioritizedTasks.remove(existingSubtask);
            if (isTimeSlotAvailable(updatedSubtask)) {
                allSubtasks.put(updatedSubtask.getId(), updatedSubtask);
                prioritizedTasks.add(updatedSubtask);
                Epic parentEpic = existingSubtask.getParentEpic();
                if (parentEpic != null) {
                    calculateEpicStatus(parentEpic);
                }
            }
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic != null) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epicToRemove = epics.remove(epicId);
        if (epicToRemove != null) {
            for (Subtask subtask : epicToRemove.getSubtasks()) {
                removeSubtaskById(subtask.getId());
            }
            historyManager.remove(epicId);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtaskToRemove = allSubtasks.remove(subtaskId);
        if (subtaskToRemove != null) {
            Epic parentEpic = subtaskToRemove.getParentEpic();
            if (parentEpic != null) {
                parentEpic.getSubtasks().remove(subtaskToRemove);
                calculateEpicStatus(parentEpic);
            }
            prioritizedTasks.remove(subtaskToRemove);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public ArrayList<Task> getSubtasksForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            return epic.getSubtasks().stream()
                    .map(subtask -> (Task) subtask)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
    }

    @Override
    public void calculateEpicStatus(Epic epic) {
        List<Status> subtaskStatuses = epic.getSubtasks().stream()
                .map(Task::getStatus)
                .collect(Collectors.toList());

        if (subtaskStatuses.isEmpty() || subtaskStatuses.stream().allMatch(status -> status == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (subtaskStatuses.stream().allMatch(status -> status == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public boolean allStatusesAre(ArrayList<Status> statuses, Status targetStatus) {
        return statuses.stream().allMatch(status -> status == targetStatus);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return allSubtasks;
    }

    private boolean isTimeSlotAvailable(Task newTask) {
        for (Task existingTask : prioritizedTasks) {
            if (existingTask.getStartTime() != null && newTask.getStartTime() != null
                    && existingTask.getEndTime() != null && newTask.getEndTime() != null) {
                if (existingTask.getStartTime().isBefore(newTask.getEndTime()) &&
                        newTask.getStartTime().isBefore(existingTask.getEndTime())) {
                    return false;
                }
            }
        }
        return true;
    }
}
