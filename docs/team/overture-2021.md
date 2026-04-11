ui Jiahao - Project Portfolio Page

## Overview
**SudoCook** is a Java-based Command-Line Interface (CLI) application designed to help users manage recipes and kitchen
inventory efficiently. It enables students and home cooks to track their ingredients and discover what they can cook
through an intuitive text interface.

### Summary of Contributions

#### Code Contributed
[Overture-2021 RepoSense](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=overture-2021&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

#### Enhancements Implemented
- Initialized the command architecture with `Command`, `ListCommand`, `DeleteCommand`, and `AddRecipeCommand`
- Implement `add-r` command: enables adding recipes through the user interface, and write corresponding tests
- Revise help command accordingly.
- Implement expiry date propagation when running add-i on an ingredient previously not posessing an expiry date.
- Implement multiple expiry batches for ingredients.
- Fix bug: add-i rejects valid ingredient names containing lowercase q.
- Contribution to the DG: `sort-i`, `cook`, `list-r` commands, drew sequence diagrams: [list-i](ListIngredients.png), [sort-i](SortInventory.png), [cook](cook.png), draw Architecture Diagram

#### Contributions to team-based tasks
- Set up the github organization and forked the repository.
- Led the division of tasks in a manner that reduces temporal and interpersonal dependency (so that, ideally, work related to the same functional code only done by one person in a week, a team member doesn't need to wait until another member has finished, and everyone has roughly equal workload).
