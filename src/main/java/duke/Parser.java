package duke;

import java.util.Scanner;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.TaskList;
import duke.task.ToDo;

/**
 * Parses input commands.
 * @author Thomas Hogben
 */
public class Parser {
    private Scanner sc;
    private Storage storage;
    private TaskList taskList;
    private Ui ui;

    /**
     * Currently only scans System.in.
     *
     * @param taskList The TaskList the Parser should operate on.
     * @param storage A Storage to use for saving tasks.
     * @param ui A Ui to send display commands to.
     */
    public Parser(TaskList taskList, Storage storage, Ui ui) {
        this.taskList = taskList;
        this.storage = storage;
        this.ui = ui;
        sc = new Scanner(System.in);
    }

    /**
     * Parses input continously and executes the commands provided.
     * Commands recognised: "bye" - exits parsing loop
     *                      "deadline" - creates new Deadline task
     *                      "delete" - deletes specified task
     *                      "done" - completes specified task
     *                      "event" - creates new Event task
     *                      "list" - lists all tasks in task list
     *                      "todo" - creates new ToDo task
     */
    public void parse() {
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            try {
                if (input.equals("bye")) {
                    break;
                } else if (input.equals("list")) {
                    taskList.listTasks();
                } else if (input.startsWith("done ")) {
                    taskList.completeTask(input.substring(5));
                } else if (input.startsWith("todo ")) {
                    taskList.addTask(new ToDo(input.substring(5)));
                } else if (input.startsWith("deadline ")) {
                    taskList.addTask(new Deadline(input.substring(9)));
                } else if (input.startsWith("event ")) {
                    taskList.addTask(new Event(input.substring(6)));
                } else if (input.startsWith("delete ")) {
                    taskList.deleteTask(input.substring(7));
                } else if (input.startsWith("find ")) {
                    taskList.find(input.substring(5));
                } else {
                    throw DukeException.DEFAULT;
                }
                storage.save(taskList);
            } catch (DukeException e) {
                ui.display(e);
            }
        }
    }
}
