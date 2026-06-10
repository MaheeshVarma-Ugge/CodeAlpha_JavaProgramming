# CodeAlpha — Java AI Chatbot (Task 3)

A rule-based AI chatbot built entirely in **Java** using Natural Language Processing (NLP) techniques.  
No external AI libraries or APIs — pure Java from scratch.

---

## Features

- **Intent Classification** — Detects 20 user intents using keyword scoring and phrase matching
- **Sentiment Analysis** — Identifies positive, negative, or neutral tone in messages
- **NLP Pipeline** — Tokenization, stop-word removal, stemming, and normalization
- **Java & OOP FAQ** — Answers 28+ Java/OOP concept questions instantly
- **Internship FAQ** — Answers questions about tasks, certificates, GitHub, LinkedIn, and submission
- **Math Evaluator** — Solves arithmetic expressions (e.g. `15 + 27`, `2 ^ 10`, `100 / 4`)
- **Real-time Time & Date** — Returns the current system time and date
- **Programming Jokes** — 10 built-in programming jokes
- **Swing GUI** — Dark-themed chat window with message bubbles and timestamps
- **Console Mode** — Run the chatbot directly in the terminal

---

## Project Structure

```
CodeAlpha_AIChatbot_v3/
├── Main.java               # Entry point — launches GUI or console mode
├── ChatEngine.java         # Core coordinator — connects all NLP components
├── ChatSession.java        # Tracks conversation history and context
├── ResponseBuilder.java    # Assembles final replies with runtime substitutions
├── IntentClassifier.java   # NLP intent detection using keyword + phrase scoring
├── SentimentAnalyzer.java  # Detects positive / negative / neutral tone
├── Tokenizer.java          # Normalize, tokenize, remove stop words, stem
├── KnowledgeBase.java      # All response templates and FAQ answer map
├── MathEvaluator.java      # Parses and evaluates arithmetic expressions
├── Intent.java             # Enum of all 20 intent categories
├── ChatMessage.java        # Data model for a single chat message
└── ChatbotGUI.java         # Full Swing dark-themed chat window
```

---

## How to Run

### Requirements
- Java 8 or higher
- No external libraries needed

### 1 — Navigate into the project folder
```bash
cd AI_Chatbot_Task_3
```

### 2 — Compile
```bash
javac -d out *.java
```

### 3 — Run GUI mode
```bash
java -cp out chatbot.Main
```

### 4 — Run Console mode
```bash
java -cp out chatbot.Main --console
```

### One-liner (compile + run)
```bash
mkdir -p out && javac -d out *.java && java -cp out chatbot.Main
```

---

## NLP Techniques Used

| Technique | Description | File |
|---|---|---|
| Tokenization | Splits sentence into individual word tokens | `Tokenizer.java` |
| Stop-word Removal | Filters out meaningless words (a, the, is...) | `Tokenizer.java` |
| Stemming | Reduces words to root form (running → run) | `Tokenizer.java` |
| Contraction Expansion | Expands can't → cannot, I'm → i am | `Tokenizer.java` |
| Intent Classification | Scores tokens against 20 intent keyword lists | `IntentClassifier.java` |
| Phrase Matching | Detects multi-word phrases (5-point bonus) | `IntentClassifier.java` |
| Regex Math Detection | Detects expressions like `15 + 3` or `15 plus 3` | `IntentClassifier.java` |
| Sentiment Analysis | Positive/negative word scoring with negation handling | `SentimentAnalyzer.java` |

---

## Supported Intents

| Intent | Example Input |
|---|---|
| GREETING | "Hello!", "Hey", "Good morning" |
| FAREWELL | "Bye", "Goodbye", "See you" |
| THANKS | "Thank you", "Thanks a lot" |
| HELP | "Help me", "What can you do?" |
| JOKE | "Tell me a joke", "Say something funny" |
| TIME | "What time is it?", "Current time" |
| DATE | "What's today's date?", "What day is it?" |
| MATH | "15 + 27", "100 divided by 4", "2 ^ 8" |
| FAQ_JAVA | "Explain inheritance", "What is a lambda?" |
| FAQ_CODEALPHA | "How do I submit?", "What is the internship?" |
| ABOUT_BOT | "Who are you?", "Are you an AI?" |
| CAPABILITIES | "What can you do?", "Your features" |
| WEATHER | "Will it rain today?" |
| COMPLIMENT | "You are amazing!" |
| INSULT_RESPONSE | "You are useless" |
| REPEAT | "Say that again", "Repeat" |
| AFFIRM | "Yes", "Sure", "Okay" |
| DENY | "No", "Nope", "Wrong" |
| DEFINITION | "Define encapsulation", "Meaning of thread" |
| UNKNOWN | Anything unrecognised → asks to rephrase |

---

## Java FAQ Topics Covered

`java` · `jvm` · `jdk` · `jre` · `oop` · `inheritance` · `polymorphism` · `encapsulation` · `abstraction` · `interface` · `thread` · `multithreading` · `exception` · `collection` · `arraylist` · `hashmap` · `lambda` · `stream` · `generics` · `spring` · `maven` · `constructor` · `overloading` · `overriding` · `annotation` · `serialization` · `bytecode` · `classpath`

---

## Task 3 Requirements Checklist

- [x] Java-based chatbot
- [x] NLP techniques (tokenization, stemming, intent classification, sentiment analysis)
- [x] Rule-based answers — no external API used
- [x] Trained to respond to frequently asked questions (28+ FAQ entries)
- [x] GUI interface (Java Swing dark-themed chat window)
- [x] Console mode also available

---

## GUI Preview

```
┌─────────────────────────────────────────────┐
│  J   AI Assistant          ● Online  [Clear] │
│      Java NLP Engine • Rule-Based AI         │
├─────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │ Hello! I am your AI assistant        │   │
│  │ powered by Java NLP...          13:45│   │
│  └──────────────────────────────────────┘   │
│                                              │
│       ┌──────────────────────────────────┐  │
│       │ What is inheritance?       13:46 │  │
│       └──────────────────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │ Inheritance allows a child class...  │   │
│  │                                 13:46│   │
│  └──────────────────────────────────────┘   │
│                                              │
├─────────────────────────────────────────────┤
│  [ Type a message...              ] [Send ▶] │
└─────────────────────────────────────────────┘
```

---

## Internship Info

**Program:** CodeAlpha Java Programming Internship  
**Task:** Task 3 — Artificial Intelligence Chatbot  
**Submission:** Upload to GitHub → Post on LinkedIn → Submit via form  
**Repository naming:** `CodeAlpha_AIChatbot`
