package seedu.sudocook;

public class SearchIngredientCommand extends Command {
    private final String query;

    public SearchIngredientCommand(String query) {
        super(false);
        this.query = query;
    }

    @Override
    public void execute(Inventory inventory) {
        inventory.searchIngredients(query);
    }
}
