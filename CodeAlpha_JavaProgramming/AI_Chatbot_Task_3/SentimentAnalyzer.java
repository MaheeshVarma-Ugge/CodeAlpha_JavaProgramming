package chatbot;

import java.util.*;

public class SentimentAnalyzer {

    public enum Sentiment { POSITIVE, NEGATIVE, NEUTRAL }

    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
        "good", "great", "awesome", "excellent", "amazing", "wonderful", "fantastic",
        "love", "like", "enjoy", "happy", "glad", "thanks", "thank", "helpful",
        "brilliant", "perfect", "best", "nice", "cool", "super", "outstanding",
        "impressive", "beautiful", "smart", "clever"
    ));

    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
        "bad", "terrible", "awful", "horrible", "hate", "dislike", "stupid",
        "useless", "dumb", "idiot", "worst", "boring", "annoying", "ugly",
        "broken", "wrong", "fail", "error", "problem", "issue", "bug"
    ));

    private static final Set<String> NEGATORS = new HashSet<>(Arrays.asList(
        "not", "no", "never", "neither", "nor", "barely", "hardly"
    ));

    public Sentiment analyze(List<String> tokens) {
        int score = 0;
        boolean negate = false;
        for (String token : tokens) {
            if (NEGATORS.contains(token))       { negate = true;  continue; }
            if (POSITIVE_WORDS.contains(token)) { score += negate ? -1 : 1; negate = false; }
            else if (NEGATIVE_WORDS.contains(token)) { score += negate ? 1 : -1; negate = false; }
            else { negate = false; }
        }
        if (score > 0) return Sentiment.POSITIVE;
        if (score < 0) return Sentiment.NEGATIVE;
        return Sentiment.NEUTRAL;
    }
}
