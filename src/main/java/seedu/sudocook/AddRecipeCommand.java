package seedu.sudocook;

import java.util.ArrayList;

public class AddRecipeCommand extends Command {
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    ArrayList<String> steps = new ArrayList<>();
    String name;
    int time;
    public AddRecipeCommand(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps, int time) {
        super(false);
        assert(name!=null);
        assert(ingredients!=null);
        assert(steps!=null);
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.time = time;
    }

    @Override
    public void execute(RecipeBook recipes) {
        assert(recipes!=null);
        recipes.addRecipe(name, ingredients, steps, time);

    }
}
