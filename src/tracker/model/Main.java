package tracker.model;

import tracker.controllers.TaskManager;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();


        Task task1 = new Task("Задача 1", "Описание задачи 1", taskManager.generateId(), Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", taskManager.generateId(), Status.IN_PROGRESS);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", taskManager.generateId(), new ArrayList<>());
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", taskManager.generateId(), Status.DONE, epic1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", taskManager.generateId(), Status.NEW, epic1);
        epic1.getSubtasks().add(subtask1);
        epic1.getSubtasks().add(subtask2);


        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", taskManager.generateId(), new ArrayList<>());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", taskManager.generateId(), Status.IN_PROGRESS, epic2);
        epic2.getSubtasks().add(subtask3);


        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);


        System.out.println("Список эпиков:");
        for (Epic epic : taskManager.epics.values()) {
            System.out.println(epic);
        }

        System.out.println("\nСписок задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nСписок подзадач:");
        for (Task subtask : taskManager.getSubtasksForEpic(epic1.getId())) {
            System.out.println(subtask);
        }


        task1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic2.setStatus(Status.DONE);


        System.out.println("\nОбновленные статусы:");
        System.out.println("Статус задачи 1: " + task1.getStatus());
        System.out.println("Статус подзадачи 2: " + subtask2.getStatus());
        System.out.println("Статус эпика 2: " + epic2.getStatus());
    }
}
