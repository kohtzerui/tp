package seedu.sudocook;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryTest {
    private Inventory testInventory = new Inventory();

    @Test
    public void addIngredient_newIngredient_increasesSize() {
        testInventory.addIngredient(new Ingredient("Sugar", 100, "g"));
        assertEquals(1, testInventory.getSize());
    }

    @Test
    public void addIngredient_sameNameAndUnit_mergesQuantity() {
        testInventory.addIngredient(new Ingredient("Sugar", 100, "g"));
        testInventory.addIngredient(new Ingredient("Sugar", 50, "g"));
        assertEquals(1, testInventory.getSize());
        assertEquals(150, testInventory.getIngredient(0).getQuantity());
    }

    @Test
    public void updateQuantity_multipleExpiries_deductsEarliestExpiryFirst() {
        testInventory.addIngredient(new Ingredient("Milk", 1, "carton", LocalDate.of(2026, 4, 1)));
        testInventory.addIngredient(new Ingredient("Milk", 2, "carton", LocalDate.of(2026, 5, 1)));

        testInventory.updateQuantity(0, 1.5);

        Ingredient ingredient = testInventory.getIngredient(0);
        assertEquals(1, testInventory.getSize());
        assertEquals(1.5, ingredient.getQuantity());
        assertEquals(1, ingredient.getExpiryQuantities().size());
        assertEquals(LocalDate.of(2026, 5, 1), ingredient.getExpiryQuantities().get(0).getExpiryDate());
        assertEquals(1.5, ingredient.getExpiryQuantities().get(0).getQuantity());
    }

    @Test
    public void removeIngredient_validIndex_removesCorrectly() {
        testInventory.addIngredient(new Ingredient("Salt", 10, "g"));
        testInventory.removeIngredient(0);
        assertEquals(0, testInventory.getSize());
    }

    @Test
    public void findIndexByName_missingIngredient_returnsNegativeOne() {
        assertEquals(-1, testInventory.findIndexByName("Garlic"));
    }

    @Test
    public void parserTest_invalidFormat_doesNotAdd() {
        Ui ui = new Ui();
        Parser parser = new Parser(ui);
        Command cmd = parser.parse("add-i Gibberish");
        cmd.execute(testInventory);
        assertEquals(0, testInventory.getSize());
    }
}
