package util;

import java.util.Scanner;
public class InputUtil {

    private static final Scanner SC = new Scanner(System.in);

    /** Read a non-empty trimmed line. */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    /** Read a positive integer; re-prompts on invalid input. */
    public static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(SC.nextLine().trim());
                if (v > 0) return v;
                System.out.println("  Please enter a positive integer.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a whole number.");
            }
        }
    }

    /** Read a positive double; re-prompts on invalid input. */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(SC.nextLine().trim());
                if (v > 0) return v;
                System.out.println("  Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }

    /** Read a single menu-choice character. */
    public static String readChoice(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim().toLowerCase();
    }

    /** Read yes/no; returns true for 'y' or 'yes'. */
    public static boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String in = SC.nextLine().trim().toLowerCase();
        return in.equals("y") || in.equals("yes");
    }
}
