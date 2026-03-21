package seedu.sudocook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendRecipeCommandTest {
    private RecipeBook recipes;
    private Inventory inventory;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipes = new RecipeBook();
        inventory = new Inventory();
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void execute_ingredientInInventoryAndRecipe_printsRecipe() {
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));

        RecommendRecipeCommand cmd = new RecommendRecipeCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_ingredientNotInInventory_printsError() {
        RecommendRecipeCommand cmd = new RecommendRecipeCommand("Egg");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("does not exist in inventory"));
    }

    @Test
    public void execute_ingredientInInventoryButNoRecipeUseIt_printsNoMatch() {
        inventory.addIngredient(new Ingredient("Egg", 1, "pcs"));

        RecommendRecipeCommand cmd = new RecommendRecipeCommand("Egg");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void parse_invalidFormat_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r");

        assertSame(Command.class, cmd.getClass());
        assertTrue(getOutput().contains("Oops!"));
    }

    @Test
    public void parse_emptyIngredientName_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r n/");

        assertSame(Command.class, cmd.getClass());
        assertTrue(getOutput().contains("Oops!"));
    }

    @Test
    public void execute_inventoryHasMultipleIngredients_findsCorrectOne() {
        inventory.addIngredient(new Ingredient("Salt", 1, "g"));   // 不匹配，走 line 16 false
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg")); // 匹配

        RecommendRecipeCommand cmd = new RecommendRecipeCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_insufficientQuantity_printsNoMatch() {
        // Mixue 需要 Sugar 1mg，但 inventory 只有 0.5mg，走 line 29 qty > amount
        inventory.addIngredient(new Ingredient("Sugar", 0.5, "mg"));

        RecommendRecipeCommand cmd = new RecommendRecipeCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
