# Koh Tze Rui - Project Portfolio Page

## Overview
**SudoCook** is a Java-based Command-Line Interface (CLI) application designed to help users manage recipes and kitchen
inventory efficiently. It enables students and home cooks to track their ingredients and discover what they can cook
through an intuitive text interface.

### Summary of Contributions

#### Code Contributed
[kohtzerui RepoSense](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=kohtzerui&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

#### Enhancements Implemented

- **Implemented `filter-r` command** (`FilterRecipeCommand.java`, `RecipeBook.filterRecipes()`)
    - Developed the recipe filtering feature that allows users to search for recipes by maximum
      preparation time (`t/MAX_TIME`) and/or maximum calorie count (`c/MAX_CALORIES`).
    - Both filter criteria are optional and can be used independently or together.
    - Updated `Parser.java` to support the `t/` and `c/` prefixes for filter arguments using regex-based extraction.

- **Implemented calorie tracking for recipes** (`Recipe.java`, `RecipeBook.java`, `AddRecipeCommand.java`, `Parser.java`, `Storage.java`)
    - Extended the `Recipe` model with a `calories` field, including getter and updated `toString()` output.
    - Updated the `add-r` command to accept a `c/CALORIES` parameter.
    - Updated `Storage` to persist and load calorie data in `recipes.json`, with backward compatibility
      for older recipe files using `optInt`.

- **Wrote comprehensive unit tests for coverage**
    - Authored and updated test classes including `AddRecipeCommandTest.java`, `ViewRecipeCommandTest.java`,
      and `UiTest.java` to ensure high code coverage across the recipe management subsystem.
    - Fixed test compatibility issues caused by constructor signature changes and new fields.

- **Improved code quality and error handling**
    - Refactored `AddRecipeCommand.java` to use `private final` fields for better encapsulation.
    - Added non-negative input validation for time and calorie values in `Parser.java`.
    - Improved `RecipeBook.java` index validation to provide clear error messages when the recipe book
      is empty (instead of displaying an illogical range like "1 to 0").

- **Implemented the `help` command** (`HelpCommand.java`, `SudoCook.java`, `Parser.java`)
    - Developed a centralized help system that displays all available commands and their formats.
    - Integrated with `SudoCook` logic to route help requests, ensuring users can discover features easily.
    - Enhanced the `Help` command output formatting with gradient line endings for a polished CLI experience.
    - Fixed startup behavior and Checkstyle violations across multiple files.

- **Developed Aesthetic ANSI Gradient System** (`Ui.java`)
    - Implemented a 24-bit TrueColor (RGB) gradient generator (`getGradientText`) that interpolates colors across text strings to provide a premium, modern terminal feel.
    - Integrated this system into the `printWelcome` banner and the `printGradientMessage` wrapper, ensuring all significant user-facing messages feature a cohesive purple-to-cyan visual identity.

- **Enforced Strict Recipe Input Validation** (`Parser.java`)
    - Improved the application's logical consistency by implementing stricter validation for calories and preparation time.
    - Enhanced the `add-r` parser to reject zero or negative calories (Issue #102), ensuring all saved recipes represent realistic meals.
    - Refactored numeric parsing to provide specific, helpful error messages for different failure modes (e.g., negative time vs. invalid calorie count).

#### Contributions to the User Guide (UG)
- Authored the `delete-i` (Delete Ingredient) section, including format specifications and usage examples (Issue #130).
- Authored the `filter-r` section with format specifications, parameter notes, and usage examples.
- Updated the `add-r` section to document the new `c/CALORIES` parameter and updated the expected output to match the current app logic.
- **UG Standardization & Consistency**:
    - Aligned the **Command Summary** table with the detailed Features section to ensure consistent format specifications (Issue #138).
    - Synchronized all **Expected Output** examples (for `add-r` and `cook`) with actual application behavior to prevent user confusion (Issue #121).
    - Explicitly defined the **Target Audience** and **Value Proposition** in the introduction to clearly communicate the product's scope (Issue #120).

#### Contributions to the Developer Guide (DG)
- Authored the `filter-r` implementation section covering:
    - Class responsibility table for `Parser`, `FilterRecipeCommand`, and `RecipeBook`.
    - Step-by-step execution walkthrough.
    - Design Consideration aspects with option tables and rationale.

#### Contributions to Team-Based Tasks
- **CI Maintenance & Regression Testing**:
    - Maintained CI build stability by proactively updating unit tests (e.g., `AddRecipeCommandTest.java`) to align with new specific validation error messages.
    - Diagnosed and fixed CI test failures related to `text-ui-test` expected output mismatches after feature additions.
- **Project Structure & Git Hygiene**:
    - Synchronized the local repository with the project's upstream master across multiple feature rounds.
    - Configured repository-wide `.gitignore` rules to exclude local environment files (like `.vscode/`), ensuring a clean codebase for all contributors.
- **Documentation Overhaul**: Updated `README.md`, `UserGuide.md`, and `DeveloperGuide.md` to replace placeholder text and ensure terminology consistency with the final SudoCook implementation.

#### Review/Mentoring Contributions
- Assisted teammates in debugging Checkstyle violations and test failures caused by cross-cutting
  feature changes (e.g., new constructor parameters affecting multiple test files).
