# Hotel Reservation System

A Java OOP console and GUI application for managing hotel room bookings, guest check-in/out, and payment simulation. Data is persisted to human-readable plain-text files.

---

## Project Structure

```
Hotel_Reservation_Task_4/
├── Main.java            — Entry point; launches GUI or console mode
├── HotelGUI.java        — Swing GUI with sidebar navigation (6 screens)
├── ConsoleApp.java      — Terminal-based UI with full feature parity
├── HotelManager.java    — Core business logic (search, book, cancel, check-in/out)
├── DataStore.java       — File I/O persistence (bookings.dat + rooms.dat)
├── Booking.java         — Reservation record linking Guest, Room, and Payment
├── Room.java            — Room model with amenities per category
├── Guest.java           — Guest personal details model
├── Payment.java         — Payment simulation (95% success; Cash always works)
├── RoomCategory.java    — STANDARD / DELUXE / SUITE enum with prices
├── BookingStatus.java   — CONFIRMED / CANCELLED / CHECKED_IN / CHECKED_OUT
└── PaymentMethod.java   — Credit Card / Debit Card / UPI / Net Banking / Cash
```

---

## Requirements

- Java 17 or higher (uses switch expressions and records)
- No external libraries — pure Java SE

---

## Compile & Run

```bash
# 1. Compile all source files into the out/ directory
javac -d out *.java

# 2a. Launch the Swing GUI (default)
java -cp out hotel.Main

# 2b. Launch in terminal / console mode
java -cp out hotel.Main --console
```

> **Note:** Delete any existing `hotel_data/` folder before first run if upgrading from an older binary-format version.

---

## Features

### Console Mode — Main Menu

| Option | Feature |
|--------|---------|
| 1 | Search Available Rooms by category and date range |
| 2 | Make a Booking — guest details, room selection, payment |
| 3 | View All Bookings — tabular list of every reservation |
| 4 | View Booking Details — full detail view by Booking ID |
| 5 | Cancel a Booking — frees the room back to available |
| 6 | Check In — marks a CONFIRMED booking as CHECKED_IN |
| 7 | Check Out — marks CHECKED_IN as CHECKED_OUT, frees room |
| 8 | Room Overview — availability count and full room list |
| 0 | Exit |

### GUI Mode — Sidebar Screens

- **Dashboard** — live stats (total rooms, available, bookings, checked-in)
- **Search** — filter rooms by category and date, click to select
- **Book** — fill guest form, auto-calculated total, choose payment method
- **Bookings** — full table with Cancel / Check-In / Check-Out buttons
- **Booking Detail** — formatted view of a single booking
- **Rooms** — full room inventory with availability status

---

## Room Categories & Pricing

| Category | Rooms | Price/Night | Amenities |
|----------|-------|-------------|-----------|
| Standard | 101–110 (Floor 1) | ₹2,500 | AC, TV, Wi-Fi, Hot Water |
| Deluxe   | 201–210 (Floor 2) | ₹5,000 | AC, Smart TV, Wi-Fi, Mini-Bar, King Bed, City View, Room Service |
| Suite    | 301–310 (Floor 3) | ₹10,000 | AC, Smart TV, Wi-Fi, Jacuzzi, Butler, Panoramic View, Lounge, King Bed |

The system is pre-seeded with 30 rooms (10 per category) on first run.

---

## Data Persistence

Data is saved automatically to `hotel_data/` after every operation.

Both files are **plain text** and can be opened in any editor or VS Code.

### `hotel_data/rooms.dat`

```
# rooms.dat — Hotel Room Data (plain text, pipe-separated)
# roomNumber|category|floor|available
101|STANDARD|1|true
201|DELUXE|2|false
301|SUITE|3|true
```

### `hotel_data/bookings.dat`

```
# bookings.dat — Hotel Booking Data (plain text, pipe-separated)
# bookingId|status|checkIn|checkOut|roomNumber|guestId|guestName|phone|email|adults|children|totalAmount|paymentTxnId|paymentMethod|paymentSuccess|paymentTime
BK1001|CONFIRMED|2026-07-03|2026-07-05|201|G12345|John Smith|9876543210|john@email.com|2|0|10000.0|TXN-A1B2C3D4|CREDIT_CARD|true|2026-06-10T01:31:00
```

---

## Payment Simulation

| Method | Success Rate |
|--------|-------------|
| Cash at Counter | 100% |
| Credit Card | 95% |
| Debit Card | 95% |
| UPI | 95% |
| Net Banking | 95% |

Each payment generates a unique transaction ID (`TXN-XXXXXXXX`). Failed payments leave the booking in CONFIRMED status so the guest can retry.

---

## Date Input (Console Mode)

The console accepts flexible date formats — all of the following work:

```
2026-07-03      # standard ISO format
2026-7-3        # no zero padding needed
2026 - 7 - 3   # spaces around dashes are fine
2026/07/03      # slashes work too
```

---

## Booking Lifecycle

```
CONFIRMED  →  CHECKED_IN  →  CHECKED_OUT
     ↓
CANCELLED
```

- Only **CONFIRMED** bookings can be cancelled or checked in.
- Only **CHECKED_IN** bookings can be checked out.
- Checking out or cancelling a booking automatically frees the room.

---

## Class Diagram (Overview)

```
Main
 ├── HotelGUI          (Swing GUI, 6 screens)
 └── ConsoleApp        (Terminal UI)
      └── HotelManager (business logic)
           ├── Room        ──→ RoomCategory (enum)
           ├── Booking     ──→ BookingStatus (enum)
           │    ├── Guest
           │    └── Payment ──→ PaymentMethod (enum)
           └── DataStore   (plain-text file I/O)
```
