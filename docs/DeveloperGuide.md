# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### `recommend-r` — Recipe Recommendation

#### Overview

The `recommend-r` command supports three modes of recipe recommendation:

- **Ingredient-based mode** (`recommend-r n/INGREDIENT_NAME`): recommends recipes that use a specific
  ingredient, provided the inventory holds enough of it.
- **Inventory-based mode** (`recommend-r`): recommends every recipe whose **full** ingredient list
  can be satisfied by the current inventory — i.e. every required ingredient is present and in
  sufficient quantity.
- **Missing-based mode** (`recommend-r missing/N`): recommends recipes that are missing **at most N**
  ingredients (or insufficient quantities), and shows the exact shortfall for each missing ingredient
  so the user knows what to buy.

**Command formats:**

| Mode | Format | Example |
|---|---|---|
| Ingredient-based | `recommend-r n/INGREDIENT_NAME` | `recommend-r n/egg` |
| Inventory-based | `recommend-r` | `recommend-r` |
| Missing-based | `recommend-r missing/N` | `recommend-r missing/2` |

  ---

#### Implementation

The feature involves six classes:

| Class | Role |
|---|---|
| `Parser` | Parses raw input, selects the correct command variant, and validates format |
| `RecommendByIngredientCommand` | Executes ingredient-based recommendation logic |
| `RecommendByInventoryCommand` | Executes inventory-based recommendation logic |
| `RecommendByMissingCommand` | Executes missing-based recommendation logic |
| `Inventory` | Provides access to current ingredient stocks |
| `RecipeBook` | Provides access to all known recipes |

**Ingredient-based mode — step-by-step execution:**

1. The user enters `recommend-r n/<ingredient>`.
2. `Parser.parse()` detects the `n/` prefix, extracts the ingredient name, and constructs a
   `RecommendByIngredientCommand`. If the format is invalid or the name is empty, an error is printed and
   a no-op `Command` is returned.
3. `SudoCook` calls `cmd.execute(inventory, recipes)`.
4. Inside `execute()`:
    - The inventory is searched linearly for a case-insensitive name match. The available quantity
      is recorded.
    - If the ingredient is not found, `Ui.printError()` is called and execution stops.
    - Otherwise, each recipe in `RecipeBook` is inspected. A recipe qualifies if it contains the
      ingredient **and** requires a quantity ≤ the available amount.
    - If no recipe qualifies, a "No recipes meet the requirement" message is printed; otherwise the
      list of matching recipe names is printed.

Key snippet from `RecommendByIngredientCommand`:

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

**Inventory-based mode — step-by-step execution:**

1. The user enters `recommend-r` (no arguments).
2. `Parser.parse()` detects the absence of arguments and constructs a
   `RecommendByInventoryCommand`.
3. `SudoCook` calls `cmd.execute(inventory, recipes)`.
4. Inside `execute()`, each recipe is evaluated by `canMake(recipe, inventory)`:
    - For every ingredient required by the recipe, the inventory is searched for a
      case-insensitive name match.
    - If the ingredient is absent or the available quantity is less than required, `canMake`
      returns `false` and the recipe is excluded.
    - If all ingredients pass, `canMake` returns `true` and the recipe is appended to the result. 
    - If no recipe is makeable, a "No recipes can be made" message is printed; otherwise the list of 
      makeable recipe names is printed.

Key snippet from `RecommendByInventoryCommand`:

```text
  private boolean canMake(Recipe recipe, Inventory inventory) {
      for (Ingredient required : recipe.getIngredients()) {
          double available = -1;
          for (int j = 0; j < inventory.size(); j++) {
              Ingredient item = inventory.getIngredient(j);
              if (item.getName().equalsIgnoreCase(required.getName())) {
                  available = item.getQuantity();
                  break;
              }
          }
          if (available < required.getQuantity()) {
              return false;
          }
      }
      return true;
  }
```

  ---

**Missing-based mode — step-by-step execution:**

