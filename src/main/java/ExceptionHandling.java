import java.util.ArrayList;

public class ExceptionHandling {
    //Solution below adapted from https://www.quora.com/What-is-the-function-of-a-isInteger-in-Java
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isEmptyInput(String input) {
        input = input.trim().replaceAll("\\s+", " ");
        boolean taskIsEmpty = false;
        // Checks if the input contains only the following keywords or is left empty
        if (input.equalsIgnoreCase("todo") || input.equalsIgnoreCase("deadline") || input.equalsIgnoreCase("event") || input.isEmpty()) {
            taskIsEmpty = true;
        } else {
            String[] splitWord = input.split(" ");
            String keyword = splitWord[0];
            switch (keyword) {
                case "todo" -> {
                    if (splitWord[1].trim().isEmpty()) {
                        taskIsEmpty = true;
                    }
                }
                case "deadline" -> {
                    input = input.replace("deadline", "").trim();
                    if (input.contains("/by")) {
                        String[] deadlineDetails = input.split("/by");
                        // Check if the task is empty
                        if (deadlineDetails.length == 0 || deadlineDetails[0].trim().isEmpty()) {
                            taskIsEmpty = true;
                        }
                    }
                }
                case "event" -> {
                    input = input.replace("event", "").trim();
                    if (!input.contains("/from") || !input.contains("/to")) {
                        return false;
                    }
                    String[] eventDetails = input.split("/from");
                    if (eventDetails.length == 0 || eventDetails[0].trim().isEmpty()) {
                        taskIsEmpty = true;
                    }
                }
            }
        }
        return taskIsEmpty;
    }
    // (Wk 5) Level-4 Updated duplication method to detect objects instead of text string
    public static boolean isTaskDuplicated(ArrayList<Task> todoTaskList, String newTask) {
        newTask = newTask.trim().replaceAll("\\s+", " ");
        for (Task task : todoTaskList) {
            if(task instanceof ToDo){
                newTask = newTask.replace("todo", "").trim();
                if (task.getTaskName().equalsIgnoreCase(newTask)){
                    return true;
                }
            } else if (task instanceof Deadline){
                newTask = newTask.replace("deadline", "");
                if (newTask.contains("/by")){
                    String[] deadlineDetails = newTask.split("/by");
                    if (deadlineDetails.length == 2 ){
                        String newDeadlineTask = deadlineDetails[0].trim();
                        String deadlineOfTask = deadlineDetails[1].trim();
                        if (((Deadline) task).by.equalsIgnoreCase(deadlineOfTask) && task.getTaskName().equalsIgnoreCase(newDeadlineTask)) {
                            return true;
                        }
                    }
                }
            } else if (task instanceof Event){
                newTask = newTask.replace("event", "");
                if (newTask.contains("/from") && newTask.contains("/to")){
                    String[] eventDetails = newTask.split("/from");
                    if (eventDetails.length == 2){
                        String newEventTask = eventDetails[0].trim();
                        String[] taskDurationDetails = eventDetails[1].split("/to");
                        if (taskDurationDetails.length == 2){
                            String from = taskDurationDetails[0].trim();
                            String to = taskDurationDetails[1].trim();
                            if (((Event) task).from.equalsIgnoreCase(from) && ((Event) task).to.equalsIgnoreCase(to) && task.getTaskName().equalsIgnoreCase(newEventTask)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    public static boolean isTaskMarked(ArrayList<Task> todoTaskList, int index, boolean isDone){
        try{
            return todoTaskList.get(index - 1).isDone == isDone;
        } catch (Exception e){
            return false;
        }
    }
    public static boolean isCommandKeywordPresent(String keyword){
        ArrayList<String> commandKeyword = new ArrayList<>();
        commandKeyword.add("todo");
        commandKeyword.add("deadline");
        commandKeyword.add("event");
        return commandKeyword.contains(keyword);
    }
    public static boolean isValidDeadlineInput(String[] deadlineDetails) {
        return deadlineDetails.length >= 2 && !deadlineDetails[1].trim().isEmpty();
    }
    public static boolean isValidEventInput(String[] eventDetails) {
        if (eventDetails.length < 2) {
            return false;
        }
        String[] eventOfTask = eventDetails[1].split("/to");
        return eventOfTask.length >= 2 && !eventOfTask[0].trim().isEmpty() && !eventOfTask[1].trim().isEmpty();
    }
}
