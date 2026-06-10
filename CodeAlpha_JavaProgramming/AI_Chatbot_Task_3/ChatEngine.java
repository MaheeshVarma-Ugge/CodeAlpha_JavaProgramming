package chatbot;

import java.util.List;
import java.util.Scanner;
import java.util.regex.*;

/**
 * ChatEngine — central coordinator.
 * FIX: Removed all "ALPHA" / "CodeAlpha" mentions from messages.
 */
public class ChatEngine {

    private final IntentClassifier  classifier;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final Tokenizer         tokenizer;
    private final KnowledgeBase     knowledgeBase;
    private final ResponseBuilder   responseBuilder;
    private final ChatSession       session;

    private static final Pattern NAME_PATTERN = Pattern.compile(
        "(?:my name is|i am|i'm|call me)\\s+([A-Za-z]+)", Pattern.CASE_INSENSITIVE
    );

    public ChatEngine() {
        classifier        = new IntentClassifier();
        sentimentAnalyzer = new SentimentAnalyzer();
        tokenizer         = new Tokenizer();
        knowledgeBase     = new KnowledgeBase();
        responseBuilder   = new ResponseBuilder(knowledgeBase);
        session           = new ChatSession();
    }

    public String processInput(String userInput) {
        if (userInput == null || userInput.isBlank())
            return "Please say something! I'm listening.";

        String input = userInput.trim();

        // Extract user name if introduced
        Matcher m = NAME_PATTERN.matcher(input);
        if (m.find()) session.setUserName(capitalize(m.group(1)));

        // NLP pipeline
        List<String> tokens = tokenizer.tokenize(tokenizer.normalize(input));
        SentimentAnalyzer.Sentiment sentiment = sentimentAnalyzer.analyze(tokens);
        session.setSentiment(sentiment);

        Intent intent = classifier.classify(input);
        session.setLastIntent(intent);

        // Log user message
        session.addMessage(new ChatMessage(input, ChatMessage.Sender.USER, intent));

        // Build response
        String response = responseBuilder.build(intent, input, session);

        // Greet by name on the very first turn
        if (session.getTurnCount() == 1 && session.getUserName() != null)
            response = "Nice to meet you, " + session.getUserName() + "! " + response;

        // Log bot message
        session.addMessage(new ChatMessage(response, ChatMessage.Sender.BOT));

        return response;
    }

    public String getWelcomeMessage() {
        return "Hello! I am your AI assistant powered by Java NLP.\n"
             + "I can help with Java questions, internship info, jokes, math, and more!\n"
             + "Type 'help' to see everything I can do, or just start chatting.";
    }

    public Intent            getLastIntent()   { return session.getLastIntent(); }
    public int               getTurnCount()    { return session.getTurnCount(); }
    public List<ChatMessage> getHistory()      { return session.getHistory(); }
    public void              resetSession()    { session.clear(); }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public void startConsoleSession() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==============================================");
        System.out.println("     Java AI Chatbot — NLP Powered           ");
        System.out.println("==============================================");
        System.out.println(getWelcomeMessage());
        System.out.println("----------------------------------------------");
        System.out.println("(Type 'quit' to end the session)");
        System.out.println();
        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();
            if (input == null || input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Bot: Goodbye! Thanks for chatting.");
                break;
            }
            System.out.println("Bot: " + processInput(input));
            System.out.println();
        }
        scanner.close();
    }
}
