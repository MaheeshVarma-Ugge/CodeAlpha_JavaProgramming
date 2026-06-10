package hotel;

/**
 * BookingStatus — lifecycle states of a reservation.
 */
public enum BookingStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    CHECKED_IN("Checked In"),
    CHECKED_OUT("Checked Out");

    private final String display;
    BookingStatus(String display) { this.display = display; }
    @Override public String toString() { return display; }
}
