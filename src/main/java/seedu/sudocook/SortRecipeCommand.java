package seedu.sudocook;

public class SortRecipeCommand extends Command {
    private final String criteria;

    public SortRecipeCommand(String criteria) {
        super(false);
        this.criteria = criteria;
    }

    public String getCriteria() {
        return criteria;
    }

    @Override
    public void execute(RecipeBook recipes) {
        Ui.printMessage("Sorting recipes...");
        recipes.sortRecipes(criteria);
    }
}
