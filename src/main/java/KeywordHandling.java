import java.util.ArrayList;

public class KeywordHandling {
    // Exception checks for processMarkKeyword / processRemoveKeyword methods:
    // 1. Check if the second input value is a numerical string
    // 2. Check if the input numerical string is out of bound from the task list or index is 0 (invalid index)
    private boolean isValidateIndex(int totalNumberOfTaskObj, String index) {
        if (totalNumberOfTaskObj == 0) {
            Message.printEmptyMessage(true);
            return false;
        } else if (!ExceptionHandling.isInteger(index)) {
            Message.printInvalidIntegerMessage();
            return false;
        } else {
            int convertedIndex = Integer.parseInt(index) - 1;
            if (convertedIndex >= totalNumberOfTaskObj || convertedIndex < 0) {
                Message.printOutOfBoundsMessage();
                return false;
            }
        }
        return true;
    }
    public void processListKeyword(ArrayList<Task> todoTaskList, int totalNumberOfTaskObj){
        if (totalNumberOfTaskObj == 0){
            Message.printEmptyMessage(true);
        } else {
            Task.printTaskList(todoTaskList);
        }
    }
    // Check if the task have been marked / unmarked before
    public void processMarkKeyword(ArrayList<Task> todoTaskList, int totalNumberOfTaskObj, String index, boolean isMark){
        if (isValidateIndex(totalNumberOfTaskObj, index)){
            // Convert user input index into base 0 index (arrayList)
            int convertedIndex = Integer.parseInt(index) - 1;
            if (ExceptionHandling.isTaskMarked(todoTaskList, convertedIndex + 1, isMark)){
                Message.printMarkedTaskMessage();
            } else {
                Task markTask = todoTaskList.get(convertedIndex);
                markTask.setTaskMarkStatus(todoTaskList, convertedIndex, isMark);
            }
        }
    }
    public void processRemoveKeyword(ArrayList<Task> todoTaskList, int totalNumberOfTaskObj, String index){
        if (isValidateIndex(totalNumberOfTaskObj, index)){
            // Convert user input index into base 0 index (arrayList)
            int convertedIndex = Integer.parseInt(index) - 1;
            Task deleteTask = todoTaskList.remove(convertedIndex);
            int totalNumberOfTodoTask = Task.decreaseTotalNumberOfTodoTask();
            Message.printDeleteTaskMessage(totalNumberOfTodoTask, deleteTask.printAddedTask());
        }
    }
    // commandKeyword = todos / deadlines / events
    public void processTodoTask(String todoTask, ArrayList<Task> todoTaskList) {
        String newTodoTask = todoTask.replace("todo", "").trim().replaceAll("\\s+", " ");
        ToDo inputTodoTask =  new ToDo(newTodoTask);
        todoTaskList.add(inputTodoTask);
        Message.printAddedTaskMessage(Task.getTotalNumberOfTodoTask(), inputTodoTask.printAddedTask());
    }
    public void processDeadlineTask(String todoTask, ArrayList<Task> todoTaskList) {
        String deadlineTask = todoTask.replace("deadline", "").trim().replaceAll("\\s+", " ");
        String[] deadlineDetails = deadlineTask.split("/by");
        // Checks if deadline is provided
        if (!ExceptionHandling.isValidDeadlineInput(deadlineDetails)) {
            Message.printMissingParameterMessage("deadline");
            return;
        }
        String newDeadlineTask = deadlineDetails[0].trim();
        String deadlineOfTask = deadlineDetails[1].trim();
        Deadline inputDeadlineTask = new Deadline(newDeadlineTask, deadlineOfTask);
        todoTaskList.add(inputDeadlineTask);
        Message.printAddedTaskMessage(Task.getTotalNumberOfTodoTask(), inputDeadlineTask.printAddedTask());
    }
    public void processEventTask(String todoTask, ArrayList<Task> todoTaskList) {
        String eventTask = todoTask.replace("event", "").trim().replaceAll("\\s+", " ");
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
        String newEventTask = eventDetails[0].trim();
        String[] eventDurationDetails = eventDetails[1].split("/to");
        String from = eventDurationDetails[0].trim();
        String to = eventDurationDetails[1].trim();
        Event inputEventTask = new Event(newEventTask, from, to);
        todoTaskList.add(inputEventTask);
        Message.printAddedTaskMessage(Task.getTotalNumberOfTodoTask(), inputEventTask.printAddedTask());
    }
}
