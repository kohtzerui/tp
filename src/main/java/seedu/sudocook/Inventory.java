package seedu.sudocook;

import java.util.ArrayList;

/**
 * Manages the inventory of ingredients.
 */
public class Inventory {
    private final ArrayList<Ingredient> ingredients;

    public Inventory() {
        this.ingredients = new ArrayList<>();
    }

    /**
     * Adds an ingredient to the inventory.
     * If an ingredient with the same name and unit already exists, updates the
     * quantity.
     *
     * @param ingredient The ingredient to add
     */
    public void addIngredient(Ingredient ingredient) {
        for (Ingredient existing : ingredients) {
            if (existing.getName().equalsIgnoreCase(ingredient.getName())
                    && existing.getUnit().equalsIgnoreCase(ingredient.getUnit())) {
                existing.setQuantity(existing.getQuantity() + ingredient.getQuantity());
                return;
            }
        }
        ingredients.add(ingredient);
    }

    /**
     * Returns a copy of the ingredients list.
     *
     * @return ArrayList of ingredients
     */
    public ArrayList<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    /**
     * Returns the number of ingredients in the inventory.
     *
     * @return The number of ingredients
     */
    public int size() {
        return ingredients.size();
    }

    public void removeIngredient(int index) {
        ingredients.remove(index);
    }

    public void updateQuantity(int index, double quantityToRemove) {
        Ingredient ingredient = ingredients.get(index);
        double newQuantity = ingredient.getQuantity() - quantityToRemove;
        ingredient.setQuantity(newQuantity);
    }

    public Ingredient getIngredient(int index) {
        return ingredients.get(index);
    }

    public int findIndexByName(String name) {
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
}
