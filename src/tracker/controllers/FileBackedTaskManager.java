package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.controllers.TaskManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
        if (saveFile.exists()) {
            loadFromFile();
        }
    }


    public void saveToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
            for (Task task : getAllTasks().values()) {
                writer.println(taskToString(task));
            }

            for (Subtask subtask : getAllSubtasks().values()) {
                writer.println(taskToString(subtask));
            }

            for (Epic epic : getAllEpics().values()) {
                writer.println(taskToString(epic));
            }
        }
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = taskFromString(line);
                if (task != null) {
                    getAllTasks().put(task.getId(), task);
                    if (task instanceof Epic) {
                        getAllEpics().put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        getAllSubtasks().put(task.getId(), (Subtask) task);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,%s",
                    epic.getId(), epic.getName(), epic.getStatus(), epic.getDescription(), "");
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(), subtask.getName(), subtask.getStatus(), subtask.getDescription(),
                    subtask.getParentEpic().getId());
        } else {
            return String.format("%d,TASK,%s,%s,%s,%s",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription(), "");
        }
    }

    private Task taskFromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Task task;
        switch (type) {
            case "EPIC":
                task = new Epic(name, description, new ArrayList<>());
                break;
            case "SUBTASK":
                int parentId = Integer.parseInt(parts[5]);
                Epic parentEpic = (Epic) getTaskById(parentId);
                task = new Subtask(name, description, status, parentEpic);
                break;
            default:
                task = new Task(name, description, status);
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    

    private static int counter = 0;


    @Override
    public int generateId(){
        return counter += 1;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Task> getSubtasksForEpic(int epicId) {
        return super.getSubtasksForEpic(epicId);
    }

    @Override
    public void calculateEpicStatus(Epic epic) {
        super.calculateEpicStatus(epic);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
