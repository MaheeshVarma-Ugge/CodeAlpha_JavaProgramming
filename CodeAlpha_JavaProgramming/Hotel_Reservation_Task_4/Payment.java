package hotel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Payment — simulates a payment transaction (no real gateway).
 * Task requirement: "Implement payment simulation".
 */
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String        transactionId;
    private final double        amount;
    private final PaymentMethod method;
    private final boolean       success;
    private final LocalDateTime paidAt;
    private final String        bookingId;

    /** Normal constructor — simulates a new payment. */
    public Payment(String bookingId, double amount, PaymentMethod method) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.bookingId     = bookingId;
        this.amount        = amount;
        this.method        = method;
        this.success       = simulatePayment(method);
        this.paidAt        = LocalDateTime.now();
    }

    /** Reconstruction constructor — used by DataStore when loading saved data. */
    public Payment(String bookingId, double amount, PaymentMethod method,
                   String transactionId, boolean success, LocalDateTime paidAt) {
        this.bookingId     = bookingId;
        this.amount        = amount;
        this.method        = method;
        this.transactionId = transactionId;
        this.success       = success;
        this.paidAt        = paidAt;
    }

    /**
     * Simulates a payment gateway.
     * Cash is always accepted; cards/UPI succeed 95% of the time.
     */
    private boolean simulatePayment(PaymentMethod method) {
        if (method == PaymentMethod.CASH) return true;
        return Math.random() > 0.05; // 95% success rate simulation
    }

    public String        getTransactionId() { return transactionId; }
    public double        getAmount()        { return amount; }
    public PaymentMethod getMethod()        { return method; }
    public boolean       isSuccess()        { return success; }
    public LocalDateTime getPaidAt()        { return paidAt; }
    public String        getBookingId()     { return bookingId; }

    public String getReceipt() {
        String status = success ? "SUCCESS" : "FAILED";
        return String.format(
            "\n  Transaction ID : %s" +
            "\n  Booking ID     : %s" +
            "\n  Amount         : Rs %.2f" +
            "\n  Method         : %s" +
            "\n  Status         : %s" +
            "\n  Date & Time    : %s",
            transactionId, bookingId,
            amount, method,
            status,
            paidAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a"))
        );
    }

    @Override public String toString() {
        return transactionId + " | Rs " + String.format("%.2f", amount) + " | " + method + " | " + (success ? "SUCCESS" : "FAILED");
    }
}
