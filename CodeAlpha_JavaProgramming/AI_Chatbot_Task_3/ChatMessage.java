package chatbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {

    public enum Sender { USER, BOT }

    private final String text;
    private final Sender sender;
    private final LocalDateTime timestamp;
    private Intent detectedIntent;

    public ChatMessage(String text, Sender sender) {
        this.text = text;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(String text, Sender sender, Intent intent) {
        this(text, sender);
        this.detectedIntent = intent;
    }

    public String getText()                        { return text; }
    public Sender getSender()                      { return sender; }
    public LocalDateTime getTimestamp()            { return timestamp; }
    public Intent getDetectedIntent()              { return detectedIntent; }
    public void setDetectedIntent(Intent intent)   { this.detectedIntent = intent; }

    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getFormattedTime(), sender, text);
    }
}
