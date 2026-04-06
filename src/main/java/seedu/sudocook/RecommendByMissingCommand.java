package seedu.sudocook;

import java.util.ArrayList;

/**
 * Command to recommend recipes that are missing at most N ingredients,
 * showing the exact shortfall for each missing ingredient.
 */
public class RecommendByMissingCommand extends Command {
    private final int maxMissing;

    public RecommendByMissingCommand(int maxMissing) {
        super(false);
        this.maxMissing = maxMissing;
    }

    @Override
    public void execute(Inventory inventory, RecipeBook recipes) {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (int i = 0; i < recipes.getSize(); i++) {
            Recipe recipe = recipes.getRecipe(i);
            ArrayList<String> missing = getMissingIngredients(recipe, inventory);
            if (missing.size() > 0 && missing.size() <= maxMissing) {
                count++;
                sb.append(count).append(". ").append(recipe.getName())
                        .append(" (missing: ").append(String.join(", ", missing)).append(")\n");
            }
        }

        if (count == 0) {
            Ui.printMessage("No recipes found missing at most " + maxMissing + " ingredient(s).");
        } else {
            Ui.printMessage("Recipes you're almost able to make:\n" + sb.toString().trim());
        }
    }

    private ArrayList<String> getMissingIngredients(Recipe recipe, Inventory inventory) {
        ArrayList<String> missing = new ArrayList<>();
        for (Ingredient required : recipe.getIngredients()) {
            double available = 0;
            for (int j = 0; j < inventory.getSize(); j++) {
                Ingredient item = inventory.getIngredient(j);
                if (item.getName().equalsIgnoreCase(required.getName())) {
                    available = item.getQuantity();
                    break;
                }
            }
            if (available < required.getQuantity()) {
                double shortfall = required.getQuantity() - available;
                String formatted = required.getName() + " (" + shortfall + " " + required.getUnit() + ")";
                missing.add(formatted);
            }
        }
        return missing;
    }
}
