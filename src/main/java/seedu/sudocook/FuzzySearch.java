package seedu.sudocook;

import java.util.ArrayList;

public class FuzzySearch {
    private static final int MATCH_THRESHOLD = 40;

    /**
     * Returns a fuzzy match score (0–100) between query and target.
     * Higher is a better match.
     */
    public static int score(String query, String target) {
        String q = query.toLowerCase().trim();
        String t = target.toLowerCase().trim();

        if (q.isEmpty()) {
            return 0;
        }
        if (q.equals(t)) {
            return 100;
        }
        if (t.contains(q)) {
            return 90;
        }
        int subseq = subsequenceScore(q, t);
        if (subseq > 0) {
            return subseq;
        }
        int dist = levenshtein(q, t);
        int maxLen = Math.max(q.length(), t.length());
        return Math.max(0, (int) ((1.0 - (double) dist / maxLen) * 60));
    }

    public static boolean isMatch(String query, String target) {
        return score(query, target) >= MATCH_THRESHOLD;
    }

    /**
     * Returns indices of matching candidates from the list, sorted by descending score.
     */
    public static ArrayList<Integer> rankMatchIndices(String query, ArrayList<String> candidates) {
        ArrayList<Integer> matchIndices = new ArrayList<>();
        for (int i = 0; i < candidates.size(); i++) {
            if (isMatch(query, candidates.get(i))) {
                matchIndices.add(i);
            }
        }
        matchIndices.sort((a, b) -> score(query, candidates.get(b)) - score(query, candidates.get(a)));
        return matchIndices;
    }

    private static int subsequenceScore(String query, String target) {
        int qi = 0;
        for (int ti = 0; ti < target.length() && qi < query.length(); ti++) {
            if (query.charAt(qi) == target.charAt(ti)) {
                qi++;
            }
        }
        if (qi == query.length()) {
            return 40 + (int) (40.0 * query.length() / target.length());
        }
        return 0;
    }

    private static int levenshtein(String a, String b) {
        int[] dp = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            dp[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            int prev = dp[0];
            dp[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int temp = dp[j];
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j - 1]));
                }
                prev = temp;
            }
        }
        return dp[b.length()];
    }
}
