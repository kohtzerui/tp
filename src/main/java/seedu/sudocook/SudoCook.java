package seedu.sudocook;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Main class for the SudoCook application.
 * Initializes the application and runs the main loop.
 */
public class SudoCook {
    public static final int DELETE_R_PREFIX = 8;

    private static RecipeBook recipes;
    private static Inventory inventory;
    private static Ui ui;

    private void run() {
        ui = new Ui();
        Parser parser = new Parser(ui);
        recipes = new RecipeBook();
        inventory = new Inventory();
        ui.printWelcome();
        Command cmd;
        String input = ui.readInput();
        while (!input.equals("bye")) {
            cmd = parser.parse(input);
            if (cmd instanceof AddIngredientCommand ||
                    cmd instanceof ListIngredientCommand ||
                    cmd instanceof DeleteIngredientCommand) {
                cmd.execute(inventory);
            } else {
                cmd.execute(recipes);
            }
            input = ui.readInput();
        }
        ui.printBye();
    }

    public static void main(String[] args) {
        Logger rootLogger = Logger.getLogger("");
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }
        rootLogger.addHandler(new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        });
        new SudoCook().run();
    }
}
