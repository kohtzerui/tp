package seedu.sudocook;

/**
 * Command to delete an ingredient from the inventory.
 */
public class DeleteIngredientCommand extends Command {
    private final String nameOrIndex;
    private final double quantityToRemove;
    private final boolean isRemoveAll;
    private final Ui ui;

    /**
     * Constructs a DeleteIngredientCommand to remove a specific quantity.
     *
     * @param nameOrIndex      The name or index (1-based) of the ingredient
     * @param quantityToRemove The quantity to remove
     * @param ui               The UI instance for printing messages
     */
    public DeleteIngredientCommand(String nameOrIndex, double quantityToRemove, Ui ui) {
        super(false);
        this.nameOrIndex = nameOrIndex;
        this.quantityToRemove = quantityToRemove;
        this.isRemoveAll = false;
        this.ui = ui;
    }

    /**
     * Constructs a DeleteIngredientCommand to remove all of an ingredient.
     *
     * @param nameOrIndex The name or index (1-based) of the ingredient
     * @param ui          The UI instance for printing messages
     */
    public DeleteIngredientCommand(String nameOrIndex, Ui ui) {
        super(false);
        this.nameOrIndex = nameOrIndex;
        this.quantityToRemove = 0;
        this.isRemoveAll = true;
        this.ui = ui;
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        // Not used
    }

    @Override
    public void execute(Inventory inventory) {
        int indexToRemove = -1;

        // Try to parse the input as a 1-based index first
        try {
            indexToRemove = Integer.parseInt(nameOrIndex) - 1;
        } catch (NumberFormatException e) {
            // It's a name, find its index (0-based)
            indexToRemove = inventory.findIndexByName(nameOrIndex);
        }

        if (indexToRemove < 0 || indexToRemove >= inventory.size()) {
            ui.printError("Ingredient not found in inventory.");
            return;
        }

        Ingredient targetIngredient = inventory.getIngredient(indexToRemove);
        String name = targetIngredient.getName();
        String unit = targetIngredient.getUnit();

        if (isRemoveAll || targetIngredient.getQuantity() <= quantityToRemove) {
            inventory.removeIngredient(indexToRemove);
            ui.printMessage("Removed all of: " + name);
        } else {
            inventory.updateQuantity(indexToRemove, quantityToRemove);
            Ingredient updatedIngredient = inventory.getIngredient(indexToRemove);
            ui.printMessage("Removed " + quantityToRemove + " " + unit + " of " + name + ".\n"
                    + "Remaining: " + updatedIngredient.getQuantity() + " " + unit);
        }
    }
}
