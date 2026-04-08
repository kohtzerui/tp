package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SearchIngredientCommandTest {
    private Inventory inventory;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Tomato", 3, "pcs"));
        inventory.addIngredient(new Ingredient("Chicken Breast", 500, "g"));

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
    public void execute_exactMatch_findsIngredient() {
        new SearchIngredientCommand("Tomato").execute(inventory);
        assertTrue(getOutput().contains("Tomato"));
    }

    @Test
    public void execute_partialMatch_findsIngredient() {
        new SearchIngredientCommand("chicken").execute(inventory);
        assertTrue(getOutput().contains("Chicken Breast"));
    }

    @Test
    public void execute_typo_findsIngredient() {
        new SearchIngredientCommand("Tomatto").execute(inventory);
        assertTrue(getOutput().contains("Tomato"));
    }

    @Test
    public void execute_caseInsensitive_findsIngredient() {
        new SearchIngredientCommand("TOMATO").execute(inventory);
        assertTrue(getOutput().contains("Tomato"));
    }

    @Test
    public void execute_noMatch_printsNoResults() {
        new SearchIngredientCommand("xyz123").execute(inventory);
        assertTrue(getOutput().contains("No ingredients matched"));
    }

    @Test
    public void execute_emptyInventory_printsNoIngredients() {
        new SearchIngredientCommand("tomato").execute(new Inventory());
        assertTrue(getOutput().contains("No ingredients found"));
    }

    @Test
    public void execute_multipleMatches_exactMatchAppearsBeforePartial() {
        inventory.addIngredient(new Ingredient("Tomato Paste", 2, "tbsp"));

        new SearchIngredientCommand("Tomato").execute(inventory);
        String out = getOutput();
        assertTrue(out.contains("Found 2 ingredient(s)"));
        // exact "Tomato" should rank above "Tomato Paste"
        assertTrue(out.indexOf("Tomato\n") < out.indexOf("Tomato Paste")
                || out.indexOf("Tomato ") < out.indexOf("Tomato Paste"));
    }
}
