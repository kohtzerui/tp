package seedu.sudocook;

import java.util.ArrayList;

public class AddRecipeCommand extends Command {
    ArrayList<String> ingredients = new ArrayList<>();
    ArrayList<String> steps = new ArrayList<>();
    String name;
    public AddRecipeCommand(String name, ArrayList<String> ingredients, ArrayList<String> steps) {
        super(false);
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    @Override
    public void execute(RecipeBook recipes) {
        recipes.addRecipe(name, ingredients, steps);

    }
}
