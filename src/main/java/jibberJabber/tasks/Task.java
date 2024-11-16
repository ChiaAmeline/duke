package jibberJabber.tasks;

import jibberJabber.commands.ExceptionHandling;
import jibberJabber.commands.KeywordHandling;
import jibberJabber.ui.Message;
/**
 * The task class represents a general task, with methods to handle task status, track task counts, and interact with task lists.
 */
public class Task {
    protected String taskName;
    public boolean isDone;
    private static int totalNumberOfTodoTask = 0;
    /**
     * Constructs a Task object with a specified task name.
     * The task is initially marked as not done.
     *
     * @param taskName The name of the task.
     */
    public Task(String taskName) {
        this.taskName = taskName;
        this.isDone = false;

        totalNumberOfTodoTask++;
    }
    /**
     * If the count is greater than zero, the method decreases the total number of To-Do tasks
     *
     * @return The updated total number of To-Do tasks object within the array list
     */
    public static int decreaseTotalNumberOfTodoTask(){
        if (totalNumberOfTodoTask > 0){
            totalNumberOfTodoTask --;
        }
        return totalNumberOfTodoTask;
    }
    /**
     * Retrieve the name of the task of the object
     *
     * @return The task name as a String.
     */
    public String getTaskName() {
        return taskName;
    }
    /**
     * Gets the status icon of the task - Done / Undone
     *
     * @return "X" if the task is done, otherwise an empty space (string)
     */
    public String getStatusIcon(){
        return (isDone ? "X" : " ");
    }
    /**
     * Sets the mark status as done or undone.
     *
     * @param todoTaskList The list containing all tasks objects.
     * @param index The index of the task to be marked.
     * @param isDone The mark status of the task: true if done, false if undone.
     * @param isFromFile Indicates whether the task was read from a file: true if read from file (not to display default message), false if its from user input (display default message)
     */
    public void setTaskMarkStatus(TaskList todoTaskList, int index, boolean isDone, boolean isFromFile) {
        Task markTask = todoTaskList.getTaskById(index);
        markTask.isDone = isDone;
        if (!isFromFile) {
            Message.printTaskStatusMessage(isDone, markTask);
        }
    }
    /**
     * Returns a formatted string representing the task name and its respective status icon
     *
     * @return The formatted task string.
     */
    public String printAddedTask(){
        return "[" + getStatusIcon() + "] " + taskName;
    }
    /**
     * Adds a new task to the task list based on the keyword, such as "todo", "deadline", or "event".
     *
     * @param todoTaskList The list that the new task will be added into.
     * @param todoTask The details of the task to be added.
     * @param keyword The task type (e.g., "todo", "deadline", "event").
     * @param keywordHandling The keyword class to handle all keyword operations.
     * @param isFromFile Indicates whether the task was read from a file: true if read from file (not to display default message), false if its from user input (display default message)
     */
    public static void addTask(TaskList todoTaskList, String todoTask, String keyword, KeywordHandling keywordHandling , boolean isFromFile, TaskFiles taskFiles){
        keyword = ExceptionHandling.removeSpaces(keyword).toLowerCase();
        switch (keyword){
            case "todo":
                keywordHandling.processTodoTask(todoTask, todoTaskList, isFromFile);
                break;
            case "deadline":
                String deadlineTask = ExceptionHandling.removeSpaces(todoTask.replaceAll("(?i)deadline", ""));
                String[] deadlineDetails = deadlineTask.split("/by");
                // Checks if deadline is provided
                if (!ExceptionHandling.isValidDeadlineInput(deadlineDetails)) {
                    Message.printMissingParameterMessage("deadline");
                    return;
                }
                String deadlineOfTask = ExceptionHandling.removeSpaces(deadlineDetails[1]);
                if (ExceptionHandling.isInvalidDate(deadlineOfTask)){
                    Message.printInvalidDateFormatMessage();
                    return;
                }
                keywordHandling.processDeadlineTask(todoTask, todoTaskList, isFromFile);
                break;
            case "event":
                String eventTask = ExceptionHandling.removeSpaces(todoTask.replaceAll("(?i)event", ""));
                if (!eventTask.contains("/from") || !eventTask.contains("/to")) {
                    // Checks if the input value is in proper format
                    Message.printMissingParameterMessage("event");
                    return;
                }
                String[] eventDetails = eventTask.split("/from");
                if (!ExceptionHandling.isValidEventInput(eventDetails)) {
                    // Checks if event duration is provided
                    Message.printMissingParameterMessage("event");
                    return;
                }
                String[] eventDurationDetails = eventDetails[1].split("/to");
                String from = ExceptionHandling.removeSpaces(eventDurationDetails[0]);
                String to = ExceptionHandling.removeSpaces(eventDurationDetails[1]);
                if (ExceptionHandling.isInvalidDate(from) || ExceptionHandling.isInvalidDate(to)) {
                    Message.printInvalidDateFormatMessage();
                    return;
                }
                keywordHandling.processEventTask(todoTask, todoTaskList, isFromFile);
                break;
        }
        if (!todoTaskList.getTasks().isEmpty()) {
            taskFiles.writeToTextFile(todoTaskList, todoTaskList.getTasks().get(todoTaskList.getTasks().size() - 1), true);
        } else {
            Message.printFailedToAppendToFileMessage();
        }
    }
}
