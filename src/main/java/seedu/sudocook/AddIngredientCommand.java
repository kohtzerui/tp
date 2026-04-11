package seedu.sudocook;

import java.time.LocalDate;

/**
 * Command to add an ingredient to the inventory.
 */
public class AddIngredientCommand extends Command {
    private final String name;
    private final double quantity;
    private final String unit;
    private final LocalDate expiryDate;

    /**
     * Constructs an AddIngredientCommand with the specified ingredient details.
     *
     * @param name The name of the ingredient
     * @param quantity The quantity of the ingredient
     * @param unit The unit of measurement
     */
    public AddIngredientCommand(String name, double quantity, String unit) {
        super(false);
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = null;
    }

    /**
     * Constructs an AddIngredientCommand with the specified ingredient details and expiry date.
     *
     * @param name The name of the ingredient
     * @param quantity The quantity of the ingredient
     * @param unit The unit of measurement
     * @param expiryDate The expiry date of the ingredient (optional)
     */
    public AddIngredientCommand(String name, double quantity, String unit, LocalDate expiryDate) {
        super(false);
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        // This method is not used for inventory operations
    }

    /**
     * Executes the command by adding the ingredient to the inventory.
     *
     * @param inventory The inventory to add the ingredient to
     */
    public void execute(Inventory inventory) {
        assert inventory != null : "Inventory must not be null";
        assert name != null && !name.isEmpty() : "Ingredient name must not be empty";
        assert quantity > 0 : "Ingredient quantity must be positive";

        Ingredient ingredient;
        if (expiryDate != null) {
            ingredient = new Ingredient(name, quantity, unit, expiryDate);
        } else {
            ingredient = new Ingredient(name, quantity, unit);
        }
        inventory.addIngredient(ingredient);
        Ui.printMessage("Added: " + ingredient);
    }
}
