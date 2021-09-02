import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Duke.
 * @author Thomas Hogben
 */
public class Duke {
    private static final String MSG_ERROR_DEFAULT = "im sorry I is no understand.";
    private static final String MSG_ERROR_BLANK_DESCRIPTION = "is no leave description blank;";
    private static final String MSG_ERROR_BLANK_DATEANDTIME = "is no leave date and time blank!";
    private static final String MSG_ERROR_CORRUPT_TASK = "save file corrupt. is delete task.";
    private static final String MSG_ERROR_CORRUPT_SAVE = "save file corrupt. is no exit program until message stops.";
    private static final String MSG_ERROR_INVALID_TASK_NUMBER = "what kind of number is (||❛︵❛.)";
    private static final String MSG_ERROR_NOT_ENOUGH_TASKS = "we is dont have that many tasks yet.";
    private static final String MSG_ERROR_UNSPECIFIED_TASK = "please is specify task please,";
    private static final String MSG_EXIT = "okay is bye!!";
    private static final String MSG_NO_TASKS = "is no tasks today.";
    private static final String MSG_TASK_ADDED = "is added.";
    private static final String MSG_TASK_DELETED = "is deleted!";
    private static final String MSG_TASK_DONE = "is done!";

    /** Program loops until this is set to false */
    private static boolean isRunning = true;
    private static boolean isSavable = true;
    private static Scanner sc;
    private static Path savePath;
    /** A list of tasks to be done */
    private static ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Lists out all Tasks numbered and on individual lines.
     * Calls the toString() method of each Task to display them
     * and their type/status.
     */
    private static void listTasks() {
        if (tasks.size() == 0) {
            System.out.println(MSG_NO_TASKS);
        } else {
            for (int i = 1; i <= tasks.size(); i++) {
                System.out.println(i + "." + tasks.get(i - 1).toString() + ".");
            }
        }
    }

    /**
     * Adds a Task to the internal list.
     *
     * @param newTask The new Task to be added.
     */
    private static void addTask(Task newTask) {
            tasks.add(newTask);
            System.out.println(MSG_TASK_ADDED);
            System.out.println(newTask.toString());
            System.out.println("now is have " + tasks.size() + " task" +
                    (tasks.size() == 1 ? "" : "s") + ".");
    }

    /**
     * Marks a specified task as complete.
     * Parses the string after the "done" command and marks the
     * specified task.  Expected format: " " + task_number
     *
     * @param input The argument string after the "done" command.
     * @throws DukeException Thrown for any possible error relating to invalid input.
     */
    private static void completeTask(String input) throws DukeException {
        try {
            if (input.length() < 1) {
                throw new DukeException(MSG_ERROR_UNSPECIFIED_TASK);
            } else {
                int i = Integer.parseInt(input.substring(1));
                if (i > tasks.size()) {
                    throw new DukeException(MSG_ERROR_NOT_ENOUGH_TASKS);
                } else if (i <= 0) {
                    throw new DukeException(MSG_ERROR_INVALID_TASK_NUMBER);
                } else {
                    Task task = tasks.get(i - 1);
                    task.setDone();
                    System.out.println(MSG_TASK_DONE);
                    System.out.println(task.toString());
                }
            }
        } catch (NumberFormatException e) {
            throw new DukeException(MSG_ERROR_DEFAULT);
        }
    }

    /**
     * Deletes a specified task.
     * Parses the string after the "delete" command and deletes the
     * specified task. Expected format: " " + task_number
     *
     * @param input The argument string after the "delete" command.
     * @throws DukeException Thrown for any possible error relating to invalid input.
     */
    private static void deleteTask(String input) throws DukeException {
        try {
            if (input.length() < 1) {
                throw new DukeException(MSG_ERROR_UNSPECIFIED_TASK);
            } else {
                int i = Integer.parseInt(input.substring(1));
                if (i > tasks.size()) {
                    throw new DukeException(MSG_ERROR_NOT_ENOUGH_TASKS);
                } else if (i <= 0) {
                    throw new DukeException(MSG_ERROR_INVALID_TASK_NUMBER);
                } else {
                    Task task = tasks.get(i - 1);
                    tasks.remove(i - 1);
                    System.out.println(MSG_TASK_DELETED);
                    System.out.println(task.toString());
                    System.out.println("now is have " + tasks.size() + " task" +
                            (tasks.size() == 1 ? "" : "s") + ".");
                }
            }
        } catch (NumberFormatException e) {
            throw new DukeException(MSG_ERROR_DEFAULT);
        }
    }

    /**
     * Marks the program to exit after completion of the current loop.
     */
    private static void exit() {
        System.out.println(MSG_EXIT);
        isRunning = false;
    }

