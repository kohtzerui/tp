package seedu.sudocook;

public class FilterRecipeCommand extends Command {
    private final Integer maxTime;

    public FilterRecipeCommand(Integer maxTime) {
        super(false);
        this.maxTime = maxTime;
    }

    @Override
    public void execute(RecipeBook recipes) {
        recipes.filterRecipes(maxTime);
    }
}
