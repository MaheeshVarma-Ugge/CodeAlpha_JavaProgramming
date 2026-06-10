package chatbot;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        boolean useConsole = false;
        for (String arg : args)
            if (arg.equalsIgnoreCase("--console")) { useConsole = true; break; }

        if (useConsole) {
            new ChatEngine().startConsoleSession();
        } else {
            SwingUtilities.invokeLater(() -> new ChatbotGUI().launch());
        }
    }
}
