package seedu.sudocook;

public class ViewRecipeCommand extends Command {
    private final int index; // -1 means view all

    public ViewRecipeCommand() {
        super(false);
        this.index = -1;
    }

    public ViewRecipeCommand(int index) {
        super(false);
        this.index = index;
    }

    @Override
    public void execute(RecipeBook recipes) {
        if (index == -1) {
            recipes.viewRecipe();
        } else {
            recipes.viewRecipe(index);
        }
    }
}
