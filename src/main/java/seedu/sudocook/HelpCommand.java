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
            "1. List Recipes\n" +
            "   Command : list-r\n" +
            "   Purpose : Shows all available recipes.\n\n" +
            "2. Add Recipe\n" +
            "   Command : add-r {NAME} i/{INGREDIENTS} s/{STEPS} t/{TIME_IN_MINUTES}\n" +
            "   Example : add-r {Fried Rice} i/rice 2 cups egg 2 pcs soy_sauce 1 tbsp\n" +
            "             s/{Cook rice} {Fry eggs} {Mix} t/15\n\n" +
            "3. Delete Recipe\n" +
            "   Command : delete-r {INDEX}\n" +
            "   Example : delete-r 1\n\n" +
            "4. Filter Recipes\n" +
            "   Command : filter-r t/{MAX_TIME_IN_MINUTES}\n" +
            "   Example : filter-r t/30\n\n" +
            "5. Recommend Recipes\n" +
            "   Command : recommend-r n/{INGREDIENT_NAME}\n" +
            "   Example : recommend-r n/egg\n"+
            "   Purpose : Shows recipes using specific ingredient with sufficient stock.\n\n" +
            "INGREDIENT COMMANDS:\n" +
            "--------------------------------------------------------\n" +
            "6. List Ingredients\n" +
            "   Command : list-i\n" +
            "   Purpose : Shows all ingredients currently in inventory.\n\n" +
            "7. Add Ingredient\n" +
            "   Command : add-i n/{NAME} q/{QUANTITY} u/{UNIT}\n" +
            "   Example : add-i n/Apple q/5 u/pcs\n\n" +
            "8. Delete Ingredient\n" +
            "   Command : delete-i {INDEX/NAME} [{QUANTITY}]\n" +
            "   Example : delete-i Apple (Deletes all Apples)\n" +
            "   Example : delete-i Apple 2 (Deletes 2 Apples)\n\n" +
            "OTHER:\n" +
            "--------------------------------------------------------\n" +
            "9. Exit\n" +
            "   Command : bye\n" +
            "   Purpose : Exits SudoCook.\n\n" +
            "========================================================";
            
        Ui.printMessage(helpMessage);
    }
}
