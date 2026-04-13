package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UndoCommandTest {
    private RecipeBook recipeBook;
    private Inventory inventory;
    private CommandHistory history;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        recipeBook = new RecipeBook();
        inventory = new Inventory();
        history = new CommandHistory();
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

    // --- Test UndoCommand message output ---
    @Test
    public void execute_noHistory_printsError() {
        UndoCommand cmd = new UndoCommand();
        cmd.execute(history, recipeBook, inventory);
        assertTrue(getOutput().contains("No previous commands to undo"));
    }

    @Test
    public void execute_undoWithNullHistory_handlesGracefully() {
        UndoCommand cmd = new UndoCommand();
        cmd.execute(null, recipeBook, inventory);
        assertTrue(getOutput().contains("No previous commands to undo"));
    }

    @Test
    public void execute_successfulUndo_printsSuccess() {
        history.saveSnapshot(recipeBook, inventory);
        inventory.addIngredient(new Ingredient("Test", 1, "unit"));
        history.saveSnapshot(recipeBook, inventory);
        
        output.reset();
        UndoCommand cmd = new UndoCommand();
        cmd.execute(history, recipeBook, inventory);
        
        assertTrue(getOutput().contains("Last command undone successfully"));
    }

    // --- Test CommandHistory functionality ---
    @Test
    public void canUndo_emptyHistory_returnsFalse() {
        assertFalse(history.canUndo());
    }

    @Test
    public void canUndo_withSnapshot_returnsTrue() {
        history.saveSnapshot(recipeBook, inventory);
        assertTrue(history.canUndo());
    }

    @Test
    public void canUndo_afterUndo_returnsFalse() {
        history.saveSnapshot(recipeBook, inventory);
        history.undo(recipeBook, inventory);
        assertFalse(history.canUndo());
    }

    @Test
    public void saveSnapshot_multipleSnapshots_raisesCanUndo() {
        assertFalse(history.canUndo());
        
        history.saveSnapshot(recipeBook, inventory);
        assertTrue(history.canUndo());
        
        history.saveSnapshot(recipeBook, inventory);
        assertTrue(history.canUndo());
        
        history.saveSnapshot(recipeBook, inventory);
        assertTrue(history.canUndo());
    }

    // --- Test undo operation ---
    @Test
    public void undo_withSnapshot_returnsTrue() {
        history.saveSnapshot(recipeBook, inventory);
        boolean result = history.undo(recipeBook, inventory);
        assertTrue(result);
    }

    @Test
    public void undo_withoutSnapshot_returnsFalse() {
        boolean result = history.undo(recipeBook, inventory);
        assertFalse(result);
    }

    @Test
    public void undo_multipleSnapshots_removesLastSnapshot() {
        history.saveSnapshot(recipeBook, inventory);
        history.saveSnapshot(recipeBook, inventory);
        history.saveSnapshot(recipeBook, inventory);
        
        assertTrue(history.canUndo());
        history.undo(recipeBook, inventory);
        assertTrue(history.canUndo());
        history.undo(recipeBook, inventory);
        assertTrue(history.canUndo());
        history.undo(recipeBook, inventory);
        assertFalse(history.canUndo());
    }

    // --- Test history management ---
    @Test
    public void clear_removesAllHistory() {
        history.saveSnapshot(recipeBook, inventory);
        history.saveSnapshot(recipeBook, inventory);
        
        assertTrue(history.canUndo());
        history.clear();
        assertFalse(history.canUndo());
    }

    @Test
    public void saveSnapshot_maxSize_maintainsLimit() {
        // Save 51 snapshots (max is 50)
        for (int i = 0; i < 51; i++) {
            history.saveSnapshot(recipeBook, inventory);
        }
        
        // Should still work
        assertTrue(history.canUndo());
    }
}
