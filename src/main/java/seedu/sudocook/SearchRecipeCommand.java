package seedu.sudocook;

public class SearchRecipeCommand extends Command {
    private final String query;

    public SearchRecipeCommand(String query) {
        super(false);
        this.query = query;
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        recipeBook.searchRecipes(query);
    }
}
