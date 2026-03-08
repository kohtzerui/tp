package seedu.sudocook;

public class ListCommand extends Command {
    public ListCommand() {
        super(false);
    }

    @Override
    public void execute(RecipeBook recipes){
        recipes.listRecipe();
    }
}