1. The user enters `recommend-r missing/<N>`.
2. `Parser.parse()` detects the `missing/` prefix, extracts and validates `N` as a positive integer,
   and constructs a `RecommendByMissingCommand(N)`. If `N` is not a positive integer, an error is
   printed and a no-op `Command` is returned.
3. `SudoCook` calls `cmd.execute(inventory, recipes)`.
4. Inside `execute()`, each recipe is evaluated by `getMissingIngredients(recipe, inventory)`:
    - For every ingredient required by the recipe, the inventory is searched for a case-insensitive
      name match.
    - If the ingredient is absent or the available quantity is less than required, the shortfall
      (`required quantity − available quantity`) and unit are recorded.
    - The method returns the list of formatted shortfall strings (e.g. `"Salt (1.0 g)"`).
5. Back in `execute()`, the recipe is included in the output only if the number of missing items is
   **between 1 and N** (inclusive). Recipes with zero missing items — i.e. fully makeable ones —
   are always excluded.
6. If no recipe qualifies, a "No recipes found" message is printed; otherwise the numbered list
   with per-recipe shortfall details is printed.

Key snippet from `RecommendByMissingCommand`:

```text
  private ArrayList<String> getMissingIngredients(Recipe recipe, Inventory inventory) {
      ArrayList<String> missing = new ArrayList<>();
      for (Ingredient required : recipe.getIngredients()) {
          double available = 0;
          for (int j = 0; j < inventory.size(); j++) {
              Ingredient item = inventory.getIngredient(j);
              if (item.getName().equalsIgnoreCase(required.getName())) {
                  available = item.getQuantity();
                  break;
              }
          }
          if (available < required.getQuantity()) {
              double shortfall = required.getQuantity() - available;
              missing.add(required.getName() + " (" + shortfall + " " + required.getUnit() + ")");
          }
      }
      return missing;
  }
```

  ---

#### Sequence Diagrams

![Recommend Recipe Sequence Diagram](team/RecommendSD.png)

*Figure 1: Sequence Diagram for `recommend-r n/INGREDIENT_NAME` (ingredient-based mode)*

<br>
<br>

![Recommend By Inventory Sequence Diagram](team/RecommendByInventorySD.png)

*Figure 2: Sequence Diagram for `recommend-r` (inventory-based mode)*

  ---

#### Design Considerations

**Aspect: Two modes under one command vs. separate commands**

| Option | Pros | Cons |
|---|---|---|
| Single `recommend-r` command with optional `n/` argument (current) | Consistent command prefix; easier to discover both modes via `help` | Slightly more complex parsing logic |
| Separate commands (e.g. `recommend-r` and `recommend-all`) | Fully independent; no shared parsing | More commands for the user to remember |

*Decision:* Keeping both modes under `recommend-r` provides a natural extension of the existing
command and keeps the help output concise.

  ---

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

#### Design Considerations

**Aspect: Separating list and view into two commands**

| Option | Pros | Cons |
|---|---|---|
| Separate `list-r` (names) and `view-r` (details) (current) | Quick overview with `list-r`; full details on demand with `view-r` | Two commands to remember |
| Single command always showing full details | Fewer commands | Clutters output when the user only wants a name reminder |

*Decision:* Splitting the commands keeps everyday browsing fast while still allowing full detail inspection when needed.

---

### `delete-r` — Delete a Recipe

#### Overview

The `delete-r` command permanently removes a recipe from the recipe book by its 1-based index.

**Command format:** `delete-r INDEX`

  ---

#### Implementation

The feature involves three classes:

| Class | Role |
|---|---|
| `Parser` | Parses raw input, validates that the index is a number, and constructs a `DeleteRecipeCommand` |
| `DeleteRecipeCommand` | Calls `RecipeBook.removeRecipe()` with the given index |
| `RecipeBook` | Validates the index range and performs the removal |

**Step-by-step execution:**

1. The user enters `delete-r <index>`.
2. `Parser.parse()` detects the `delete-r` prefix and extracts the index using the constant
   `DELETE_R_PREFIX` (= 8, the length of `"delete-r"`).
