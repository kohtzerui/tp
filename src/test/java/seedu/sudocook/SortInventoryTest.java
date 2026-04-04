package seedu.sudocook;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortInventoryTest {

    private Inventory inventory;

    @BeforeEach
    public void setup() {
        inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Water", 1, "Liter", LocalDate.of(2020, 1, 1)));
        inventory.addIngredient(new Ingredient("Milk", 1, "Liter", LocalDate.of(2019, 1, 1)));
        inventory.addIngredient(new Ingredient("Juice", 1, "Liter", LocalDate.of(2022, 1, 1)));
    }

    @Test
    public void sortTest() {
        Command test = new SortInventoryCommand(false);
        test.execute(inventory);

        boolean firstBeforeSecond = inventory.getIngredients().get(0).getExpiryDate()
                .isBefore(inventory.getIngredients().get(1).getExpiryDate());
        boolean secondBeforeThird = inventory.getIngredients().get(1).getExpiryDate()
                .isBefore(inventory.getIngredients().get(2).getExpiryDate());

        assertTrue(firstBeforeSecond && secondBeforeThird);
    }

    @Test
    public void sort_emptyInventory_doesNotThrowAndRemainsEmpty() {
        Inventory emptyInventory = new Inventory();
        Command test = new SortInventoryCommand(false);

        assertDoesNotThrow(() -> test.execute(emptyInventory));
        assertTrue(emptyInventory.getIngredients().isEmpty());
    }

    @Test
    public void sort_nullExpiryDate_placesIngredientLast() {
        inventory.addIngredient(new Ingredient("Salt", 1, "kg"));
        Command test = new SortInventoryCommand(false);

        assertDoesNotThrow(() -> test.execute(inventory));

        List<Ingredient> sortedIngredients = inventory.getIngredients();
        assertEquals("Milk", sortedIngredients.get(0).getName());
        assertEquals("Water", sortedIngredients.get(1).getName());
        assertEquals("Juice", sortedIngredients.get(2).getName());
        assertEquals("Salt", sortedIngredients.get(3).getName());
        assertNull(sortedIngredients.get(3).getExpiryDate());
    }
}
