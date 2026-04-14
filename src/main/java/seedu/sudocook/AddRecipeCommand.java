package seedu.sudocook;

import java.util.ArrayList;

public class AddRecipeCommand extends Command {
    private final ArrayList<Ingredient> ingredients;
    private final ArrayList<String> steps;
    private final String name;
    private final int time;
    private final int calories;
    public AddRecipeCommand(String name, ArrayList<Ingredient> ingredients,
            ArrayList<String> steps, int time, int calories) {
        super(false);
        assert(name!=null);
        assert(ingredients!=null);
        assert(steps!=null);
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.time = time;
        this.calories = calories;
    }

    @Override
    public void execute(RecipeBook recipes) {
        assert(recipes!=null);
        try {
            Recipe addedRecipe = recipes.addRecipe(name, ingredients, steps, time, calories);
            Ui.printGradientMessage("Added recipe:\n" + addedRecipe.toString());
        } catch (IllegalArgumentException e) {
            Ui.printError(e.getMessage());
        }
    }
}
