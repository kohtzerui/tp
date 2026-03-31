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
            "   Purpose : Shows names of all available recipes.\n\n" +
            "2. View Recipes\n" +
            "   Command : view-r [INDEX]\n" +
            "   Purpose : Shows full details of all recipes, or a specific recipe by index.\n" +
            "   Example : view-r 1\n\n" +
            "3. Add Recipe\n" +
            "   Command : add-r {NAME} i/{INGREDIENTS} s/{STEPS} t/{TIME_IN_MINUTES}\n" +
            "   Example : add-r {Fried Rice} i/rice 2 cups egg 2 pcs soy_sauce 1 tbsp\n" +
            "             s/{Cook rice} {Fry eggs} {Mix} t/15\n\n" +
            "4. Delete Recipe\n" +
            "   Command : delete-r {INDEX}\n" +
            "   Example : delete-r 1\n\n" +
            "5. Filter Recipes\n" +
            "   Command : filter-r t/{MAX_TIME_IN_MINUTES}\n" +
            "   Example : filter-r t/30\n\n" +
            "6. Recommend Recipes\n" +
            "   Command : recommend-r [n/{INGREDIENT_NAME}]\n" +
            "   Example : recommend-r n/egg\n" +
            "   Example : recommend-r\n" +
            "   Purpose : Recommend available recipes based on specific ingredient or current inventory.\n\n" +
            "   Command : recommend-r missing/{N}\n" +
            "   Example : recommend-r missing/2\n" +
            "   Purpose : Show recipes missing at most N ingredients, with exact shortfall per item.\n\n" +
            "INGREDIENT COMMANDS:\n" +
            "--------------------------------------------------------\n" +
            "7. List Ingredients\n" +
            "   Command : list-i [ex/{YYYY-MM-DD}]\n" +
            "   Example : list-i ex/2026-04-01\n" +
            "   Purpose : Shows all ingredients, or only those expiring before the given date.\n\n" +
            "8. Add Ingredient\n" +
            "   Command : add-i n/{NAME} q/{QUANTITY} u/{UNIT}\n" +
            "   Example : add-i n/Apple q/5 u/pcs\n\n" +
            "9. Delete Ingredient\n" +
            "   Command : delete-i {INDEX/NAME} [{QUANTITY}]\n" +
            "   Example : delete-i Apple (Deletes all Apples)\n" +
            "   Example : delete-i Apple 2 (Deletes 2 Apples)\n\n" +
            "OTHER:\n" +
            "--------------------------------------------------------\n" +
            "10. Exit\n" +
            "   Command : bye\n" +
            "   Purpose : Exits SudoCook.\n\n" +
            "========================================================";

        Ui.printMessage(helpMessage);
    }
}
