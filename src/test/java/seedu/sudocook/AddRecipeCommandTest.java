package seedu.sudocook;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


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

        AddRecipeCommand test = new AddRecipeCommand("Zhajiangmian", ingredients, steps, 10);
        test.execute(testRecipeBook);

        assertEquals(2, testRecipeBook.size());
    }

    @Test
    public void formatErrorTest() {
        String testCmd = "add-r Gibberish Gibberish";
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd;
        cmd = parser.parse(testCmd);
        cmd.execute(testRecipeBook);
        assertEquals(1, testRecipeBook.size());

    }

    @Test
    public void parserTest() {
        String testCmd = "add-r {Fried Rice} i/rice 2 cups egg " +
                "2 pcs soy_sauce 1 tbsp s/{Cook the rice.} {Scramble the eggs.} {Mix everything together.} t/15";
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd;
        cmd = parser.parse(testCmd);
        cmd.execute(testRecipeBook);
        assertEquals(2, testRecipeBook.size());

    }


}
