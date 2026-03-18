package seedu.sudocook;

/**
 * Command to display help information.
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super(false);
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        // Not used
    }

    @Override
    public void execute(Inventory inventory) {
        String helpMessage = 
            "SudoCook Help Guide\n" +
            "========================================================\n\n" +
            "RECIPE COMMANDS:\n" +
            "--------------------------------------------------------\n" +
            "1. List Recipes: list-r\n" +
            "   Shows all available recipes.\n\n" +
            "2. Add Recipe: add-r {NAME} i/{INGREDIENTS} s/{STEPS}\n" +
            "   Example: add-r {Fried Rice} i/rice 2 cups egg 2 pcs soy_sauce 1 tbsp s/{Cook rice} {Fry eggs} {Mix}\n\n" +
            "3. Delete Recipe: delete-r {INDEX}\n" +
            "   Example: delete-r 1\n\n" +
            "INGREDIENT COMMANDS:\n" +
            "--------------------------------------------------------\n" +
            "4. List Ingredients: list-i\n" +
            "   Shows all ingredients currently in inventory.\n\n" +
            "5. Add Ingredient: add-i n/{NAME} q/{QUANTITY} u/{UNIT}\n" +
            "   Example: add-i n/Apple q/5 u/pcs\n\n" +
            "6. Delete Ingredient: delete-i {INDEX/NAME} [{QUANTITY}]\n" +
            "   Example: delete-i Apple (Deletes all Apples)\n" +
            "   Example: delete-i Apple 2 (Deletes 2 Apples)\n\n" +
            "OTHER:\n" +
            "--------------------------------------------------------\n" +
            "7. Exit: bye\n" +
            "   Exits SudoCook.\n\n" +
            "========================================================";
            
        Ui.printMessage(helpMessage);
    }
}
