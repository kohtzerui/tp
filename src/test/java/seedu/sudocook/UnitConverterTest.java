package seedu.sudocook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitConverterTest {
    private static final double DELTA = 1e-6;

    // --- same unit ---

    @Test
    public void convert_sameUnit_returnsUnchanged() {
        assertEquals(5.0, UnitConverter.convert(5.0, "g", "g"), DELTA);
    }

    @Test
    public void convert_sameUnitCaseInsensitive_returnsUnchanged() {
        assertEquals(3.0, UnitConverter.convert(3.0, "Liter", "liter"), DELTA);
    }

    // --- mass conversions ---

    @Test
    public void convert_gToKg() {
        assertEquals(0.5, UnitConverter.convert(500, "g", "kg"), DELTA);
    }

    @Test
    public void convert_kgToG() {
        assertEquals(1000.0, UnitConverter.convert(1, "kg", "g"), DELTA);
    }

    @Test
    public void convert_mgToG() {
        assertEquals(0.001, UnitConverter.convert(1, "mg", "g"), DELTA);
    }

    @Test
    public void convert_gToMg() {
        assertEquals(1000.0, UnitConverter.convert(1, "g", "mg"), DELTA);
    }

    @Test
    public void convert_kgToMg() {
        assertEquals(1_000_000.0, UnitConverter.convert(1, "kg", "mg"), DELTA);
    }

    // --- volume conversions ---

    @Test
    public void convert_lToMl() {
        assertEquals(1000.0, UnitConverter.convert(1, "l", "ml"), DELTA);
    }

    @Test
    public void convert_mlToL() {
        assertEquals(0.5, UnitConverter.convert(500, "ml", "l"), DELTA);
    }

    @Test
    public void convert_cupToMl() {
        assertEquals(240.0, UnitConverter.convert(1, "cup", "ml"), DELTA);
    }

    @Test
    public void convert_mlToCup() {
        assertEquals(0.5, UnitConverter.convert(120, "ml", "cup"), DELTA);
    }

    @Test
    public void convert_literToCups() {
        // 1 L = 1000 ml, 1 cup = 240 ml → 1000/240 ≈ 4.1667
        assertEquals(1000.0 / 240.0, UnitConverter.convert(1, "liter", "cups"), DELTA);
    }

    // --- incompatible families ---

    @Test
    public void convert_massToVolume_returnsNegativeOne() {
        assertEquals(-1.0, UnitConverter.convert(1, "g", "ml"), DELTA);
    }

    @Test
    public void convert_volumeToMass_returnsNegativeOne() {
        assertEquals(-1.0, UnitConverter.convert(1, "cup", "kg"), DELTA);
    }

    @Test
    public void convert_unknownUnit_returnsNegativeOne() {
        assertEquals(-1.0, UnitConverter.convert(1, "pcs", "g"), DELTA);
    }

    @Test
    public void convert_bothUnknown_returnsNegativeOne() {
        // Neither "pcs" nor "pinch" is in any map, and they differ, so -1
        assertEquals(-1.0, UnitConverter.convert(1, "pcs", "pinch"), DELTA);
    }
}
