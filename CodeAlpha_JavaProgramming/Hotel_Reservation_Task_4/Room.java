package hotel;

import java.io.Serializable;

/**
 * Room — represents a single hotel room.
 * Implements Serializable for File I/O persistence.
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int          roomNumber;
    private final RoomCategory category;
    private final int          floor;
    private boolean            available;
    private final String       amenities;

    public Room(int roomNumber, RoomCategory category, int floor) {
        this.roomNumber = roomNumber;
        this.category   = category;
        this.floor      = floor;
        this.available  = true;
        this.amenities  = buildAmenities(category);
    }

    private String buildAmenities(RoomCategory cat) {
        switch (cat) {
            case STANDARD: return "AC, TV, Wi-Fi, Hot Water";
            case DELUXE:   return "AC, Smart TV, Wi-Fi, Mini-Bar, King Bed, City View, Room Service";
            case SUITE:    return "AC, Smart TV, Wi-Fi, Jacuzzi, Butler, Panoramic View, Lounge, King Bed";
            default:       return "Standard Amenities";
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public int          getRoomNumber() { return roomNumber; }
    public RoomCategory getCategory()   { return category; }
    public int          getFloor()      { return floor; }
    public boolean      isAvailable()   { return available; }
    public void         setAvailable(boolean available) { this.available = available; }
    public String       getAmenities()  { return amenities; }
    public double       getPricePerNight() { return category.getPricePerNight(); }

    public String getSummary() {
        return String.format("Room %-4d | %-8s | Floor %d | Rs %.0f/night | %-9s | %s",
            roomNumber, category.getDisplayName(), floor,
            getPricePerNight(),
            available ? "Available" : "Booked",
            amenities);
    }

    @Override
    public String toString() {
        return String.format("Room %d (%s)", roomNumber, category.getDisplayName());
    }
}
