package seedu.sudocook;

import java.util.Scanner;


public class Ui {
    private static final boolean IS_TTY = System.console() != null;
    public static final String RESET = IS_TTY ? "\u001B[0m" : "";
    public static final String RED = IS_TTY ? "\u001B[31m" : "";
    public static final String CYAN = IS_TTY ? "\u001B[36m" : "";
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "    ";

    // Instance variables after static variables
    private final Scanner scanner = new Scanner(System.in);

    public static void printLine() {
        System.out.println(INDENT + CYAN + DIVIDER + RESET);
    }

    /**
     * Formats and prints a response message wrapped between two divider lines.
     */
    public static void formatResponse(String message) {
        System.out.println("");
        for (String line : message.split("\r?\n")) {
            if (line.isEmpty()) {
                System.out.println();
                continue;
            }
            System.out.println(INDENT + " " + line);
        }
        printLine();
    }

    public static String getGradientText(String text, int r1, int g1, int b1, int r2, int g2, int b2) {
        if (!IS_TTY) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            double ratio = (double) i / (len > 1 ? len - 1 : 1);
            int r = (int) (r1 + ratio * (r2 - r1));
            int g = (int) (g1 + ratio * (g2 - g1));
            int b = (int) (b1 + ratio * (b2 - b1));
            sb.append(String.format("\u001B[38;2;%d;%d;%dm", r, g, b));
            sb.append(text.charAt(i));
        }
        sb.append(RESET);
        return sb.toString();
    }

    public static void printWelcome() {
        System.out.println("");
        System.out.println(INDENT + CYAN + "Welcome to..." + RESET);
        String logo = 
            INDENT + "  ____            _           ____             _    \n" +
            INDENT + " / ___| _   _  __| | ___     / ___|___   ___  | | __\n" +
            INDENT + " \\___ \\| | | |/ _` |/ _ \\   | |   / _ \\ / _ \\ | |/ /\n" +
            INDENT + "  ___) | |_| | (_| | (_) |  | |__| (_) | (_) ||   < \n" +
            INDENT + " |____/ \\__,_|\\__,_|\\___/    \\____\\___/ \\___/ |_|\\_\\";
        
        String[] lines = logo.split("\n");
        for (String line : lines) {
            System.out.println(getGradientText(line, 190, 80, 255, 50, 200, 255)); // Purple to Cyan
        }
        printLine();
    }

    public static void printBye() {
        printMessage("Goodbye! Happy cooking!");
    }

    public static void printError(String message) {
        formatResponse(RED + "Oops! " + message + RESET);
    }

    public static void printMessage(String message) {
        printGradientMessage(message);
    }

    public static void printGradientMessage(String message) {
        System.out.println("");
        for (String line : message.split("\r?\n")) {
            if (line.isEmpty()) {
                System.out.println();
                continue;
            }
            if (line.contains("\u001B")) {
                System.out.println(INDENT + " " + line);
            } else {
                String gradientLine = getGradientText(line, 190, 80, 255, 50, 200, 255);
                System.out.println(INDENT + " " + gradientLine);
            }
        }
        printLine();
    }

    public String readInput() {
        System.out.print(INDENT + "> ");
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        } else {
            return "bye";
        }
    }
}
