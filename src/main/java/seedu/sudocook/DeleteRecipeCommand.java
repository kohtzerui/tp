package seedu.sudocook;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteRecipeCommand extends Command {
    private static final Logger logger = Logger.getLogger(DeleteRecipeCommand.class.getName());
    private final int index;


    public DeleteRecipeCommand(int index) {
        super(false);
        this.index = index;
    }

    @Override
    public void execute(RecipeBook recipes){
        logger.log(Level.INFO, "Deleting recipe at index: " + index);
        try {
            recipes.removeRecipe(index);
            Ui.printMessage("Recipe " + index + " deleted successfully.");
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Delete failed: " + e.getMessage());
            Ui.printMessage("Invalid index: " + e.getMessage());
        }

    }
}
