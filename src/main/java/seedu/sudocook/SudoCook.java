package seedu.sudocook;

/**
 * Main class for the SudoCook application.
 * Initializes the application and runs the main loop.
 */
public class SudoCook {
    public static final int DELETE_R_PREFIX = 8;

    private static RecipeBook recipes;
    private static Ui ui;

    private void run() {
        ui = new Ui();
        Parser parser = new Parser(ui);
        recipes = new RecipeBook();
        ui.printWelcome();
        Command cmd;
        String input = ui.readInput();
        while (!input.equals("bye")) {
            cmd = parser.parse(input);
            cmd.execute(recipes);

            input = ui.readInput();
            ui.printLine();
        }
        ui.printBye();
    }

    public static void main(String[] args) {
        new SudoCook().run();
    }
}
