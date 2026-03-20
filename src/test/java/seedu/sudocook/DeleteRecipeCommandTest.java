package seedu.sudocook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteRecipeCommandTest {
    private RecipeBook recipeBook;
    private Recipe testRecipe;

    @BeforeEach
    public void setUp() {
        recipeBook = new RecipeBook();
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Water", 1, "Liter"));
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Boil it.");

        testRecipe = new Recipe("Boiled Water", ingredients, steps, 5);
        recipeBook.addRecipe(testRecipe);
    }

    @Test
    public void execute_validIndex_recipeRemoved() {

        assertEquals(2, recipeBook.size());

        DeleteRecipeCommand deleteCommand = new DeleteRecipeCommand(1);
        deleteCommand.execute(recipeBook);

        assertEquals(1, recipeBook.size());
    }

    @Test
    public void execute_invalidIndex_recipeBookUnchanged() {
        int sizeBefore = recipeBook.size();

        DeleteRecipeCommand deleteCommand = new DeleteRecipeCommand(100);
        deleteCommand.execute(recipeBook);  // 不再抛异常，内部处理

        assertEquals(sizeBefore, recipeBook.size());  // 大小不变
    }

    @Test
    public void execute_zeroIndex_recipeBookUnchanged() {
        int sizeBefore = recipeBook.size();

        DeleteRecipeCommand deleteCommand = new DeleteRecipeCommand(0);
        deleteCommand.execute(recipeBook);

        assertEquals(sizeBefore, recipeBook.size());
    }
}
