package seedu.sudocook;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Command to list all ingredients in the inventory.
 */
public class ListIngredientCommand extends Command {
    private final LocalDate expiryCutoff;

    /**
     * Constructs a command that lists all ingredients in the inventory.
     */
    public ListIngredientCommand() {
        this(null);
    }

    /**
     * Constructs a command that lists only ingredients expiring before the given date.
     *
     * @param expiryCutoff The exclusive expiry-date cutoff, or {@code null} to list all ingredients
     */
    public ListIngredientCommand(LocalDate expiryCutoff) {
        super(false);
        this.expiryCutoff = expiryCutoff;
    }

    @Override
    public void execute(RecipeBook recipeBook) {
        // Not used
    }

    /**
     * Lists ingredients using the cutoff configured on this command, if any.
     *
     * @param inventory The inventory to read ingredients from
     */
    @Override
    public void execute(Inventory inventory) {
        execute(inventory, expiryCutoff);
    }

    /**
     * Lists ingredients in the inventory, optionally restricting results to items whose expiry
     * date is before the supplied cutoff date.
     *
     * @param inventory The inventory to read ingredients from
     * @param expiry The exclusive expiry-date cutoff, or {@code null} to list all ingredients
     */
    @Override
    public void execute(Inventory inventory, LocalDate expiry) {
        ArrayList<Ingredient> ingredients = getIngredientsToDisplay(inventory, expiry);
        if (ingredients.isEmpty()) {
            Ui.printMessage(getEmptyMessage(expiry));
            return;
        }

        StringBuilder sb = new StringBuilder(getHeader(expiry));
        for (int i = 0; i < ingredients.size(); i++) {
            sb.append(i + 1).append(". ").append(ingredients.get(i).toString(expiry));
            if (i < ingredients.size() - 1) {
                sb.append("\n");
            }
        }
        Ui.printMessage(sb.toString());
    }

    private ArrayList<Ingredient> getIngredientsToDisplay(Inventory inventory, LocalDate expiry) {
        ArrayList<Ingredient> ingredients = inventory.getIngredients();
        if (expiry == null) {
            return ingredients;
        }

        ArrayList<Ingredient> filteredIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.hasExpiryBefore(expiry)) {
                filteredIngredients.add(ingredient);
            }
        }
        return filteredIngredients;
    }

    private String getHeader(LocalDate expiry) {
        if (expiry == null) {
            return "Here are the ingredients in your inventory:\n";
        }
        return "Here are the ingredients in your inventory expiring before " + expiry + ":\n";
    }

    private String getEmptyMessage(LocalDate expiry) {
        if (expiry == null) {
            return "There are no ingredients in the inventory.";
        }
        return "There are no ingredients expiring before " + expiry + ".";
    }
}
