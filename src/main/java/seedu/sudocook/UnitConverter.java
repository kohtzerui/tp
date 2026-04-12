package seedu.sudocook;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting between compatible units of measurement.
 * Supports mass (mg, g, kg, lb, oz) and volume (ml, l, tsp, tbsp, cup) families.
 * Returns -1 when units belong to incompatible families (e.g. grams vs cups).
 */
final class UnitConverter {
    private static final Map<String, Double> MASS_TO_GRAMS = new HashMap<>();
    private static final Map<String, Double> VOLUME_TO_ML = new HashMap<>();

    static {
        // Mass (base unit: grams)
        MASS_TO_GRAMS.put("mg", 0.001);
        MASS_TO_GRAMS.put("milligram", 0.001);
        MASS_TO_GRAMS.put("milligrams", 0.001);
        MASS_TO_GRAMS.put("g", 1.0);
        MASS_TO_GRAMS.put("gram", 1.0);
        MASS_TO_GRAMS.put("grams", 1.0);
        MASS_TO_GRAMS.put("kg", 1000.0);
        MASS_TO_GRAMS.put("kilogram", 1000.0);
        MASS_TO_GRAMS.put("kilograms", 1000.0);
        MASS_TO_GRAMS.put("lb", 453.592);
        MASS_TO_GRAMS.put("lbs", 453.592);
        MASS_TO_GRAMS.put("oz", 28.3495);

        // Volume (base unit: millilitres)
        VOLUME_TO_ML.put("ml", 1.0);
        VOLUME_TO_ML.put("milliliter", 1.0);
        VOLUME_TO_ML.put("milliliters", 1.0);
        VOLUME_TO_ML.put("millilitre", 1.0);
        VOLUME_TO_ML.put("millilitres", 1.0);
        VOLUME_TO_ML.put("l", 1000.0);
        VOLUME_TO_ML.put("liter", 1000.0);
        VOLUME_TO_ML.put("liters", 1000.0);
        VOLUME_TO_ML.put("litre", 1000.0);
        VOLUME_TO_ML.put("litres", 1000.0);
        VOLUME_TO_ML.put("tsp", 4.92892);
        VOLUME_TO_ML.put("teaspoon", 4.92892);
        VOLUME_TO_ML.put("teaspoons", 4.92892);
        VOLUME_TO_ML.put("tbsp", 14.7868);
        VOLUME_TO_ML.put("tablespoon", 14.7868);
        VOLUME_TO_ML.put("tablespoons", 14.7868);
        VOLUME_TO_ML.put("cup", 240.0);
        VOLUME_TO_ML.put("cups", 240.0);
    }

    /**
     * Converts {@code quantity} from {@code fromUnit} to {@code toUnit}.
     *
     * @return the converted quantity, or -1 if the units are incompatible.
     */
    static double convert(double quantity, String fromUnit, String toUnit) {
        String from = fromUnit.toLowerCase();
        String to = toUnit.toLowerCase();
        if (from.equals(to)) {
            return quantity;
        }
        if (MASS_TO_GRAMS.containsKey(from) && MASS_TO_GRAMS.containsKey(to)) {
            return quantity * MASS_TO_GRAMS.get(from) / MASS_TO_GRAMS.get(to);
        }
        if (VOLUME_TO_ML.containsKey(from) && VOLUME_TO_ML.containsKey(to)) {
            return quantity * VOLUME_TO_ML.get(from) / VOLUME_TO_ML.get(to);
        }
        return -1;
    }
}
