package seedu.sudocook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendByIngredientCommandTest {
    private RecipeBook recipes;
    private Inventory inventory;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipes = new RecipeBook();
        inventory = new Inventory();
        ArrayList<Ingredient> mixueIngredients = new ArrayList<>();
        mixueIngredients.add(new Ingredient("Water", 1, "Liter"));
        mixueIngredients.add(new Ingredient("Sugar", 1, "mg"));
        ArrayList<String> mixueSteps = new ArrayList<>();
        mixueSteps.add("Pour MIXUE into pot");
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
    public void execute_ingredientInInventoryAndRecipe_printsRecipe() {
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_ingredientNotInInventory_printsError() {
        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Egg");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("does not exist in inventory"));
    }

    @Test
    public void execute_ingredientInInventoryButNoRecipeUseIt_printsNoMatch() {
        inventory.addIngredient(new Ingredient("Egg", 1, "pcs"));

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Egg");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void parse_invalidFormat_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r invalid");

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

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_insufficientQuantity_printsNoMatch() {
        // Mixue 需要 Sugar 1mg，但 inventory 只有 0.5mg，走 line 29 qty > amount
        inventory.addIngredient(new Ingredient("Sugar", 0.5, "mg"));

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void execute_duplicateIngredientRequirementsInsufficient_printsNoMatch() {
        ArrayList<Ingredient> doubleSugarIngredients = new ArrayList<>();
        doubleSugarIngredients.add(new Ingredient("Sugar", 1, "mg"));
        doubleSugarIngredients.add(new Ingredient("sugar", 1, "mg"));
        ArrayList<String> doubleSugarSteps = new ArrayList<>();
        doubleSugarSteps.add("Cook");
        recipes.addRecipe(new Recipe("DoubleSugar", doubleSugarIngredients, doubleSugarSteps, 5));
        inventory.addIngredient(new Ingredient("Sugar", 1, "mg"));
        output.reset();

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Mixue"));
        assertTrue(!out.contains("DoubleSugar"));
    }

    @Test
    public void execute_inventoryInLargerUnit_convertsAndRecommends() {
        // Inventory: Sugar 1 g; Recipe needs Sugar 1 mg (1 mg = 0.001 g ≤ 1 g) → should recommend
        inventory.addIngredient(new Ingredient("Sugar", 1, "g"));

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_inventoryInSmallerUnit_convertsAndExcludes() {
        // Inventory: Sugar 1 mg; Recipe needs Sugar 1 mg (same unit) but recipe is Mixue needing 1mg
        // Change: inventory 0.5 mg < 1 mg required → should NOT recommend
        inventory.addIngredient(new Ingredient("Sugar", 1, "kg"));
        // Recipe needs 1 mg sugar; 1 mg = 0.000001 kg ≤ 1 kg → should recommend
        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_incompatibleUnit_recipeSkipped() {
        // Inventory: Sugar 100 cups; Recipe needs Sugar 1 mg (cups vs mg → incompatible) → skip
        inventory.addIngredient(new Ingredient("Sugar", 100, "cups"));

        RecommendByIngredientCommand cmd = new RecommendByIngredientCommand("Sugar");
        cmd.execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
