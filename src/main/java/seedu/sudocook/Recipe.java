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
    }

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
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

        sb.append(INDENT + "Recipe Name: ").append(name).append("\n");

        sb.append("\n" + INDENT + "Ingredients:\n");
        if (ingredients.isEmpty()) {
            sb.append(INDENT + "(No ingredients listed)\n");
        } else {
            for (Ingredient ingredient : ingredients) {
                sb.append(INDENT + "- ").append(ingredient).append("\n");
            }
        }

        sb.append("\n" + INDENT + "Steps:\n");
        if (steps.isEmpty()) {
            sb.append(INDENT + "(No steps listed)\n");
        } else {
            for (String step : steps) {
                sb.append(INDENT + "- ").append(step).append("\n");
            }
        }

        return sb.toString();
    }
}
