public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask("Задача 1", "Описание 1", TaskManager.generateId(), Status.NEW);
        Task task2 = taskManager.createTask("Задача 2", "Описание 2", TaskManager.generateId(), Status.IN_PROGRESS);

        Epic epic1 = taskManager.createEpic("Эпик 1", "Описание эпика 1", Status.NEW);
        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", TaskManager.generateId(),  epic1, Status.NEW);
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", TaskManager.generateId(), epic1, Status.DONE);

        Epic epic2 = taskManager.createEpic("Эпик 2", "Описание эпика 2", Status.IN_PROGRESS);
        Subtask subtask3 = taskManager.createSubtask("Подзадача 3", "Описание подзадачи 3", TaskManager.generateId(), epic2, Status.NEW);

        System.out.println("До изменения статуса:");
        System.out.println("Эпики: " + taskManager.epics.values());
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Подзадачи для Эпика 1: " + taskManager.getSubtasksForEpic(epic1.getId()));
        System.out.println("Подзадачи для Эпика 2: " + taskManager.getSubtasksForEpic(epic2.getId()));

        task1.setStatus(Status.DONE);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);

        taskManager.calculateEpicStatus(epic1);
        taskManager.calculateEpicStatus(epic2);

        System.out.println("\nПосле изменения статуса:");
        System.out.println("Эпики: " + taskManager.epics.values());
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Подзадачи для Эпика 1: " + taskManager.getSubtasksForEpic(epic1.getId()));
        System.out.println("Подзадачи для Эпика 2: " + taskManager.getSubtasksForEpic(epic2.getId()));

        taskManager.removeTaskById(task2.getId());
        taskManager.removeTaskById(epic1.getId());

        System.out.println("\nПосле удаления:");
        System.out.println("Эпики: " + taskManager.epics.values());
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Подзадачи для Эпика 2: " + taskManager.getSubtasksForEpic(epic2.getId()));
    }
}
