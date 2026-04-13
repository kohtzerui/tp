package seedu.sudocook;

import java.util.ArrayList;

public class Recipe {
    private static final String INDENT = "    ";

    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> steps;
    private int time;
    private int calories;

    public Recipe() {
        this.name = "Unknown Dish";
        this.ingredients = null;
        this.steps = null;
        this.time = 0;
        this.calories = 0;
        assert(name.equals("Unknown Dish"));
    }

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps, int time, int calories) {
        this.name = normalizeSpaces(name);
        this.ingredients = ingredients;
        this.steps = steps;
        this.time = time;
        this.calories = calories;
        assert(name != null);
        assert(ingredients != null);
        assert(steps != null);
    }

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps, int time) {
        this(name, ingredients, steps, time, 0);
    }

    private String normalizeSpaces(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getCalories() {
        return calories;
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
        sb.append("Preparation Time: ").append(time).append(" minutes\n");
        sb.append("Calories: ").append(calories).append(" kcal\n");

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
