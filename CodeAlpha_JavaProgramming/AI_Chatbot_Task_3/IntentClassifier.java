package chatbot;

import java.util.*;

/**
 * IntentClassifier — NLP-based intent detection using keyword scoring.
 *
 * FIX: Completely rewritten scoring to eliminate false ties and wrong winners.
 * Key changes:
 *   - Exact multi-word phrase match = 5 points (highest priority)
 *   - Exact single-word match       = 3 points
 *   - Prefix/partial match          = 1 point  (used rarely)
 *   - Stop-words removed BEFORE scoring so common words don't pollute scores
 *   - Each intent's keyword list is carefully curated to avoid overlap
 *   - MATH: detects digit + operator patterns via regex (no keyword collision)
 *   - DEFINITION vs FAQ: "what is java" → FAQ_JAVA wins because "java" is a
 *     strong FAQ keyword whereas "what is" alone cannot beat a topic keyword
 */
public class IntentClassifier {

    // Multi-word phrases — checked against the full normalised string (worth 5 pts each)
    private static final Map<Intent, List<String>> PHRASES = new LinkedHashMap<>();

    // Single-word keywords — checked against individual tokens (worth 3 pts each)
    private static final Map<Intent, List<String>> KEYWORDS = new LinkedHashMap<>();

    static {
        // ── Phrases (multi-word, high confidence) ────────────────────────
        PHRASES.put(Intent.GREETING,     Arrays.asList("good morning","good afternoon","good evening","good night","how are you","how r you"));
        PHRASES.put(Intent.FAREWELL,     Arrays.asList("good bye","see you","talk later","take care","have a nice","signing off","catch you later"));
        PHRASES.put(Intent.HELP,         Arrays.asList("what can you do","how do i use","i need help","can you help","show me help","how to use"));
        PHRASES.put(Intent.ABOUT_BOT,    Arrays.asList("who are you","what are you","your name","about you","are you a bot","are you ai","are you human","tell me about yourself"));
        PHRASES.put(Intent.CAPABILITIES, Arrays.asList("what can you","what do you know","what do you do","your features","your abilities","your skills"));
        PHRASES.put(Intent.JOKE,         Arrays.asList("tell me a joke","make me laugh","say something funny","give me a joke","crack a joke","funny joke"));
        PHRASES.put(Intent.TIME,         Arrays.asList("what time","current time","what is the time","tell me the time"));
        PHRASES.put(Intent.DATE,         Arrays.asList("what date","what day","what is today","today date","current date","tell me the date","which day"));
        PHRASES.put(Intent.FAQ_JAVA,     Arrays.asList("what is java","explain java","tell me about java","what is oop","explain oop","what is jvm","what is jdk","what is jre",
                                                        "what is inheritance","explain inheritance","what is polymorphism","explain polymorphism",
                                                        "what is encapsulation","explain encapsulation","what is abstraction","explain abstraction",
                                                        "what is interface","explain interface","what is thread","explain thread","what is exception",
                                                        "what is arraylist","what is hashmap","what is lambda","what is stream","what is generics",
                                                        "what is spring","what is maven","what is collection","explain collection"));
        PHRASES.put(Intent.FAQ_CODEALPHA,Arrays.asList("what is codealpha","about codealpha","about internship","how to submit","submit task",
                                                        "what is internship","how to get certificate","linkedin post","github repo","letter of recommendation"));
        PHRASES.put(Intent.MATH,         Arrays.asList("what is 2 plus","what is 2 minus","calculate this","solve this","compute this","find the result"));

        // ── Single keywords ───────────────────────────────────────────────
        KEYWORDS.put(Intent.GREETING,     Arrays.asList("hello","hi","hey","howdy","greet","hiya","namaste","sup","yo","heya"));
        KEYWORDS.put(Intent.FAREWELL,     Arrays.asList("bye","goodbye","farewell","exit","quit","cya","adios","goodnight","leaving","tata"));
        KEYWORDS.put(Intent.THANKS,       Arrays.asList("thank","thanks","thankyou","appreciate","grateful","cheers","thx","thanking"));
        KEYWORDS.put(Intent.HELP,         Arrays.asList("help","assist","support","guide","confused","stuck","instructions","manual","usage","lost"));
        KEYWORDS.put(Intent.WEATHER,      Arrays.asList("weather","rain","sunny","temperature","forecast","climate","storm","snow","humidity","drizzle","cloudy","sunshine"));
        KEYWORDS.put(Intent.TIME,         Arrays.asList("time","clock","hour","minute"));
        KEYWORDS.put(Intent.DATE,         Arrays.asList("date","today","calendar","month","year"));
        KEYWORDS.put(Intent.JOKE,         Arrays.asList("joke","funny","laugh","humor","humour","pun","comic","hilarious","comedy"));
        KEYWORDS.put(Intent.COMPLIMENT,   Arrays.asList("amazing","brilliant","impressive","outstanding","excellent","wonderful","fantastic","superb","genius","talented"));
        KEYWORDS.put(Intent.INSULT_RESPONSE, Arrays.asList("stupid","dumb","idiot","useless","garbage","trash","pathetic","awful","disgusting","worthless"));
        KEYWORDS.put(Intent.ABOUT_BOT,    Arrays.asList("introduce","yourself","bot","chatbot","robot","assistant","ai","artificial"));
        KEYWORDS.put(Intent.CAPABILITIES, Arrays.asList("capabilities","features","abilities","functions","skills","tasks","powers"));
        KEYWORDS.put(Intent.REPEAT,       Arrays.asList("repeat","again","rephrase","pardon","restate","resay","once more"));
        KEYWORDS.put(Intent.AFFIRM,       Arrays.asList("yes","yeah","yep","yup","sure","okay","alright","correct","absolutely","definitely","agreed","indeed","right"));
        KEYWORDS.put(Intent.DENY,         Arrays.asList("nope","nah","never","wrong","incorrect","disagree","false","negative","refusal","refused"));
        // Java FAQ — rich keyword set; these are very specific terms unlikely to appear in other intents
        KEYWORDS.put(Intent.FAQ_JAVA,     Arrays.asList("java","jvm","jdk","jre","oop","inheritance","polymorphism","encapsulation","abstraction",
                                                         "interface","thread","exception","arraylist","hashmap","lambda","stream","generics","spring","maven","collection","multithreading","classpath","bytecode","overloading","overriding","constructor","annotation","serialization"));
        KEYWORDS.put(Intent.FAQ_CODEALPHA,Arrays.asList("codealpha","internship","certificate","submission","linkedin","github","recommendation","intern","placement","evaluation"));
        KEYWORDS.put(Intent.DEFINITION,   Arrays.asList("define","definition","meaning","describe","elaborate","concept","overview","introduction","basics","fundamentals"));
    }

