package jibberJabber.tasks;

import jibberJabber.commands.ExceptionHandling;
import jibberJabber.commands.KeywordHandling;
import jibberJabber.ui.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
/**
 * The task files class handles file related operations such as storing and retrieving tasks in the task management system.
 */
public class TaskFiles {
    private final String filePath;
    /**
     * Constructs a Task Files object with the specified file path (relative path)
     *
     * @param filePath the path to the file for storing tasks.
     */
    public TaskFiles(String filePath) {
        this.filePath = filePath;
    }
    /**
     * Extracts tasks from the file and populates them into the TaskList array list
     * If the file does not exist, a new file is created using the user's directory path, and an empty TaskList is returned.
     * If the file exist, it reads all the contents from the file and save into the TaskList
     *
     * @return a TaskList containing tasks read from the file.
     */
    public TaskList extractTasksFromFile() {
        TaskList todoTaskList = new TaskList();
        int itemIndex = 0;
        String relativePath = System.getProperty("user.dir") + File.separator + "data/tasks.txt";
        TaskFiles taskFiles = new TaskFiles(relativePath);
        File file = new File(relativePath);
        try {
            Scanner scanner = new Scanner(file);
            if (taskFiles.fileExists()) {
                while (scanner.hasNext()) {
                    // Keep track of the index of the added item from the list
                    itemIndex ++;
                    KeywordHandling keywordHandling = new KeywordHandling();
                    String task = scanner.nextLine().trim();
                    boolean isTaskCompleted = task.contains("[X]");
                    char keywordCharacter = task.charAt(1);
                    task = switch (keywordCharacter) {
                        case 'T' -> task.replaceAll("\\[T]\\[([ X])]", "todo").trim();
                        case 'D' -> task.replaceAll("\\[D]\\[([ X])]", "deadline").replaceAll("\\(by:", "/by").trim();
                        default ->  task.replaceAll("\\[E]\\[([ X])]", "event").replaceAll("\\(from:", "/from").replaceAll("to:", "/to").trim();
                    };
                    // Happens after first attempt to load file, so manually remove additional closing parenthesis
                    task = task.replaceAll("\\)+$", "");
                    String keyword = task.split(" ")[0].trim();
                    Task.addTask(todoTaskList, task, keyword, keywordHandling, true);
                    if (isTaskCompleted) {
                        keywordHandling.processMarkKeyword(todoTaskList, Integer.toString(itemIndex), true, taskFiles, true);
                    }
                }
            } else {
                taskFiles.createFile();
                return new TaskList();
            }
        } catch (FileNotFoundException e) {
            taskFiles.createFile();
            return new TaskList();
        }
        return todoTaskList;
    }
    /**
     * Creates the parent directory for the file if no file exist
     *
     * @return true if the directory was created or already exists, false otherwise.
     */
    private boolean createDirectory(){
        String parentDirectory = new File(filePath).getParent();
        File directory = new File(parentDirectory);
        // Check if the parent directory folder is created
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
    /**
     * Creates the task file and its parent directory if they do not exist.
     */
    protected void createFile() {
        if (!createDirectory()) {
            Message.printFailedDirectoryCreationMessage();
            return;
        }
        File file = new File(filePath);
        if (!fileExists() & !ExceptionHandling.isFileCreated(file)) {
            Message.printFailedToCreateFileMessage();
        }
    }
    /**
     * Writes tasks to the text file and append a single task to the back of the list in the file or overwriting the file with all tasks when marking / unmarking tasks
     *
     * @param todoTaskList the TaskList containing tasks that needs to be added onto
     * @param task         the task to append or adjust in the list
     * @param isAppend     true to append a single task, false to overwrite the file with all tasks.
     */
    public void writeToTextFile(TaskList todoTaskList, Task task, boolean isAppend) {
        try (FileWriter fw = new FileWriter(filePath, isAppend)) {
            // Append to list via terminal
            if (isAppend && task != null) {
                fw.write(task.printAddedTask() + "\n");
            } else {
                // Reads from the file and add
                for (Task i : todoTaskList.getTasks()) {
                    fw.write(i.printAddedTask() + "\n");
                }
            }
        } catch (IOException e) {
            Message.printFailedToAppendToFileMessage();
        }
    }
    /**
     * Checks whether the task file exists.
     *
     * @return true if the file exists, false otherwise.
     */
    public boolean fileExists() {
        File file = new File(filePath);
        return file.exists();
    }
}
