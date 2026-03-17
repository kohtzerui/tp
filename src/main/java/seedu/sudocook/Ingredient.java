package seedu.sudocook;

/**
 * Represents an ingredient with a name, quantity, and unit.
 */
public class Ingredient {
    private final String name;
    private double quantity;
    private final String unit;

    /**
     * Constructs an Ingredient with the specified name, quantity, and unit.
     *
     * @param name The name of the ingredient
     * @param quantity The quantity of the ingredient
     * @param unit The unit of measurement
     */
    public Ingredient(String name, double quantity, String unit) {
        assert name != null && !name.isEmpty() : "Ingredient name must not be null or empty";
        assert quantity > 0 : "Ingredient quantity must be positive";
        assert unit != null && !unit.isEmpty() : "Ingredient unit must not be null or empty";
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
    }
}
