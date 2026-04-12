package seedu.sudocook;

/**
 * Command to recommend recipes whose full ingredient list
 * can be satisfied by the current inventory.
 */
public class RecommendByInventoryCommand extends Command {

    public RecommendByInventoryCommand() {
        super(false);
    }

    @Override
    public void execute(Inventory inventory, RecipeBook recipes) {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (int i = 0; i < recipes.getSize(); i++) {
            Recipe recipe = recipes.getRecipe(i);
            if (canMake(recipe, inventory)) {
                count++;
                sb.append(count).append(". ").append(recipe.getName()).append("\n");
            }
        }

        if (count == 0) {
            Ui.printMessage("No recipes can be made with the current inventory.");
        } else {
            Ui.printMessage("Recipes you can make with your inventory:\n" + sb.toString().trim());
        }
    }

    private boolean canMake(Recipe recipe, Inventory inventory) {
        for (Ingredient required : IngredientRequirements.aggregateFor(recipe)) {
            double available = -1;
            String availableUnit = null;
            for (int j = 0; j < inventory.getSize(); j++) {
                Ingredient item = inventory.getIngredient(j);
                if (item.getName().equalsIgnoreCase(required.getName())) {
                    available = item.getQuantity();
                    availableUnit = item.getUnit();
                    break;
                }
            }
            if (available < 0) {
                return false;
            }
            double requiredInAvailableUnit = UnitConverter.convert(
                    required.getQuantity(), required.getUnit(), availableUnit);
            if (requiredInAvailableUnit < 0 || available < requiredInAvailableUnit) {
                return false;
            }
        }
        return true;
    }
}
