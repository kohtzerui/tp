package seedu.sudocook;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.Level;


/**
 * Main class for the SudoCook application.
 * Initializes the application and runs the main loop.
 */
public class SudoCook {
    public static final int DELETE_R_PREFIX = 8;

    private static final Logger logger = Logger.getLogger(SudoCook.class.getName());

    private static RecipeBook recipes;
    private static Inventory inventory;
    private static Ui ui;

    private void run() {
        logger.log(Level.INFO, "Starting SudoCook application");

        ui = new Ui();
        Parser parser = new Parser(ui);
        recipes = new RecipeBook();
        inventory = new Inventory();

        ui.printWelcome();

        Command cmd;
        String input = ui.readInput();
        while (!input.equals("bye")) {
            if (input.isBlank()) {
                logger.log(Level.FINE, "Empty input received, skipping");
                input = ui.readInput();
                continue;
            }
            cmd = parser.parse(input);
            if (cmd instanceof AddIngredientCommand ||
                    cmd instanceof ListIngredientCommand ||
                    cmd instanceof DeleteIngredientCommand) {
                logger.log(Level.FINE, "Routing command to Inventory");
                cmd.execute(inventory);
            } else {
                logger.log(Level.FINE, "Routing command to RecipeBook");
                cmd.execute(recipes);
            }
            input = ui.readInput();
        }
        logger.log(Level.INFO, "SudoCook application shutting down");
        ui.printBye();
    }

    public static void main(String[] args) {
        Logger rootLogger = Logger.getLogger("");
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }
        StreamHandler handler = new StreamHandler(System.err, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        handler.setLevel(Level.FINE);
        rootLogger.setLevel(Level.FINE);
        rootLogger.addHandler(handler);
        new SudoCook().run();
    }
}
