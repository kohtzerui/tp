package seedu.sudocook;

import java.util.ArrayList;
import java.util.Comparator;

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
            throw new IndexOutOfBoundsException("Index " + index
                    + " is out of range. (Valid range: 1 to " + recipes.size() + ")");
        }
        recipes.remove(index - 1);
    }

    public Recipe getRecipe(int index){

        try {
            return recipes.get(index);
        } catch (IndexOutOfBoundsException exception) {
            Ui.printError("Index out of bounds");
            return null;
        }
    }

    public void listRecipe() {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes found.");
            return;
        }
        StringBuilder recipeListBuilder = new StringBuilder();
        for (int i = 0; i < recipes.size(); i++) {
            recipeListBuilder.append(i + 1).append(". ").append(recipes.get(i).getName());
            if (i < recipes.size() - 1) {
                recipeListBuilder.append("\n");
            }
        }
        Ui.printGradientMessage(recipeListBuilder.toString());
    }

    public void viewRecipe() {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes found.");
            return;
        }
        StringBuilder recipeListBuilder = new StringBuilder();
        for (int i = 0; i < recipes.size(); i++) {
            recipeListBuilder.append(i + 1).append(". ").append(recipes.get(i).toString().stripLeading());
            if (i < recipes.size() - 1) {
                recipeListBuilder.append("\n");
            }
        }
        Ui.printGradientMessage(recipeListBuilder.toString());
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
        ArrayList<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            boolean shouldKeepRecipe = true;
            if (maxTime != null && recipe.getTime() > maxTime) {
                shouldKeepRecipe = false;
            }
            if (maxCalories != null && recipe.getCalories() > maxCalories) {
                shouldKeepRecipe = false;
            }
            if (shouldKeepRecipe) {
                filteredRecipes.add(recipe);
            }
        }
        
        if (filteredRecipes.isEmpty()) {
            Ui.printMessage("No recipes found matching the criteria.");
            return;
        }
        StringBuilder filteredRecipesBuilder = new StringBuilder();
        for (int i = 0; i < filteredRecipes.size(); i++) {
            filteredRecipesBuilder.append(i + 1).append(". ")
                    .append(filteredRecipes.get(i).toString().stripLeading());
            if (i < filteredRecipes.size() - 1) {
                filteredRecipesBuilder.append("\n");
            }
        }
        Ui.printGradientMessage(filteredRecipesBuilder.toString());
    }

    public void searchRecipes(String query) {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes found.");
            return;
        }
        ArrayList<String> names = new ArrayList<>();
        for (Recipe recipe : recipes) {
            names.add(recipe.getName());
        }
        ArrayList<Integer> rankedIndices = FuzzySearch.rankMatchIndices(query, names);
        if (rankedIndices.isEmpty()) {
            Ui.printMessage("No recipes matched \"" + query + "\".");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rankedIndices.size(); i++) {
            int idx = rankedIndices.get(i);
            sb.append(idx + 1).append(". ").append(recipes.get(idx).getName());
            if (i < rankedIndices.size() - 1) {
                sb.append("\n");
            }
        }
        Ui.printGradientMessage("Found " + rankedIndices.size() + " recipe(s) matching \""
                + query + "\":\n" + sb.toString());
    }

    public void sortRecipes(String criteria) {
        if (recipes.isEmpty()) {
            Ui.printMessage("No recipes to sort.");
            return;
        }
        switch (criteria) {
        case "n/":
            recipes.sort(Comparator.comparing(r -> r.getName().toLowerCase()));
            break;
        case "t/":
            recipes.sort(Comparator.comparingInt(Recipe::getTime));
            break;
        case "c/":
            recipes.sort(Comparator.comparingInt(Recipe::getCalories));
            break;
        default:
            Ui.printError("Unknown sort criteria. Use: sort-r n/ | t/ | c/");
            return;
        }
        listRecipe();
    }

    public int getSize(){
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
