package seedu.sudocook;

import java.util.ArrayList;

/**
 * Command to list all ingredients in the inventory.
 */
public class ListIngredientCommand extends Command {
    private final Ui ui;

    public ListIngredientCommand(Ui ui) {
        super(false);
        this.ui = ui;
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        // Not used
    }

    @Override
    public void execute(Inventory inventory) {
        ArrayList<Ingredient> ingredients = inventory.getIngredients();
        if (ingredients.isEmpty()) {
            ui.printMessage("There are no ingredients in the inventory.");
            return;
        }

        StringBuilder sb = new StringBuilder("Here are the ingredients in your inventory:\n");
        for (int i = 0; i < ingredients.size(); i++) {
            sb.append(i + 1).append(". ").append(ingredients.get(i).toString());
            if (i < ingredients.size() - 1) {
                sb.append("\n");
            }
        }
        ui.printMessage(sb.toString());
    }
}
