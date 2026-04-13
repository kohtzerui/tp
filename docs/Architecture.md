# SudoCook Architecture

This diagram reflects the implemented architecture in `src/main/java/seedu/sudocook`, not features described only in documentation.

```mermaid
flowchart LR
    user["CLI User"]

    ui["Ui<br/>input and output"]
    app["SudoCook<br/>application loop and command routing"]
    parser["Parser<br/>CLI syntax to Command"]

    commands["Command layer<br/>add, delete, list, view, filter, sort, search, cook, recommend, help"]

    inventory["Inventory<br/>ingredient quantities and expiry batches"]
    recipes["RecipeBook<br/>recipe catalogue, filters, sorting and search"]
    models["Model objects<br/>Ingredient and Recipe"]

    helpers["Domain helpers<br/>FuzzySearch, IngredientRequirements, UnitConverter"]
    storage["Storage<br/>JSON files in data/"]

    user -->|types commands| ui
    ui -->|readInput| app
    app -->|parse input| parser
    parser -->|returns concrete Command| app
    app -->|routes by command type| commands

    commands -->|ingredient commands| inventory
    commands -->|recipe commands| recipes
    commands -->|cook and recommend| inventory
    commands -->|cook and recommend| recipes
    commands -->|prints feedback| ui

    inventory -->|stores| models
    recipes -->|stores| models
    recipes -->|search matching| helpers
    inventory -->|search matching| helpers
    commands -->|cook/recommend matching| helpers

    app -->|load at startup| storage
    app -->|save on shutdown| storage
    storage -->|serialises/deserialises| inventory
    storage -->|serialises/deserialises| recipes
```

## Notes

- `SudoCook` owns application startup, loads persisted state, runs the input loop, and routes commands using `instanceof`.
- `Parser` converts raw CLI text into a concrete `Command` subclass.
- The command layer is broad but shallow: most command classes delegate directly to `Inventory`, `RecipeBook`, or both, while cross-domain commands such as `cook` and `recommend-r` coordinate both.
- `Inventory` owns ingredient quantities, expiry batches, sorting, and ingredient search. `RecipeBook` owns recipe listing, viewing, filtering, sorting, and recipe search.
- Shared functional helpers are kept small: `FuzzySearch` ranks recipe and ingredient search matches, `IngredientRequirements` aggregates recipe ingredients for cooking and recommendations, and `UnitConverter` compares quantities across compatible units.
- `Storage` is a simple file-based persistence layer that loads at startup and saves on shutdown using JSON files under `data/`.
- Output is not isolated to a single presentation layer. Commands, `Inventory`, and `RecipeBook` all call `Ui` directly.
