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
            "   Command : add-r NAME i/INGREDIENTS s/STEPS t/TIME_IN_MINUTES c/CALORIES\n" +
            "   Example : add-r {Fried Rice} i/rice 2 cups egg 2 pcs {soy sauce} 1 tbsp\n" +
            "             s/{Cook rice} {Fry eggs} {Mix} t/15 c/450\n" +
            "   Note    : TIME and CALORIES must each be between 1 and 100,000.\n\n" +
            "4. Delete Recipe\n" +
            "   Command : delete-r INDEX\n" +
            "   Example : delete-r 1\n\n" +
            "5. Filter Recipes\n" +
            "   Command : filter-r [t/MAX_TIME_IN_MINUTES] [c/MAX_CALORIES]\n" +
            "   Example : filter-r t/30\n" +
            "   Example : filter-r c/500\n" +
            "   Example : filter-r t/30 c/500\n\n" +
            "6. Search Recipes (Fuzzy)\n" +
            "   Command : search-r QUERY\n" +
            "   Example : search-r fried rice\n" +
            "   Purpose : Fuzzy search recipes by name (handles typos and partial matches).\n\n" +
            "7. Sort Recipes\n" +
            "   Command : sort-r n/ | t/ | c/\n" +
            "   Example : sort-r n/   (alphabetical)\n" +
            "   Example : sort-r t/   (by time)\n" +
            "   Example : sort-r c/   (by calories)\n\n" +
            "8. Cook Recipe\n" +
            "   Command : cook INDEX\n" +
            "   Example : cook 1\n" +
            "   Purpose : Uses ingredients from inventory to cook the selected recipe.\n\n" +
            "9. Recommend Recipes\n" +
            "   Command : recommend-r [n/INGREDIENT_NAME]\n" +
            "   Example : recommend-r n/egg\n" +
            "   Example : recommend-r\n" +
            "   Purpose : Recommend available recipes based on specific ingredient or current inventory.\n\n" +
            "   Command : recommend-r missing/N\n" +
            "   Example : recommend-r missing/2\n" +
            "   Purpose : Show recipes missing at most N ingredients, with exact shortfall per item.\n\n" +
            "INGREDIENT COMMANDS:\n" +
            "--------------------------------------------------------\n" +
            "10. List Ingredients\n" +
            "   Command : list-i [ex/YYYY-MM-DD]\n" +
            "   Example : list-i ex/2026-04-01\n" +
            "   Purpose : Shows all ingredients, or only those expiring before the given date.\n\n" +
            "11. Add Ingredient\n" +
            "   Command : add-i n/NAME q/QUANTITY u/UNIT [ex/YYYY-MM-DD]\n" +
            "   Example : add-i n/Apple q/5 u/pcs\n" +
            "   Example : add-i n/Milk q/1 u/carton ex/2026-04-10\n\n" +
            "12. Delete Ingredient\n" +
            "   Command : delete-i INDEX/NAME [QUANTITY]\n" +
            "   Example : delete-i 1\n" +
            "   Example : delete-i Apple\n" +
            "   Example : delete-i Apple 2\n" +
            "   Purpose : Deletes the indexed ingredient, or the first ingredient matching the name.\n\n" +
            "13. Search Ingredients (Fuzzy)\n" +
            "   Command : search-i QUERY\n" +
            "   Example : search-i tomato\n" +
            "   Purpose : Fuzzy search ingredients by name (handles typos and partial matches).\n\n" +
            "14. Sort Ingredients\n" +
            "   Command : sort-i\n" +
            "   Purpose : Sorts inventory by expiry date, with items without expiry shown last.\n\n" +
            "OTHER:\n" +
            "--------------------------------------------------------\n" +
            "15. Undo\n" +
            "   Command : undo\n" +
            "   Purpose : Reverts the most recent command that modified the inventory or recipes.\n\n" +
            "16. Help\n" +
            "   Command : help\n" +
            "   Purpose : Displays this help guide.\n\n" +
            "17. Exit\n" +
            "   Command : bye\n" +
            "   Purpose : Exits SudoCook.\n\n" +
            "========================================================";

        Ui.printMessage(helpMessage);
    }
}
