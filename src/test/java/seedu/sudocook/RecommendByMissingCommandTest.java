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

public class RecommendByMissingCommandTest {
    private RecipeBook recipes;
    private Inventory inventory;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipes = new RecipeBook();
        inventory = new Inventory();

        // Recipe 1: Omelette — needs Egg 2pcs + Salt 1g
        ArrayList<Ingredient> omeletteIngredients = new ArrayList<>();
        omeletteIngredients.add(new Ingredient("Egg", 2, "pcs"));
        omeletteIngredients.add(new Ingredient("Salt", 1, "g"));
        ArrayList<String> omeletteSteps = new ArrayList<>();
        omeletteSteps.add("Beat eggs and fry");
        recipes.addRecipe(new Recipe("Omelette", omeletteIngredients, omeletteSteps, 10));

        // Recipe 2: Pasta — needs Flour 200g + Egg 2pcs + Salt 5g
        ArrayList<Ingredient> pastaIngredients = new ArrayList<>();
        pastaIngredients.add(new Ingredient("Flour", 200, "g"));
        pastaIngredients.add(new Ingredient("Egg", 2, "pcs"));
        pastaIngredients.add(new Ingredient("Salt", 5, "g"));
        ArrayList<String> pastaSteps = new ArrayList<>();
        pastaSteps.add("Mix and boil");
        recipes.addRecipe(new Recipe("Pasta", pastaIngredients, pastaSteps, 30));

        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void execute_recipeFullyMakeable_excluded() {
        // Omelette is fully makeable, so missing/1 should NOT show it
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 2, "g"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        assertTrue(!getOutput().contains("Omelette"));
    }

    @Test
    public void execute_missingExactlyOne_recipeIncluded() {
        // Omelette needs Egg and Salt; inventory has Egg but not Salt
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        assertTrue(getOutput().contains("Omelette"));
    }

    @Test
    public void execute_missingMoreThanN_recipeExcluded() {
        // Omelette is missing 2 ingredients (Egg and Salt); missing/1 should exclude it
        new RecommendByMissingCommand(1).execute(inventory, recipes);

        assertTrue(!getOutput().contains("Omelette"));
    }

    @Test
    public void execute_showsShortfallQuantityAndUnit() {
        // Omelette needs Salt 1g; inventory has none, so shortfall is 1.0 g
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Salt"));
        assertTrue(out.contains("1.0"));
        assertTrue(out.contains("g"));
    }

    @Test
    public void execute_partialQuantity_showsCorrectShortfall() {
        // Omelette needs Egg 2pcs; inventory has 1pcs, so shortfall is 1.0 pcs
        inventory.addIngredient(new Ingredient("Egg", 1, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 2, "g"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Omelette"));
        assertTrue(out.contains("Egg"));
        assertTrue(out.contains("1.0"));
        assertTrue(out.contains("pcs"));
    }

    @Test
    public void execute_duplicateIngredientRequirements_showsAggregatedShortfall() {
        ArrayList<Ingredient> doubleEggIngredients = new ArrayList<>();
        doubleEggIngredients.add(new Ingredient("Egg", 1, "pcs"));
        doubleEggIngredients.add(new Ingredient("egg", 1, "pcs"));
        ArrayList<String> doubleEggSteps = new ArrayList<>();
        doubleEggSteps.add("Cook");
        recipes.addRecipe(new Recipe("DoubleEgg", doubleEggIngredients, doubleEggSteps, 5));
        inventory.addIngredient(new Ingredient("Egg", 1, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 2, "g"));
        output.reset();

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("DoubleEgg"));
        assertTrue(out.contains("Egg"));
        assertTrue(out.contains("1.0"));
        assertTrue(out.contains("pcs"));
    }

    @Test
    public void execute_noQualifyingRecipes_printsNoMatch() {
        // Empty inventory: Omelette missing 2, Pasta missing 3 — missing/0 finds nothing
        new RecommendByMissingCommand(0).execute(inventory, recipes);

        assertTrue(getOutput().contains("No recipes"));
    }

    @Test
    public void execute_multipleRecipes_onlyQualifyingOnesShown() {
        // Have Egg: Omelette missing only Salt (1), Pasta missing Flour+Salt (2)
        // missing/1 should show only Omelette
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Omelette"));
        assertTrue(!out.contains("Pasta"));
    }

    @Test
    public void execute_missingNEqualsTwo_showsBothRecipes() {
        // Have Egg: Omelette missing 1 (Salt), Pasta missing 2 (Flour + Salt)
        // missing/2 should show both
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));

        new RecommendByMissingCommand(2).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Omelette"));
        assertTrue(out.contains("Pasta"));
    }

    @Test
    public void parse_validMissingN_returnsRecommendByMissingCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r missing/2");

        assertInstanceOf(RecommendByMissingCommand.class, cmd);
    }

    @Test
    public void parse_zeroMissing_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r missing/0");

        assertTrue(cmd.getClass() == Command.class);
        assertTrue(getOutput().contains("Oops!"));
    }

    @Test
    public void parse_nonNumericMissing_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("recommend-r missing/abc");

        assertTrue(cmd.getClass() == Command.class);
        assertTrue(getOutput().contains("Oops!"));
    }

    @Test
    public void execute_largerUnitSufficient_notMissing() {
        // Omelette needs Salt 1 g; inventory has Salt 0.002 kg (= 2 g ≥ 1 g) → Salt not missing
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 0.002, "kg"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        // Omelette should not appear in missing/1 because it is fully makeable
        assertFalse(getOutput().contains("Omelette"));
    }

    @Test
    public void execute_smallerUnitInsufficient_showsCorrectShortfall() {
        // Omelette needs Salt 1 g; inventory has Salt 500 mg (= 0.5 g < 1 g) → shortfall 0.5 g
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 500, "mg"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Omelette"));
        assertTrue(out.contains("Salt"));
        // shortfall = 1 g - 0.5 g = 0.5 g
        assertTrue(out.contains("0.5"));
        assertTrue(out.contains("g"));
    }

    @Test
    public void execute_incompatibleUnit_treatedAsMissing() {
        // Omelette needs Salt 1 g; inventory has Salt 999 cups (incompatible) → treated as 0 available
        inventory.addIngredient(new Ingredient("Egg", 3, "pcs"));
        inventory.addIngredient(new Ingredient("Salt", 999, "cups"));

        new RecommendByMissingCommand(1).execute(inventory, recipes);

        String out = getOutput();
        assertTrue(out.contains("Omelette"));
        assertTrue(out.contains("Salt"));
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
