package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.controllers.TaskManager;
import tracker.exceptions.ManagerSaveException;
import tracker.exceptions.ManagerLoadException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
        if (saveFile.exists()) {
            load();
        }
    }

    


    public void save() throws ManagerSaveException {
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
            catch(IOException e){
                throw new ManagerSaveException("Ошибка при сохранении в файл: " + saveFile.getName(), e);
            }
        }

        private void load () throws ManagerLoadException {
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
                throw new ManagerLoadException("Error loading from file: " + saveFile.getName(), e);
            }
        }

        public static FileBackedTaskManager loadFromFile (File file) throws ManagerLoadException {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
            taskManager.load();
            return taskManager;
        }

        private String taskToString (Task task){
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

        private Task taskFromString (String line){
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
        public int generateId () {
            return counter += 1;
        }

        @Override
        public int addNewTask (Task task){
            int id = super.addNewTask(task);
            save();
            return id;
        }

        @Override
        public int addNewSubtask (Subtask subtask){
            save();
            return subtask.getId();
        }

        @Override
        public int addNewEpic (Epic epic){
            int id = super.addNewEpic(epic);
            save();
            return id;
        }

        @Override
        public void updateTask (Task updatedTask){
            super.updateTask(updatedTask);
            save();
        }

        @Override
        public void removeTaskById ( int taskId){
            super.removeTaskById(taskId);
            save();
        }

        @Override
        public void updateSubtask (Subtask updatedSubtask){
            super.updateSubtask(updatedSubtask);
            save();
        }

        @Override
        public void updateEpic (Epic updatedEpic){
            super.updateEpic(updatedEpic);
            save();
        }

        @Override
        public void removeEpicById ( int epicId){
            super.removeEpicById(epicId);
            save();
        }

        @Override
        public void removeSubtaskById ( int subtaskId){
            super.removeSubtaskById(subtaskId);
            save();
        }

        @Override
        public ArrayList<Task> getSubtasksForEpic ( int epicId){
            return super.getSubtasksForEpic(epicId);
        }

        @Override
        public void calculateEpicStatus (Epic epic){
            super.calculateEpicStatus(epic);
            save();
        }

        @Override
        public List<Task> getHistory () {
            return super.getHistory();
        }
    }

