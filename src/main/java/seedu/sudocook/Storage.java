package seedu.sudocook;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Handles storage and retrieval of recipes and ingredients from JSON files.
 */
public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());
    private static final String DATA_DIR = "data";
    private static final String RECIPES_FILE = DATA_DIR + File.separator + "recipes.json";
    private static final String INVENTORY_FILE = DATA_DIR + File.separator + "inventory.json";

    /**
     * Initializes storage directory if it doesn't exist.
     */
    public static void initializeStorage() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                logger.log(Level.INFO, "Created data directory");
            } else {
                logger.log(Level.WARNING, "Failed to create data directory");
            }
        }
    }

    /**
     * Saves the inventory to a JSON file.
     *
     * @param inventory The inventory to save
     */
    public static void saveInventory(Inventory inventory) {
        initializeStorage();
        try {
            JSONArray ingredientList = new JSONArray();
            for (Ingredient ingredient : inventory.getIngredients()) {
                JSONObject ingredientObj = new JSONObject();
                ingredientObj.put("name", ingredient.getName());
                ingredientObj.put("quantity", ingredient.getQuantity());
                ingredientObj.put("unit", ingredient.getUnit());

                JSONArray expiryQuantities = new JSONArray();
                for (Ingredient.ExpiryQuantity expiryQuantity : ingredient.getExpiryQuantities()) {
                    JSONObject expiryQuantityObj = new JSONObject();
                    expiryQuantityObj.put("quantity", expiryQuantity.getQuantity());
                    if (expiryQuantity.getExpiryDate() != null) {
                        expiryQuantityObj.put("expiryDate", expiryQuantity.getExpiryDate().toString());
                    }
                    expiryQuantities.put(expiryQuantityObj);
                }
                ingredientObj.put("expiryQuantities", expiryQuantities);

                if (ingredient.getExpiryQuantities().size() == 1 && ingredient.getExpiryDate() != null) {
                    ingredientObj.put("expiryDate", ingredient.getExpiryDate().toString());
                }
                ingredientList.put(ingredientObj);
            }

            try (FileWriter fileWriter = new FileWriter(INVENTORY_FILE)) {
                fileWriter.write(ingredientList.toString(2));
                fileWriter.flush();
                logger.log(Level.INFO, "Inventory saved successfully");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to save inventory: " + e.getMessage());
        }
    }

    /**
     * Loads the inventory from a JSON file.
     *
     * @param inventory The inventory to populate
     */
    public static void loadInventory(Inventory inventory) {
        initializeStorage();
        File file = new File(INVENTORY_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "Inventory file does not exist, starting with empty inventory");
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            StringBuilder content = new StringBuilder();
            int character;
            while ((character = fileReader.read()) != -1) {
                content.append((char) character);
            }

            JSONArray ingredientList = new JSONArray(content.toString());
            for (int i = 0; i < ingredientList.length(); i++) {
                JSONObject ingredientObj = ingredientList.getJSONObject(i);
                String name = ingredientObj.getString("name");
                double quantity = ingredientObj.getDouble("quantity");
                String unit = ingredientObj.getString("unit");
                inventory.addIngredient(loadInventoryIngredient(ingredientObj, name, quantity, unit));
            }
            logger.log(Level.INFO, "Inventory loaded successfully");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load inventory: " + e.getMessage());
        }
    }

    private static Ingredient loadInventoryIngredient(JSONObject ingredientObj, String name, double quantity,
            String unit) {
        if (!ingredientObj.has("expiryQuantities")) {
            return createIngredient(name, quantity, unit, ingredientObj);
        }

        JSONArray expiryQuantities = ingredientObj.getJSONArray("expiryQuantities");
        if (expiryQuantities.length() == 0) {
            return createIngredient(name, quantity, unit, ingredientObj);
        }

        JSONObject firstExpiryQuantity = expiryQuantities.getJSONObject(0);
        Ingredient ingredient = createIngredient(name, firstExpiryQuantity.getDouble("quantity"), unit,
                firstExpiryQuantity);
        for (int i = 1; i < expiryQuantities.length(); i++) {
            JSONObject expiryQuantity = expiryQuantities.getJSONObject(i);
            ingredient.addQuantity(expiryQuantity.getDouble("quantity"), getExpiryDate(expiryQuantity));
        }
        return ingredient;
    }

    private static Ingredient createIngredient(String name, double quantity, String unit, JSONObject ingredientObj) {
        LocalDate expiryDate = getExpiryDate(ingredientObj);
        if (expiryDate != null) {
            return new Ingredient(name, quantity, unit, expiryDate);
        }
        return new Ingredient(name, quantity, unit);
    }

    private static LocalDate getExpiryDate(JSONObject ingredientObj) {
        if (ingredientObj.has("expiryDate")) {
            return LocalDate.parse(ingredientObj.getString("expiryDate"));
        }
        return null;
    }

    /**
     * Saves the recipe book to a JSON file.
     *
     * @param recipeBook The recipe book to save
     */
    public static void saveRecipes(RecipeBook recipeBook) {
        initializeStorage();
        try {
            JSONArray recipeList = new JSONArray();
            for (Recipe recipe : recipeBook.getRecipes()) {
                JSONObject recipeObj = new JSONObject();
                recipeObj.put("name", recipe.getName());

                JSONArray ingredientList = new JSONArray();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    JSONObject ingredientObj = new JSONObject();
                    ingredientObj.put("name", ingredient.getName());
                    ingredientObj.put("quantity", ingredient.getQuantity());
                    ingredientObj.put("unit", ingredient.getUnit());
                    ingredientList.put(ingredientObj);
                }
                recipeObj.put("ingredients", ingredientList);

                JSONArray stepList = new JSONArray();
                for (String step : recipe.getSteps()) {
                    stepList.put(step);
                }
                recipeObj.put("steps", stepList);
                recipeObj.put("time", recipe.getTime());
                recipeObj.put("calories", recipe.getCalories());
                recipeList.put(recipeObj);
            }

            try (FileWriter fileWriter = new FileWriter(RECIPES_FILE)) {
                fileWriter.write(recipeList.toString(2));
                fileWriter.flush();
                logger.log(Level.INFO, "Recipes saved successfully");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to save recipes: " + e.getMessage());
        }
    }

    /**
     * Loads recipes from a JSON file.
     *
     * @param recipeBook The recipe book to populate
     */
    public static void loadRecipes(RecipeBook recipeBook) {
        initializeStorage();
        File file = new File(RECIPES_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "Recipes file does not exist, starting with empty recipe book");
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            StringBuilder content = new StringBuilder();
            int character;
            while ((character = fileReader.read()) != -1) {
                content.append((char) character);
            }

            JSONArray recipeList = new JSONArray(content.toString());
            for (int i = 0; i < recipeList.length(); i++) {
                JSONObject recipeObj = recipeList.getJSONObject(i);
                String name = recipeObj.getString("name");

                ArrayList<Ingredient> ingredients = new ArrayList<>();
                JSONArray ingredientArray = recipeObj.getJSONArray("ingredients");
                for (int j = 0; j < ingredientArray.length(); j++) {
                    JSONObject ingredientObj = ingredientArray.getJSONObject(j);
                    Ingredient ingredient = new Ingredient(
                            ingredientObj.getString("name"),
                            ingredientObj.getDouble("quantity"),
                            ingredientObj.getString("unit"));
                    ingredients.add(ingredient);
                }

                ArrayList<String> steps = new ArrayList<>();
                JSONArray stepArray = recipeObj.getJSONArray("steps");
                for (int j = 0; j < stepArray.length(); j++) {
                    steps.add(stepArray.getString(j));
                }

                int time = recipeObj.optInt("time", 0);
                int calories = recipeObj.optInt("calories", 0);
                recipeBook.addRecipe(new Recipe(name, ingredients, steps, time, calories));
            }
            logger.log(Level.INFO, "Recipes loaded successfully");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load recipes: " + e.getMessage());
        }
    }
}
