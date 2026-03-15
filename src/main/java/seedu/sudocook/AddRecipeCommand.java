package seedu.sudocook;

import java.util.ArrayList;

public class AddRecipeCommand extends Command {
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    ArrayList<String> steps = new ArrayList<>();
    String name;
    public AddRecipeCommand(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps) {
        super(false);
        assert(name!=null);
        assert(ingredients!=null);
        assert(steps!=null);
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    @Override
    public void execute(RecipeBook recipes) {
        assert(recipes!=null);
        Recipe addedRecipe = recipes.addRecipe(name, ingredients, steps);
        Ui.printMessage("Added recipe:\n" + addedRecipe.toString());
    }
}
