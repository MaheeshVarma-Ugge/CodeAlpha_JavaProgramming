package hotel;

import javax.swing.SwingUtilities;

/**
 * Main — entry point for the Hotel Reservation System.
 *
 * Run modes:
 *   java -cp out hotel.Main           → launches GUI (default)
 *   java -cp out hotel.Main --console → launches console mode
 */
public class Main {

    public static void main(String[] args) {
        boolean console = false;
        for (String arg : args)
            if (arg.equalsIgnoreCase("--console")) { console = true; break; }

        if (console) {
            new ConsoleApp().run();
        } else {
            SwingUtilities.invokeLater(() -> new HotelGUI().launch());
        }
    }
}
