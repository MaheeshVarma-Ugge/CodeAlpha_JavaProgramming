package chatbot;

import java.util.*;

public class Tokenizer {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "is", "it", "its", "in", "on", "at", "to", "for",
        "of", "and", "or", "but", "not", "with", "this", "that", "are", "was",
        "were", "be", "been", "being", "have", "has", "had", "do", "does",
        "did", "will", "would", "could", "should", "may", "might", "shall",
        "can", "i", "you", "he", "she", "we", "they", "me", "him", "her",
        "us", "them", "my", "your", "his", "our", "their"
    ));

    private static final Map<String, String> CONTRACTIONS = new HashMap<>();
    static {
        CONTRACTIONS.put("what's", "what is");  CONTRACTIONS.put("it's",  "it is");
        CONTRACTIONS.put("i'm",    "i am");      CONTRACTIONS.put("i've",  "i have");
        CONTRACTIONS.put("i'd",    "i would");   CONTRACTIONS.put("i'll",  "i will");
        CONTRACTIONS.put("you're", "you are");   CONTRACTIONS.put("you've","you have");
        CONTRACTIONS.put("don't",  "do not");    CONTRACTIONS.put("doesn't","does not");
        CONTRACTIONS.put("can't",  "cannot");    CONTRACTIONS.put("won't", "will not");
        CONTRACTIONS.put("isn't",  "is not");    CONTRACTIONS.put("aren't","are not");
        CONTRACTIONS.put("who's",  "who is");    CONTRACTIONS.put("that's","that is");
        CONTRACTIONS.put("there's","there is");  CONTRACTIONS.put("we're", "we are");
        CONTRACTIONS.put("they're","they are");  CONTRACTIONS.put("didn't","did not");
    }

    public String normalize(String input) {
        if (input == null || input.isBlank()) return "";
        String s = input.toLowerCase().trim();
        for (Map.Entry<String, String> e : CONTRACTIONS.entrySet())
            s = s.replace(e.getKey(), e.getValue());
        s = s.replaceAll("[^a-z0-9 '?!.,]", " ").replaceAll("\\s+", " ").trim();
        return s;
    }

    public List<String> tokenize(String normalized) {
        List<String> tokens = new ArrayList<>();
        for (String part : normalized.split("\\s+")) {
            String clean = part.replaceAll("[^a-z0-9]", "");
            if (!clean.isEmpty()) tokens.add(clean);
        }
        return tokens;
    }

    public List<String> removeStopWords(List<String> tokens) {
        List<String> out = new ArrayList<>();
        for (String t : tokens) if (!STOP_WORDS.contains(t)) out.add(t);
        return out;
    }

    public List<String> stem(List<String> tokens) {
        List<String> out = new ArrayList<>();
        for (String t : tokens) out.add(simpleStem(t));
        return out;
    }

    private String simpleStem(String w) {
        if (w.endsWith("ing")  && w.length() > 5) return w.substring(0, w.length() - 3);
        if (w.endsWith("tion") && w.length() > 6) return w.substring(0, w.length() - 4);
        if (w.endsWith("ness") && w.length() > 5) return w.substring(0, w.length() - 4);
        if (w.endsWith("ment") && w.length() > 5) return w.substring(0, w.length() - 4);
        if (w.endsWith("able") && w.length() > 5) return w.substring(0, w.length() - 4);
        if (w.endsWith("ed")   && w.length() > 4) return w.substring(0, w.length() - 2);
        if (w.endsWith("ly")   && w.length() > 4) return w.substring(0, w.length() - 2);
        if (w.endsWith("es")   && w.length() > 3) return w.substring(0, w.length() - 2);
        if (w.endsWith("s")    && w.length() > 3) return w.substring(0, w.length() - 1);
        return w;
    }

    public List<String> getKeyTokens(String input) {
        return stem(removeStopWords(tokenize(normalize(input))));
    }
}