    /**
     * Adds a ToDo Task to the list.
     * Parses the string after the "todo" command and adds a ToDo task
     * with that description to the list.
     * Expected format: " " + task_description
     *
     * @param description The argument string after the "todo" command.
     * @throws DukeException Thrown if the description of the task is blank.
     */
    private static void addToDo(String description) throws DukeException {
        if (description.length() <= 1) {
            throw new DukeException(MSG_ERROR_BLANK_DESCRIPTION);
        } else {
            addTask(new ToDo(description.substring(1)));
        }
    }

    /**
     * Adds a Deadline Task to the list.
     * Parses the string after the "deadline" command and adds a deadline task
     * with that description to the list.
     * Expected format: " " + task_description + " /by " + task_deadline
     *
     * It is also possible to omit the deadline currently.
     * Expected format: " " + task_description
     *
     * @param description The argument string after the "deadline" command.
     * @throws DukeException Thrown if the description of the task is blank.
     */
    private static void addDeadline(String description) throws DukeException {
        if (description.length() <= 1) {
            throw new DukeException(MSG_ERROR_BLANK_DESCRIPTION);
        } else {
            int i = description.indexOf(" /by ");
            if (i < 0) {
                throw new DukeException(MSG_ERROR_BLANK_DATEANDTIME);
            } else if (i <= 1) {
                throw new DukeException(MSG_ERROR_BLANK_DESCRIPTION);
            } else {
                addTask(new Deadline(description.substring(1, i), description.substring(i + 5)));
            }
        }
    }

    /**
     * Adds an Event Task to the list.
     * Parses the string after the "event" command and adds an event task
     * with that description to the list.
     * Expected format: " " + task_description + " /at " + task_date_and_time
     *
     * It is also possible to omit the date and time currently.
     * Expected format: " " + task_description
     *
     * @param description The argument string after the "event" command.
     * @throws DukeException Thrown if the description of the task is blank.
     */
    private static void addEvent(String description) throws DukeException {
        if (description.length() <= 1) {
            throw new DukeException(MSG_ERROR_BLANK_DESCRIPTION);
        } else {
            int i = description.indexOf(" /at ");
            if (i < 0) {
                throw new DukeException(MSG_ERROR_BLANK_DATEANDTIME);
            } else if (i <= 1) {
                throw new DukeException(MSG_ERROR_BLANK_DESCRIPTION);
            } else {
                addTask(new Event(description.substring(1, i), description.substring(i + 5)));
            }
        }
    }

    private static void save() throws DukeException {
        try {
            Files.delete(savePath);
            Files.createFile(savePath);
            String saves = "";
            for (int i = 0; i < tasks.size(); i++) {
                saves += tasks.get(i).getSave() + "\n";
            }
            byte[] savesToBytes = saves.getBytes();
            Files.write(savePath, savesToBytes);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void loadSaveFile() throws DukeException {
        Path saveDirectory = Paths.get(".", "data");
        savePath = Paths.get(".", "data", "duke.txt");
        try {
            if (!Files.exists(saveDirectory)) {
                Files.createDirectory(saveDirectory);
            }
            if (!Files.exists(savePath)) {
                Files.createFile(savePath);
            }
            Scanner sc = new Scanner(savePath);
            while(sc.hasNextLine()) {
                String nextTask = sc.nextLine();
                char taskType = nextTask.charAt(0);
                boolean isDone = Integer.parseInt(nextTask.substring(1,2)) == 1;
                String taskDetails = nextTask.substring(3);
                if (taskType == 'T') {
                    tasks.add(new ToDo(taskDetails, isDone));
                } else {
                    int breakIndex = taskDetails.indexOf('|');
                    String taskDescription = taskDetails.substring(0, breakIndex);
                    String taskDateAndTime = taskDetails.substring(breakIndex + 1);
                    if (taskType == 'D') {
                        tasks.add(new Deadline(taskDescription, taskDateAndTime, isDone));
                    } else if (taskType == 'E') {
                        tasks.add(new Event(taskDescription, taskDateAndTime, isDone));
                    } else {
                        throw new DukeException(MSG_ERROR_CORRUPT_TASK);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    /**
     * Parses the provided input and executes the command inside.
     *
     * @param input The input to be parsed.
     */
    private static void parseInput(String input) {
        try {
            if (input.equals("bye")) {
                exit();
            } else if (input.equals("list")) {
                listTasks();
            } else if (input.startsWith("done")) {
                completeTask(input.substring(4));
            } else if (input.startsWith("todo")) {
                addToDo(input.substring(4));
            } else if (input.startsWith("deadline")) {
                addDeadline(input.substring(8));
            } else if (input.startsWith("event")) {
                addEvent(input.substring(5));
            } else if (input.startsWith("delete")) {
                deleteTask(input.substring(6));
            } else {
                throw new DukeException(MSG_ERROR_DEFAULT);
            }
            save();
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
        System.out.println("hello name is duke");
        System.out.println("how is help today; （´・｀ ）♡");

        try {
            loadSaveFile();
        } catch (DukeException e) {
            System.out.println(e);
        }

        sc = new Scanner(System.in);

        while (isRunning) {
            parseInput(sc.nextLine());
        }
    }
}
