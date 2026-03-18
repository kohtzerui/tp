package seedu.sudocook;

import java.util.ArrayList;

public class RecipeBook {
    private ArrayList<Recipe> recipes;

    public RecipeBook() {
        this.recipes = new ArrayList<>();
        /*Test Purpose*/
        ArrayList<Ingredient> testIngs = new ArrayList<>();
        ArrayList<String> testSteps = new ArrayList<>();
        testIngs.add(new Ingredient("Water", 1, "Liter"));
        testIngs.add(new Ingredient("Sugar", 1, "mg"));
        testSteps.add("Pour MIXUE into pot");
        testSteps.add("Heat the pot");
        testSteps.add("Drink with your extraordinary courage");
        recipes.add(new Recipe("Mixue", testIngs, testSteps));
    }

    public void removeRecipe(int index) {
        if (index < 1 || index > recipes.size()) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is out of range. Valid range: 1 to " + recipes.size()
            );
        }
        recipes.remove(index - 1);
    }

    public void listRecipe() {
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

    public void addRecipe(String name, ArrayList<Ingredient> ingredients, ArrayList<String> steps){
        Recipe newRecipe = new Recipe(name, ingredients, steps);
        recipes.add(newRecipe);
        Ui.printGradientMessage("Added recipe:\n" + newRecipe.toString());
    }

    public void addRecipe(Recipe recipe){
        recipes.add(recipe);
        Ui.printGradientMessage("Added recipe:\n" + recipe.toString());
    }

    public int size(){
        return recipes.size();
    }
}
