package seedu.sudocook;

import java.util.ArrayList;

public class RecipeBook {
    private ArrayList<Recipe> recipes;

    public RecipeBook() {
        this.recipes = new ArrayList<>();
        /*Test Purpose*/
        ArrayList<String> testIngs = new ArrayList<>();
        ArrayList<String> testSteps = new ArrayList<>();
        testIngs.add("MIXUE");
        testIngs.add("Courage");
        testSteps.add("Pour MIXUE into pot");
        testSteps.add("Heat the pot");
        testSteps.add("Drink with your extraordinary courage");
        Recipe heatingMIXUE = new Recipe("Heated MIXUE",testIngs,testSteps);
        addRecipe("Mixue", testIngs, testSteps);
    }

    public void removeRecipe(int index) {
        recipes.remove(index - 1);
    }

    public void listRecipe() {
        for (Recipe recipe : recipes) {
            System.out.println(recipe);
        }
    }

    public void addRecipe(String name, ArrayList<String> ingredients, ArrayList<String> steps){
        Recipe newRecipe = new Recipe(name, ingredients, steps);
        recipes.add(newRecipe);
        Ui.printMessage("Added recipe:\n" + newRecipe.toString());
    }

    public void addRecipe(Recipe recipe){
        recipes.add(recipe);
        Ui.printMessage("Added recipe:\n" + recipe.toString());
    }

    public int size(){
        return recipes.size();
    }
}
