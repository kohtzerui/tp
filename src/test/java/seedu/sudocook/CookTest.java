package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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
        ArrayList<Ingredient> mixueIngredients = new ArrayList<>();
        mixueIngredients.add(new Ingredient("Water", 1, "Liter"));
        mixueIngredients.add(new Ingredient("Sugar", 1, "mg"));
        ArrayList<String> mixueSteps = new ArrayList<>();
        mixueSteps.add("Pour MIXUE into pot");
        mixueSteps.add("Heat the pot");
        mixueSteps.add("Drink with your extraordinary courage");
        recipes.addRecipe(new Recipe("Mixue", mixueIngredients, mixueSteps, 5));
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

        assertEquals(2, ingredients.getSize());
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
    public void parse_missingIndex_returnsNoOpCommand() {
        Command cmd = parser.parse("cook");

        assertEquals(Command.class, cmd.getClass());
        assertTrue(getOutput().contains("Oops! You should indicate the index of the recipe when cooking!"));
    }

    @Test
    public void parse_prefixedKeyword_returnsNoOpCommand() {
        Command cmd = parser.parse("cookbook 1");

        assertEquals(Command.class, cmd.getClass());
        assertTrue(getOutput().contains("Oops! I don't recognise that command!"));
    }

    @Test
    public void execute_indexOutOfBounds_printsErrorAndDoesNotChangeInventory() {
        ingredients.addIngredient(new Ingredient("Water", 1, "Liter"));

        Command cmd = parser.parse("cook 2");
        executeCookCommand(cmd);

        assertEquals(1, ingredients.getSize());
        assertEquals(1.0, ingredients.getIngredient(0).getQuantity());
        assertTrue(getOutput().contains("Oops! Index out of bounds"));
    }

    @Test
    public void execute_validCook_consumesRecipeIngredients() {
        ingredients.addIngredient(new Ingredient("Water", 1, "Liter"));
        ingredients.addIngredient(new Ingredient("Sugar", 1, "mg"));

        Command cmd = parser.parse("cook 1");
        executeCookCommand(cmd);

        assertEquals(0, ingredients.getSize());
    }

    @Test
    public void execute_validCookWithMultipleExpiries_consumesEarliestExpiryFirst() {
        ingredients.addIngredient(new Ingredient("Water", 0.5, "Liter", LocalDate.of(2026, 4, 1)));
        ingredients.addIngredient(new Ingredient("Water", 1, "Liter", LocalDate.of(2026, 5, 1)));
        ingredients.addIngredient(new Ingredient("Sugar", 1, "mg"));

        Command cmd = parser.parse("cook 1");
        executeCookCommand(cmd);

        Ingredient water = ingredients.getIngredient(ingredients.findIndexByName("Water"));
        assertEquals(1, ingredients.getSize());
        assertEquals(0.5, water.getQuantity());
        assertEquals(1, water.getExpiryQuantities().size());
        assertEquals(LocalDate.of(2026, 5, 1), water.getExpiryQuantities().get(0).getExpiryDate());
    }

    private void executeCookCommand(Command cmd) {
        cmd.execute(recipes.getRecipe(cmd.getIndex()), ingredients);
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
