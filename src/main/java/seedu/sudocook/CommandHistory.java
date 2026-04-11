package seedu.sudocook;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Manages the command history by storing snapshots of RecipeBook and Inventory states.
 */
public class CommandHistory {
    private static final int MAX_SNAPSHOTS = 50;
    private Stack<Snapshot> snapshots;

    /**
     * Represents a snapshot of both RecipeBook and Inventory states.
     */
    private static class Snapshot {
        private final RecipeBook recipeBook;
        private final Inventory inventory;

        /**
         * Creates a snapshot by deep copying the current states.
         *
         * @param recipeBook The recipe book to snapshot
         * @param inventory The inventory to snapshot
         */
        Snapshot(RecipeBook recipeBook, Inventory inventory) {
            // Deep copy recipe book
            this.recipeBook = new RecipeBook();
            for (Recipe recipe : recipeBook.getRecipes()) {
                ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients());
                ArrayList<String> steps = new ArrayList<>(recipe.getSteps());
                this.recipeBook.addRecipe(new Recipe(recipe.getName(), ingredients, steps, 
                        recipe.getTime(), recipe.getCalories()));
            }

            // Deep copy inventory
            this.inventory = new Inventory();
            for (Ingredient ingredient : inventory.getIngredients()) {
                this.inventory.addIngredient(ingredient.copy());
            }
        }
    }

    /**
     * Creates a new CommandHistory instance.
     */
    public CommandHistory() {
        this.snapshots = new Stack<>();
    }

    /**
     * Saves a snapshot of the current RecipeBook and Inventory states.
     * Maintains a maximum of 50 snapshots, removing the oldest if limit is exceeded.
     *
     * @param recipeBook The recipe book to snapshot
     * @param inventory The inventory to snapshot
     */
    public void saveSnapshot(RecipeBook recipeBook, Inventory inventory) {
        if (snapshots.size() >= MAX_SNAPSHOTS) {
            // Remove the oldest snapshot (bottom of stack) to maintain the limit
            if (!snapshots.isEmpty()) {
                snapshots.remove(0);
            }
        }
        snapshots.push(new Snapshot(recipeBook, inventory));
    }

    /**
     * Checks if there are any snapshots to undo.
     *
     * @return true if undo is available, false otherwise
     */
    public boolean canUndo() {
        return !snapshots.isEmpty();
    }

    /**
     * Undoes the last command by restoring the previous state.
     *
     * @param recipeBook The recipe book to restore to
     * @param inventory The inventory to restore to
     * @return true if undo was successful, false if no snapshots available
     */
    public boolean undo(RecipeBook recipeBook, Inventory inventory) {
        if (!canUndo()) {
            return false;
        }

        Snapshot snapshot = snapshots.pop();

        // Clear and restore inventory
        inventory.clear();
        for (Ingredient ingredient : snapshot.inventory.getIngredients()) {
            inventory.addIngredient(ingredient.copy());
        }

        // Clear and restore recipe book
        recipeBook.clear();
        for (Recipe recipe : snapshot.recipeBook.getRecipes()) {
            ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients());
            ArrayList<String> steps = new ArrayList<>(recipe.getSteps());
            recipeBook.addRecipe(new Recipe(recipe.getName(), ingredients, steps, 
                    recipe.getTime(), recipe.getCalories()));
        }

        return true;
    }

    /**
     * Clears all snapshots from the history.
     */
    public void clear() {
        snapshots.clear();
    }
}
