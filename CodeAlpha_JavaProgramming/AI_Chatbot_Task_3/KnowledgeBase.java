package chatbot;

import java.util.*;

/**
 * KnowledgeBase — stores all responses and FAQ answers.
 *
 * FIX:
 *   - Removed ALL occurrences of "ALPHA", "CodeAlpha" from responses
 *   - Removed "FAQ_LOOKUP" / "MATH_COMPUTE" dummy placeholders — every intent
 *     now has real response strings; special intents are handled by ResponseBuilder
 *   - Expanded FAQ map with more keywords and longer answers
 *   - Added many more conversational responses so the bot never feels stuck
 */
public class KnowledgeBase {

    private final Map<Intent, List<String>> responsePool = new EnumMap<>(Intent.class);
    private final Map<String, String> faqMap = new LinkedHashMap<>();
    private final Random random = new Random();

    public KnowledgeBase() {
        buildResponsePool();
        buildFAQMap();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void buildResponsePool() {

        responsePool.put(Intent.GREETING, Arrays.asList(
            "Hello there! How can I help you today?",
            "Hey! Great to see you. What's on your mind?",
            "Hi! I'm your Java-powered AI assistant. How can I help?",
            "Greetings! I'm ready to chat. What would you like to know?",
            "Hello! Hope you're having a great day. What can I do for you?"
        ));

        responsePool.put(Intent.FAREWELL, Arrays.asList(
            "Goodbye! It was great chatting with you.",
            "See you later! Come back anytime you need help.",
            "Take care! Feel free to return whenever you have questions.",
            "Farewell! Hope I was helpful today. Have a great day!"
        ));

        responsePool.put(Intent.THANKS, Arrays.asList(
            "You're very welcome! Happy to help.",
            "Glad I could assist! Is there anything else you need?",
            "No problem at all! That's what I'm here for.",
            "My pleasure! Feel free to ask if you have more questions.",
            "Anytime! Let me know if you need anything else."
        ));

        responsePool.put(Intent.HELP, Arrays.asList(
            "Sure! Here is what I can do:\n" +
            "  - Java & OOP programming questions\n" +
            "  - Internship information\n" +
            "  - Programming jokes\n" +
            "  - Current time & date\n" +
            "  - Simple math (e.g. 15 + 27, 100 / 4)\n" +
            "  - General conversation\n" +
            "Just type naturally and I will do my best!",
            "I am here to assist! Try asking me about Java concepts, internship tasks, jokes, or math."
        ));

        responsePool.put(Intent.WEATHER, Arrays.asList(
            "I don't have access to real-time weather data. Please check a weather app or weather.com for accurate forecasts!",
            "I can't fetch live weather right now. Try Google Weather or your local weather service!"
        ));

        responsePool.put(Intent.TIME, Arrays.asList(
            "The current time is: RUNTIME_TIME",
            "It's RUNTIME_TIME right now!"
        ));

        responsePool.put(Intent.DATE, Arrays.asList(
            "Today is RUNTIME_DATE.",
            "The current date is RUNTIME_DATE."
        ));

        responsePool.put(Intent.JOKE, Arrays.asList(
            "Why do Java developers wear glasses? Because they don't C#!",
            "How many programmers does it take to change a light bulb? None — that's a hardware problem!",
            "A SQL query walks into a bar and asks two tables: 'Can I join you?'",
            "Why don't programmers like nature? It has too many bugs and no documentation!",
            "Why did the developer go broke? He used up all his cache!",
            "Oct 31 == Dec 25. That's why programmers mix up Halloween and Christmas!",
            "What's a computer's favourite snack? Microchips!",
            "I told my computer I needed a break. Now it won't stop sending me Kit-Kat ads.",
            "Why was the JavaScript developer sad? Because he didn't know how to null his feelings!",
            "What do you call a programmer from Finland? Nerdic!"
        ));

        responsePool.put(Intent.COMPLIMENT, Arrays.asList(
            "Thank you, that's very kind! I'm just doing my best to be helpful.",
            "That means a lot! I work hard to give you accurate answers.",
            "You are too kind! I am always trying to improve."
        ));

        responsePool.put(Intent.INSULT_RESPONSE, Arrays.asList(
            "I'm sorry you feel that way. I'm always working to improve. How can I help better?",
            "I understand your frustration. Let's try again — what can I help you with?",
            "I won't take offense! I'm here to help. What would you like to know?"
        ));

        responsePool.put(Intent.ABOUT_BOT, Arrays.asList(
            "I'm an AI chatbot built entirely with Java for the internship program! I use NLP techniques like tokenization, intent classification, and sentiment analysis to understand you.",
            "I'm a rule-based AI chatbot developed in Java. I understand natural language through tokenization, stop-word removal, stemming, and intent scoring — no external AI libraries!",
            "I'm a Java-powered chatbot. I understand your messages by breaking them into words, scoring them against intent patterns, and picking the best matching response."
        ));

        responsePool.put(Intent.CAPABILITIES, Arrays.asList(
            "Here is what I can do:\n" +
            "  - Natural Language Understanding (tokenization, stemming, intent classification)\n" +
            "  - Java & OOP Q&A (20+ topics)\n" +
            "  - Internship FAQ\n" +
            "  - Programming jokes\n" +
            "  - Current time & date\n" +
            "  - Simple arithmetic\n" +
            "  - Sentiment-aware responses\n" +
            "  - General conversation\n" +
            "Just type naturally!"
        ));

        responsePool.put(Intent.REPEAT, Arrays.asList(
            "Sure! Here's what I said: REPEAT_LAST"
        ));

        responsePool.put(Intent.AFFIRM, Arrays.asList(
            "Great! How can I continue to help you?",
            "Glad we're on the same page! What else would you like to know?",
            "Awesome! Feel free to ask me anything else."
        ));

        responsePool.put(Intent.DENY, Arrays.asList(
            "No worries! Let me know how I can help differently.",
            "Understood! What would you like instead?",
            "That's alright — feel free to rephrase or ask something else."
        ));

        // FAQ intents — ResponseBuilder will call lookupFAQ() for these.
        // These strings are fallbacks only when lookupFAQ returns null.
        responsePool.put(Intent.FAQ_JAVA, Arrays.asList(
            "I don't have specific info on that Java topic yet. Try asking about: java, jvm, oop, inheritance, polymorphism, encapsulation, abstraction, interface, thread, exception, arraylist, hashmap, lambda, stream, generics, spring, or maven.",
            "That Java topic isn't in my knowledge base yet. Try one of: java, jvm, jdk, jre, oop, inheritance, polymorphism, thread, exception, lambda, stream, collections, generics, spring, maven."
        ));

        responsePool.put(Intent.FAQ_CODEALPHA, Arrays.asList(
            "I don't have that internship detail yet. Try asking about: internship, certificate, task, submission, linkedin, github, or recommendation.",
            "That internship topic isn't in my FAQ. Try: internship, certificate, submission, linkedin, github, recommendation."
        ));

        // MATH fallback — shown when MathEvaluator can't parse the expression
        responsePool.put(Intent.MATH, Arrays.asList(
            "I can do arithmetic! Try: '15 + 27', '100 / 4', '3 * 8', or '2 ^ 10'. Supports +, -, *, /, ^, %.",
            "Please write a math expression like: 25 + 75, 144 / 12, or 2 ^ 8. I support +, -, *, /, ^, and %."
        ));

        responsePool.put(Intent.DEFINITION, Arrays.asList(
            "I don't have a definition for that yet. Try asking about Java or OOP terms like: java, oop, inheritance, polymorphism, encapsulation, abstraction, interface, thread, exception, lambda, or stream.",
            "That term isn't in my knowledge base. Try asking about Java concepts or internship topics."
        ));

        responsePool.put(Intent.UNKNOWN, Arrays.asList(
            "I'm not quite sure I understood that. Could you rephrase it?",
            "Hmm, that stumped me! Try typing 'help' to see what I can do.",
            "I didn't quite catch that. Ask me about Java, jokes, the time, math, or the internship!",
            "I'm still learning! Try a different question or type 'help'.",
            "I'm not sure how to respond to that. Type 'help' to see all my capabilities."
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void buildFAQMap() {
        // Java Topics
        faqMap.put("java",            "Java is a high-level, platform-independent, object-oriented programming language. It compiles to bytecode that runs on the JVM, making it write-once-run-anywhere.");
        faqMap.put("jvm",             "The JVM (Java Virtual Machine) is a runtime engine that executes Java bytecode. It enables Java programs to run on any device or OS without modification.");
        faqMap.put("jdk",             "The JDK (Java Development Kit) includes the compiler (javac), JRE, debugger, and all tools needed to develop Java applications.");
        faqMap.put("jre",             "The JRE (Java Runtime Environment) contains the JVM and libraries needed to run Java applications, but not to compile them.");
        faqMap.put("oop",             "OOP (Object-Oriented Programming) organises code around objects. Its four pillars are: Encapsulation (data hiding), Inheritance (code reuse), Polymorphism (many forms), and Abstraction (hiding complexity).");
        faqMap.put("object oriented", "Object-Oriented Programming (OOP) organises code using classes and objects. The four pillars are Encapsulation, Inheritance, Polymorphism, and Abstraction.");
        faqMap.put("inheritance",     "Inheritance allows a child class to acquire properties and methods from a parent class using the 'extends' keyword. It promotes code reuse and establishes an is-a relationship.");
        faqMap.put("polymorphism",    "Polymorphism means 'many forms'. In Java it comes in two types: method overloading (same name, different parameters — compile-time) and method overriding (child class redefines parent method — runtime).");
        faqMap.put("encapsulation",   "Encapsulation bundles data (fields) and methods into a class, hiding internal details using access modifiers (private, protected). Data is exposed safely via public getters and setters.");
        faqMap.put("abstraction",     "Abstraction hides implementation details and exposes only the necessary interface. In Java it is achieved through abstract classes (abstract keyword) and interfaces.");
        faqMap.put("interface",       "An interface in Java defines a contract — method signatures without implementations. Classes implement it using 'implements'. Since Java 8, interfaces can have default and static methods.");
        faqMap.put("thread",          "A thread is a lightweight unit of execution. Java supports multithreading via the Thread class and Runnable interface. Key concepts: synchronization, wait/notify, and java.util.concurrent.");
        faqMap.put("multithreading",  "Multithreading lets multiple threads run concurrently in a Java program. Use Thread, Runnable, or ExecutorService. Synchronize shared data with synchronized blocks or locks.");
        faqMap.put("exception",       "Exceptions represent runtime errors. Java has checked exceptions (must be declared or handled) and unchecked exceptions (extend RuntimeException). Use try-catch-finally to handle them.");
        faqMap.put("collection",      "The Java Collections Framework provides List (ordered), Set (unique), Queue (FIFO), and Map (key-value) interfaces. Key implementations: ArrayList, LinkedList, HashSet, HashMap, TreeMap.");
        faqMap.put("arraylist",       "ArrayList is a resizable-array implementation of List. It allows duplicates and maintains insertion order. Random access is O(1); insertion/deletion is O(n).");
        faqMap.put("hashmap",         "HashMap stores key-value pairs. It allows one null key and multiple null values. Average O(1) for get/put. It is NOT thread-safe — use ConcurrentHashMap for concurrent access.");
        faqMap.put("lambda",          "Lambda expressions (Java 8+) are anonymous functions written as: (parameters) -> body. Example: list.forEach(x -> System.out.println(x)). Used with functional interfaces.");
        faqMap.put("stream",          "Streams (Java 8+) enable declarative, functional-style operations on collections. Key methods: filter(), map(), reduce(), collect(), forEach(). Streams are lazy and can be parallel.");
        faqMap.put("generics",        "Generics allow type parameters in classes, interfaces, and methods for compile-time type safety. Example: List<String> ensures only String objects are stored.");
        faqMap.put("spring",          "Spring is a powerful Java framework for enterprise applications. Key modules: Spring Core (IoC/DI), Spring MVC (web layer), Spring Boot (auto-config, embedded server), Spring Data (DB access).");
        faqMap.put("maven",           "Maven is a build automation and dependency management tool for Java. It uses pom.xml to define project structure, dependencies, plugins, and build lifecycle phases.");
        faqMap.put("constructor",     "A constructor is a special method called when an object is created. It has the same name as the class and no return type. Java provides a default no-arg constructor if none is defined.");
        faqMap.put("overloading",     "Method overloading means defining multiple methods with the same name but different parameter lists in the same class. It is resolved at compile time (static polymorphism).");
        faqMap.put("overriding",      "Method overriding means a child class provides its own implementation of a method defined in the parent class. It is resolved at runtime (dynamic polymorphism). Use @Override annotation.");
        faqMap.put("annotation",      "Annotations are metadata added to Java code using @. Common ones: @Override, @Deprecated, @SuppressWarnings, @FunctionalInterface. They are processed at compile-time or runtime.");
        faqMap.put("serialization",   "Serialization converts a Java object into a byte stream for storage or transmission. Implement the Serializable interface. Use ObjectOutputStream to write and ObjectInputStream to read.");
        faqMap.put("bytecode",        "Java source code (.java) is compiled into bytecode (.class files) by javac. The JVM interprets or JIT-compiles this bytecode to machine code at runtime.");
        faqMap.put("classpath",       "The classpath tells the JVM where to find compiled class files and JAR libraries. Set it with -cp or -classpath flag, or via the CLASSPATH environment variable.");

        // Internship Topics
        faqMap.put("internship",      "The internship program requires completing 2-3 out of 4 assigned tasks, uploading source code to GitHub (repo: ProgramName_ProjectName), posting a video on LinkedIn, and submitting via the form.");
        faqMap.put("certificate",     "Upon successful completion you receive a QR-verified Completion Certificate and a Unique ID Certificate. A Letter of Recommendation is awarded based on performance.");
        faqMap.put("task",            "Interns must complete any 2-3 of 4 assigned tasks. Each task must be uploaded to GitHub and explained in a LinkedIn video walkthrough.");
        faqMap.put("submission",      "Submit completed tasks using the official Submission Form. Ensure your GitHub repository is public and your LinkedIn video is posted before submitting.");
        faqMap.put("linkedin",        "Share your internship status on LinkedIn. Post a video walkthrough of your project with the GitHub repository link and tag the company page.");
        faqMap.put("github",          "Upload your complete source code to a public GitHub repository. The naming convention is: ProgramName_ProjectName (e.g., JavaChatbot or AIChatbot).");
        faqMap.put("recommendation",  "A Letter of Recommendation is awarded based on your performance during the internship — quality of code, documentation, and timely submission.");
        faqMap.put("certificate",     "You receive a QR-verified Completion Certificate and a Unique ID Certificate upon successfully finishing the internship.");
        faqMap.put("offer letter",    "An internship Offer Letter is provided at the start of the program, along with perks like a completion certificate, LOR, and placement support.");
    }

    public String getResponse(Intent intent) {
        List<String> list = responsePool.getOrDefault(intent, responsePool.get(Intent.UNKNOWN));
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Look up an FAQ answer by scanning the user's full input string.
     * Returns the LONGEST matching key's answer (most specific match wins).
     */
    public String lookupFAQ(String input) {
        String lower = input.toLowerCase();
        String bestKey = null;
        String bestAnswer = null;
        for (Map.Entry<String, String> entry : faqMap.entrySet()) {
            if (lower.contains(entry.getKey())) {
                // Prefer longer (more specific) keys
                if (bestKey == null || entry.getKey().length() > bestKey.length()) {
                    bestKey = entry.getKey();
                    bestAnswer = entry.getValue();
                }
            }
        }
        return bestAnswer;
    }
}
