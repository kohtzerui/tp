package seedu.sudocook;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AddRecipeCommandTest {
    private RecipeBook testRecipeBook = new RecipeBook();


    @Test
    public void addCommandTest() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Water", 1, "cup"));
        ingredients.add(new Ingredient("Noodles", 2, "packets"));

        ArrayList<String> steps = new ArrayList<>();
        steps.add("Add water");
        steps.add("Add noodles");
        steps.add("Add cucumber");

        AddRecipeCommand test = new AddRecipeCommand("Zhajiangmian", ingredients, steps, 10, 350);
        test.execute(testRecipeBook);

        assertEquals(1, testRecipeBook.getSize());
    }

    @Test
    public void addCommand_existingNameCaseInsensitive_overwritesOriginalRecipe() {
        ArrayList<Ingredient> originalIngredients = new ArrayList<>();
        originalIngredients.add(new Ingredient("Rice", 2, "cups"));
        ArrayList<String> originalSteps = new ArrayList<>();
        originalSteps.add("Cook rice");

        AddRecipeCommand original = new AddRecipeCommand("Fried Rice", originalIngredients, originalSteps, 15, 400);
        original.execute(testRecipeBook);

        ArrayList<Ingredient> replacementIngredients = new ArrayList<>();
        replacementIngredients.add(new Ingredient("Noodles", 1, "packet"));
        ArrayList<String> replacementSteps = new ArrayList<>();
        replacementSteps.add("Boil noodles");

        AddRecipeCommand replacement = new AddRecipeCommand("fried rice", replacementIngredients, replacementSteps,
                5, 350);
        replacement.execute(testRecipeBook);

        Recipe savedRecipe = testRecipeBook.getRecipe(0);
        assertEquals(1, testRecipeBook.getSize());
        assertEquals("fried rice", savedRecipe.getName());
        assertEquals("Noodles", savedRecipe.getIngredients().get(0).getName());
        assertEquals("Boil noodles", savedRecipe.getSteps().get(0));
        assertEquals(5, savedRecipe.getTime());
        assertEquals(350, savedRecipe.getCalories());
    }

    @Test
    public void formatErrorTest() {
        String testCmd = "add-r Gibberish Gibberish";
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd;
        cmd = parser.parse(testCmd);
        cmd.execute(testRecipeBook);
        assertEquals(0, testRecipeBook.getSize());

    }

    @Test
    public void parserTest() {
        String testCmd = "add-r {Fried Rice} i/rice 2 cups egg " +
                "2 pcs soy_sauce 1 tbsp s/{Cook the rice.} {Scramble the eggs.} {Mix everything together.} t/15 c/400";
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd;
        cmd = parser.parse(testCmd);
        cmd.execute(testRecipeBook);
        assertEquals(1, testRecipeBook.getSize());

    }

    @Test
    public void parserTest_emptyRecipeName_rejected() {
        assertInvalidRecipeText("add-r {} i/salt 1 g s/{Add salt.} t/10 c/100");
    }

    @Test
    public void parserTest_emptyRecipeStep_rejected() {
        assertInvalidRecipeText("add-r {Salt Mix} i/salt 1 g s/{} t/10 c/100");
    }

    @Test
    public void parserTest_zeroIngredientQuantity_rejected() {
        assertInvalidIngredientQuantity("add-r {Fried Rice} i/rice 0 cups egg 2 pcs "
                + "s/{Cook the rice.} {Scramble the eggs.} t/15 c/400");
    }

    @Test
    public void parserTest_negativeIngredientQuantity_rejected() {
        assertInvalidIngredientQuantity("add-r {Fried Rice} i/rice -1 cups egg 2 pcs "
                + "s/{Cook the rice.} {Scramble the eggs.} t/15 c/400");
    }

    @Test
    public void parserTest_negativeTime_rejected() {
        assertInvalidTime("add-r {Fried Rice} i/rice 2 cups egg 2 pcs "
                + "s/{Cook the rice.} {Scramble the eggs.} t/-15 c/400");
    }

    @Test
    public void parserTest_negativeCalories_rejected() {
        assertInvalidCalories("add-r {Fried Rice} i/rice 2 cups egg 2 pcs "
                + "s/{Cook the rice.} {Scramble the eggs.} t/15 c/-400");
    }

    @Test
    public void parserTest_zeroCalories_rejected() {
        assertInvalidCalories("add-r {Fried Rice} i/rice 2 cups egg 2 pcs "
                + "s/{Cook the rice.} {Scramble the eggs.} t/15 c/0");
    }

    private void assertInvalidTime(String testCmd) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try {
            Ui ui = new Ui();
            Parser parser = new Parser(ui);
            Command cmd = parser.parse(testCmd);
            cmd.execute(testRecipeBook);

            assertEquals(0, testRecipeBook.getSize());
            assertTrue(output.toString(StandardCharsets.UTF_8)
                    .contains("Oops! Time cannot be negative."));
        } finally {
            System.setOut(originalOut);
        }
    }

    private void assertInvalidCalories(String testCmd) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try {
            Ui ui = new Ui();
            Parser parser = new Parser(ui);
            Command cmd = parser.parse(testCmd);
            cmd.execute(testRecipeBook);

            assertEquals(0, testRecipeBook.getSize());
            assertTrue(output.toString(StandardCharsets.UTF_8)
                    .contains("Oops! Calories must be a positive number. A meal cannot have 0 or negative calories."));
        } finally {
            System.setOut(originalOut);
        }
    }

    private void assertInvalidIngredientQuantity(String testCmd) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try {
            Ui ui = new Ui();
            Parser parser = new Parser(ui);
            Command cmd = parser.parse(testCmd);
            cmd.execute(testRecipeBook);

            assertEquals(0, testRecipeBook.getSize());
            assertTrue(output.toString(StandardCharsets.UTF_8)
                    .contains("Oops! Invalid ingredient quantity in add-r format."));
        } finally {
            System.setOut(originalOut);
        }
    }

    private void assertInvalidRecipeText(String testCmd) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try {
            Ui ui = new Ui();
            Parser parser = new Parser(ui);
            Command cmd = parser.parse(testCmd);
            cmd.execute(testRecipeBook);

            assertEquals(0, testRecipeBook.getSize());
            assertTrue(output.toString(StandardCharsets.UTF_8)
                    .contains("Oops! Recipe name and steps cannot be empty."));
        } finally {
            System.setOut(originalOut);
        }
    }


}
