package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SearchRecipeCommandTest {
    private RecipeBook recipeBook;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipeBook = new RecipeBook();
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Rice", 2, "cups"));
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Cook rice");

        recipeBook.addRecipe(new Recipe("Fried Rice", ingredients, steps, 15, 400));
        recipeBook.addRecipe(new Recipe("Tomato Soup", ingredients, steps, 20, 200));

        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void execute_exactMatch_findsRecipe() {
        new SearchRecipeCommand("Fried Rice").execute(recipeBook);
        assertTrue(getOutput().contains("Fried Rice"));
    }

    @Test
    public void execute_partialMatch_findsRecipe() {
        new SearchRecipeCommand("fried").execute(recipeBook);
        assertTrue(getOutput().contains("Fried Rice"));
    }

    @Test
    public void execute_typo_findsRecipe() {
        new SearchRecipeCommand("Tomatto Soup").execute(recipeBook);
        assertTrue(getOutput().contains("Tomato Soup"));
    }

    @Test
    public void execute_caseInsensitive_findsRecipe() {
        new SearchRecipeCommand("TOMATO").execute(recipeBook);
        assertTrue(getOutput().contains("Tomato Soup"));
    }

    @Test
    public void execute_noMatch_printsNoResults() {
        new SearchRecipeCommand("xyz123").execute(recipeBook);
        assertTrue(getOutput().contains("No recipes matched"));
    }

    @Test
    public void execute_emptyBook_printsNoRecipes() {
        new SearchRecipeCommand("rice").execute(new RecipeBook());
        assertTrue(getOutput().contains("No recipes found"));
    }

    @Test
    public void execute_multipleMatches_exactMatchAppearsBeforePartial() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Rice", 1, "cup"));
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Cook");
        recipeBook.addRecipe(new Recipe("Rice", ingredients, steps, 5, 100));

        new SearchRecipeCommand("Rice").execute(recipeBook);
        String out = getOutput();
        // "Rice" (exact) should appear before "Fried Rice" (partial)
        assertTrue(out.indexOf("Rice\n") < out.indexOf("Fried Rice")
                || out.indexOf("3. Rice") < out.indexOf("1. Fried Rice"));
    }
}