3. The extracted string is parsed as an integer. If it is not a valid number, an error is printed
   and a no-op `Command` is returned.
4. A `DeleteRecipeCommand` is constructed with the 1-based index.
5. `SudoCook` routes the command to `cmd.execute(recipes)`.
6. Inside `execute()`:
    - `RecipeBook.removeRecipe(index)` is called.
    - If the index is outside the valid range (1 to size), an `IndexOutOfBoundsException` is
      thrown, caught, and reported via `Ui.printMessage()`.
    - If the index is valid, the recipe is removed (converting to 0-based internally with
      `recipes.remove(index - 1)`) and a success message is printed.

Key snippet from `RecipeBook`:

```text
  public void removeRecipe(int index) {
      if (index < 1 || index > recipes.size()) {
          throw new IndexOutOfBoundsException(
                  "Index " + index + " is out of range. Valid range: 1 to " + recipes.size()
          );
      }
      recipes.remove(index - 1);
  }
```

  ---

#### Design Considerations

**Aspect: Index convention (1-based vs 0-based)**

| Option | Pros | Cons |
|---|---|---|
| 1-based user input (current) | Matches the numbered list shown by `list-r` and `view-r` | Requires `index - 1` conversion before `ArrayList.remove()` |
| 0-based user input | Aligns directly with internal storage | Counter-intuitive; users see 1-based numbering in list output |

*Decision:* 1-based indexing is used to stay consistent with `list-r` and `view-r` output, so the
index the user sees is the same index they use to delete.

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


## Product scope
### Target user profile

A single student living independently (e.g., in a campus dorm) who types fast and prefers keyboard-driven workflows over 
mouse/touch input. This student enjoys cooking by himself/herself, but often gets frustrated because of being too lazy 
to organize the stored ingredients and not knowing what to cook.


### Value proposition

SudoCook is a cross-platform, portable, command-line pantry and recipe helper that reduces food waste by letting the 
user quickly log ingredients and expiry dates, and reduces meal indecision by suggesting recipes based on what’s 
currently in the pantry and the user’s available cooking time. All data is stored locally in a human-editable plain-text 
file (e.g., JSON or CSV) and managed through an object-oriented Java 17 codebase packaged as a single runnable JAR, with 
no DBMS and no reliance on remote servers.


## User Stories

|Version| As a ...     | I want to ... | So that I can ...|
|--------|--------------|---------------|------------------|
|v1.0| Busy Student |Add an item and expiry date using a single short command|I can digitize my pantry quickly after grocery shopping|
|v1.0| Novice Cook  |View step-by-step instructions for a specific recipe|I can follow the process accurately and complete the dish|
|v1.0| User|Delete items quickly|My inventory list remains accurate after I throw things away/ use them|
|v1.0| User |View all ingredients|I know what ingredients have been added so far|
|v1.0| User |Add a recipe|I don't have to rely on my memory for instructions|
|v1.0| User |Delete a recipe|I can keep my recipe list clean and organized|
|v2.0| Budget-conscious Student|List all items sorted by their expiry dates|I can prioritize ingredients about to spoil and avoid wasting money|
|v2.0| Indecisive Student|Request recipe suggestions based on current stock|I don't have to spend mental energy deciding what to cook|
|v2.0| Power User|Mark a recipe as "cooked" to auto-deduct ingredients|My stock levels remain accurate with minimal manual adjustment|
|v2.0| Organized Student|Compare a specific dish's requirements against inventory|I can see if I have everything or need a precise shopping list|
|v2.0| Fast-typer|Use an "undo" command to revert the last change|I can quickly fix accidental deletions or typos|
|v2.0| Tech-savvy User|Store data in a human-editable JSON/CSV file|My data is permanent, portable, and easy to backup|
|v2.0| Health-conscious Student|Specify a dietary plan (e.g., Vegan) for automated meal plans|I can maintain nutritional goals without manual calculations|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
