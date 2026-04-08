# 1cecream03 - Project Portfolio Page

## Project: SudoCook
**SudoCook** is a Java-based Command-Line Interface (CLI) application designed to help users manage recipes and kitchen
inventory efficiently. It enables students and home cooks to track their ingredients and discover what they can cook
through an intuitive text interface.

---

## Summary of Contributions

### 1. Code Contributed
[Link to my code on the tP Code Dashboard](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=1cecream03&breakdown=true&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=)

---

### 2. Enhancements Implemented

* **Set up core application infrastructure** (`SudoCook.java`, `Parser.java`, `Ui.java`)
    * Implemented the main application entry point (`SudoCook`) including the main loop, command
      routing, and storage initialisation.
    * Implemented `Parser` to handle parsing and input validation for all commands, including
      format checks, error messages, and construction of the appropriate `Command` objects.
    * Implemented `Ui` including gradient text rendering, the welcome banner, divider lines,
      error formatting, and user input handling via `Scanner`.

* **Implemented `list-r` and `view-r` commands** (`ListRecipeCommand.java`, `ViewRecipeCommand.java`)
    * `list-r` prints a compact numbered list of all recipe names, giving users a quick overview
      without clutter.
    * `view-r` prints full recipe details (ingredients, steps, time, calories) for all recipes or
      a specific one by 1-based index. Invalid indices are caught and reported with a clear error.

* **Implemented fuzzy search** (`search-r`, `search-i`) (`FuzzySearch.java`, `SearchRecipeCommand.java`, `SearchIngredientCommand.java`)
    * `search-r QUERY` searches the recipe book and `search-i QUERY` searches the inventory, both
      using fuzzy name matching to tolerate typos, partial input, and case differences.
    * Implemented a pure-Java `FuzzySearch` utility class with a priority cascade: exact match →
      substring → character subsequence → Levenshtein distance. No external libraries were used.
    * The match threshold (score ≥ 40) was chosen as the minimum score for a full character
      subsequence match, giving a natural cutoff between recognisable and unrecognisable results.
    * Enhanced search results to display matches ranked by relevance score (best match first)
      via a new `rankMatchIndices()` method in `FuzzySearch`, replacing the previous insertion-order output.

* **Implemented `sort-r` command** (`SortRecipeCommand.java`)
    * `sort-r n/` sorts recipes alphabetically by name, `sort-r t/` by ascending preparation time,
      and `sort-r c/` by ascending calorie count.
    * Invalid or missing criteria are caught with a clear error message guiding the user to the correct format.

---

### 3. Contributions to the User Guide (UG)
* Documented `list-r` and `view-r` with format specifications, index rules, usage examples, and
  expected output variants (all recipes, single recipe, invalid index).
* Documented `search-r` and `search-i` with format specifications, examples showing partial and
  typo-tolerant queries, and expected output variants (matches found, no matches).
* Documented `sort-r` with format specifications, all three criteria options, usage examples,
  and expected output variants (sorted list, empty book, invalid criteria).

---

### 4. Contributions to the Developer Guide (DG)
* Authored the `list-r` and `view-r` implementation section:
    * Class responsibility table for the four involved classes.
    * Step-by-step execution walkthrough for both the indexed and non-indexed variants.
    * One Design Consideration aspect with option table and rationale (separating list and view).
    * Added sequence diagrams: `ListRecipe.png` and `ViewRecipe.png`.
* Authored the `search-r` and `search-i` implementation section:
    * Class responsibility table for all six involved classes.
    * Step-by-step execution walkthrough for `search-r` (and noted `search-i` is identical).
    * Fuzzy scoring priority table and key code snippet from `FuzzySearch`.
    * Three Design Consideration aspects with option tables and rationale (scoring strategy,
      match threshold, placement of fuzzy logic).
    * Added sequence diagrams: `SearchRecipe.png` and `SearchIngredient.png`.
* Authored the `sort-r` implementation section:
    * Class responsibility table for the involved classes.
    * Step-by-step execution walkthrough for all three sort criteria.
    * One Design Consideration aspect with rationale (supporting multiple sort criteria vs. fixed order).
    * Added sequence diagram: `SortRecipe.png`.

---

### 5. Contributions to Team-Based Tasks
* Set up the foundational `Parser` and `Ui` classes used by all teammates' commands throughout
  the project, establishing consistent input handling and output formatting conventions.
* Wrote unit tests for `SearchRecipeCommand`, `SearchIngredientCommand`, `UiTest`,
  `ListRecipeCommand`, `ViewRecipeCommand`, `SortRecipeCommand`, and `FuzzySearch`.
