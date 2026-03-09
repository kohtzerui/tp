package seedu.sudocook;

import static seedu.sudocook.SudoCook.DELETE_R_PREFIX;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final Ui ui;

    public Parser(Ui ui) {
        this.ui = ui;
    }

    public Command parse(String input){
        Command C;
        if(input.startsWith("delete-r")){
            int index = Integer.parseInt(input.substring(DELETE_R_PREFIX).trim());
            C = new DeleteCommand(index);
        } else if (input.startsWith("list-r")){
            C = new ListCommand();
        } else if (input.startsWith("add-r")) {
            ArrayList<String> ingredients = new ArrayList<>();
            ArrayList<String> steps = new ArrayList<>();
            String addRecipeInput = input.substring("add-r".length()).trim();
            Pattern addRecipePattern = Pattern.compile("^(.*?)\\s+i/(.+?)\\s+s/(.+)$");
            Matcher addRecipeMatcher = addRecipePattern.matcher(addRecipeInput);

            if (!addRecipeMatcher.matches()) {
                ui.printError("Invalid add-r format.");
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

            for (int i = 0; i + 2 < ingredientTokens.size(); i += 3) {
                ingredients.add(ingredientTokens.get(i) + " "
                        + ingredientTokens.get(i + 1) + " "
                        + ingredientTokens.get(i + 2));
            }

            Matcher stepMatcher = tokenPattern.matcher(stepInput);
            while (stepMatcher.find()) {
                steps.add(stripOptionalBraces(stepMatcher.group()));
            }

            C = new AddRecipeCommand(name, ingredients, steps);


        } else {
            C = new Command(false);
            ui.printError("I don't recognise that command!");
        }
        return C;

    }


    private String stripOptionalBraces(String token) {
        if (token.startsWith("{") && token.endsWith("}")) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }
}
