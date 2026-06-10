package hotel;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DataStore — File I/O persistence layer using plain-text CSV format.
 *
 * Task requirement: "Use OOP + database/File I/O for storing bookings and availability"
 *
 * Saves and loads:
 *   - bookings.dat  → CSV text file (human-readable, opens in any editor)
 *   - rooms.dat     → CSV text file (human-readable, opens in any editor)
 *
 * All data is stored in a local "hotel_data/" folder.
 *
 * bookings.dat columns:
 *   bookingId,status,checkIn,checkOut,roomNumber,guestId,guestName,phone,email,adults,children,totalAmount,paymentTxnId,paymentMethod,paymentSuccess,paymentTime
 *
 * rooms.dat columns:
 *   roomNumber,category,floor,available
 */
public class DataStore {

    private static final String DATA_DIR      = "hotel_data";
    private static final String BOOKINGS_FILE = DATA_DIR + File.separator + "bookings.dat";
    private static final String ROOMS_FILE    = DATA_DIR + File.separator + "rooms.dat";
    private static final String SEP           = "|";  // pipe separator (safe for CSV-like text)

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ── Rooms ─────────────────────────────────────────────────────────────────

    public static void saveRooms(List<Room> rooms) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            pw.println("# rooms.dat — Hotel Room Data (plain text, pipe-separated)");
            pw.println("# roomNumber|category|floor|available");
            for (Room r : rooms) {
                pw.println(r.getRoomNumber() + SEP
                         + r.getCategory().name() + SEP
                         + r.getFloor() + SEP
                         + r.isAvailable());
            }
        } catch (Exception e) {
            System.err.println("[DataStore] Could not save rooms: " + e.getMessage());
        }
    }

    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File f = new File(ROOMS_FILE);
        if (!f.exists()) return rooms;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 4) continue;
                int          roomNo   = Integer.parseInt(p[0].trim());
                RoomCategory category = RoomCategory.valueOf(p[1].trim());
                int          floor    = Integer.parseInt(p[2].trim());
                boolean      avail    = Boolean.parseBoolean(p[3].trim());
                Room room = new Room(roomNo, category, floor);
                room.setAvailable(avail);
                rooms.add(room);
            }
        } catch (Exception e) {
            System.err.println("[DataStore] Could not load rooms: " + e.getMessage());
        }
        return rooms;
    }

    // ── Bookings ──────────────────────────────────────────────────────────────

    public static void saveBookings(List<Booking> bookings) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            pw.println("# bookings.dat — Hotel Booking Data (plain text, pipe-separated)");
            pw.println("# bookingId|status|checkIn|checkOut|roomNumber|guestId|guestName|phone|email|adults|children|totalAmount|paymentTxnId|paymentMethod|paymentSuccess|paymentTime");
            for (Booking b : bookings) {
                Payment pay = b.getPayment();
                String txnId   = pay != null ? pay.getTransactionId() : "";
                String method  = pay != null ? pay.getMethod().name() : "";
                String success = pay != null ? String.valueOf(pay.isSuccess()) : "";
                String paidAt  = pay != null ? pay.getPaidAt().toString() : "";

                pw.println(b.getBookingId()         + SEP
                         + b.getStatus().name()      + SEP
                         + b.getCheckIn()            + SEP
                         + b.getCheckOut()           + SEP
                         + b.getRoom().getRoomNumber()+ SEP
                         + b.getGuest().getGuestId() + SEP
                         + escape(b.getGuest().getName())   + SEP
                         + escape(b.getGuest().getPhone())  + SEP
                         + escape(b.getGuest().getEmail())  + SEP
                         + b.getGuest().getAdults()  + SEP
                         + b.getGuest().getChildren()+ SEP
                         + b.getTotalAmount()        + SEP
                         + txnId + SEP + method + SEP + success + SEP + paidAt);
            }
        } catch (Exception e) {
            System.err.println("[DataStore] Could not save bookings: " + e.getMessage());
        }
    }

    /**
     * Load bookings. Requires the room list so rooms can be linked by number.
     */
    public static List<Booking> loadBookings(List<Room> rooms) {
        List<Booking> bookings = new ArrayList<>();
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return bookings;

        Map<Integer, Room> roomMap = new HashMap<>();
        for (Room r : rooms) roomMap.put(r.getRoomNumber(), r);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 12) continue;

                String       bookingId = p[0].trim();
                BookingStatus status   = BookingStatus.valueOf(p[1].trim());
                LocalDate    checkIn   = LocalDate.parse(p[2].trim());
                LocalDate    checkOut  = LocalDate.parse(p[3].trim());
                int          roomNo    = Integer.parseInt(p[4].trim());
                String       guestId   = p[5].trim();
                String       guestName = unescape(p[6].trim());
                String       phone     = unescape(p[7].trim());
                String       email     = unescape(p[8].trim());
                int          adults    = Integer.parseInt(p[9].trim());
                int          children  = Integer.parseInt(p[10].trim());

                Room room = roomMap.get(roomNo);
                if (room == null) continue; // room missing, skip

                Guest   guest   = new Guest(guestId, guestName, phone, email, adults, children);
                Booking booking = new Booking(bookingId, guest, room, checkIn, checkOut);
                booking.setStatus(status);

                // Restore payment if present
                if (p.length >= 16 && !p[12].trim().isEmpty()) {
                    String       txnId      = p[12].trim();
                    PaymentMethod method    = PaymentMethod.valueOf(p[13].trim());
                    boolean       success   = Boolean.parseBoolean(p[14].trim());
                    LocalDateTime paidAt    = p[15].trim().isEmpty() ? LocalDateTime.now()
                                                                      : LocalDateTime.parse(p[15].trim());
                    Payment pay = new Payment(bookingId, booking.getTotalAmount(), method, txnId, success, paidAt);
                    booking.setPayment(pay);
                }

                bookings.add(booking);
            }
        } catch (Exception e) {
            System.err.println("[DataStore] Could not load bookings: " + e.getMessage());
        }
        return bookings;
    }

    /** Wipe all saved data (for testing / reset). */
    public static void clearAll() {
        new File(BOOKINGS_FILE).delete();
        new File(ROOMS_FILE).delete();
    }

    public static String getDataDir() { return DATA_DIR; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Replace pipe and newline chars so they don't break the format. */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("|", "\\pipe").replace("\n", " ").replace("\r", "");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\pipe", "|");
    }
}
