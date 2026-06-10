package hotel;

/**
 * RoomCategory — defines the three room tiers with base price per night.
 */
public enum RoomCategory {

    STANDARD("Standard", 2500.00,
        "Comfortable room with essential amenities: AC, TV, Wi-Fi, and attached bathroom."),

    DELUXE("Deluxe", 5000.00,
        "Spacious room with premium furnishings, mini-bar, king bed, city view, and 24-hr room service."),

    SUITE("Suite", 10000.00,
        "Luxury suite with separate living area, jacuzzi, butler service, and panoramic view.");

    private final String displayName;
    private final double pricePerNight;
    private final String description;

    RoomCategory(String displayName, double pricePerNight, String description) {
        this.displayName   = displayName;
        this.pricePerNight = pricePerNight;
        this.description   = description;
    }

    public String getDisplayName()   { return displayName; }
    public double getPricePerNight() { return pricePerNight; }
    public String getDescription()   { return description; }

    @Override
    public String toString() { return displayName; }
}
