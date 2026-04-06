package seedu.sudocook;

import static seedu.sudocook.SudoCook.DELETE_R_PREFIX;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static final int INGREDIENT_TOKEN_GROUP_SIZE = 3;
    private static Logger logger = Logger.getLogger("Parser");
    private final Ui ui;


    public Parser(Ui ui) {
        this.ui = ui;
    }

    public Command parse(String input) {
        String trimmedInput = input.trim();
        Command c;
        if (matchesCommandKeyword(trimmedInput, "delete-r")) {
            logger.log(Level.INFO, "Received delete-r request");
            try {
                int index = Integer.parseInt(trimmedInput.substring(DELETE_R_PREFIX).trim());
                assert index > 0 : "Parsed index must be positive";
                c = new DeleteRecipeCommand(index);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid index format for delete-r");
                Ui.printError("Invalid index for delete-r. Use: delete-r INDEX");
                return new Command(false);
            }
        } else if (matchesCommandKeyword(trimmedInput, "list-r")) {
            logger.log(Level.INFO, "Received list-r request");
            c = new ListRecipeCommand();
        } else if (matchesCommandKeyword(trimmedInput, "view-r")) {
            logger.log(Level.INFO, "Received view-r request");
            String viewArgs = trimmedInput.substring("view-r".length()).trim();
            if (viewArgs.isEmpty()) {
                c = new ViewRecipeCommand();
            } else {
                try {
                    int index = Integer.parseInt(viewArgs);
                    c = new ViewRecipeCommand(index);
                } catch (NumberFormatException e) {
                    Ui.printError("Invalid index for view-r. Use: view-r [INDEX]");
                    c = new Command(false);
                }
            }
        } else if (matchesCommandKeyword(trimmedInput, "recommend-r")) {
            logger.log(Level.INFO, "Received recommend-r request");
            String args = trimmedInput.substring("recommend-r".length()).trim();
            if (args.isEmpty()) {
                c = new RecommendByInventoryCommand();
            } else if (args.startsWith("missing/")) {
                String numStr = args.substring("missing/".length()).trim();
                try {
                    int n = Integer.parseInt(numStr);
                    if (n <= 0) {
                        Ui.printError("Missing count must be a positive number.");
                        return new Command(false);
                    }
                    c = new RecommendByMissingCommand(n);
                } catch (NumberFormatException e) {
                    Ui.printError("Invalid format. Use: recommend-r missing/N");
                    return new Command(false);
                }
            } else if (!args.startsWith("n/")) {
                Ui.printError("Invalid format. Use: recommend-r n/INGREDIENT_NAME, "
                        + "recommend-r missing/N, or recommend-r");
                return new Command(false);
            } else {
                String ingredientName = args.substring("n/".length()).trim();
                if (ingredientName.isEmpty()) {
                    Ui.printError("Ingredient name cannot be empty.");
                    return new Command(false);
                }
                c = new RecommendByIngredientCommand(ingredientName);
            }
        } else if (matchesCommandKeyword(trimmedInput, "list-i")) {
            logger.log(Level.INFO, "Received list-i request");
            String listIngredientInput = trimmedInput.substring("list-i".length()).trim();
            if (listIngredientInput.isEmpty()) {
                c = new ListIngredientCommand();
            } else {
                Pattern listIngredientPattern = Pattern.compile("ex/(\\d{4}-\\d{2}-\\d{2})");
                Matcher listIngredientMatcher = listIngredientPattern.matcher(listIngredientInput);
                if (!listIngredientMatcher.matches()) {
                    Ui.printError("Invalid list-i format. Use: list-i [ex/YYYY-MM-DD]");
                    return new Command(false);
                }
                try {
                    LocalDate expiryDate = LocalDate.parse(listIngredientMatcher.group(1));
                    c = new ListIngredientCommand(expiryDate);
                } catch (java.time.format.DateTimeParseException e) {
                    logger.log(Level.WARNING, "Invalid expiry date format for list-i: "
                            + listIngredientMatcher.group(1));
                    Ui.printError("Invalid expiry date format. Use: YYYY-MM-DD");
                    return new Command(false);
                }
            }
        } else if (matchesCommandKeyword(trimmedInput, "delete-i")) {
            String deleteInput = trimmedInput.substring("delete-i".length()).trim();
            String[] parts = deleteInput.split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) {
                Ui.printError("Invalid delete-i format. Use: delete-i INDEX/NAME [QUANTITY]");
                return new Command(false);
            }
            if (parts.length == 1) {
                c = new DeleteIngredientCommand(parts[0], ui);
            } else if (parts.length == 2) {
                try {
                    double quantity = Double.parseDouble(parts[1]);
                    if (quantity <= 0) {
                        Ui.printError("Quantity must be a positive number.");
                        return new Command(false);
                    }
                    c = new DeleteIngredientCommand(parts[0], quantity);
                } catch (NumberFormatException e) {
                    Ui.printError("Invalid quantity for delete-i.");
                    return new Command(false);
                }
            } else {
                Ui.printError("Invalid delete-i format. Use: delete-i INDEX/NAME [QUANTITY]");
                return new Command(false);
            }
        } else if (matchesCommandKeyword(trimmedInput, "add-i")) {
            logger.log(Level.FINE, "Received add-i request");
            String addIngredientInput = trimmedInput.substring("add-i".length()).trim();
            
            // Extract optional expiry date first
            LocalDate expiryDate = null;
            Pattern expiryPattern = Pattern.compile("(.*)\\s+ex/(\\d{4}-\\d{2}-\\d{2})");
            Matcher expiryMatcher = expiryPattern.matcher(addIngredientInput);
            if (expiryMatcher.matches()) {
                addIngredientInput = expiryMatcher.group(1);
                try {
                    expiryDate = LocalDate.parse(expiryMatcher.group(2));
                } catch (java.time.format.DateTimeParseException e) {
                    logger.log(Level.WARNING, "Invalid expiry date format: " + expiryMatcher.group(2));
                    Ui.printError("Invalid expiry date format. Use: YYYY-MM-DD");
                    return new Command(false);
                }
            }
            
            Pattern addIngredientPattern = Pattern.compile("n/(.+?)\\s+q/([\\d.]+)\\s+u/(.+)");
            Matcher addIngredientMatcher = addIngredientPattern.matcher(addIngredientInput);

            if (!addIngredientMatcher.matches()) {
                logger.log(Level.WARNING, "Invalid add-i format");
                Ui.printError("Invalid add-i format. Use: add-i n/NAME q/QUANTITY u/UNIT [ex/YYYY-MM-DD]");
                return new Command(false);
            }

            String name = addIngredientMatcher.group(1).trim();
            String quantityStr = addIngredientMatcher.group(2).trim();
            String unit = addIngredientMatcher.group(3).trim();

            // Validate name doesn't contain special characters
            if (!name.matches("[a-zA-Z0-9\\s]+")) {
                logger.log(Level.WARNING, "Ingredient name contains special characters");
                Ui.printError("Ingredient name should not contain special characters.");
                return new Command(false);
            }

            // Parse and validate quantity
            double quantity;
            try {
                quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    logger.log(Level.WARNING, "Invalid quantity: " + quantityStr);
                    Ui.printError("Quantity must be a positive number.");
                    return new Command(false);
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid quantity format: " + quantityStr);
                Ui.printError("Invalid quantity format.");
                return new Command(false);
            }

            logger.log(Level.FINE, "Creating add-i command for: " + name);
            c = new AddIngredientCommand(name, quantity, unit, expiryDate);
        } else if (matchesCommandKeyword(trimmedInput, "add-r")) {
            logger.log(Level.INFO, "Received logging request");

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ArrayList<String> steps = new ArrayList<>();
            String addRecipeInput = trimmedInput.substring("add-r".length()).trim();
            Pattern addRecipePattern = Pattern.compile("^(.*?)\\s+i/(.+?)\\s+s/(.+?)\\s+t/(\\d+)\\s+c/(\\d+)$");
            Matcher addRecipeMatcher = addRecipePattern.matcher(addRecipeInput);

            if (!addRecipeMatcher.matches()) {
                Ui.printError("Invalid add-r format. Use: add-r NAME i/INGREDIENTS s/STEPS t/TIME c/CALORIES");
                logger.log(Level.INFO, "Caught invalid add-r command format");
                return new Command(false);
            }

            String name = stripOptionalBraces(addRecipeMatcher.group(1).trim());
            String ingredientInput = addRecipeMatcher.group(2).trim();
            String stepInput = addRecipeMatcher.group(3).trim();
            String timeInput = addRecipeMatcher.group(4).trim();
            String calorieInput = addRecipeMatcher.group(5).trim();
            int time;
            int calories;
            try {
                time = Integer.parseInt(timeInput);
                calories = Integer.parseInt(calorieInput);
                if (time < 0 || calories < 0) {
                    Ui.printError("Time and calories cannot be negative.");
                    return new Command(false);
                }
            } catch (NumberFormatException e) {
                Ui.printError("Invalid add-r format. Time and calories should be integers.");
                logger.log(Level.INFO, "Caught invalid add-r command format in numeric fields");
                return new Command(false);
            }

            Pattern tokenPattern = Pattern.compile("\\{[^}]*\\}|\\S+");
            Matcher ingredientMatcher = tokenPattern.matcher(ingredientInput);
            ArrayList<String> ingredientTokens = new ArrayList<>();
            while (ingredientMatcher.find()) {
                ingredientTokens.add(stripOptionalBraces(ingredientMatcher.group()));
            }

            if (ingredientTokens.size() % INGREDIENT_TOKEN_GROUP_SIZE != 0) {
                Ui.printError("Invalid add-r format. Ingredients should be NAME QUANTITY UNIT.");
                logger.log(Level.INFO, "Caught invalid add-r command format in INGREDIENT NAME field");
                return new Command(false);
            }

            for (int i = 0; i < ingredientTokens.size(); i += INGREDIENT_TOKEN_GROUP_SIZE) {
                String ingredientName = ingredientTokens.get(i);
                String quantityToken = ingredientTokens.get(i + 1);
                String unit = ingredientTokens.get(i + 2);
                double quantity;

                try {
                    quantity = Double.parseDouble(quantityToken);
                    if (quantity <= 0) {
                        Ui.printError("Invalid ingredient quantity in add-r format.");
                        logger.log(Level.INFO, "Caught non-positive add-r ingredient quantity");
                        return new Command(false);
                    }
                } catch (NumberFormatException e) {
                    Ui.printError("Invalid ingredient quantity in add-r format.");
                    logger.log(Level.INFO, "Caught invalid add-r command format in QUANTITY field");
                    return new Command(false);
                }

                ingredients.add(new Ingredient(ingredientName, quantity, unit));
            }

            Matcher stepMatcher = tokenPattern.matcher(stepInput);
            while (stepMatcher.find()) {
                steps.add(stripOptionalBraces(stepMatcher.group()));
            }

            c = new AddRecipeCommand(name, ingredients, steps, time, calories);

        } else if (matchesCommandKeyword(trimmedInput, "filter-r")) {
            logger.log(Level.INFO, "Received filter-r request");
            String filterInput = trimmedInput.substring("filter-r".length()).trim();

            Integer maxTime = null;
            Integer maxCalories = null;

            Pattern timePattern = Pattern.compile("t/(\\d+)");
            Matcher timeMatcher = timePattern.matcher(filterInput);
            if (timeMatcher.find()) {
                try {
                    maxTime = Integer.parseInt(timeMatcher.group(1));
                } catch (NumberFormatException e) {
                    Ui.printError("Invalid time format for filter-r.");
                    return new Command(false);
                }
            }

            Pattern caloriePattern = Pattern.compile("c/(\\d+)");
            Matcher calorieMatcher = caloriePattern.matcher(filterInput);
            if (calorieMatcher.find()) {
                try {
                    maxCalories = Integer.parseInt(calorieMatcher.group(1));
                } catch (NumberFormatException e) {
                    Ui.printError("Invalid calorie format for filter-r.");
                    return new Command(false);
                }
            }

            if (maxTime == null && maxCalories == null) {
                Ui.printError("No valid filter targets provided. Use: filter-r [t/MAX_TIME] [c/MAX_CALORIES]");
                return new Command(false);
            }

            c = new FilterRecipeCommand(maxTime, maxCalories);

        } else if (matchesCommandKeyword(trimmedInput, "cook")) {
            logger.log(Level.INFO, "Received cook-r request");
            String cookArgs = trimmedInput.substring("cook".length()).trim();
            try {
                int index = Integer.parseInt(cookArgs) - 1;
                c = new CookCommand(false, index);
            } catch (NumberFormatException e) {
                Ui.printError("You should indicate the index of the recipe when cooking!");
                c = new Command(false);
            }
        } else if (matchesCommandKeyword(trimmedInput, "sort-i")){
            logger.log(Level.INFO, "Received sort-i request");
            c = new SortInventoryCommand(false);
            return c;
        } else if (matchesCommandKeyword(trimmedInput, "search-r")) {
            logger.log(Level.INFO, "Received search-r request");
            String query = trimmedInput.substring("search-r".length()).trim();
            if (query.isEmpty()) {
                Ui.printError("Please provide a search query. Use: search-r QUERY");
                return new Command(false);
            }
            c = new SearchRecipeCommand(query);
        } else if (matchesCommandKeyword(trimmedInput, "search-i")) {
            logger.log(Level.INFO, "Received search-i request");
            String query = trimmedInput.substring("search-i".length()).trim();
            if (query.isEmpty()) {
                Ui.printError("Please provide a search query. Use: search-i QUERY");
                return new Command(false);
            }
            c = new SearchIngredientCommand(query);
        } else if (trimmedInput.equalsIgnoreCase("help")) {
            c = new HelpCommand();
        } else {
            c = new Command(false);
            Ui.printError("I don't recognise that command!");
        }
        return c;

    }

    private String stripOptionalBraces(String token) {
        if (token.startsWith("{") && token.endsWith("}")) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }

    private boolean matchesCommandKeyword(String input, String keyword) {
        return input.equals(keyword)
                || (input.startsWith(keyword)
                && input.length() > keyword.length()
                && Character.isWhitespace(input.charAt(keyword.length())));
    }
}
