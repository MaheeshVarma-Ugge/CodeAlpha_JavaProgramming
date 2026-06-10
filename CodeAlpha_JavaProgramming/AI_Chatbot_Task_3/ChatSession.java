package chatbot;

import java.util.*;

public class ChatSession {

    private final List<ChatMessage> history = new ArrayList<>();
    private String lastBotResponse = null;
    private Intent lastIntent = Intent.UNKNOWN;
    private SentimentAnalyzer.Sentiment sentiment = SentimentAnalyzer.Sentiment.NEUTRAL;
    private int turnCount = 0;
    private String userName = null;

    public void addMessage(ChatMessage msg) {
        history.add(msg);
        if (msg.getSender() == ChatMessage.Sender.BOT)  lastBotResponse = msg.getText();
        if (msg.getSender() == ChatMessage.Sender.USER) turnCount++;
    }

    public List<ChatMessage> getHistory()                        { return Collections.unmodifiableList(history); }
    public String getLastBotResponse()                           { return lastBotResponse; }
    public Intent getLastIntent()                                { return lastIntent; }
    public void setLastIntent(Intent i)                          { this.lastIntent = i; }
    public SentimentAnalyzer.Sentiment getSentiment()            { return sentiment; }
    public void setSentiment(SentimentAnalyzer.Sentiment s)      { this.sentiment = s; }
    public int getTurnCount()                                    { return turnCount; }
    public String getUserName()                                  { return userName; }
    public void setUserName(String name)                         { this.userName = name; }

    public void clear() {
        history.clear();
        lastBotResponse = null;
        lastIntent = Intent.UNKNOWN;
        turnCount = 0;
    }
}
