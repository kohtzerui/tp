package seedu.sudocook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteCommandTest {
    private RecipeBook recipeBook;
    private Recipe testRecipe;

    @BeforeEach
    public void setUp() {
        recipeBook = new RecipeBook();
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("Water");
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Boil it.");

        testRecipe = new Recipe("Boiled Water", ingredients, steps);
        recipeBook.addRecipe(testRecipe);
    }

    @Test
    public void execute_validIndex_recipeRemoved() {

        assertEquals(2, recipeBook.size());

        DeleteCommand deleteCommand = new DeleteCommand(1);
        deleteCommand.execute(recipeBook);

        assertEquals(1, recipeBook.size());
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        DeleteCommand deleteCommand = new DeleteCommand(100);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            deleteCommand.execute(recipeBook);
        });
    }
}