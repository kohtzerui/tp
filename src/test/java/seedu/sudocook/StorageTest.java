package seedu.sudocook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for Storage.
 * Tests the functionality of saving and loading inventory and recipes from JSON files.
 */
public class StorageTest {
    private static final String DATA_DIR = "data";
    private static final String INVENTORY_FILE = DATA_DIR + File.separator + "inventory.json";
    private static final String RECIPES_FILE = DATA_DIR + File.separator + "recipes.json";

    @BeforeEach
    public void setUp() {
        // Initialize storage directory
        Storage.initializeStorage();
        // Clean up any existing test files
        deleteTestFiles();
    }

    @AfterEach
    public void tearDown() {
        // Clean up test files after each test
        deleteTestFiles();
    }

    @Test
    public void saveAndLoadInventory_withoutExpiry_savesAndLoadsSuccessfully() {
        Inventory inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Milk", 2, "liters"));
        inventory.addIngredient(new Ingredient("Bread", 1, "loaf"));

        Storage.saveInventory(inventory);

        Inventory loadedInventory = new Inventory();
        Storage.loadInventory(loadedInventory);

        assertEquals(2, loadedInventory.size());
    }

    @Test
    public void saveAndLoadInventory_withExpiry_preservesExpiryDates() {
        Inventory inventory = new Inventory();
        LocalDate expiryDate = LocalDate.of(2026, 4, 15);
        inventory.addIngredient(new Ingredient("Chicken", 1.5, "kg", expiryDate));
        inventory.addIngredient(new Ingredient("Milk", 2, "liters", LocalDate.of(2026, 4, 10)));

        Storage.saveInventory(inventory);

        Inventory loadedInventory = new Inventory();
        Storage.loadInventory(loadedInventory);

        assertEquals(2, loadedInventory.size());
    }

    @Test
    public void saveAndLoadInventory_multipleExpiries_preservesExpiryQuantities() {
        Inventory inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Milk", 1, "carton", LocalDate.of(2026, 4, 1)));
        inventory.addIngredient(new Ingredient("Milk", 2, "carton", LocalDate.of(2026, 5, 1)));

        Storage.saveInventory(inventory);

        Inventory loadedInventory = new Inventory();
        Storage.loadInventory(loadedInventory);

        Ingredient loadedIngredient = loadedInventory.getIngredient(0);
        assertEquals(1, loadedInventory.size());
        assertEquals(3, loadedIngredient.getQuantity());
        assertEquals(2, loadedIngredient.getExpiryQuantities().size());
        assertEquals(LocalDate.of(2026, 4, 1), loadedIngredient.getExpiryQuantities().get(0).getExpiryDate());
        assertEquals(1, loadedIngredient.getExpiryQuantities().get(0).getQuantity());
        assertEquals(LocalDate.of(2026, 5, 1), loadedIngredient.getExpiryQuantities().get(1).getExpiryDate());
        assertEquals(2, loadedIngredient.getExpiryQuantities().get(1).getQuantity());
    }

    @Test
    public void loadInventory_fileDoesNotExist_startsWithEmptyInventory() {
        Inventory inventory = new Inventory();
        Storage.loadInventory(inventory);

        assertEquals(0, inventory.size());
    }

    @Test
    public void saveAndLoadRecipes_savesAndLoadsSuccessfully() {
        RecipeBook recipeBook = new RecipeBook();
        Ingredient ingredient = new Ingredient("Milk", 2, "cups");
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);
        
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Mix dry ingredients");
        steps.add("Add milk and eggs");
        
        Recipe recipe = new Recipe("Pancakes", ingredients, steps, 15, 250);
        recipeBook.addRecipe(recipe);

        Storage.saveRecipes(recipeBook);

        RecipeBook loadedRecipeBook = new RecipeBook();
        Storage.loadRecipes(loadedRecipeBook);

        assertEquals(1, loadedRecipeBook.size());
    }

    @Test
    public void saveInventory_createsValidJsonFile() {
        Inventory inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Salt", 1, "kg"));

        Storage.saveInventory(inventory);

        assertTrue(new File(INVENTORY_FILE).exists());
    }

    @Test
    public void saveRecipes_createsValidJsonFile() {
        RecipeBook recipeBook = new RecipeBook();
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ArrayList<String> steps = new ArrayList<>();
        steps.add("Test step");
        Recipe recipe = new Recipe("Test Recipe", ingredients, steps, 10);
        recipeBook.addRecipe(recipe);

        Storage.saveRecipes(recipeBook);

        assertTrue(new File(RECIPES_FILE).exists());
    }

    @Test
    public void loadInventory_multipleIngredients_loadsAllCorrectly() {
        Inventory inventory = new Inventory();
        inventory.addIngredient(new Ingredient("Tomato", 5, "pieces"));
        inventory.addIngredient(new Ingredient("Onion", 2, "pieces", LocalDate.of(2026, 4, 20)));
        inventory.addIngredient(new Ingredient("Garlic", 10, "cloves"));

        Storage.saveInventory(inventory);

        Inventory loadedInventory = new Inventory();
        Storage.loadInventory(loadedInventory);

        assertEquals(3, loadedInventory.size());
    }

    private void deleteTestFiles() {
        try {
            File inventoryFile = new File(INVENTORY_FILE);
            File recipesFile = new File(RECIPES_FILE);
            
            if (inventoryFile.exists()) {
                inventoryFile.delete();
            }
            if (recipesFile.exists()) {
                recipesFile.delete();
            }
            
            File dataDir = new File(DATA_DIR);
            if (dataDir.exists() && dataDir.listFiles().length == 0) {
                dataDir.delete();
            }
        } catch (Exception e) {
            // Silently ignore deletion errors
        }
    }
}
