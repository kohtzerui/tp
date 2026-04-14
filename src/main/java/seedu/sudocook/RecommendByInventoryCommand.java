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
            double available = 0;
            boolean found = false;
            for (int j = 0; j < inventory.getSize(); j++) {
                Ingredient item = inventory.getIngredient(j);
                if (item.getName().equalsIgnoreCase(required.getName())) {
                    double converted = UnitConverter.convert(
                            item.getQuantity(), item.getUnit(), required.getUnit());
                    if (converted >= 0) {
                        available += converted;
                        found = true;
                    }
                }
            }
            if (!found || available < required.getQuantity()) {
                return false;
            }
        }
        return true;
    }
}
