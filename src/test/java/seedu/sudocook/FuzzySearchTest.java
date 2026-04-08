package seedu.sudocook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class FuzzySearchTest {

    @Test
    public void rankMatchIndices_exactMatchRanksFirst() {
        ArrayList<String> candidates = new ArrayList<>();
        candidates.add("Fried Rice");   // partial match for "rice"
        candidates.add("Rice");         // exact match for "rice"
        ArrayList<Integer> ranked = FuzzySearch.rankMatchIndices("rice", candidates);
        assertEquals(1, ranked.get(0)); // "Rice" (index 1) should rank first
    }

    @Test
    public void rankMatchIndices_noMatches_returnsEmptyList() {
        ArrayList<String> candidates = new ArrayList<>();
        candidates.add("Pasta");
        candidates.add("Burger");
        ArrayList<Integer> ranked = FuzzySearch.rankMatchIndices("xyz123", candidates);
        assertTrue(ranked.isEmpty());
    }

    @Test
    public void rankMatchIndices_emptyList_returnsEmptyList() {
        ArrayList<Integer> ranked = FuzzySearch.rankMatchIndices("rice", new ArrayList<>());
        assertTrue(ranked.isEmpty());
    }

    @Test
    public void rankMatchIndices_allMatchesReturned() {
        ArrayList<String> candidates = new ArrayList<>();
        candidates.add("Tomato Soup");
        candidates.add("Tomato Salad");
        candidates.add("Pasta");
        ArrayList<Integer> ranked = FuzzySearch.rankMatchIndices("tomato", candidates);
        assertEquals(2, ranked.size());
    }

    @Test
    public void rankMatchIndices_higherScoreRanksFirst() {
        ArrayList<String> candidates = new ArrayList<>();
        candidates.add("tom");          // weaker match for "tomato"
        candidates.add("tomato sauce"); // stronger match for "tomato"
        ArrayList<Integer> ranked = FuzzySearch.rankMatchIndices("tomato", candidates);
        assertEquals(1, ranked.get(0)); // "tomato sauce" should rank first
    }
}
