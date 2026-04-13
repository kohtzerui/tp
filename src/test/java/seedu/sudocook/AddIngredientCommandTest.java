package seedu.sudocook;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AddIngredientCommandTest {
    @Test
    public void execute_matchingUndatedIngredient_propagatesExpiryAndMergesQuantity() {
        Inventory inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Milk", 1, "carton"));

        LocalDate expiryDate = LocalDate.of(2026, 4, 10);
        AddIngredientCommand command = new AddIngredientCommand("Milk", 2, "carton", expiryDate);
        command.execute(inventory);

        assertEquals(1, inventory.getSize());
        assertEquals(3, inventory.getIngredient(0).getQuantity());
        assertEquals(expiryDate, inventory.getIngredient(0).getExpiryDate());
    }

    @Test
    public void execute_matchingDatedIngredientWithDifferentExpiry_preservesExpiryQuantities() {
        Inventory inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Milk", 1, "carton", LocalDate.of(2026, 4, 1)));

        AddIngredientCommand command = new AddIngredientCommand("Milk", 2, "carton",
                LocalDate.of(2026, 5, 1));
        command.execute(inventory);

        Ingredient ingredient = inventory.getIngredient(0);
        assertEquals(1, inventory.getSize());
        assertEquals(3, ingredient.getQuantity());
        assertEquals(2, ingredient.getExpiryQuantities().size());
        assertEquals(LocalDate.of(2026, 4, 1), ingredient.getExpiryQuantities().get(0).getExpiryDate());
        assertEquals(1, ingredient.getExpiryQuantities().get(0).getQuantity());
        assertEquals(LocalDate.of(2026, 5, 1), ingredient.getExpiryQuantities().get(1).getExpiryDate());
        assertEquals(2, ingredient.getExpiryQuantities().get(1).getQuantity());
    }

    @Test
    public void parse_validInputWithoutExpiry_addsIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Flour q/1.5 u/kg", inventory);

        assertEquals(1, inventory.getSize());
        assertEquals("Flour", inventory.getIngredient(0).getName());
        assertEquals(1.5, inventory.getIngredient(0).getQuantity());
        assertEquals("kg", inventory.getIngredient(0).getUnit());
        assertNull(inventory.getIngredient(0).getExpiryDate());
    }

    @Test
    public void parse_validInputWithExpiry_addsIngredientWithExpiry() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Tomato q/3 u/pcs ex/2026-04-10", inventory);

        assertEquals(1, inventory.getSize());
        assertEquals("Tomato", inventory.getIngredient(0).getName());
        assertEquals(3, inventory.getIngredient(0).getQuantity());
        assertEquals("pcs", inventory.getIngredient(0).getUnit());
        assertEquals(LocalDate.of(2026, 4, 10), inventory.getIngredient(0).getExpiryDate());
    }

    @Test
    public void parse_uppercaseCommandAndPrefixes_addsIngredientWithExpiry() {
        Inventory inventory = new Inventory();

        parseAndExecute("ADD-I N/Tomato Q/3 U/pcs EX/2026-04-10", inventory);

        assertEquals(1, inventory.getSize());
        assertEquals("Tomato", inventory.getIngredient(0).getName());
        assertEquals(3, inventory.getIngredient(0).getQuantity());
        assertEquals("pcs", inventory.getIngredient(0).getUnit());
        assertEquals(LocalDate.of(2026, 4, 10), inventory.getIngredient(0).getExpiryDate());
    }

    @Test
    public void parse_validInputWithLowercaseQInName_addsIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/quinoa q/1 u/kg", inventory);

        assertEquals(1, inventory.getSize());
        assertEquals("quinoa", inventory.getIngredient(0).getName());
        assertEquals(1, inventory.getIngredient(0).getQuantity());
        assertEquals("kg", inventory.getIngredient(0).getUnit());
        assertNull(inventory.getIngredient(0).getExpiryDate());
    }

    @Test
    public void parse_missingUnit_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Flour q/1.5", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_invalidExpiryDate_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Milk q/1 u/carton ex/2026-02-30", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_malformedExpiryDate_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Milk q/1 u/carton ex/2026-2-30", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_monthOutOfRange_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Milk q/1 u/carton ex/2024-13-01", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_malformedDateExtraDigits_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Milk q/1 u/carton ex/20230-13-100", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_invalidName_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Tom@to q/3 u/pcs", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_invalidUnitSymbols_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/salt q/5 u/???", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_zeroQuantity_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Sugar q/0 u/g", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_negativeQuantity_doesNotAddIngredient() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/Sugar q/-1 u/g", inventory);

        assertEquals(0, inventory.getSize());
    }

    @Test
    public void parse_extraInternalSpacesInName_normalizesName() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/fried  rice q/1 u/cup", inventory);

        assertEquals(1, inventory.getSize());
        assertEquals("fried rice", inventory.getIngredient(0).getName());
    }

    @Test
    public void parse_extraInternalSpacesInUnit_normalizesUnit() {
        Inventory inventory = new Inventory();

        parseAndExecute("add-i n/rice q/1 u/big  cup", inventory);

        assertEquals(1, inventory.getSize());
        assertEquals("big cup", inventory.getIngredient(0).getUnit());
    }

    private void parseAndExecute(String input, Inventory inventory) {
        Parser parser = new Parser(new Ui());
        Command command = parser.parse(input);
        command.execute(inventory);
    }
}
