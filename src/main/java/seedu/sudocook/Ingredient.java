package seedu.sudocook;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represents an ingredient with a name, quantity, unit, and optional expiry date.
 */
public class Ingredient {
    private static final double EPSILON = 0.000000001;

    private final String name;
    private final String unit;
    private final ArrayList<ExpiryQuantity> expiryQuantities;

    /**
     * Represents one amount of an ingredient with a shared expiry date.
     */
    public static class ExpiryQuantity {
        private LocalDate expiryDate;
        private double quantity;

        private ExpiryQuantity(LocalDate expiryDate, double quantity) {
            this.expiryDate = expiryDate;
            this.quantity = quantity;
        }

        public LocalDate getExpiryDate() {
            return expiryDate;
        }

        public double getQuantity() {
            return quantity;
        }
    }

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
        this.unit = unit;
        this.expiryQuantities = new ArrayList<>();
        addQuantity(quantity, null);
    }

    /**
     * Constructs an Ingredient with the specified name, quantity, unit, and expiry date.
     *
     * @param name The name of the ingredient
     * @param quantity The quantity of the ingredient
     * @param unit The unit of measurement
     * @param expiryDate The expiry date of the ingredient (optional)
     */
    public Ingredient(String name, double quantity, String unit, LocalDate expiryDate) {
        assert name != null && !name.isEmpty() : "Ingredient name must not be null or empty";
        assert quantity > 0 : "Ingredient quantity must be positive";
        assert unit != null && !unit.isEmpty() : "Ingredient unit must not be null or empty";
        this.name = name;
        this.unit = unit;
        this.expiryQuantities = new ArrayList<>();
        addQuantity(quantity, expiryDate);
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        double total = 0;
        for (ExpiryQuantity expiryQuantity : expiryQuantities) {
            total += expiryQuantity.quantity;
        }
        return total;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDate getExpiryDate() {
        sortExpiryQuantities();
        for (ExpiryQuantity expiryQuantity : expiryQuantities) {
            if (expiryQuantity.expiryDate != null) {
                return expiryQuantity.expiryDate;
            }
        }
        return null;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        double quantity = getQuantity();
        expiryQuantities.clear();
        addQuantity(quantity, expiryDate);
    }

    public void setQuantity(double quantity) {
        assert quantity >= 0 : "Ingredient quantity must not be negative";
        double currentQuantity = getQuantity();
        if (quantity < currentQuantity) {
            deductQuantity(currentQuantity - quantity);
        } else if (quantity > currentQuantity) {
            addQuantity(quantity - currentQuantity, getExpiryDate());
        }
    }

    public void addQuantity(double quantity, LocalDate expiryDate) {
        assert quantity > 0 : "Ingredient quantity must be positive";
        for (ExpiryQuantity expiryQuantity : expiryQuantities) {
            if (Objects.equals(expiryQuantity.expiryDate, expiryDate)) {
                expiryQuantity.quantity += quantity;
                sortExpiryQuantities();
                return;
            }
        }
        expiryQuantities.add(new ExpiryQuantity(expiryDate, quantity));
        sortExpiryQuantities();
    }

    public void addQuantitiesFrom(Ingredient ingredient) {
        for (ExpiryQuantity expiryQuantity : ingredient.getExpiryQuantities()) {
            addQuantity(expiryQuantity.quantity, expiryQuantity.expiryDate);
        }
    }

    public void deductQuantity(double quantityToDeduct) {
        assert quantityToDeduct >= 0 : "Ingredient quantity to deduct must not be negative";
        sortExpiryQuantities();
        double remainingQuantityToDeduct = quantityToDeduct;
        for (int i = 0; i < expiryQuantities.size() && remainingQuantityToDeduct > EPSILON; i++) {
            ExpiryQuantity expiryQuantity = expiryQuantities.get(i);
            double deductedQuantity = Math.min(expiryQuantity.quantity, remainingQuantityToDeduct);
            expiryQuantity.quantity -= deductedQuantity;
            remainingQuantityToDeduct -= deductedQuantity;
        }
        expiryQuantities.removeIf(expiryQuantity -> expiryQuantity.quantity <= EPSILON);
    }

    public ArrayList<ExpiryQuantity> getExpiryQuantities() {
        ArrayList<ExpiryQuantity> copiedExpiryQuantities = new ArrayList<>();
        for (ExpiryQuantity expiryQuantity : expiryQuantities) {
            copiedExpiryQuantities.add(new ExpiryQuantity(expiryQuantity.expiryDate, expiryQuantity.quantity));
        }
        return copiedExpiryQuantities;
    }

    public boolean hasExpiryBefore(LocalDate expiry) {
        for (ExpiryQuantity expiryQuantity : expiryQuantities) {
            if (expiryQuantity.expiryDate != null && expiryQuantity.expiryDate.isBefore(expiry)) {
                return true;
            }
        }
        return false;
    }

    public Ingredient copy() {
        ArrayList<ExpiryQuantity> copiedExpiryQuantities = getExpiryQuantities();
        ExpiryQuantity firstExpiryQuantity = copiedExpiryQuantities.get(0);
        Ingredient copiedIngredient = new Ingredient(name, firstExpiryQuantity.quantity, unit,
                firstExpiryQuantity.expiryDate);
        for (int i = 1; i < copiedExpiryQuantities.size(); i++) {
            ExpiryQuantity expiryQuantity = copiedExpiryQuantities.get(i);
            copiedIngredient.addQuantity(expiryQuantity.quantity, expiryQuantity.expiryDate);
        }
        return copiedIngredient;
    }

    public boolean isExpired() {
        for (ExpiryQuantity expiryQuantity : expiryQuantities) {
            if (expiryQuantity.expiryDate != null && LocalDate.now().isAfter(expiryQuantity.expiryDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return format(null, false);
    }

    public String toString(LocalDate expiryCutoff) {
        return format(expiryCutoff, true);
    }

    private String format(LocalDate expiryCutoff, boolean alwaysShowExpiryQuantities) {
        ArrayList<ExpiryQuantity> expiryQuantitiesToDisplay = getExpiryQuantitiesToDisplay(expiryCutoff);
        double displayedQuantity = getTotalQuantity(expiryQuantitiesToDisplay);
        String result = name + " (" + displayedQuantity + " " + unit + ")";
        if (!alwaysShowExpiryQuantities && expiryQuantitiesToDisplay.size() == 1) {
            LocalDate expiryDate = expiryQuantitiesToDisplay.get(0).expiryDate;
            if (expiryDate != null) {
                result += " expires: " + expiryDate;
            }
            return result;
        }
        if (!expiryQuantitiesToDisplay.isEmpty()) {
            result += " expiries: " + formatExpiryQuantities(expiryQuantitiesToDisplay);
        }
        return result;
    }

    private ArrayList<ExpiryQuantity> getExpiryQuantitiesToDisplay(LocalDate expiryCutoff) {
        ArrayList<ExpiryQuantity> expiryQuantitiesToDisplay = new ArrayList<>();
        for (ExpiryQuantity expiryQuantity : getExpiryQuantities()) {
            if (expiryCutoff == null
                    || expiryQuantity.expiryDate != null && expiryQuantity.expiryDate.isBefore(expiryCutoff)) {
                expiryQuantitiesToDisplay.add(expiryQuantity);
            }
        }
        return expiryQuantitiesToDisplay;
    }

    private String formatExpiryQuantities(ArrayList<ExpiryQuantity> expiryQuantitiesToDisplay) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < expiryQuantitiesToDisplay.size(); i++) {
            ExpiryQuantity expiryQuantity = expiryQuantitiesToDisplay.get(i);
            if (expiryQuantity.expiryDate == null) {
                sb.append("no expiry");
            } else {
                sb.append(expiryQuantity.expiryDate);
            }
            sb.append(": ").append(expiryQuantity.quantity).append(" ").append(unit);
            if (i < expiryQuantitiesToDisplay.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private double getTotalQuantity(ArrayList<ExpiryQuantity> expiryQuantitiesToDisplay) {
        double totalQuantity = 0;
        for (ExpiryQuantity expiryQuantity : expiryQuantitiesToDisplay) {
            totalQuantity += expiryQuantity.quantity;
        }
        return totalQuantity;
    }

    private void sortExpiryQuantities() {
        expiryQuantities.sort(Comparator.comparing(
                ExpiryQuantity::getExpiryDate,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
    }
}
