package seedu.sudocook;

public class RecommendRecipeCommand extends Command {
    private final String ingredientName;

    public RecommendRecipeCommand(String ingredientName) {
        super(false);
        this.ingredientName = ingredientName;
    }

    @Override
    public void execute(Inventory inventory, RecipeBook recipes) {
        double amount = -1;
        for (int i = 0; i < inventory.size(); i++) {
            Ingredient ingredient = inventory.getIngredient(i);
            if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
                amount = ingredient.getQuantity();
            }
        }
        if (amount < 0) {
            Ui.printError("Ingredient \"" + ingredientName + "\" does not exist in inventory.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.getRecipe(i);
            for (Ingredient ing : recipe.getIngredients()) {
                if (ing.getName().equalsIgnoreCase(ingredientName) && ing.getQuantity() <= amount) {
                    count += 1;
                    sb.append(count).append(". ").append(recipe.getName()).append("\n");
                    break;
                }
            }
        }
        if (count == 0) {
            Ui.printMessage("No recipes meet the requirement");
        } else {
            Ui.printMessage("Recipes containing " + ingredientName + ":\n" + sb.toString().trim());
        }
    }
}
