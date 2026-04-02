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

- **UI enhancements** (`Ui.java`)
    - Refactored the `Help` command output formatting with gradient line endings for a polished CLI experience.
    - Fixed startup behavior and Checkstyle violations across multiple files.

#### Contributions to the User Guide (UG)
- Authored the `filter-r` section with format specifications, parameter notes, and usage examples.
- Updated the `add-r` section to document the new `c/CALORIES` parameter.
- Updated the command summary table to include the `filter-r` command.

#### Contributions to the Developer Guide (DG)
- Authored the `filter-r` implementation section covering:
    - Class responsibility table for `Parser`, `FilterRecipeCommand`, and `RecipeBook`.
    - Step-by-step execution walkthrough.
    - Design Consideration aspects with option tables and rationale.

#### Contributions to Team-Based Tasks
- **Test Coverage**: Significantly improved unit test coverage across recipe-related classes, ensuring
  reliable CI builds and regression detection.
- **CI Maintenance**: Diagnosed and fixed CI test failures related to `text-ui-test` expected output
  mismatches after feature additions, and resolved Checkstyle line-length violations.
- **Documentation Cleanup**: Updated `README.md`, `UserGuide.md`, and `DeveloperGuide.md` to replace
  placeholder text and ensure consistency with the SudoCook project name.

#### Review/Mentoring Contributions
- Assisted teammates in debugging Checkstyle violations and test failures caused by cross-cutting
  feature changes (e.g., new constructor parameters affecting multiple test files).
