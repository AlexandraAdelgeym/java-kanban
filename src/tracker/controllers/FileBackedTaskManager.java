package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.exceptions.ManagerSaveException;
import tracker.exceptions.ManagerLoadException;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File saveFile;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
        if (saveFile.exists()) {
            load();
        }
    }

    public void save() throws ManagerSaveException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
            getAllTasks().values().stream()
                    .map(this::taskToString)
                    .forEach(writer::println);

            getAllSubtasks().values().stream()
                    .map(this::taskToString)
                    .forEach(writer::println);

            getAllEpics().values().stream()
                    .map(this::taskToString)
                    .forEach(writer::println);
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving to file: " + saveFile.getName(), e);
        }
    }

    private void load() throws ManagerLoadException {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            reader.lines()
                    .map(this::taskFromString)
                    .forEach(task -> {
                        if (task != null) {
                            getAllTasks().put(task.getId(), task);
                            if (task instanceof Epic) {
                                getAllEpics().put(task.getId(), (Epic) task);
                            } else if (task instanceof Subtask) {
                                getAllSubtasks().put(task.getId(), (Subtask) task);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new ManagerLoadException("Error loading from file: " + saveFile.getName(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerLoadException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.load();
        return taskManager;
    }

    private String taskToString(Task task) {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,%s,%d,%s",
                    epic.getId(), epic.getName(), epic.getStatus(), epic.getDescription(), "",
                    epic.getDuration().toMinutes(), formatDateTime(epic.getStartTime()));
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d,%d,%s",
                    subtask.getId(), subtask.getName(), subtask.getStatus(), subtask.getDescription(),
                    subtask.getParentEpic().getId(), subtask.getDuration().toMinutes(), formatDateTime(subtask.getStartTime()));
        } else {
            return String.format("%d,TASK,%s,%s,%s,%s,%d,%s",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription(), "",
                    task.getDuration().toMinutes(), formatDateTime(task.getStartTime()));
        }
    }

    private Task taskFromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        LocalDateTime startTime = parseDateTime(parts[7]);

        Task task;
        switch (type) {
            case "EPIC":
                task = new Epic(name, description, new ArrayList<>());
                break;
            case "SUBTASK":
                int parentId = Integer.parseInt(parts[5]);
                Epic parentEpic = (Epic) getTaskById(parentId);
                task = new Subtask(name, description, status, parentEpic, duration, startTime);
                break;
            default:
                task = new Task(name, description, status, duration, startTime);
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void calculateEpicStatus(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks().stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task)
                .collect(Collectors.toList());

        if (subtasks.isEmpty() || subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (subtasks.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        Duration totalDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(totalDuration);

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(startTime);

        LocalDateTime endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setEndTime(endTime);
    }
}
