
# SudoCook User Guide

## Introduction

**SudoCook** is a Java-based Command-Line Interface (CLI) application designed to help users manage
recipes and kitchen inventory efficiently. It enables students and home cooks to track their
ingredients, discover what they can cook, and filter recipes based on preparation time and calorie
count — all through an intuitive text interface.

## Quick Start

1. Ensure that you have Java 17 or above installed.
2. Download the latest version of `SudoCook` from [here](https://github.com/AY2526S2-CS2113-W13-2/tp/releases).
3. Copy the JAR file to the folder you want to use as the home folder for SudoCook.
4. Open a terminal, navigate to the folder, and run: `java -jar sudocook.jar`

## Features

### Adding a recipe: `add-r`

Adds a new recipe to the recipe book.

Format: `add-r {NAME} i/INGREDIENT_NAME QUANTITY UNIT [INGREDIENT_NAME QUANTITY UNIT]... s/{STEP_1} [{STEP_2}]... t/TIME_IN_MINUTES c/CALORIES`

* `NAME` can be wrapped in `{}` to support spaces.
* Each ingredient must be provided in groups of three: `NAME QUANTITY UNIT`.
* Ingredients or steps containing spaces should be wrapped in `{}`.
* `TIME_IN_MINUTES` must be a non-negative integer.
* `CALORIES` must be a non-negative integer representing the calorie count in kcal.

Examples:

`add-r {Fried Rice} i/rice 2 cups egg 2 pcs {soy sauce} 1 tbsp s/{Cook the rice.} {Scramble the eggs.} {Mix everything together.} t/15 c/400`

`add-r {Instant Noodles} i/water 2 cups noodles 1 packet s/{Boil water.} {Cook noodles.} t/5 c/350`

Example output excerpt (successful addition):
```
Added recipe:
Recipe Name: Fried Rice
Preparation Time: 15 minutes
Calories: 400 kcal
```

Expected output (invalid format):
```
Oops! Invalid add-r format. Use: add-r NAME i/INGREDIENTS s/STEPS t/TIME c/CALORIES
```

Expected output (invalid ingredient quantity):
```
Oops! Invalid ingredient quantity in add-r format.
```

Expected output (negative time or calories):
```
Oops! Time and calories cannot be negative.
```

---

### Listing ingredients: `list-i`

Shows the ingredients currently stored in your inventory.

Format:

* `list-i`
* `list-i ex/YYYY-MM-DD`

* `list-i` shows every ingredient in the inventory.
* `list-i ex/YYYY-MM-DD` shows only ingredients whose expiry date is **before** the given date.
* Ingredients without an expiry date are excluded from filtered results.
* The date must be in `YYYY-MM-DD` format.

Examples:

`list-i`

`list-i ex/2026-04-01`

Expected output (listing all ingredients):
```
Here are the ingredients in your inventory:
1. Milk (1.0 carton) expires: 2026-03-30
2. Salt (1.0 kg)
```

Expected output (listing ingredients before a cutoff date):
```
Here are the ingredients in your inventory expiring before 2026-04-01:
1. Milk (1.0 carton) expires: 2026-03-30
```

Expected output (no matching ingredients):
```
There are no ingredients expiring before 2026-04-01.
```

Expected output (invalid date):
```
Oops! Invalid expiry date format. Use: YYYY-MM-DD
```

---

### Sorting ingredients by expiry date: `sort-i`

Sorts the inventory so that ingredients with earlier expiry dates appear first.

Format: `sort-i`

* Ingredients with no expiry date are placed at the end of the inventory list.
* Use `list-i` after sorting to view the updated order.

Example:

`sort-i`

Expected output:
```
Sorted!
```

---

### Cooking a recipe: `cook`

Prepares a recipe and deducts its required ingredients from the inventory.

Format: `cook INDEX`

* `INDEX` refers to the recipe's position in the recipe list.
* Use `list-r` or `view-r` first if you need to confirm the correct index.
* The recipe is only cooked if all required ingredients exist in the inventory in sufficient quantity.
* If any ingredient is missing or insufficient, no inventory changes are made.

Examples:

`cook 1`

`cook 3`

Result after a successful cook:
```
The recipe is cooked successfully and the required ingredients are removed from the inventory.
```

Expected output (not enough ingredients):
```
Oops! Not enough ingredients
```

Expected output (invalid index format):
```
Oops! You should indicate the index of the recipe when cooking!
```

Expected output (index out of range):
```
Oops! Index out of bounds
```

---

### Recommending recipes: `recommend-r`

The `recommend-r` command has three modes:

- **Ingredient-based** — finds recipes that use a specific ingredient you already have enough of.
- **Inventory-based** — finds every recipe that can be fully made using your current inventory.
- **Missing-based** — finds recipes you are almost able to make, listing exactly what you still need to buy.

<br>

#### Ingredient-based recommendation

Shows all recipes that contain a specific ingredient, provided your inventory holds at least the required quantity.

Format: `recommend-r n/INGREDIENT_NAME`

* `INGREDIENT_NAME` is case-insensitive (`egg`, `Egg`, and `EGG` all work).
* The ingredient must exist in your inventory; otherwise an error is shown.
* Only recipes whose required quantity of the ingredient is **≤** the amount you have are listed.

Examples:

`recommend-r n/egg`

`recommend-r n/Sugar`

Expected output (ingredient found, matching recipes exist):
```
Recipes containing egg:
1. Omelette
2. Fried Rice
```

Expected output (ingredient not in inventory):
```
Oops! Ingredient "beef" does not exist in inventory.
```

Expected output (ingredient in inventory but no recipe uses enough of it):
```
No recipes meet the requirement
```

<br>

#### Inventory-based recommendation

Shows all recipes that can be fully made with your current inventory — every required ingredient must be present in sufficient quantity.

Format: `recommend-r`

* A recipe is only listed if **all** of its ingredients are available in the inventory with enough quantity.
* Ingredient name matching is case-insensitive.

Example:

`recommend-r`

Expected output (some recipes are makeable):
```
Recipes you can make with your inventory:
1. Omelette
2. Mixue
```

Expected output (no recipe can be fully made):
```
No recipes can be made with the current inventory.
```

<br>

#### Missing-based recommendation

Shows recipes you are **almost** able to make — specifically, those missing at most `N` ingredients (or quantities). For each recipe shown, the exact shortfall for each missing ingredient is listed so you know precisely what to buy.

Format: `recommend-r missing/N`

* `N` must be a positive integer (e.g. `1`, `2`).
* A recipe is included only if the number of ingredients with insufficient stock is **between 1 and N** (inclusive). Recipes you can already make in full are excluded.
* For each missing ingredient the output shows the ingredient name, the shortfall amount, and the unit.
* Ingredient name matching is case-insensitive.

Examples:

`recommend-r missing/1`

`recommend-r missing/2`

Expected output (some recipes qualify):
```
Recipes you're almost able to make:
1. Omelette (missing: Salt (1.0 g))
2. Pasta (missing: Flour (200.0 g), Salt (5.0 g))
```

Expected output (no recipe is missing at most N ingredients):
```
No recipes found missing at most 1 ingredient(s).
```

Expected output (invalid N):
```
Oops! Missing count must be a positive number.
```

---

### Deleting a recipe: `delete-r`

Removes a recipe from the recipe book by its index.

Format: `delete-r INDEX`

* `INDEX` must be a positive integer corresponding to the recipe's position in the recipe list.
* Use `list-r` first to confirm the index of the recipe you want to delete.
* The deletion cannot be undone.

Examples:

`delete-r 1`

`delete-r 3`

Expected output (successful deletion):
```
Recipe 1 deleted successfully.
```

Expected output (index out of range):
```
Invalid index: Index 5 is out of range. Valid range: 1 to 3
```

Expected output (non-numeric index):
```
Oops! Invalid index for delete-r. Use: delete-r INDEX
```

---

### Filtering recipes: `filter-r`

Filters recipes by maximum preparation time and/or maximum calorie count.

Format: `filter-r [t/MAX_TIME] [c/MAX_CALORIES]`

* At least one of `t/MAX_TIME` or `c/MAX_CALORIES` must be provided.
* `MAX_TIME` is the maximum preparation time in minutes (non-negative integer).
* `MAX_CALORIES` is the maximum calorie count in kcal (non-negative integer).
* Both filters can be used together to narrow results further.

Examples:

`filter-r t/20`

`filter-r c/300`

`filter-r t/30 c/500`

Expected output (matching recipes found):
```
1. Recipe Name: Instant Noodles
Preparation Time: 5 minutes
Calories: 350 kcal
...
```

Expected output (no matching recipes):
```
No recipes found matching the criteria.
```

Expected output (no filter provided):
```
Oops! No valid filter targets provided. Use: filter-r [t/MAX_TIME] [c/MAX_CALORIES]
```

---

### Listing recipes: `list-r`

Shows a compact numbered list of all recipe names.

Format: `list-r`

Example:

`list-r`

Expected output:
```
1. Fried Rice
2. Instant Noodles
```

Expected output (no recipes saved):
```
No recipes found.
```

---

### Viewing recipe details: `view-r`

Shows the full details (ingredients, steps, time, and calories) for recipes.

Format:
* `view-r` — shows full details for all recipes.
* `view-r INDEX` — shows full details for the recipe at the given 1-based index.

* `INDEX` must be a positive integer matching the recipe's position in `list-r`.
* Use `list-r` first if you need to confirm the correct index.

Examples:

`view-r`

`view-r 1`

Expected output (specific recipe):
```
Recipe Name: Fried Rice
Preparation Time: 15 minutes
Calories: 400 kcal

    Ingredients:
    - rice (2.0 cups)
    - egg (2.0 pcs)

    Steps:
    - Cook the rice.
    - Scramble the eggs.
    - Mix everything together.
```

Expected output (index out of range):
```
Oops! Index 5 is out of range. (Valid range: 1 to 2)
```

Expected output (no recipes saved):
```
No recipes found.
```

---

### Searching recipes: `search-r`

Fuzzy-searches the recipe book by name. Handles partial input, case differences, and minor typos.

Format: `search-r QUERY`

* `QUERY` is case-insensitive.
* Partial matches and typos are tolerated (e.g. `freid rice` will still match `Fried Rice`).

Examples:

`search-r fried rice`

`search-r freid` *(typo tolerated)*

`search-r SOUP` *(case insensitive)*

Expected output (matches found):
```
Found 1 recipe(s) matching "fried rice":
1. Fried Rice
```

Expected output (no matches):
```
No recipes matched "xyz".
```

Expected output (empty query):
```
Oops! Please provide a search query. Use: search-r QUERY
```

---

### Searching ingredients: `search-i`

Fuzzy-searches the inventory by ingredient name. Handles partial input, case differences, and minor typos.

Format: `search-i QUERY`

* `QUERY` is case-insensitive.
* Partial matches and typos are tolerated (e.g. `tomatto` will still match `Tomato`).

Examples:

`search-i tomato`

`search-i tomatto` *(typo tolerated)*

`search-i MILK` *(case insensitive)*

Expected output (matches found):
```
Found 1 ingredient(s) matching "tomato":
1. Tomato (3.0 pcs)
```

Expected output (no matches):
```
No ingredients matched "xyz".
```

Expected output (empty query):
```
Oops! Please provide a search query. Use: search-i QUERY
```

---

### Viewing help: `help`

Displays a guide of all available commands and their formats.

Format: `help`

Example:

`help`

Example output excerpt:
```
SudoCook Help Guide
========================================================

RECIPE COMMANDS:
--------------------------------------------------------
1. List Recipes
   Command : list-r
   Purpose : Shows names of all available recipes.
...
```

---

## FAQ

**Q**: How do I transfer my data to another computer?

**A**: Copy the `data/` folder (which contains `recipes.json` and `inventory.json`) from your current
SudoCook home directory to the same location on the other computer. The data files are plain JSON and
are fully portable.

## Command Summary

| Action | Format |
|---|---|
| Add recipe | `add-r {NAME} i/INGREDIENTS s/STEPS t/TIME c/CALORIES` |
| List recipes | `list-r` |
| View recipe(s) | `view-r` or `view-r INDEX` |
| Filter recipes | `filter-r [t/MAX_TIME] [c/MAX_CALORIES]` |
| Delete recipe | `delete-r INDEX` |
| Cook recipe | `cook INDEX` |
| Add ingredient | `add-i n/NAME q/QUANTITY u/UNIT ex/EXPIRY_DATE` |
| List ingredients | `list-i` or `list-i ex/YYYY-MM-DD` |
| Sort ingredients | `sort-i` |
| Search recipes | `search-r QUERY` |
| Search ingredients | `search-i QUERY` |
| Recommend by ingredient | `recommend-r n/INGREDIENT_NAME` |
| Recommend from inventory | `recommend-r` |
| Recommend nearly-makeable | `recommend-r missing/N` |
| View help | `help` |
| Exit | `bye` |
