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
        if (matchesCommandKeyword(trimmedInput, "delete-r")) {
            return parseDeleteR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "list-r")) {
            return parseListR();
        } else if (matchesCommandKeyword(trimmedInput, "view-r")) {
            return parseViewR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "recommend-r")) {
            return parseRecommendR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "list-i")) {
            return parseListI(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "delete-i")) {
            return parseDeleteI(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "add-i")) {
            return parseAddI(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "add-r")) {
            return parseAddR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "filter-r")) {
            return parseFilterR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "cook")) {
            return parseCook(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "sort-i")) {
            return parseSortI();
        } else if (matchesCommandKeyword(trimmedInput, "sort-r")) {
            return parseSortR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "search-r")) {
            return parseSearchR(trimmedInput);
        } else if (matchesCommandKeyword(trimmedInput, "search-i")) {
            return parseSearchI(trimmedInput);
        } else if (trimmedInput.equalsIgnoreCase("help")) {
            return new HelpCommand();
        } else {
            Ui.printError("I don't recognise that command!");
            return new Command(false);
        }
    }

    private Command parseDeleteR(String input) {
        logger.log(Level.INFO, "Received delete-r request");
        try {
            int index = Integer.parseInt(input.substring(DELETE_R_PREFIX).trim());
            assert index > 0 : "Parsed index must be positive";
            return new DeleteRecipeCommand(index);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid index format for delete-r");
            Ui.printError("Invalid index for delete-r. Use: delete-r INDEX");
            return new Command(false);
        }
    }

    private Command parseListR() {
        logger.log(Level.INFO, "Received list-r request");
        return new ListRecipeCommand();
    }

    private Command parseViewR(String input) {
        logger.log(Level.INFO, "Received view-r request");
        String viewArgs = input.substring("view-r".length()).trim();
        if (viewArgs.isEmpty()) {
            return new ViewRecipeCommand();
        }
        try {
            int index = Integer.parseInt(viewArgs);
            return new ViewRecipeCommand(index);
        } catch (NumberFormatException e) {
            Ui.printError("Invalid index for view-r. Use: view-r [INDEX]");
            return new Command(false);
        }
    }

    private Command parseRecommendR(String input) {
        logger.log(Level.INFO, "Received recommend-r request");
        String args = input.substring("recommend-r".length()).trim();
        if (args.isEmpty()) {
            return new RecommendByInventoryCommand();
        } else if (args.startsWith("missing/")) {
            return parseRecommendByMissing(args);
        } else if (!args.startsWith("n/")) {
            Ui.printError("Invalid format. Use: recommend-r n/INGREDIENT_NAME, "
                    + "recommend-r missing/N, or recommend-r");
            return new Command(false);
        } else {
            return parseRecommendByIngredient(args);
        }
    }

    private Command parseRecommendByMissing(String args) {
        String maxMissingStr = args.substring("missing/".length()).trim();
        try {
            int maxMissing = Integer.parseInt(maxMissingStr);
            if (maxMissing <= 0) {
                Ui.printError("Missing count must be a positive number.");
                return new Command(false);
            }
            return new RecommendByMissingCommand(maxMissing);
        } catch (NumberFormatException e) {
            Ui.printError("Invalid format. Use: recommend-r missing/N");
            return new Command(false);
        }
    }

    private Command parseRecommendByIngredient(String args) {
        String ingredientName = args.substring("n/".length()).trim();
        if (ingredientName.isEmpty()) {
            Ui.printError("Ingredient name cannot be empty.");
            return new Command(false);
        }
        return new RecommendByIngredientCommand(ingredientName);
    }

    private Command parseListI(String input) {
        logger.log(Level.INFO, "Received list-i request");
        String listIngredientInput = input.substring("list-i".length()).trim();
        if (listIngredientInput.isEmpty()) {
            return new ListIngredientCommand();
        }
        Pattern listIngredientPattern = Pattern.compile("ex/(\\d{4}-\\d{2}-\\d{2})");
        Matcher listIngredientMatcher = listIngredientPattern.matcher(listIngredientInput);
        if (!listIngredientMatcher.matches()) {
            Ui.printError("Invalid list-i format. Use: list-i [ex/YYYY-MM-DD]");
            return new Command(false);
        }
        try {
            LocalDate expiryDate = LocalDate.parse(listIngredientMatcher.group(1));
            return new ListIngredientCommand(expiryDate);
        } catch (java.time.format.DateTimeParseException e) {
            logger.log(Level.WARNING, "Invalid expiry date format for list-i: "
                    + listIngredientMatcher.group(1));
            Ui.printError("Invalid expiry date format. Use: YYYY-MM-DD");
            return new Command(false);
        }
    }

    private Command parseDeleteI(String input) {
        String deleteInput = input.substring("delete-i".length()).trim();
        String[] parts = deleteInput.split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            Ui.printError("Invalid delete-i format. Use: delete-i INDEX/NAME [QUANTITY]");
            return new Command(false);
        }
        if (parts.length == 1) {
            return new DeleteIngredientCommand(parts[0], ui);
        } else if (parts.length == 2) {
            try {
                double quantity = Double.parseDouble(parts[1]);
                if (quantity <= 0) {
                    Ui.printError("Quantity must be a positive number.");
                    return new Command(false);
                }
                return new DeleteIngredientCommand(parts[0], quantity);
            } catch (NumberFormatException e) {
                Ui.printError("Invalid quantity for delete-i.");
                return new Command(false);
            }
        } else {
            Ui.printError("Invalid delete-i format. Use: delete-i INDEX/NAME [QUANTITY]");
            return new Command(false);
        }
    }

    private Command parseAddI(String input) {
        logger.log(Level.FINE, "Received add-i request");
        String addIngredientInput = input.substring("add-i".length()).trim();

        LocalDate expiryDate = null;
        Pattern expiryPattern = Pattern.compile("(.*)\\s+ex/(.*)");
        Matcher expiryMatcher = expiryPattern.matcher(addIngredientInput);
        if (expiryMatcher.matches()) {
            addIngredientInput = expiryMatcher.group(1);
            String expiryDateInput = expiryMatcher.group(2);
            if (!expiryDateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                logger.log(Level.WARNING, "Invalid expiry date format: " + expiryDateInput);
                Ui.printError("Invalid expiry date format. Use: YYYY-MM-DD");
                return new Command(false);
            }
            try {
                expiryDate = LocalDate.parse(expiryDateInput);
            } catch (java.time.format.DateTimeParseException e) {
                logger.log(Level.WARNING, "Invalid expiry date format: " + expiryDateInput);
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

        if (!name.matches("[a-zA-Z0-9\\s]+")) {
            logger.log(Level.WARNING, "Ingredient name contains special characters");
            Ui.printError("Ingredient name should not contain special characters.");
            return new Command(false);
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                logger.log(Level.WARNING, "Invalid quantity: " + quantityStr);
                Ui.printError("Quantity must be a positive number.");
                return new Command(false);
            }
            logger.log(Level.FINE, "Creating add-i command for: " + name);
            return new AddIngredientCommand(name, quantity, unit, expiryDate);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid quantity format: " + quantityStr);
            Ui.printError("Invalid quantity format.");
            return new Command(false);
        }
    }

    private Command parseAddR(String input) {
        logger.log(Level.INFO, "Received add-r request");
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ArrayList<String> steps = new ArrayList<>();
        String addRecipeInput = input.substring("add-r".length()).trim();
        Pattern addRecipePattern = Pattern.compile("^(.*?)\\s+i/(.+?)\\s+s/(.+?)\\s+t/(-?\\d+)\\s+c/(-?\\d+)$");
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

        if (name.trim().isEmpty()) {
            Ui.printError("Recipe name and steps cannot be empty.");
            logger.log(Level.INFO, "Caught invalid add-r command format in required text fields");
            return new Command(false);
        }

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
            String step = stripOptionalBraces(stepMatcher.group());
            if (step.trim().isEmpty()) {
                Ui.printError("Recipe name and steps cannot be empty.");
                logger.log(Level.INFO, "Caught invalid add-r command format in required text fields");
                return new Command(false);
            }
            steps.add(step);
        }

        if (steps.isEmpty()) {
            Ui.printError("Recipe name and steps cannot be empty.");
            logger.log(Level.INFO, "Caught invalid add-r command format in required text fields");
            return new Command(false);
        }

        return new AddRecipeCommand(name, ingredients, steps, time, calories);
    }

    private Command parseFilterR(String input) {
        logger.log(Level.INFO, "Received filter-r request");
        String filterInput = input.substring("filter-r".length()).trim();

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

        return new FilterRecipeCommand(maxTime, maxCalories);
    }

    private Command parseCook(String input) {
        logger.log(Level.INFO, "Received cook request");
        String cookArgs = input.substring("cook".length()).trim();
        try {
            int index = Integer.parseInt(cookArgs) - 1;
            return new CookCommand(false, index);
        } catch (NumberFormatException e) {
            Ui.printError("You should indicate the index of the recipe when cooking!");
            return new Command(false);
        }
    }

    private Command parseSortR(String input) {
        logger.log(Level.INFO, "Received sort-r request");
        String criteria = input.substring("sort-r".length()).trim();
        if (!criteria.equals("n/") && !criteria.equals("t/") && !criteria.equals("c/")) {
            Ui.printError("Invalid sort-r format. Use: sort-r n/ | t/ | c/");
            return new Command(false);
        }
        return new SortRecipeCommand(criteria);
    }

    private Command parseSortI() {
        logger.log(Level.INFO, "Received sort-i request");
        return new SortInventoryCommand(false);
    }

    private Command parseSearchR(String input) {
        logger.log(Level.INFO, "Received search-r request");
        String query = input.substring("search-r".length()).trim();
        if (query.isEmpty()) {
            Ui.printError("Please provide a search query. Use: search-r QUERY");
            return new Command(false);
        }
        return new SearchRecipeCommand(query);
    }

    private Command parseSearchI(String input) {
        logger.log(Level.INFO, "Received search-i request");
        String query = input.substring("search-i".length()).trim();
        if (query.isEmpty()) {
            Ui.printError("Please provide a search query. Use: search-i QUERY");
            return new Command(false);
        }
        return new SearchIngredientCommand(query);
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
