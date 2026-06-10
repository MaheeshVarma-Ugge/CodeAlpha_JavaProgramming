package hotel;

/**
 * PaymentMethod — simulated payment options.
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    CASH("Cash at Counter");

    private final String display;
    PaymentMethod(String display) { this.display = display; }
    @Override public String toString() { return display; }
}
