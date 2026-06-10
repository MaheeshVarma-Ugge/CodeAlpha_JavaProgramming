package hotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Booking — a complete reservation record linking Guest, Room, and Payment.
 * Implements Serializable for File I/O persistence.
 **/
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final String      bookingId;
    private final Guest       guest;
    private final Room        room;
    private final LocalDate   checkIn;
    private final LocalDate   checkOut;
    private final long        nights;
    private final double      totalAmount;
    private       BookingStatus status;
    private       Payment     payment;

    public Booking(String bookingId, Guest guest, Room room,
                   LocalDate checkIn, LocalDate checkOut) {
        this.bookingId   = bookingId;
        this.guest       = guest;
        this.room        = room;
        this.checkIn     = checkIn;
        this.checkOut    = checkOut;
        this.nights      = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalAmount = nights * room.getPricePerNight();
        this.status      = BookingStatus.CONFIRMED;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String        getBookingId()   { return bookingId; }
    public Guest         getGuest()       { return guest; }
    public Room          getRoom()        { return room; }
    public LocalDate     getCheckIn()     { return checkIn; }
    public LocalDate     getCheckOut()    { return checkOut; }
    public long          getNights()      { return nights; }
    public double        getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus()      { return status; }
    public Payment       getPayment()     { return payment; }

    public void setStatus(BookingStatus status) { this.status = status; }
    public void setPayment(Payment payment)     { this.payment = payment; }

    public boolean isPaid() { return payment != null && payment.isSuccess(); }

    /**
     * Full booking detail view — shown in "View Booking" screen.
     */
    public String getDetailView() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(58));
        sb.append("\n         BOOKING CONFIRMATION DETAILS");
        sb.append("\n").append("=".repeat(58));
        sb.append(String.format("\n  Booking ID     : %s", bookingId));
        sb.append(String.format("\n  Status         : %s", status));
        sb.append("\n" + "-".repeat(58));
        sb.append("\n  GUEST INFORMATION");
        sb.append(String.format("\n  Name           : %s", guest.getName()));
        sb.append(String.format("\n  Phone          : %s", guest.getPhone()));
        sb.append(String.format("\n  Email          : %s", guest.getEmail()));
        sb.append(String.format("\n  Guests         : %d Adult(s), %d Child(ren)",
            guest.getAdults(), guest.getChildren()));
        sb.append("\n" + "-".repeat(58));
        sb.append("\n  ROOM INFORMATION");
        sb.append(String.format("\n  Room Number    : %d", room.getRoomNumber()));
        sb.append(String.format("\n  Category       : %s", room.getCategory().getDisplayName()));
        sb.append(String.format("\n  Floor          : %d", room.getFloor()));
        sb.append(String.format("\n  Amenities      : %s", room.getAmenities()));
        sb.append(String.format("\n  Price/Night    : Rs %.2f", room.getPricePerNight()));
        sb.append("\n" + "-".repeat(58));
        sb.append("\n  STAY DETAILS");
        sb.append(String.format("\n  Check-In       : %s", checkIn.format(FMT)));
        sb.append(String.format("\n  Check-Out      : %s", checkOut.format(FMT)));
        sb.append(String.format("\n  Total Nights   : %d", nights));
        sb.append(String.format("\n  Total Amount   : Rs %.2f", totalAmount));
        if (payment != null) {
            sb.append("\n" + "-".repeat(58));
            sb.append("\n  PAYMENT DETAILS");
            sb.append(payment.getReceipt());
        }
        sb.append("\n").append("=".repeat(58));
        return sb.toString();
    }

    /** Short one-line summary for list views */
    public String getShortSummary() {
        return String.format("%-12s | Room %-4d | %-8s | %-12s | %-12s | Rs %-8.0f | %s",
            bookingId,
            room.getRoomNumber(),
            room.getCategory().getDisplayName(),
            checkIn.format(FMT),
            checkOut.format(FMT),
            totalAmount,
            status);
    }

    @Override public String toString() { return bookingId + " — " + guest.getName() + " | " + room; }
}
