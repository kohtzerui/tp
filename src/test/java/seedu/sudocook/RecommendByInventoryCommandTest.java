package seedu.sudocook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecommendByInventoryCommandTest {
    private RecipeBook recipes;
    private Inventory inventory;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipes = new RecipeBook();
        inventory = new Inventory();

        // Recipe 1: Mixue — needs Water 1L + Sugar 1mg
        ArrayList<Ingredient> mixueIngredients = new ArrayList<>();
        mixueIngredients.add(new Ingredient("Water", 1, "Liter"));
        mixueIngredients.add(new Ingredient("Sugar", 1, "mg"));
        ArrayList<String> mixueSteps = new ArrayList<>();
        mixueSteps.add("Pour MIXUE into pot");
        recipes.addRecipe(new Recipe("Mixue", mixueIngredients, mixueSteps, 5));

        // Recipe 2: Omelette — needs Egg 2pcs + Salt 1g
        ArrayList<Ingredient> omeletteIngredients = new ArrayList<>();
        omeletteIngredients.add(new Ingredient("Egg", 2, "pcs"));
        omeletteIngredients.add(new Ingredient("Salt", 1, "g"));
        ArrayList<String> omeletteSteps = new ArrayList<>();
        omeletteSteps.add("Beat eggs and fry");
        recipes.addRecipe(new Recipe("Omelette", omeletteIngredients, omeletteSteps, 10));

        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void execute_allIngredientsAvailable_printsRecipe() {
        inventory.addIngredient(new Ingredient("Water", 2, "Liter"));
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_emptyInventory_printsNoRecipes() {
        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void execute_insufficientQuantity_recipeExcluded() {
        // Water is enough but Sugar is not
        inventory.addIngredient(new Ingredient("Water", 2, "Liter"));
        inventory.addIngredient(new Ingredient("Sugar", 0.5, "mg"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void execute_duplicateIngredientRequirementsInsufficient_recipeExcluded() {
        ArrayList<Ingredient> doubleEggIngredients = new ArrayList<>();
        doubleEggIngredients.add(new Ingredient("Egg", 1, "pcs"));
        doubleEggIngredients.add(new Ingredient("egg", 1, "pcs"));
        ArrayList<String> doubleEggSteps = new ArrayList<>();
        doubleEggSteps.add("Cook");
        recipes.addRecipe(new Recipe("DoubleEgg", doubleEggIngredients, doubleEggSteps, 5));
        inventory.addIngredient(new Ingredient("egg", 1, "pcs"));
        output.reset();

        new RecommendByInventoryCommand().execute(inventory, recipes);

        String out = getOutput();
        assertTrue(!out.contains("DoubleEgg"));
    }

    @Test
    public void execute_missingOneIngredient_recipeExcluded() {
        // Has Water but no Sugar
        inventory.addIngredient(new Ingredient("Water", 2, "Liter"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void execute_multipleRecipes_onlyMakeableOnesShown() {
        // Inventory satisfies Mixue but not Omelette (no Salt)
        inventory.addIngredient(new Ingredient("Water", 2, "Liter"));
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));
        // Salt is missing

        new RecommendByInventoryCommand().execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Mixue"));
        assertTrue(!out.contains("Omelette"));
    }

    @Test
    public void execute_allRecipesMakeable_allShown() {
        inventory.addIngredient(new Ingredient("Water", 2, "Liter"));
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 5, "g"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Mixue"));
        assertTrue(out.contains("Omelette"));
    }

    @Test
    public void execute_exactQuantityMatch_recipeIncluded() {
        // Exactly meets Mixue requirements
        inventory.addIngredient(new Ingredient("Water", 1, "Liter"));
        inventory.addIngredient(new Ingredient("Sugar", 1, "mg"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_unitMismatch_recipeExcluded() {
        ArrayList<Ingredient> riceIngredients = new ArrayList<>();
        riceIngredients.add(new Ingredient("rice", 1, "grams"));
        ArrayList<String> riceSteps = new ArrayList<>();
        riceSteps.add("Cook");
        recipes.addRecipe(new Recipe("UnitCook", riceIngredients, riceSteps, 5, 100));
        inventory.addIngredient(new Ingredient("rice", 1, "cups"));
        output.reset();

        new RecommendByInventoryCommand().execute(inventory, recipes);

        String out = getOutput();
        assertTrue(!out.contains("UnitCook"));
        assertTrue(out.contains("No recipes"));
    }

    @Test
    public void parse_noArgs_returnsRecommendByInventoryCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r");

        assertInstanceOf(RecommendByInventoryCommand.class, cmd);
    }

    @Test
    public void parse_invalidArgs_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r invalid");

        assertTrue(cmd.getClass() == Command.class);
        assertTrue(getOutput().contains("Oops!"));
    }

    @Test
    public void execute_inventoryInLargerUnit_convertsAndRecommends() {
        // Mixue needs Water 1 Liter + Sugar 1 mg
        // Inventory: Water 2000 ml (= 2 L ≥ 1 L), Sugar 2 mg → should recommend Mixue
        inventory.addIngredient(new Ingredient("Water", 2000, "ml"));
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("Mixue"));
    }

    @Test
    public void execute_smallerUnitInsufficient_recipeExcluded() {
        // Mixue needs Water 1 Liter; Inventory has only 500 ml (< 1000 ml) → cannot make
        inventory.addIngredient(new Ingredient("Water", 500, "ml"));
        inventory.addIngredient(new Ingredient("Sugar", 2, "mg"));

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void execute_incompatibleUnit_recipeExcluded() {
        // Already-existing test style: recipe uses "grams", inventory has "cups" → excluded
        // (preserved to confirm incompatible family still returns false)
        ArrayList<Ingredient> riceIngredients = new ArrayList<>();
        riceIngredients.add(new Ingredient("rice", 1, "grams"));
        ArrayList<String> riceSteps = new ArrayList<>();
        riceSteps.add("Cook");
        recipes.addRecipe(new Recipe("UnitCook", riceIngredients, riceSteps, 5, 100));
        inventory.addIngredient(new Ingredient("rice", 1, "cups"));
        output.reset();

        new RecommendByInventoryCommand().execute(inventory, recipes);

        assertFalse(getOutput().contains("UnitCook"));
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
