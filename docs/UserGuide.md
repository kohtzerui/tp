
# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 17 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features

### Adding a recipe: `add-r`

Adds a new recipe to the recipe book.

Format: `add-r {NAME} i/INGREDIENT_NAME QUANTITY UNIT [INGREDIENT_NAME QUANTITY UNIT]... s/{STEP_1} [{STEP_2}]... t/TIME_IN_MINUTES`

* `NAME` can be wrapped in `{}` to support spaces.
* Each ingredient must be provided in groups of three: `NAME QUANTITY UNIT`.
* Ingredients or steps containing spaces should be wrapped in `{}`.
* `TIME_IN_MINUTES` must be a positive integer.

Examples:

`add-r {Fried Rice} i/rice 2 cups egg 2 pcs {soy sauce} 1 tbsp s/{Cook the rice.} {Scramble the eggs.} {Mix everything together.} t/15`

`add-r {Instant Noodles} i/water 2 cups noodles 1 packet s/{Boil water.} {Cook noodles.} t/5`

Example output excerpt (successful addition):
```
Added recipe:
Recipe Name: Fried Rice
Preparation Time: 15 minutes
```

Expected output (invalid format):
```
Oops! Invalid add-r format.
```

Expected output (invalid ingredient quantity):
```
Oops! Invalid ingredient quantity in add-r format.
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

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

* Add recipe: `add-r {NAME} i/INGREDIENT_NAME QUANTITY UNIT... s/{STEP_1} {STEP_2}... t/TIME_IN_MINUTES`
* List ingredients: `list-i`
* List ingredients expiring before a date: `list-i ex/YYYY-MM-DD`
* Sort ingredients by expiry: `sort-i`
* Cook recipe by index: `cook INDEX`
* Recommend recipes by ingredient: `recommend-r n/INGREDIENT_NAME`
* Recommend recipes from inventory: `recommend-r`
* Recommend nearly-makeable recipes: `recommend-r missing/N`
* Delete recipe: `delete-r INDEX`
