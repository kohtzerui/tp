package seedu.sudocook;

import java.util.Scanner;


public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "    ";
    private final Scanner scanner = new Scanner(System.in);

    public static void printLine() {
        System.out.println(INDENT + DIVIDER);
    }

    //Formats and prints a response message wrapped between two divider lines.
    public static void formatResponse(String message) {
        System.out.println("");
        for (String line : message.split("\n")) {
            if (line.isEmpty()) {
                System.out.println();
                continue;
            }
            System.out.println(INDENT + " " + line);
        }
        printLine();
    }

    public void printWelcome() {
        formatResponse("Welcome to SudoCook!");
    }

    public void printBye() {
        formatResponse("Goodbye! Happy cooking!");
    }

    public void printError(String message) {
        formatResponse("Oops! " + message);
    }

    public static void printMessage(String message) {
        formatResponse(message);
    }

    public String readInput() {
        System.out.print(INDENT + "> ");
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        } else {
            return "";
        }
    }
}
