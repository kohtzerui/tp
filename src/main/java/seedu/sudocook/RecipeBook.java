package seedu.sudocook;

import java.util.ArrayList;

public class RecipeBook {
    private ArrayList<Recipe> recipes;

    public RecipeBook() {
        this.recipes = new ArrayList<>();
    }

    public void removeRecipe(int index) {
        if (recipes.isEmpty()) {
            throw new IndexOutOfBoundsException("The recipe book is currently empty.");
        }
        if (index < 1 || index > recipes.size()) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is out of range. (Valid range: 1 to " + recipes.size() + ")"
            );
        }
        recipes.remove(index - 1);
    }

    public Recipe getRecipe(int i){

        try {
            return recipes.get(i);
        } catch (IndexOutOfBoundsException e) {
            Ui.printError("Index out of bounds");
            return null;
        }
    }

    public void listRecipe() {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recipes.size(); i++) {
            sb.append(i + 1).append(". ").append(recipes.get(i).getName());
            if (i < recipes.size() - 1) {
                sb.append("\n");
            }
        }
        Ui.printGradientMessage(sb.toString());
    }

    public void viewRecipe() {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recipes.size(); i++) {
            sb.append(i + 1).append(". ").append(recipes.get(i).toString().stripLeading());
            if (i < recipes.size() - 1) {
                sb.append("\n");
            }
        }
        Ui.printGradientMessage(sb.toString());
    }

    public void viewRecipe(int index) {
        if (recipes.isEmpty()) {
            Ui.printError("The recipe book is currently empty.");
            return;
        }
        if (index < 1 || index > recipes.size()) {
            Ui.printError("Index " + index + " is out of range. (Valid range: 1 to " + recipes.size() + ")");
            return;
        }
        Ui.printGradientMessage(recipes.get(index - 1).toString().stripLeading());
    }

    public void addRecipe(String name, ArrayList<Ingredient> ingredients,
            ArrayList<String> steps, int time, int calories) {
        Recipe newRecipe = new Recipe(name, ingredients, steps, time, calories);
        recipes.add(newRecipe);
        Ui.printGradientMessage("Added recipe:\n" + newRecipe.toString());
    }

    public void addRecipe(Recipe recipe){
        recipes.add(recipe);
        Ui.printGradientMessage("Added recipe:\n" + recipe.toString());
    }

    public void filterRecipes(Integer maxTime, Integer maxCalories) {
        ArrayList<Recipe> filtered = new ArrayList<>();
        for (Recipe r : recipes) {
            boolean keep = true;
            if (maxTime != null && r.getTime() > maxTime) {
                keep = false;
            }
            if (maxCalories != null && r.getCalories() > maxCalories) {
                keep = false;
            }
            if (keep) {
                filtered.add(r);
            }
        }
        
        if (filtered.isEmpty()) {
            Ui.printMessage("No recipes found matching the criteria.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filtered.size(); i++) {
            sb.append(i + 1).append(". ").append(filtered.get(i).toString().stripLeading());
            if (i < filtered.size() - 1) {
                sb.append("\n");
            }
        }
        Ui.printGradientMessage(sb.toString());
    }

    public void searchRecipes(String query) {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < recipes.size(); i++) {
            if (FuzzySearch.isMatch(query, recipes.get(i).getName())) {
                count++;
                sb.append(i + 1).append(". ").append(recipes.get(i).getName()).append("\n");
            }
        }
        if (count == 0) {
            Ui.printMessage("No recipes matched \"" + query + "\".");
        } else {
            Ui.printGradientMessage("Found " + count + " recipe(s) matching \""
                    + query + "\":\n" + sb.toString().stripTrailing());
        }
    }

    public int size(){
        return recipes.size();
    }

    /**
     * Returns a copy of the recipes list for external use.
     *
     * @return ArrayList of recipes
     */
    public ArrayList<Recipe> getRecipes() {
        return new ArrayList<>(recipes);
    }
}
