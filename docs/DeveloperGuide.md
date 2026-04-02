# Developer Guide

## Acknowledgements

This project is based on the [AddressBook-Level3 (AB3)](https://se-education.org/addressbook-level3/)
project created by the [SE-EDU initiative](https://se-education.org).

## Design & implementation

### `recommend-r` — Ingredient-based Recipe Recommendation

#### Overview

The `recommend-r` command recommends recipes that the user can make given a specific ingredient                                                                       
currently in their inventory. It checks that the ingredient exists in the inventory and that the
recipe's required quantity does not exceed what is available.

**Command format:** `recommend-r n/INGREDIENT_NAME`

  ---

#### Implementation

The feature involves four classes:

| Class | Role |
|---|---|
| `Parser` | Parses raw input, validates format, and constructs a `RecommendRecipeCommand` |
| `RecommendRecipeCommand` | Executes the recommendation logic |
| `Inventory` | Provides access to current ingredient stocks |
| `RecipeBook` | Provides access to all known recipes |

**Step-by-step execution:**

1. The user enters `recommend-r n/<ingredient>`.
2. `Parser.parse()` verifies the `n/` prefix and extracts the ingredient name. If the format is
   invalid or the name is empty, an error is printed and a no-op `Command` is returned.
3. A `RecommendRecipeCommand` is constructed with the ingredient name.
4. `SudoCook` detects the command type and calls `cmd.execute(inventory, recipes)`.
5. Inside `execute()`:
    - The inventory is searched linearly for a case-insensitive match. The available quantity is recorded.
    - If the ingredient is not found, `Ui.printError()` is called and execution stops.
    - Otherwise, each recipe in `RecipeBook` is inspected. A recipe qualifies if it contains the
      ingredient **and** requires a quantity ≤ the available amount.
    - If no recipe qualifies, a "No recipes meet the requirement" message is printed; otherwise the
      list of matching recipe names is printed.

Key snippet from `RecommendRecipeCommand`:

```text
  for (int i = 0; i < recipes.size(); i++) {
      Recipe recipe = recipes.getRecipe(i);
      for (Ingredient ing : recipe.getIngredients()) {
          if (ing.getName().equalsIgnoreCase(ingredientName)
                  && ing.getQuantity() <= amount) {
              count += 1;
              sb.append(count).append(". ").append(recipe.getName()).append("\n");
              break;
          }
      }
  }
```

  ---

#### Sequence Diagram

![Recommend Recipe Sequence Diagram](team/RecommendSD-0.png)

*Figure 1: Sequence Diagram for the `recommend-r` command*

  ---

#### Design Considerations

**Aspect: Case sensitivity of ingredient matching**

| Option | Pros | Cons |
|---|---|---|
| Case-insensitive (current) | User-friendly; `Sugar`, `sugar`, `SUGAR` all match | Slight overhead from `equalsIgnoreCase()` |
| Case-sensitive | Simpler comparison | Error-prone for users; `sugar` would not match `Sugar` |

*Decision:* Case-insensitive matching was chosen to reduce user friction.

  ---

**Aspect: Quantity comparison**

| Option | Pros | Cons |
|---|---|---|
| `required ≤ available` (current) | Includes recipes the user has just enough for | Cannot account for partial use in the same session |
| `required < available` | Leaves a buffer | Unnecessarily excludes exact-match recipes |

*Decision:* `≤` comparison is used so that a recipe requiring exactly the available quantity is still recommended.

  ---

**Aspect: Searching strategy**

| Option | Pros | Cons |
|---|---|---|
| Linear scan (current) | Simple; no extra data structure needed | O(n·m) where n = recipes, m = ingredients per recipe |
| Pre-built index (ingredient → recipes) | O(1) lookup per ingredient | Added complexity; index must stay in sync |

*Decision:* Linear scan is sufficient for the expected data sizes. An index can be introduced if performance becomes a concern.

### `list-r` and `view-r` — Recipe Listing and Viewing

#### Overview

Two commands are provided for browsing recipes:

- `list-r` prints a compact numbered list of recipe names only, giving the user a quick overview.
- `view-r` prints full recipe details (ingredients and steps). It can be used with or without an index.

**Command formats:**
- `list-r` — lists all recipe names
- `view-r` — shows full details for all recipes
- `view-r INDEX` — shows full details for the recipe at the given 1-based index

---

#### Implementation

Both commands delegate to `RecipeBook` via `ListRecipeCommand` and `ViewRecipeCommand` respectively.

| Class | Role |
|---|---|
| `Parser` | Detects `list-r` or `view-r` prefix and constructs the appropriate command |
| `ListRecipeCommand` | Calls `RecipeBook.listRecipe()` |
| `ViewRecipeCommand` | Calls `RecipeBook.viewRecipe()` or `RecipeBook.viewRecipe(index)` |
| `RecipeBook` | Builds and prints the output string |

**`list-r` execution:**

1. `RecipeBook.listRecipe()` iterates over all recipes and appends only the name of each to a `StringBuilder`.
2. The result is printed via `Ui.printGradientMessage()`.

**`view-r` execution:**

1. If no index is given, `RecipeBook.viewRecipe()` iterates over all recipes and appends the full `toString()` of each (with a numbered prefix) to a `StringBuilder`.
2. If an index is given, `RecipeBook.viewRecipe(int index)` validates the index and prints the single recipe's `toString()` directly.
3. Invalid indices print an error via `Ui.printError()`.

#### Sequence Diagrams

![List Recipe Sequence Diagram](team/ListRecipe.png)

*Figure 5: Sequence Diagram for the `list-r` command*

![View Recipe Sequence Diagram](team/ViewRecipe.png)

*Figure 6: Sequence Diagram for the `view-r` command*

---

#### Design Considerations

**Aspect: Separating list and view into two commands**

| Option | Pros | Cons |
|---|---|---|
| Separate `list-r` (names) and `view-r` (details) (current) | Quick overview with `list-r`; full details on demand with `view-r` | Two commands to remember |
| Single command always showing full details | Fewer commands | Clutters output when the user only wants a name reminder |

*Decision:* Splitting the commands keeps everyday browsing fast while still allowing full detail inspection when needed.

---

### `cook` - Cook a Recipe

#### Overview

The `cook` command prepares a recipe by consuming the required ingredients from the user's
inventory. It first checks that the requested recipe exists and that every required ingredient is
available in sufficient quantity before any inventory updates are made.

**Command format:** `cook INDEX`

  ---

#### Implementation

The feature involves four main classes:

| Class | Role |
|---|---|
| `Parser` | Parses raw input, validates the recipe index, and constructs a `CookCommand` |
| `CookCommand` | Validates ingredient availability and performs the cooking logic |
| `RecipeBook` | Provides access to the recipe selected by the user |
| `Inventory` | Stores ingredient quantities and is updated after a successful cook |

**Step-by-step execution:**

1. The user enters `cook <index>`.
2. `Parser.parse()` detects the `cook` prefix, parses the recipe index, converts it from 1-based to
   0-based form, and constructs a `CookCommand`.
3. If the index is not a valid number, an error is printed and a no-op `Command` is returned.
4. `SudoCook` detects the command type, retrieves the target recipe using
   `recipes.getRecipe(cmd.getIndex())`, and calls `cmd.execute(recipe, inventory)`.
5. Inside `execute()`:
    - If the recipe is `null`, execution stops immediately. This happens when the requested index is
      out of bounds.
    - The recipe's ingredient list is checked first to ensure every required ingredient exists in
      the inventory and has enough quantity.
    - If any ingredient is missing or insufficient, `Ui.printError()` is called and the inventory
      remains unchanged.
    - If all checks pass, the required quantities are removed from the inventory and a success
      message is printed.

Key snippet from `CookCommand`:

```text
  for (Ingredient i : recipe.getIngredients()) {
      int ingredientIndex = inventory.findIndexByName(i.getName());
      if (ingredientIndex < 0
              || inventory.getIngredient(ingredientIndex).getQuantity() < i.getQuantity()) {
          throw new RuntimeException("Not enough ingredients");
      }
  }

  for (Ingredient i : recipe.getIngredients()) {
      Command c = new DeleteIngredientCommand(i.getName(), i.getQuantity());
      c.execute(inventory);
  }
```

  ---

#### Sequence Diagram

![Cook Sequence Diagram](team/cook.png)

*Figure 2: Sequence Diagram for the `cook` command*

  ---

#### Design Considerations

**Aspect: Indexing of recipes**

| Option | Pros | Cons |
|---|---|---|
| 1-based user input, converted internally (current) | Matches how recipes are shown in lists; more natural for users | Requires conversion before lookup |
| 0-based user input | Aligns directly with internal storage | Less intuitive for end users |

*Decision:* 1-based indexing was chosen for the user-facing command because recipe lists are also
displayed starting from 1.

  ---

**Aspect: Inventory update strategy**

| Option | Pros | Cons |
|---|---|---|
| Validate all ingredients before removal (current) | Prevents partial updates; preserves consistency on failure | Requires two passes over the ingredient list |
| Remove ingredients as they are checked | Slightly simpler flow | Can leave inventory partially updated if a later ingredient is missing |

*Decision:* Validation is performed before removal so `cook` behaves as an all-or-nothing operation.

  ---

**Aspect: Reusing deletion logic**

| Option | Pros | Cons |
|---|---|---|
| Reuse `DeleteIngredientCommand` for quantity removal (current) | Avoids duplicating inventory update logic | Adds an extra command object per ingredient |
| Update `Inventory` directly inside `CookCommand` | Fewer intermediate objects | Duplicates removal behavior and message handling |

*Decision:* Reusing `DeleteIngredientCommand` keeps ingredient-removal behavior centralized even
though it adds a small amount of indirection.

### `sort-i` - Sort Inventory by Expiry Date

#### Overview

The `sort-i` command sorts the inventory so that ingredients with earlier expiry dates appear
first. Ingredients without an expiry date are placed at the end of the list.

**Command format:** `sort-i`

  ---

#### Implementation

The feature involves four main classes:

| Class | Role |
|---|---|
| `Parser` | Detects the `sort-i` prefix and constructs a `SortInventoryCommand` |
| `SortInventoryCommand` | Delegates sorting to `Inventory` and prints a confirmation message |
| `Inventory` | Stores ingredients and performs the in-place sort |
| `Ui` | Displays the success message |

**Step-by-step execution:**

1. The user enters `sort-i`.
2. `Parser.parse()` detects the prefix and constructs a `SortInventoryCommand`.
3. `SudoCook` detects the command type and calls `cmd.execute(inventory)`.
4. Inside `execute()`:
    - `Inventory.sortIngredients()` sorts the internal ingredient list by expiry date.
    - `Ui.printMessage("Sorted!")` is called to confirm completion.

Key snippet from `SortInventoryCommand`:

```text
  public void execute (Inventory ingredients){
      ingredients.sortIngredients();
      Ui.printMessage("Sorted!");
  }
```

  ---

#### Sequence Diagram

![Sort Inventory Sequence Diagram](team/SortInventory.png)

*Figure 3: Sequence Diagram for the `sort-i` command*

  ---

#### Design Considerations

**Aspect: Sorting criterion**

| Option | Pros | Cons |
|---|---|---|
| Sort by expiry date with `null` values last (current) | Helps users prioritise ingredients that expire sooner | Less useful when many ingredients have no expiry date |
| Sort alphabetically by name | Easy to scan for a specific ingredient | Does not help with expiry-based planning |

*Decision:* Sorting by expiry date is more useful for kitchen inventory management because it
surfaces ingredients that should be used sooner.

  ---

**Aspect: Location of sorting logic**

| Option | Pros | Cons |
|---|---|---|
| Keep sorting in `Inventory` (current) | Keeps data manipulation close to the stored list; command stays simple | Sort order is defined in the inventory layer |
| Implement sorting in `SortInventoryCommand` | Makes the command self-contained | Mixes orchestration with collection logic |

*Decision:* The sorting logic is kept in `Inventory` so command classes remain focused on
triggering behaviour rather than manipulating internal data structures directly.

### `list-i` - List Inventory Ingredients

#### Overview

The `list-i` command displays the ingredients currently stored in the inventory. It supports two
variants: listing all ingredients, or listing only ingredients whose expiry date is before a given
cutoff.

**Command formats:**
- `list-i`
- `list-i ex/YYYY-MM-DD`

  ---

#### Implementation

The feature involves four main classes:

| Class | Role |
|---|---|
| `Parser` | Detects the `list-i` prefix, validates the optional expiry cutoff, and constructs a `ListIngredientCommand` |
| `ListIngredientCommand` | Retrieves the ingredients to display, optionally filters them, and builds the output |
| `Inventory` | Provides the stored ingredient list |
| `Ui` | Displays either the ingredient list or an empty-state message |

**Step-by-step execution:**

1. The user enters either `list-i` or `list-i ex/<date>`.
2. `Parser.parse()` detects the `list-i` prefix.
3. If no additional argument is provided, `Parser` constructs `new ListIngredientCommand()`.
4. If an expiry cutoff is provided, `Parser` validates the `ex/YYYY-MM-DD` format, parses the date,
   and constructs `new ListIngredientCommand(expiryDate)`.
5. If the format or date is invalid, an error is printed and a no-op `Command` is returned.
6. `SudoCook` detects the command type and calls `cmd.execute(inventory)`.
7. Inside `execute()`:
    - `Inventory.getIngredients()` is called to retrieve the stored ingredients.
    - If an expiry cutoff exists, ingredients are filtered to those with a non-null expiry date
      before the cutoff.
    - If the resulting list is empty, `Ui.printMessage()` prints an empty-state message.
    - Otherwise, a numbered list with the appropriate header is built and printed.

Key snippet from `ListIngredientCommand`:

```text
  ArrayList<Ingredient> ingredients = inventory.getIngredients();
  if (expiry == null) {
      return ingredients;
  }

  ArrayList<Ingredient> filteredIngredients = new ArrayList<>();
  for (Ingredient ingredient : ingredients) {
      LocalDate ingredientExpiry = ingredient.getExpiryDate();
      if (ingredientExpiry != null && ingredientExpiry.isBefore(expiry)) {
          filteredIngredients.add(ingredient);
      }
  }
```

  ---

#### Sequence Diagram

![List Ingredients Sequence Diagram](team/ListIngredients.png)

*Figure 4: Sequence Diagram for the `list-i` command*

  ---

#### Design Considerations

**Aspect: Supporting filtered and unfiltered listing**

| Option | Pros | Cons |
|---|---|---|
| Single command with an optional expiry cutoff (current) | Keeps the interface compact; both variants share the same execution path | Parser logic is slightly more complex |
| Separate commands for full listing and filtered listing | Simpler parsing per command | Adds another command for users to remember |

*Decision:* A single command with an optional cutoff keeps the user interface smaller while still
covering both use cases.

  ---

**Aspect: Handling ingredients without expiry dates in filtered mode**

| Option | Pros | Cons |
|---|---|---|
| Exclude ingredients with `null` expiry dates (current) | Keeps the filtered result precise; avoids guessing how undated items should compare | Undated ingredients never appear in cutoff-based results |
| Include ingredients with `null` expiry dates | Ensures no ingredient is hidden | Makes "expiring before" results less accurate |

*Decision:* Ingredients without expiry dates are excluded in cutoff mode because the filter is
intended to show only items known to expire before the requested date.

### `add-i` — Add an Ingredient

#### Overview

The `add-i` command adds an ingredient to the user's inventory with optional expiry date tracking.
The user specifies the ingredient name, quantity, and unit of measurement. An expiry date in
YYYY-MM-DD format can optionally be provided to help track when ingredients should be used.

**Command format:** `add-i n/NAME q/QUANTITY u/UNIT [ex/YYYY-MM-DD]`

---

#### Implementation

The feature involves four main classes:

| Class | Role |
|---|---|
| `Parser` | Parses raw input, extracts name/quantity/unit/expiry, validates format, and constructs an `AddIngredientCommand` |
| `AddIngredientCommand` | Delegates to `Inventory.addIngredient()` to store the ingredient |
| `Inventory` | Stores ingredients and provides access to the ingredient list |
| `Ui` | Displays success or error messages |

**Step-by-step execution:**

1. The user enters `add-i n/<name> q/<quantity> u/<unit> [ex/<date>]`.
2. `Parser.parse()` detects the `add-i` prefix and uses regex to extract the name, quantity, unit,
   and optional expiry date fields.
3. If the expiry date is provided, it is validated against the YYYY-MM-DD format. If invalid, an
   error is printed and a no-op `Command` is returned.
4. The quantity is validated to ensure it is a positive number. If invalid, an error is printed
   and a no-op `Command` is returned.
5. An `AddIngredientCommand` is constructed with the parsed values.
6. `SudoCook` detects the command type and calls `cmd.execute(inventory)`.
7. Inside `execute()`:
    - `Inventory.addIngredient(name, quantity, unit, expiryDate)` adds the ingredient to the list.
    - A success message is printed via `Ui.printMessage()`.

Key snippet from `AddIngredientCommand`:

```text
  public void execute(Inventory inventory) {
      inventory.addIngredient(name, quantity, unit, expiryDate);
      Ui.printMessage("Added " + name + " (" + quantity + " " + unit + ")"
          + (expiryDate != null ? " expires: " + expiryDate : ""));
  }
```

---

#### Design Considerations

**Aspect: Expiry date requirement**

| Option | Pros | Cons |
|---|---|---|
| Optional expiry date (current) | Supports items without known expiry; flexible for non-perishables | Users may forget to enter expiry for perishables |
| Mandatory expiry date | Ensures all items are tracked with expiry | Less flexible for indefinite items like spices |

*Decision:* Making expiry date optional provides maximum flexibility while still supporting expiry
tracking where it matters most.

---

**Aspect: Quantity validation**

| Option | Pros | Cons |
|---|---|---|
| Allow decimals (current) | Accommodates fractional measurements like 2.5 cups | Slightly more complex parsing and validation |
| Integer quantities only | Simpler validation | Loses precision for recipes using fractions |

*Decision:* Decimal quantities were chosen to support common cooking measurements that often involve
fractions.

---

### `filter-r` — Filter Recipes

#### Overview

The `filter-r` command allows users to filter recipes based on maximum preparation time and/or
maximum calorie count. Both filter criteria are optional and can be used independently or combined.

**Command format:** `filter-r [t/MAX_TIME] [c/MAX_CALORIES]`

---

#### Implementation

The feature involves three classes:

| Class | Role |
|---|---|
| `Parser` | Detects the `filter-r` prefix, extracts optional `t/` and `c/` values using regex, and constructs a `FilterRecipeCommand` |
| `FilterRecipeCommand` | Stores the filter criteria and delegates to `RecipeBook.filterRecipes()` |
| `RecipeBook` | Iterates over all recipes, applies the time and/or calorie filters, and prints matching results |

**Step-by-step execution:**

1. The user enters `filter-r [t/MAX_TIME] [c/MAX_CALORIES]`.
2. `Parser.parse()` detects the `filter-r` prefix and extracts the optional `t/` and `c/` arguments
   using `Pattern.compile("t/(\\d+)")` and `Pattern.compile("c/(\\d+)")` respectively.
3. If neither argument is provided, an error is printed and a no-op `Command` is returned.
4. A `FilterRecipeCommand` is constructed with `maxTime` and `maxCalories` (both nullable `Integer`).
5. `SudoCook` calls `cmd.execute(recipes)`.
6. Inside `RecipeBook.filterRecipes(maxTime, maxCalories)`:
    - Each recipe is checked against both criteria (if provided).
    - A recipe is kept only if its time ≤ `maxTime` (when set) **and** its calories ≤ `maxCalories`
      (when set).
    - If no recipes match, a "No recipes found" message is printed.
    - Otherwise, the matching recipes are printed in a numbered list.

Key snippet from `RecipeBook`:

```text
  for (Recipe r : recipes) {
      boolean keep = true;
      if (maxTime != null && r.getTime() > maxTime) {
          keep = false;
      }
      if (maxCalories != null && r.getCalories() > maxCalories) {
          keep = false;
      }
      if (keep) {
          filtered.add(r);
      }
  }
```

---

#### Sequence Diagram

![Filter Recipe Sequence Diagram](team/FilterRecipe.png)

*Figure 9: Sequence Diagram for the `filter-r` command*

---

#### Design Considerations

**Aspect: Optional vs. mandatory filter criteria**

| Option | Pros | Cons |
|---|---|---|
| Both optional (current) | Flexible; user can filter by time only, calories only, or both | Parser must handle missing arguments gracefully |
| Both mandatory | Simpler parsing | Forces the user to always specify both, even when only one is relevant |

*Decision:* Making both criteria optional provides maximum flexibility for the user.

---

**Aspect: Filter logic (AND vs. OR)**

| Option | Pros | Cons |
|---|---|---|
| AND logic (current) — recipe must satisfy all provided criteria | Produces more precise, narrowed results | May return fewer results |
| OR logic — recipe must satisfy at least one criterion | Returns more results | Less useful for precise searching |

*Decision:* AND logic was chosen because users who specify multiple criteria typically want to narrow
their search, not broaden it.

---

### `delete-r` — Delete a Recipe

#### Overview

The `delete-r` command removes a recipe from the recipe book by its displayed index. The index
corresponds to the 1-based numbering shown by the `list-r` command.

**Command format:** `delete-r INDEX`

---

#### Implementation

The feature involves three classes:

| Class | Role |
|---|---|
| `Parser` | Detects the `delete-r` prefix, parses the index, and constructs a `DeleteRecipeCommand` |
| `DeleteRecipeCommand` | Calls `RecipeBook.removeRecipe()` and handles success/error output |
| `RecipeBook` | Validates the index and removes the recipe from the internal list |

**Step-by-step execution:**

1. The user enters `delete-r <index>`.
2. `Parser.parse()` detects the `delete-r` prefix and extracts the index. If the index is not a
   valid number, an error is printed and a no-op `Command` is returned.
3. A `DeleteRecipeCommand` is constructed with the parsed index.
4. `SudoCook` routes the command to `cmd.execute(recipes)` via the default recipe routing branch.
5. Inside `execute()`:
    - `RecipeBook.removeRecipe(index)` is called.
    - If the recipe book is empty or the index is out of range, an `IndexOutOfBoundsException` is
      thrown and caught, and an error message is printed via `Ui.printMessage()`.
    - If the index is valid, the recipe is removed from the internal list and a success message is
      printed.

Key snippet from `DeleteRecipeCommand`:

```text
  try {
      recipes.removeRecipe(index);
      Ui.printMessage("Recipe " + index + " deleted successfully.");
  } catch (IndexOutOfBoundsException e) {
      Ui.printMessage("Invalid index: " + e.getMessage());
  }
```

---

#### Sequence Diagram

![Delete Recipe Sequence Diagram](team/DeleteRecipe.png)

*Figure 10: Sequence Diagram for the `delete-r` command*

---

#### Design Considerations

**Aspect: Index-based vs. name-based deletion**

| Option | Pros | Cons |
|---|---|---|
| Index-based deletion (current) | Unambiguous; works even when multiple recipes have similar names | User must first run `list-r` to find the index |
| Name-based deletion | More intuitive for users who remember the recipe name | Ambiguous if multiple recipes share a name; requires exact match or fuzzy logic |

*Decision:* Index-based deletion was chosen for its simplicity and unambiguity, consistent with
how recipes are displayed in numbered lists.

---

**Aspect: Error handling strategy**

| Option | Pros | Cons |
|---|---|---|
| Catch `IndexOutOfBoundsException` (current) | Centralises bounds checking in `RecipeBook`; command stays simple | Uses exceptions for control flow |
| Pre-validate index in the command before calling `removeRecipe()` | Avoids exception-based control flow | Duplicates bounds-checking logic across command and data class |

*Decision:* Exception-based handling was chosen to keep index validation in `RecipeBook`, the
class that owns the recipe list and knows its valid range.

---

### `search-r` and `search-i` — Fuzzy Search for Recipes and Ingredients

#### Overview

Two commands are provided for fuzzy searching by name:

- `search-r QUERY` searches the recipe book for recipes whose names fuzzy-match the query.
- `search-i QUERY` searches the inventory for ingredients whose names fuzzy-match the query.

Unlike exact or substring matching, fuzzy search tolerates typos and partial input, making it more
forgiving for users who cannot recall an exact name.

**Command formats:**
- `search-r QUERY` — e.g. `search-r fried rice`
- `search-i QUERY` — e.g. `search-i tomatto` (typo tolerated)

---

#### Implementation

The feature involves the following classes:

| Class | Role |
|---|---|
| `Parser` | Detects the `search-r` or `search-i` prefix, extracts the query, and constructs the appropriate command |
| `SearchRecipeCommand` | Calls `RecipeBook.searchRecipes(query)` |
| `SearchIngredientCommand` | Calls `Inventory.searchIngredients(query)` |
| `RecipeBook` | Iterates over recipes and prints those whose names pass `FuzzySearch.isMatch()` |
| `Inventory` | Iterates over ingredients and prints those whose names pass `FuzzySearch.isMatch()` |
| `FuzzySearch` | Stateless utility class that computes a fuzzy score between a query and a target string |

**Step-by-step execution (`search-r`):**

1. The user enters `search-r <query>`.
2. `Parser.parse()` detects the `search-r` prefix and extracts the query string. If it is empty, an error is printed and a no-op `Command` is returned.
3. A `SearchRecipeCommand` is constructed with the query.
4. `SudoCook` calls `cmd.execute(recipeBook)` (falls through to the default recipe routing branch).
5. Inside `execute()`, `RecipeBook.searchRecipes(query)` iterates over all recipes and calls `FuzzySearch.isMatch(query, recipe.getName())` for each. Matching recipes are collected and printed.

The `search-i` flow is identical, but targets `Inventory` and `SearchIngredientCommand`.

**Fuzzy scoring in `FuzzySearch`:**

`FuzzySearch.score(query, target)` returns an integer 0–100 using a priority cascade:

| Priority | Condition | Score |
|---|---|---|
| 1 | Exact match (case-insensitive) | 100 |
| 2 | Target contains query as a substring | 90 |
| 3 | Query characters appear as a subsequence in target | 40–80 (scales with coverage) |
| 4 | Levenshtein distance below threshold | 0–60 |

`FuzzySearch.isMatch()` returns `true` when the score is ≥ 40.

Key snippet from `FuzzySearch`:

```text
  if (t.contains(q)) return 90;

  int subseq = subsequenceScore(q, t);
  if (subseq > 0) return subseq;

  int dist = levenshtein(q, t);
  int maxLen = Math.max(q.length(), t.length());
  return Math.max(0, (int) ((1.0 - (double) dist / maxLen) * 60));
```

---

#### Sequence Diagrams

![Search Recipe Sequence Diagram](team/SearchRecipe.png)

*Figure 7: Sequence Diagram for the `search-r` command*

![Search Ingredient Sequence Diagram](team/SearchIngredient.png)

*Figure 8: Sequence Diagram for the `search-i` command*

---

#### Design Considerations

**Aspect: Scoring strategy**

| Option | Pros | Cons |
|---|---|---|
| Priority cascade (exact → substring → subsequence → Levenshtein) (current) | Fast short-circuit for common cases; graceful degradation for typos | Scores are heuristic, not a formal similarity metric |
| Pure Levenshtein distance only | Simple and well-understood | Penalises partial queries (e.g. `rice` searching `Fried Rice`) |
| External fuzzy library | Battle-tested and feature-rich | Adds a dependency; overkill for a small dataset |

*Decision:* A pure-Java cascade was chosen to avoid external dependencies while still handling the most common search patterns (substring and typo).

---

**Aspect: Match threshold**

| Option | Pros | Cons |
|---|---|---|
| Threshold at 40 (current) | Accepts subsequence matches and minor typos; rejects poor matches | May include weak matches for very short queries |
| Higher threshold (e.g. 60) | More precise results | Rejects useful subsequence/typo matches |
| No threshold (return all scored) | Maximum recall | Clutters results with irrelevant entries |

*Decision:* 40 was chosen as it is the minimum score awarded to a full character-subsequence match, giving a natural cutoff between recognisable and unrecognisable matches.

---

**Aspect: Where to place fuzzy logic**

| Option | Pros | Cons |
|---|---|---|
| Separate `FuzzySearch` utility class (current) | Single responsibility; reusable across recipe and ingredient search | One extra file |
| Inline inside `RecipeBook` / `Inventory` | Fewer files | Duplicates logic; harder to test or modify |

*Decision:* A dedicated utility class keeps scoring logic testable and reusable without coupling it to any particular data class.

---

### `help` — Help Command

#### Overview

The `help` command provides users with a comprehensive guide of all available commands, their
purposes, and correct formats. This is particularly useful for new users or for reminding seasoned
users of exact command syntax.

**Command format:** `help`

---

#### Implementation

The `help` feature is implemented using a simple command pattern that bridges to the UI layer:

| Class | Role |
|---|---|
| `Parser` | Detects the `help` keyword and constructs a `HelpCommand` |
| `HelpCommand` | Contains the help message string and triggers its display |
| `Ui` | Outputs the help message to the terminal |

**Step-by-step execution:**

1. The user enters `help`.
2. `Parser.parse()` detects the keyword and returns a new `HelpCommand`.
3. `SudoCook` receives the command and calls `cmd.execute(inventory)`.
4. Inside `execute()`, the `helpMessage` string is passed to `Ui.printMessage()`.

---

#### Sequence Diagram

![Help Sequence Diagram](team/Help.png)

*Figure 11: Sequence Diagram for the `help` command*

---

#### Design Considerations

**Aspect: Centralisation of help information**

| Option | Pros | Cons |
|---|---|---|
| Store help text in `HelpCommand` (current) | Command is self-contained; easy to find and edit the "source of truth" | Slightly increases the size of the command class |
| Separate help text into a text file or resource | Keeps code clean; allows for localization | Requires additional file I/O and complicates deployment/JAR packaging |

*Decision:* Storing the help message directly as a static string in `HelpCommand` was chosen for its
simplicity and lack of external dependencies, ensuring the help feature always works even if the
filesystem is restricted.

---

### `undo` — Undo Commands

#### Overview

The `undo` command reverts the most recent command that modified the inventory or recipes. It maintains
a history of up to 50 command snapshots, allowing users to undo multiple changes in sequence. Read-only
commands (like `list-r`, `search-i`, `help`) and their execution side effects are not undoable.

**Command format:** `undo`

---

#### Implementation

The feature involves four main classes:

| Class | Role |
|---|---|
| `Parser` | Detects the `undo` keyword and constructs an `UndoCommand` |
| `UndoCommand` | Restores the previous inventory and recipe state from history |
| `CommandHistory` | Manages a queue of up to 50 state snapshots (inventory + recipes) |
| `SudoCook` | Saves state snapshots before executing modifying commands |

**Step-by-step execution:**

1. The user enters `undo`.
2. `Parser.parse()` detects the `undo` keyword and constructs a new `UndoCommand`.
3. `SudoCook` detects the command type and calls `cmd.execute(history, recipes, inventory)`.
4. Inside `execute()`:
    - `CommandHistory.canUndo()` checks if there are any saved snapshots.
    - If no snapshots exist, a "No commands to undo" message is printed.
    - If snapshots exist, the most recent snapshot is retrieved and restored to both `recipes` and
      `inventory`.
    - A success message is printed via `Ui.printMessage()`.

Key snippet from `UndoCommand`:

```text
  public void execute(CommandHistory history, RecipeBook recipes, Inventory inventory) {
      if (!history.canUndo()) {
          Ui.printMessage("No commands to undo.");
          return;
      }
      CommandHistory.Snapshot snapshot = history.undo();
      recipes.restoreState(snapshot.getRecipes());
      inventory.restoreState(snapshot.getIngredients());
      Ui.printMessage("Command undone successfully!");
  }
```

---

#### Design Considerations

**Aspect: History size limit**

| Option | Pros | Cons |
|---|---|---|
| Fixed limit of 50 snapshots (current) | Prevents unbounded memory growth; reasonable for typical session | Users cannot undo very old commands |
| Unbounded history | Users can undo any past command | Memory usage grows without limit over time |
| Configurable limit | Flexible; can be tuned per system | Adds complexity to configuration |

*Decision:* A fixed limit of 50 snapshots was chosen as a practical balance between usability and
memory efficiency. Users can typically revisit recent changes without excessive memory overhead.

---

**Aspect: What to undo**

| Option | Pros | Cons |
|---|---|---|
| Only modifying commands (current) | Keeps history focused on state-changing operations; read-only commands don't interfere | Users cannot undo side effects of read-only commands |
| All commands including read-only | Truly reverses every action | Adds unnecessary entries to history; clutters UI |

*Decision:* Only modifying commands are placed in history. Read-only commands like `list-i` and
`search-r` do not alter state, so undoing them would be meaningless.

---

**Aspect: Automatic snapshot timing**

| Option | Pros | Cons |
|---|---|---|
| Save snapshot before each modifying command (current) | Simple and deterministic; undo always reverses the last modification | Minor overhead for every command |
| Snapshot only on explicit `save` command | Gives users control over what to preserve | Requires users to manually checkpoint, which is cumbersome |

*Decision:* Automatic snapshots before each modifying command were chosen because users expect `undo`
to work immediately without manual setup, and the performance overhead is negligible.

---

## Product scope
### Target user profile

A single student living independently (e.g., in a campus dorm) who types fast and prefers
keyboard-driven workflows over mouse/touch input. This student enjoys cooking but often gets
frustrated by disorganised ingredients and indecision about what to cook.

### Value proposition

SudoCook is a cross-platform, portable, command-line pantry and recipe helper that reduces food
waste by letting the user quickly log ingredients and expiry dates, and reduces meal indecision by
suggesting recipes based on what’s currently in the pantry and the user’s available cooking time. All
data is stored locally in a human-editable JSON file and managed through an object-oriented Java 17
codebase packaged as a single runnable JAR, with no DBMS and no reliance on remote servers.

## User Stories

| Version | As a ... | I want to ... | So that I can ... |
|---------|----------|---------------|-------------------|
| v1.0 | Busy Student | Add an item and expiry date using a single short command | I can digitize my pantry quickly after grocery shopping |
| v1.0 | Novice Cook | View step-by-step instructions for a specific recipe | I can follow the process accurately and complete the dish |
| v1.0 | User | Delete items quickly | My inventory list remains accurate after I throw things away or use them |
| v1.0 | User | View all ingredients | I know what ingredients have been added so far |
| v1.0 | User | Add a recipe | I don’t have to rely on my memory for instructions |
| v1.0 | User | Delete a recipe | I can keep my recipe list clean and organized |
| v2.0 | Budget-conscious Student | List all items sorted by their expiry dates | I can prioritize ingredients about to spoil and avoid wasting money |
| v2.0 | Indecisive Student | Request recipe suggestions based on current stock | I don’t have to spend mental energy deciding what to cook |
| v2.0 | Power User | Mark a recipe as “cooked” to auto-deduct ingredients | My stock levels remain accurate with minimal manual adjustment |
| v2.0 | Health-conscious Student | Filter recipes by calorie count | I can maintain nutritional goals without manual calculations |

## Non-Functional Requirements

1. The application should work on any mainstream OS (Windows, macOS, Linux) with Java 17 installed.
2. A user with above-average typing speed should be able to accomplish most tasks faster than using a GUI-based application.
3. The data files should be human-readable and editable with a plain text editor.
4. The application should respond to user commands within 1 second under normal usage.

## Glossary

* *Recipe* — A named dish with a list of required ingredients, preparation steps, time, and calorie count.
* *Ingredient* — A named item with a quantity, unit, and optional expiry date.
* *Inventory* — The collection of ingredients currently available to the user.
* *Recipe Book* — The collection of all saved recipes.

## Instructions for manual testing

1. **Launch**: Run `java -jar sudocook.jar`. Verify the welcome banner appears.
2. **Add a recipe**: `add-r {Fried Rice} i/rice 2 cups egg 2 pcs s/{Cook rice} {Fry egg} t/15 c/400`. Verify the recipe is added with the correct details.
3. **List recipes**: `list-r`. Verify the recipe name appears in a numbered list.
4. **View recipe**: `view-r 1`. Verify full recipe details including calories are displayed.
5. **Filter recipes**: `filter-r t/20`. Verify only recipes with time ≤ 20 are shown.
6. **Filter by calories**: `filter-r c/300`. Verify only recipes with calories ≤ 300 are shown.
7. **Delete a recipe**: `delete-r 1`. Verify the recipe is removed and `list-r` no longer shows it.
8. **Persistence**: Exit with `bye`, restart the application, and verify saved recipes are loaded.
