package chatbot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ResponseBuilder — assembles the final reply.
 *
 * FIX:
 *   - FAQ_JAVA, FAQ_CODEALPHA, DEFINITION all properly call lookupFAQ()
 *     and fall back to the pool string only when no FAQ entry is found.
 *   - MATH properly calls MathEvaluator and falls back gracefully.
 *   - Sentiment prefix is only applied when sentiment is strong enough
 *     (avoids annoying prefix on every single message).
 *   - Removed all "ALPHA" / "CodeAlpha" mentions from prefixes.
 */
public class ResponseBuilder {

    private final KnowledgeBase kb;
    private final MathEvaluator math;
    private final Random random = new Random();

    private static final List<String> POSITIVE_PREFIX = Arrays.asList(
        "Great to hear you're in a good mood! ",
        "Love the positive energy! ",
        ""  // sometimes no prefix — feels more natural
    );
    private static final List<String> NEGATIVE_PREFIX = Arrays.asList(
        "I'm sorry you're frustrated. Let me help: ",
        "I understand. Here's what I know: ",
        "Don't worry, I've got you! "
    );

    public ResponseBuilder(KnowledgeBase kb) {
        this.kb   = kb;
        this.math = new MathEvaluator();
    }

    public String build(Intent intent, String userInput, ChatSession session) {
        String response = resolve(intent, userInput, session);
        response = substituteRuntime(response);
        response = applySentimentPrefix(response, session.getSentiment());
        return response;
    }

    private String resolve(Intent intent, String input, ChatSession session) {

        switch (intent) {

            // ── FAQ intents: try knowledge base first ────────────────────
            case FAQ_JAVA:
            case FAQ_CODEALPHA:
            case DEFINITION: {
                String answer = kb.lookupFAQ(input);
                if (answer != null) return answer;
                // Fall back to pool response (which is now a real helpful sentence)
                return kb.getResponse(intent);
            }

            // ── Math: parse and compute ──────────────────────────────────
            case MATH: {
                String result = math.evaluate(input);
                if (result != null) return result;
                return kb.getResponse(Intent.MATH);
            }

            // ── Repeat last bot response ─────────────────────────────────
            case REPEAT: {
                String last = session.getLastBotResponse();
                if (last == null) return "I haven't said anything yet! Ask me something first.";
                return "Sure! Here's what I said earlier:\n" + last;
            }

            // ── Everything else from the pool ────────────────────────────
            default:
                return kb.getResponse(intent);
        }
    }

    private String substituteRuntime(String text) {
        if (text.contains("RUNTIME_TIME")) {
            text = text.replace("RUNTIME_TIME",
                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
        }
        if (text.contains("RUNTIME_DATE")) {
            text = text.replace("RUNTIME_DATE",
                LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        }
        return text;
    }

    private String applySentimentPrefix(String text, SentimentAnalyzer.Sentiment s) {
        if (s == SentimentAnalyzer.Sentiment.POSITIVE) {
            String prefix = POSITIVE_PREFIX.get(random.nextInt(POSITIVE_PREFIX.size()));
            return prefix + text;
        }
        if (s == SentimentAnalyzer.Sentiment.NEGATIVE) {
            String prefix = NEGATIVE_PREFIX.get(random.nextInt(NEGATIVE_PREFIX.size()));
            return prefix + text;
        }
        return text;
    }
}
