package seedu.sudocook;

/**
 * Command to undo the last modification to the inventory or recipe book.
 */
public class UndoCommand extends Command {
    /**
     * Constructs an UndoCommand.
     */
    public UndoCommand() {
        super(false);
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        // This method is not used for undo operations
        Ui.printError("Undo operation requires command history");
    }

    /**
     * Executes the undo command with command history.
     *
     * @param history The command history to undo from
     * @param recipeBook The recipe book to restore
     * @param inventory The inventory to restore
     */
    public void execute(CommandHistory history, RecipeBook recipeBook, Inventory inventory) {
        if (history == null || !history.canUndo()) {
            Ui.printError("No commands to undo.");
            return;
        }

        boolean success = history.undo(recipeBook, inventory);
        if (success) {
            Ui.printMessage("Command undone successfully!");
        } else {
            Ui.printError("No commands to undo.");
        }
    }
}
