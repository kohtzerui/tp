package seedu.sudocook;

public class SortInventoryCommand extends Command{
    public SortInventoryCommand(boolean isExit) {
        super(isExit);
    }

    @Override
    public void execute (Inventory ingredients){
        Ui.printMessage("Sorting...");
        ingredients.sortIngredients();
    }
}
