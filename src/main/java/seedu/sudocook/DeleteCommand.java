package seedu.sudocook;

public class DeleteCommand extends Command {
    private final int index;


    public DeleteCommand(int index) {
        super(false);
        this.index = index;
    }

    @Override
    public void execute(RecipeBook recipes){
        recipes.removeRecipe(index);
    }
}
