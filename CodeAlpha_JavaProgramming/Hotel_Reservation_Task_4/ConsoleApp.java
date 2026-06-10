package hotel;

import java.time.LocalDate;
import java.util.*;

/**
 * ConsoleApp — terminal-based UI for the Hotel Reservation System.
 * Full feature parity with the GUI: search, book, cancel, view, check-in/out.
 **/
public class ConsoleApp {

    private final HotelManager manager = new HotelManager();
    private final Scanner       scanner = new Scanner(System.in);

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> searchRooms();
                case "2" -> makeBooking();
                case "3" -> viewAllBookings();
                case "4" -> viewBookingDetail();
                case "5" -> cancelBooking();
                case "6" -> checkIn();
                case "7" -> checkOut();
                case "8" -> viewRoomOverview();
                case "0" -> { System.out.println("\nThank you for using Hotel Reservation System. Goodbye!"); running = false; }
                default  -> System.out.println("  Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private void printBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         HOTEL RESERVATION SYSTEM");
        System.out.println("=".repeat(60));
    }

    private void printMainMenu() {
        System.out.println("\n── MAIN MENU " + "─".repeat(47));
        System.out.println("  1. Search Available Rooms");
        System.out.println("  2. Make a Booking");
        System.out.println("  3. View All Bookings");
        System.out.println("  4. View Booking Details");
        System.out.println("  5. Cancel a Booking");
        System.out.println("  6. Check In");
        System.out.println("  7. Check Out");
        System.out.println("  8. Room Overview");
        System.out.println("  0. Exit");
        System.out.print("\n  Enter choice: ");
    }

    private void searchRooms() {
        System.out.println("\n── SEARCH ROOMS " + "─".repeat(44));
        System.out.println("  Categories: 1=Standard  2=Deluxe  3=Suite");
        System.out.print("  Select category: ");
        RoomCategory cat = pickCategory();
        if (cat == null) return;

        LocalDate in  = readDate("  Check-In  (YYYY-MM-DD): ");
        LocalDate out = readDate("  Check-Out (YYYY-MM-DD): ");
        if (in == null || out == null || !in.isBefore(out)) {
            System.out.println("  Invalid dates. Check-in must be before check-out."); return;
        }

        List<Room> rooms = manager.searchAvailableRooms(cat, in, out);
        if (rooms.isEmpty()) { System.out.println("  No rooms available for these dates."); return; }
        System.out.println("\n  " + rooms.size() + " room(s) available:\n");
        rooms.forEach(r -> System.out.println("  " + r.getSummary()));
    }

    private void makeBooking() {
        System.out.println("\n── MAKE A BOOKING " + "─".repeat(42));
        System.out.print("  Room Number: ");
        int roomNo; try { roomNo = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) { System.out.println("  Invalid."); return; }
        LocalDate in  = readDate("  Check-In  (YYYY-MM-DD): ");
        LocalDate out = readDate("  Check-Out (YYYY-MM-DD): ");
        if (in == null || out == null || !in.isBefore(out)) { System.out.println("  Invalid dates. Check-in must be before check-out."); return; }

        System.out.print("  Guest Name:  "); String name  = scanner.nextLine().trim();
        System.out.print("  Phone:       "); String phone = scanner.nextLine().trim();
        System.out.print("  Email:       "); String email = scanner.nextLine().trim();
        System.out.print("  Adults:      "); int adults   = readInt(1);
        System.out.print("  Children:    "); int children = readInt(0);

        String guestId = "G" + System.currentTimeMillis() % 100000;
        Guest  guest   = new Guest(guestId, name, phone, email, adults, children);
        Booking booking = manager.createBooking(guest, roomNo, in, out);

        if (booking == null) { System.out.println("  Booking failed. Room may not be available."); return; }

        System.out.println("\n  Booking created: " + booking.getBookingId());
        System.out.printf("  Total Amount: Rs %.2f%n", booking.getTotalAmount());

        System.out.println("\n  Payment Methods: 1=Credit Card  2=Debit Card  3=UPI  4=Net Banking  5=Cash");
        System.out.print("  Choose method: ");
        PaymentMethod method = pickPaymentMethod();
        Payment payment = manager.processPayment(booking.getBookingId(), method);

        if (payment != null && payment.isSuccess()) {
            System.out.println("\n  Payment successful!");
            System.out.println(payment.getReceipt());
        } else {
            System.out.println("\n  Payment failed. Booking is still confirmed — please retry.");
        }
    }

    private void viewAllBookings() {
        System.out.println("\n── ALL BOOKINGS " + "─".repeat(44));
        List<Booking> all = manager.getAllBookings();
        if (all.isEmpty()) { System.out.println("  No bookings found."); return; }
        System.out.printf("  %-12s %-18s %-6s %-8s %-12s %-12s %-10s %s%n",
            "Booking ID","Guest","Room","Cat","Check-In","Check-Out","Amount","Status");
        System.out.println("  " + "-".repeat(95));
        all.forEach(b -> System.out.println("  " + b.getShortSummary()));
    }

    private void viewBookingDetail() {
        System.out.print("\n  Booking ID: ");
        String id = scanner.nextLine().trim();
        manager.findBookingById(id).ifPresentOrElse(
            b -> System.out.println(b.getDetailView()),
            () -> System.out.println("  Booking not found.")
        );
    }

    private void cancelBooking() {
        System.out.print("\n  Booking ID to cancel: ");
        String id = scanner.nextLine().trim();
        boolean ok = manager.cancelBooking(id);
        System.out.println(ok ? "  Booking " + id + " cancelled. Room is now available."
                              : "  Cannot cancel. Booking not found or already cancelled.");
    }

    private void checkIn() {
        System.out.print("\n  Booking ID for check-in: ");
        String id = scanner.nextLine().trim();
        System.out.println(manager.checkIn(id) ? "  Guest checked in." : "  Check-in failed. Booking must be CONFIRMED.");
    }

    private void checkOut() {
        System.out.print("\n  Booking ID for check-out: ");
        String id = scanner.nextLine().trim();
        System.out.println(manager.checkOut(id) ? "  Guest checked out. Room is now available." : "  Check-out failed. Booking must be CHECKED IN.");
    }

    private void viewRoomOverview() {
        System.out.println("\n── ROOM OVERVIEW " + "─".repeat(43));
        Map<RoomCategory, Long> stats = manager.getAvailabilityStats();
        for (RoomCategory cat : RoomCategory.values()) {
            long avail = stats.getOrDefault(cat, 0L);
            long total = manager.getRoomsByCategory(cat).size();
            System.out.printf("  %-8s : %d/%d available  |  Rs %.0f/night%n",
                cat.getDisplayName(), avail, total, cat.getPricePerNight());
        }
        System.out.println();
        manager.getAllRooms().forEach(r -> System.out.println("  " + r.getSummary()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private RoomCategory pickCategory() {
        try {
            int n = Integer.parseInt(scanner.nextLine().trim());
            return RoomCategory.values()[n - 1];
        } catch (Exception e) { System.out.println("  Invalid category."); return null; }
    }

    private PaymentMethod pickPaymentMethod() {
        try {
            int n = Integer.parseInt(scanner.nextLine().trim());
            return PaymentMethod.values()[n - 1];
        } catch (Exception e) { return PaymentMethod.CASH; }
    }

    /**
     * Reads a date from user input.
     * Accepts flexible formats: "2026-7-3", "2026 - 7 - 3", "2026/07/03"
     * Normalises to ISO format (YYYY-MM-DD) before parsing.
     */
    private LocalDate readDate(String prompt) {
        System.out.print(prompt);
        try {
            String raw = scanner.nextLine().trim();
            // Remove spaces around separators, normalise slashes/dots to dashes
            String cleaned = raw.replaceAll("\\s*[-/.]\\s*", "-");
            // Zero-pad month and day if single digit: "2026-7-3" → "2026-07-03"
            String[] parts = cleaned.split("-");
            if (parts.length == 3) {
                String year  = parts[0].trim();
                String month = parts[1].trim().length() == 1 ? "0" + parts[1].trim() : parts[1].trim();
                String day   = parts[2].trim().length() == 1 ? "0" + parts[2].trim() : parts[2].trim();
                cleaned = year + "-" + month + "-" + day;
            }
            return LocalDate.parse(cleaned);
        } catch (Exception e) {
            System.out.println("  (Invalid date format. Please use YYYY-MM-DD, e.g. 2026-07-03)");
            return null;
        }
    }

    private int readInt(int def) {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return def; }
    }
}
