package hotel;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HotelManager — the core business logic / service layer.
 *
 * Responsibilities:
 *   - Manages the room inventory (search, filter by category/availability)
 *   - Creates and cancels bookings
 *   - Processes simulated payments
 *   - Persists all data via DataStore (plain-text CSV format)
 */
public class HotelManager {

    private final List<Room>    rooms    = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private int bookingCounter = 1000;

    public HotelManager() {
        loadData();
        if (rooms.isEmpty()) initDefaultRooms();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Initialisation
    // ─────────────────────────────────────────────────────────────────────────

    /** Seed the hotel with 30 rooms across 3 categories. */
    private void initDefaultRooms() {
        // Standard — rooms 101-110, Floor 1
        for (int i = 1; i <= 10; i++)
            rooms.add(new Room(100 + i, RoomCategory.STANDARD, 1));

        // Deluxe — rooms 201-210, Floor 2
        for (int i = 1; i <= 10; i++)
            rooms.add(new Room(200 + i, RoomCategory.DELUXE, 2));

        // Suite — rooms 301-310, Floor 3
        for (int i = 1; i <= 10; i++)
            rooms.add(new Room(300 + i, RoomCategory.SUITE, 3));

        saveData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Room Search
    // ─────────────────────────────────────────────────────────────────────────

    public List<Room> getAllRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Room> getAvailableRooms() {
        return rooms.stream()
            .filter(Room::isAvailable)
            .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsByCategory(RoomCategory category) {
        return rooms.stream()
            .filter(r -> r.isAvailable() && r.getCategory() == category)
            .collect(Collectors.toList());
    }

    public List<Room> getRoomsByCategory(RoomCategory category) {
        return rooms.stream()
            .filter(r -> r.getCategory() == category)
            .collect(Collectors.toList());
    }

    public Optional<Room> getRoomByNumber(int number) {
        return rooms.stream().filter(r -> r.getRoomNumber() == number).findFirst();
    }

    /**
     * Search available rooms by category and date range.
     * Checks existing bookings to exclude rooms with overlapping dates.
     */
    public List<Room> searchAvailableRooms(RoomCategory category, LocalDate checkIn, LocalDate checkOut) {
        Set<Integer> occupiedRoomNumbers = bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED
                      || b.getStatus() == BookingStatus.CHECKED_IN)
            .filter(b -> b.getRoom().getCategory() == category)
            .filter(b -> datesOverlap(b.getCheckIn(), b.getCheckOut(), checkIn, checkOut))
            .map(b -> b.getRoom().getRoomNumber())
            .collect(Collectors.toSet());

        return rooms.stream()
            .filter(r -> r.getCategory() == category)
            .filter(r -> !occupiedRoomNumbers.contains(r.getRoomNumber()))
            .collect(Collectors.toList());
    }

    private boolean datesOverlap(LocalDate existStart, LocalDate existEnd,
                                  LocalDate newStart, LocalDate newEnd) {
        return newStart.isBefore(existEnd) && newEnd.isAfter(existStart);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Booking Operations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a new booking and saves to disk.
     * @return the created Booking or null if room not available.
     */
    public Booking createBooking(Guest guest, int roomNumber,
                                  LocalDate checkIn, LocalDate checkOut) {
        Optional<Room> optRoom = getRoomByNumber(roomNumber);
        if (optRoom.isEmpty()) return null;

        Room room = optRoom.get();
        if (!room.isAvailable()) return null;
        if (!checkIn.isBefore(checkOut)) return null;

        String bookingId = "BK" + (++bookingCounter);
        Booking booking  = new Booking(bookingId, guest, room, checkIn, checkOut);

        room.setAvailable(false);
        bookings.add(booking);
        saveData();
        return booking;
    }

    /**
     * Process simulated payment for a booking.
     * @return the Payment object (check isSuccess()).
     */
    public Payment processPayment(String bookingId, PaymentMethod method) {
        Optional<Booking> optBooking = findBookingById(bookingId);
        if (optBooking.isEmpty()) return null;

        Booking booking = optBooking.get();
        Payment payment = new Payment(bookingId, booking.getTotalAmount(), method);
        booking.setPayment(payment);

        if (!payment.isSuccess()) {
            System.out.println("[Payment] Transaction failed. Booking remains confirmed.");
        }

        saveData();
        return payment;
    }

    /**
     * Cancel a booking — frees the room and updates status.
     * Cancellations are only allowed for CONFIRMED bookings.
     */
    public boolean cancelBooking(String bookingId) {
        Optional<Booking> optBooking = findBookingById(bookingId);
        if (optBooking.isEmpty()) return false;

        Booking booking = optBooking.get();
        if (booking.getStatus() != BookingStatus.CONFIRMED) return false;

        booking.setStatus(BookingStatus.CANCELLED);
        booking.getRoom().setAvailable(true);
        saveData();
        return true;
    }

    /** Mark guest as checked in. */
    public boolean checkIn(String bookingId) {
        Optional<Booking> opt = findBookingById(bookingId);
        if (opt.isEmpty()) return false;
        Booking b = opt.get();
        if (b.getStatus() != BookingStatus.CONFIRMED) return false;
        b.setStatus(BookingStatus.CHECKED_IN);
        saveData();
        return true;
    }

    /** Mark guest as checked out — frees the room. */
    public boolean checkOut(String bookingId) {
        Optional<Booking> opt = findBookingById(bookingId);
        if (opt.isEmpty()) return false;
        Booking b = opt.get();
        if (b.getStatus() != BookingStatus.CHECKED_IN) return false;
        b.setStatus(BookingStatus.CHECKED_OUT);
        b.getRoom().setAvailable(true);
        saveData();
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────────────────────────────────

    public Optional<Booking> findBookingById(String id) {
        return bookings.stream().filter(b -> b.getBookingId().equals(id)).findFirst();
    }

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public List<Booking> getActiveBookings() {
        return bookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED
                      || b.getStatus() == BookingStatus.CHECKED_IN)
            .collect(Collectors.toList());
    }

    public Map<RoomCategory, Long> getAvailabilityStats() {
        Map<RoomCategory, Long> stats = new LinkedHashMap<>();
        for (RoomCategory cat : RoomCategory.values()) {
            long count = rooms.stream()
                .filter(r -> r.getCategory() == cat && r.isAvailable())
                .count();
            stats.put(cat, count);
        }
        return stats;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Persistence
    // ─────────────────────────────────────────────────────────────────────────

    private void loadData() {
        // Rooms must be loaded first so bookings can reference them by room number
        List<Room>    savedRooms    = DataStore.loadRooms();
        List<Booking> savedBookings = DataStore.loadBookings(savedRooms);
        rooms.addAll(savedRooms);
        bookings.addAll(savedBookings);
        // Sync booking counter
        for (Booking b : bookings) {
            try {
                int num = Integer.parseInt(b.getBookingId().replace("BK", ""));
                if (num >= bookingCounter) bookingCounter = num + 1;
            } catch (NumberFormatException ignored) {}
        }
    }

    private void saveData() {
        DataStore.saveRooms(rooms);
        DataStore.saveBookings(bookings);
    }
}
