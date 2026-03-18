package seedu.sudocook;

import static seedu.sudocook.SudoCook.DELETE_R_PREFIX;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Parser {
    private static Logger logger = Logger.getLogger("Parser");
    private final Ui ui;


    public Parser(Ui ui) {
        this.ui = ui;
    }

    public Command parse(String input) {
        Command c;
        if (input.startsWith("delete-r")) {
            logger.log(Level.INFO, "Received delete-r request");
            try {
                int index = Integer.parseInt(input.substring(DELETE_R_PREFIX).trim());
                assert index > 0 : "Parsed index must be positive";
                c = new DeleteRecipeCommand(index);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid index format for delete-r");
                ui.printError("Invalid index for delete-r. Use: delete-r INDEX");
                return new Command(false);
            }
        } else if (input.startsWith("list-r")) {
            logger.log(Level.INFO, "Received list-r request");
            c = new ListRecipeCommand();
        } else if (input.startsWith("list-i")) {
            c = new ListIngredientCommand();
        } else if (input.startsWith("delete-i")) {
            String deleteInput = input.substring("delete-i".length()).trim();
            String[] parts = deleteInput.split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) {
                ui.printError("Invalid delete-i format. Use: delete-i INDEX/NAME [QUANTITY]");
                return new Command(false);
            }
            if (parts.length == 1) {
                c = new DeleteIngredientCommand(parts[0], ui);
            } else if (parts.length == 2) {
                try {
                    double quantity = Double.parseDouble(parts[1]);
                    if (quantity <= 0) {
                        ui.printError("Quantity must be a positive number.");
                        return new Command(false);
                    }
                    c = new DeleteIngredientCommand(parts[0], quantity, ui);
                } catch (NumberFormatException e) {
                    ui.printError("Invalid quantity for delete-i.");
                    return new Command(false);
                }
            } else {
                ui.printError("Invalid delete-i format. Use: delete-i INDEX/NAME [QUANTITY]");
                return new Command(false);
            }
        } else if (input.startsWith("add-i")) {
            logger.log(Level.FINE, "Received add-i request");
            String addIngredientInput = input.substring("add-i".length()).trim();
            Pattern addIngredientPattern = Pattern.compile("n/([^q/]+)\\s+q/([\\d.]+)\\s+u/(.+)");
            Matcher addIngredientMatcher = addIngredientPattern.matcher(addIngredientInput);

            if (!addIngredientMatcher.matches()) {
                logger.log(Level.WARNING, "Invalid add-i format");
                ui.printError("Invalid add-i format. Use: add-i n/NAME q/QUANTITY u/UNIT");
                return new Command(false);
            }

            String name = addIngredientMatcher.group(1).trim();
            String quantityStr = addIngredientMatcher.group(2).trim();
            String unit = addIngredientMatcher.group(3).trim();

            // Validate name doesn't contain special characters
            if (!name.matches("[a-zA-Z0-9\\s]+")) {
                logger.log(Level.WARNING, "Ingredient name contains special characters");
                ui.printError("Ingredient name should not contain special characters.");
                return new Command(false);
            }

            // Parse and validate quantity
            double quantity;
            try {
                quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    logger.log(Level.WARNING, "Invalid quantity: " + quantityStr);
                    ui.printError("Quantity must be a positive number.");
                    return new Command(false);
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid quantity format: " + quantityStr);
                ui.printError("Invalid quantity format.");
                return new Command(false);
            }

            logger.log(Level.FINE, "Creating add-i command for: " + name);
            c = new AddIngredientCommand(name, quantity, unit);
        } else if (input.startsWith("add-r")) {
            logger.log(Level.INFO, "Received logging request");

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ArrayList<String> steps = new ArrayList<>();
            String addRecipeInput = input.substring("add-r".length()).trim();
            Pattern addRecipePattern = Pattern.compile("^(.*?)\\s+i/(.+?)\\s+s/(.+)$");
            Matcher addRecipeMatcher = addRecipePattern.matcher(addRecipeInput);

            if (!addRecipeMatcher.matches()) {
                ui.printError("Invalid add-r format.");
                logger.log(Level.INFO, "Caught invalid add-r command format");
                return new Command(false);
            }

            String name = stripOptionalBraces(addRecipeMatcher.group(1).trim());
            String ingredientInput = addRecipeMatcher.group(2).trim();
            String stepInput = addRecipeMatcher.group(3).trim();

            Pattern tokenPattern = Pattern.compile("\\{[^}]*\\}|\\S+");
            Matcher ingredientMatcher = tokenPattern.matcher(ingredientInput);
            ArrayList<String> ingredientTokens = new ArrayList<>();
            while (ingredientMatcher.find()) {
                ingredientTokens.add(stripOptionalBraces(ingredientMatcher.group()));
            }

            if (ingredientTokens.size() % 3 != 0) {
                ui.printError("Invalid add-r format. Ingredients should be NAME QUANTITY UNIT.");
                logger.log(Level.INFO, "Caught invalid add-r command format in INGREDIENT NAME field");
                return new Command(false);
            }

            for (int i = 0; i < ingredientTokens.size(); i += 3) {
                String ingredientName = ingredientTokens.get(i);
                String quantityToken = ingredientTokens.get(i + 1);
                String unit = ingredientTokens.get(i + 2);
                double quantity;

                try {
                    quantity = Double.parseDouble(quantityToken);
                } catch (NumberFormatException e) {
                    ui.printError("Invalid ingredient quantity in add-r format.");
                    logger.log(Level.INFO, "Caught invalid add-r command format in QUANTITY field");
                    return new Command(false);
                }

                ingredients.add(new Ingredient(ingredientName, quantity, unit));
            }

            Matcher stepMatcher = tokenPattern.matcher(stepInput);
            while (stepMatcher.find()) {
                steps.add(stripOptionalBraces(stepMatcher.group()));
            }

            c = new AddRecipeCommand(name, ingredients, steps);

        } else if (input.trim().equalsIgnoreCase("help")) {
            c = new HelpCommand();
        } else {
            c = new Command(false);
            ui.printError("I don't recognise that command!");
        }
        return c;

    }

    private String stripOptionalBraces(String token) {
        if (token.startsWith("{") && token.endsWith("}")) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }
}