    // Regex to detect a math expression like "15 + 3", "100 / 4", "2 ^ 8", "15 plus 3" etc.
    private static final java.util.regex.Pattern MATH_PATTERN = java.util.regex.Pattern.compile(
        "\\d+\\s*([+\\-*/^%])\\s*\\d+" +
        "|\\d+\\s+(plus|minus|times|divided by|multiplied by|mod|to the power of)\\s+\\d+",
        java.util.regex.Pattern.CASE_INSENSITIVE
    );

    // Words to strip before single-keyword scoring (same as Tokenizer stop-words)
    private static final Set<String> STOPS = new HashSet<>(Arrays.asList(
        "a","an","the","is","it","in","on","at","to","for","of","and","or","but",
        "not","with","this","that","are","was","were","be","been","do","does",
        "did","will","would","could","should","may","might","can","i","you","he",
        "she","we","they","me","him","her","us","them","my","your","his","our","their","no"
    ));

    private final Tokenizer tokenizer = new Tokenizer();

    public Intent classify(String input) {
        if (input == null || input.isBlank()) return Intent.UNKNOWN;

        String normalized = tokenizer.normalize(input).toLowerCase();

        // ── 1. Check for math expression first (regex, unambiguous) ──────
        if (MATH_PATTERN.matcher(normalized).find()) {
            return Intent.MATH;
        }

        Map<Intent, Integer> scores = new EnumMap<>(Intent.class);
        for (Intent i : Intent.values()) scores.put(i, 0);

        // ── 2. Score multi-word phrases (5 pts each) ──────────────────────
        for (Map.Entry<Intent, List<String>> entry : PHRASES.entrySet()) {
            for (String phrase : entry.getValue()) {
                if (normalized.contains(phrase)) {
                    scores.merge(entry.getKey(), 5, Integer::sum);
                }
            }
        }

        // ── 3. Score single keywords against clean tokens (3 pts each) ───
        String[] rawTokens = normalized.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokens) {
            String clean = t.replaceAll("[^a-z0-9]", "");
            if (!clean.isEmpty() && !STOPS.contains(clean)) tokens.add(clean);
        }

        for (Map.Entry<Intent, List<String>> entry : KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                for (String token : tokens) {
                    if (token.equals(keyword)) {
                        scores.merge(entry.getKey(), 3, Integer::sum);
                    }
                }
            }
        }

        // ── 4. Pick winner ────────────────────────────────────────────────
        Intent best = Intent.UNKNOWN;
        int bestScore = 0;
        for (Map.Entry<Intent, Integer> e : scores.entrySet()) {
            if (e.getValue() > bestScore) {
                bestScore = e.getValue();
                best = e.getKey();
            }
        }

        // ── 5. Tie-break: prefer more specific intents ────────────────────
        // e.g. FAQ_JAVA vs DEFINITION tied → FAQ_JAVA wins (more specific)
        if (bestScore > 0) {
            List<Intent> tied = new ArrayList<>();
            for (Map.Entry<Intent, Integer> e : scores.entrySet())
                if (e.getValue() == bestScore) tied.add(e.getKey());
            if (tied.size() > 1) best = pickMostSpecific(tied);
        }

        return best;
    }

    private Intent pickMostSpecific(List<Intent> tied) {
        // Priority order: FAQ_JAVA > FAQ_CODEALPHA > DEFINITION > MATH > specific > general
        int[] priority = new int[Intent.values().length];
        priority[Intent.FAQ_JAVA.ordinal()]      = 10;
        priority[Intent.FAQ_CODEALPHA.ordinal()] = 10;
        priority[Intent.MATH.ordinal()]          = 9;
        priority[Intent.JOKE.ordinal()]          = 8;
        priority[Intent.TIME.ordinal()]          = 7;
        priority[Intent.DATE.ordinal()]          = 7;
        priority[Intent.ABOUT_BOT.ordinal()]     = 6;
        priority[Intent.CAPABILITIES.ordinal()]  = 6;
        priority[Intent.DEFINITION.ordinal()]    = 5;
        priority[Intent.GREETING.ordinal()]      = 4;
        priority[Intent.FAREWELL.ordinal()]      = 4;
        priority[Intent.THANKS.ordinal()]        = 4;
        priority[Intent.HELP.ordinal()]          = 3;
        Intent best = tied.get(0);
        for (Intent i : tied) if (priority[i.ordinal()] > priority[best.ordinal()]) best = i;
        return best;
    }
}
