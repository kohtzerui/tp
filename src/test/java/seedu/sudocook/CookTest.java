package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CookTest {
    private RecipeBook recipes;
    private Inventory ingredients;
    private Parser parser;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipes = new RecipeBook();
        ingredients = new Inventory();
        parser = new Parser(new Ui());
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void execute_insufficientIngredients_printsErrorAndLeavesInventoryUntouched() {
        ingredients.addIngredient(new Ingredient("Water", 1, "Liter"));
        ingredients.addIngredient(new Ingredient("Sugar", 0.5, "mg"));

        Command cmd = parser.parse("cook 1");
        executeCookCommand(cmd);

        assertEquals(2, ingredients.size());
        assertEquals(1.0, ingredients.getIngredient(ingredients.findIndexByName("Water")).getQuantity());
        assertEquals(0.5, ingredients.getIngredient(ingredients.findIndexByName("Sugar")).getQuantity());
        assertTrue(getOutput().contains("Oops! Not enough ingredients"));
    }

    @Test
    public void parse_incorrectFormat_returnsNoOpCommand() {
        Command cmd = parser.parse("cook s");

        assertEquals(Command.class, cmd.getClass());
        assertTrue(getOutput().contains("Oops! You should indicate the index of the recipe when cooking!"));
    }

    @Test
    public void execute_indexOutOfBounds_printsErrorAndDoesNotChangeInventory() {
        ingredients.addIngredient(new Ingredient("Water", 1, "Liter"));

        Command cmd = parser.parse("cook 2");
        executeCookCommand(cmd);

        assertEquals(1, ingredients.size());
        assertEquals(1.0, ingredients.getIngredient(0).getQuantity());
        assertTrue(getOutput().contains("Oops! Index out of bounds"));
    }

    @Test
    public void execute_validCook_consumesRecipeIngredients() {
        ingredients.addIngredient(new Ingredient("Water", 1, "Liter"));
        ingredients.addIngredient(new Ingredient("Sugar", 1, "mg"));

        Command cmd = parser.parse("cook 1");
        executeCookCommand(cmd);

        assertEquals(0, ingredients.size());
    }

    private void executeCookCommand(Command cmd) {
        cmd.execute(recipes.getRecipe(cmd.getIndex()), ingredients);
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
