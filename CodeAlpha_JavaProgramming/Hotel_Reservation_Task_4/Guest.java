package hotel;

import java.io.Serializable;

/**
 * Guest — stores guest personal details.
 */
public class Guest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String guestId;
    private       String name;
    private       String phone;
    private       String email;
    private       int    adults;
    private       int    children;

    public Guest(String guestId, String name, String phone, String email, int adults, int children) {
        this.guestId  = guestId;
        this.name     = name;
        this.phone    = phone;
        this.email    = email;
        this.adults   = adults;
        this.children = children;
    }

    public String getGuestId()  { return guestId; }
    public String getName()     { return name; }
    public String getPhone()    { return phone; }
    public String getEmail()    { return email; }
    public int    getAdults()   { return adults; }
    public int    getChildren() { return children; }

    public void setName(String name)       { this.name = name; }
    public void setPhone(String phone)     { this.phone = phone; }
    public void setEmail(String email)     { this.email = email; }

    public String getSummary() {
        return String.format("Guest ID: %s | Name: %s | Phone: %s | Email: %s | Guests: %d adult(s), %d child(ren)",
            guestId, name, phone, email, adults, children);
    }

    @Override public String toString() { return name + " (" + guestId + ")"; }
}
