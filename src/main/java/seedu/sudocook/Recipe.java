package seedu.sudocook;

import java.util.ArrayList;

public class Recipe {
    private static final String INDENT = "    ";

    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> steps;

    public Recipe() {
        this.name = "Unknown Dish";
        this.ingredients = null;
        this.steps = null;
        assert(name.equals("Unknown Dish"));
    }

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        assert(name != null);
        assert(ingredients != null);
        assert(steps != null);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Recipe Name: ").append(name).append("\n");

        sb.append("\n").append(Ui.CYAN).append(INDENT).append("Ingredients:").append(Ui.RESET).append("\n");
        if (ingredients.isEmpty()) {
            sb.append(Ui.CYAN).append(INDENT).append("(No ingredients listed)").append(Ui.RESET).append("\n");
        } else {
            for (Ingredient ingredient : ingredients) {
                sb.append(Ui.CYAN).append(INDENT).append("- ").append(ingredient).append(Ui.RESET).append("\n");
            }
        }

        sb.append("\n").append(Ui.CYAN).append(INDENT).append("Steps:").append(Ui.RESET).append("\n");
        if (steps.isEmpty()) {
            sb.append(Ui.CYAN).append(INDENT).append("(No steps listed)").append(Ui.RESET).append("\n");
        } else {
            for (String step : steps) {
                sb.append(Ui.CYAN).append(INDENT).append("- ").append(step).append(Ui.RESET).append("\n");
            }
        }

        return sb.toString();
    }
}
