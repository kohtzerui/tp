package seedu.sudocook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListIngredientCommandTest {
    private Inventory inventory;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory();
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void parse_withExpiryFilter_listsOnlyMatchingIngredients() {
        inventory.addIngredient(new Ingredient("Spinach", 1, "bag", LocalDate.of(2026, 3, 20)));
        inventory.addIngredient(new Ingredient("Milk", 1, "carton", LocalDate.of(2026, 4, 2)));
        inventory.addIngredient(new Ingredient("Salt", 1, "kg"));

        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("list-i ex/2026-03-25");
        cmd.execute(inventory);

        assertTrue(getOutput().contains("Spinach"));
        assertFalse(getOutput().contains("Milk"));
        assertFalse(getOutput().contains("Salt"));
        assertTrue(getOutput().contains("expiring before 2026-03-25"));
    }

    @Test
    public void parse_withExpiryFilter_listsOnlyMatchingExpiryQuantities() {
        inventory.addIngredient(new Ingredient("Milk", 1, "carton", LocalDate.of(2026, 4, 1)));
        inventory.addIngredient(new Ingredient("Milk", 2, "carton", LocalDate.of(2026, 5, 1)));

        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("list-i ex/2026-04-15");
        cmd.execute(inventory);

        assertTrue(getOutput().contains("Milk (1.0 carton) expiries: [2026-04-01: 1.0 carton]"));
        assertFalse(getOutput().contains("2026-05-01"));
    }

    @Test
    public void parse_withNoMatches_printsFilteredEmptyMessage() {
        inventory.addIngredient(new Ingredient("Milk", 1, "carton", LocalDate.of(2026, 4, 2)));

        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("list-i ex/2026-03-25");
        cmd.execute(inventory);

        assertTrue(getOutput().contains("There are no ingredients expiring before 2026-03-25."));
    }

    @Test
    public void parse_invalidExpiryDate_returnsNoOpCommand() {
        Parser parser = new Parser(new Ui());
        Command cmd = parser.parse("list-i ex/2026-02-30");

        assertSame(Command.class, cmd.getClass());
        assertTrue(getOutput().contains("Invalid expiry date format. Use: YYYY-MM-DD"));
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
