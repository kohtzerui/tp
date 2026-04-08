package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SortRecipeCommandTest {
    private RecipeBook recipeBook;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipeBook = new RecipeBook();
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Water", 1, "cup"));
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Cook");

        recipeBook.addRecipe(new Recipe("Zucchini Soup", ingredients, steps, 30, 300));
        recipeBook.addRecipe(new Recipe("Apple Pie", ingredients, steps, 60, 500));
        recipeBook.addRecipe(new Recipe("Mushroom Stir Fry", ingredients, steps, 15, 200));

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
    public void sortByName_sortsAlphabetically() {
        new SortRecipeCommand("n/").execute(recipeBook);
        ArrayList<Recipe> sorted = recipeBook.getRecipes();
        assertEquals("Apple Pie", sorted.get(0).getName());
        assertEquals("Mushroom Stir Fry", sorted.get(1).getName());
        assertEquals("Zucchini Soup", sorted.get(2).getName());
    }

    @Test
    public void sortByTime_sortsByAscendingTime() {
        new SortRecipeCommand("t/").execute(recipeBook);
        ArrayList<Recipe> sorted = recipeBook.getRecipes();
        assertEquals(15, sorted.get(0).getTime());
        assertEquals(30, sorted.get(1).getTime());
        assertEquals(60, sorted.get(2).getTime());
    }

    @Test
    public void sortByCalories_sortsByAscendingCalories() {
        new SortRecipeCommand("c/").execute(recipeBook);
        ArrayList<Recipe> sorted = recipeBook.getRecipes();
        assertEquals(200, sorted.get(0).getCalories());
        assertEquals(300, sorted.get(1).getCalories());
        assertEquals(500, sorted.get(2).getCalories());
    }

    @Test
    public void sortByName_emptyBook_doesNotThrowAndPrintsMessage() {
        RecipeBook emptyBook = new RecipeBook();
        assertDoesNotThrow(() -> new SortRecipeCommand("n/").execute(emptyBook));
        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void sortByInvalidCriteria_printsError() {
        new SortRecipeCommand("x/").execute(recipeBook);
        assertTrue(getOutput().contains("Unknown sort criteria"));
    }

    @Test
    public void parseSortR_validNameCriteria_returnsSortRecipeCommand() {
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd = parser.parse("sort-r n/");
        assertTrue(cmd instanceof SortRecipeCommand);
        assertEquals("n/", ((SortRecipeCommand) cmd).getCriteria());
    }

    @Test
    public void parseSortR_validTimeCriteria_returnsSortRecipeCommand() {
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd = parser.parse("sort-r t/");
        assertTrue(cmd instanceof SortRecipeCommand);
        assertEquals("t/", ((SortRecipeCommand) cmd).getCriteria());
    }

    @Test
    public void parseSortR_noCriteria_printsError() {
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        parser.parse("sort-r");
        assertTrue(getOutput().contains("Invalid sort-r format"));
    }
}
