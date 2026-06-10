package hotel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * HotelGUI — full Swing GUI for the Hotel Reservation System.
 *
 * Screens (Cards):
 *   DASHBOARD   — overview stats, quick actions
 *   SEARCH      — search available rooms by category & dates
 *   BOOK        — fill guest details, confirm booking, simulate payment
 *   BOOKINGS    — view all bookings in a table, cancel/check-in/check-out
 *   BOOKING_DETAIL — full detail view of a single booking
 *   ROOMS       — view all rooms and their availability
 */
public class HotelGUI extends JFrame {

    // ── Colour Palette ────────────────────────────────────────────────────────
    private static final Color C_BG        = new Color(245, 247, 250);
    private static final Color C_SIDEBAR   = new Color(30,  41,  59);
    private static final Color C_ACCENT    = new Color(59,  130, 246);
    private static final Color C_ACCENT2   = new Color(16,  185, 129);
    private static final Color C_WARNING   = new Color(245, 158, 11);
    private static final Color C_DANGER    = new Color(239, 68,  68);
    private static final Color C_CARD      = Color.WHITE;
    private static final Color C_TEXT      = new Color(30,  41,  59);
    private static final Color C_MUTED     = new Color(100, 116, 139);
    private static final Color C_BORDER    = new Color(226, 232, 240);
    private static final Color C_ROW_ALT   = new Color(248, 250, 252);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_HEAD   = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_MONO   = new Font("Courier New", Font.PLAIN, 12);

    // ── State ─────────────────────────────────────────────────────────────────
    private final HotelManager manager = new HotelManager();
    private Room   selectedRoom;         // room picked in Search → carried to Book
    private String viewingBookingId;     // booking to show in detail view

    // ── UI Components ─────────────────────────────────────────────────────────
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JLabel     statusBar;

    // Search screen fields
    private JComboBox<RoomCategory> cbCategory;
    private JTextField              tfCheckIn, tfCheckOut;
    private JTable                  roomTable;
    private DefaultTableModel       roomTableModel;

    // Book screen fields
    private JTextField tfGuestName, tfPhone, tfEmail, tfAdults, tfChildren;
    private JLabel     lblRoomInfo, lblTotalAmount;
    private JComboBox<PaymentMethod> cbPayment;
    private JLabel     lblBookCheckIn, lblBookCheckOut;

    // Bookings list
    private JTable            bookingTable;
    private DefaultTableModel bookingTableModel;

    // Rooms overview
    private JTable            allRoomsTable;
    private DefaultTableModel allRoomsModel;

    // Detail panel
    private JTextArea taDetail;

    // ─────────────────────────────────────────────────────────────────────────
    public HotelGUI() {
        initFrame();
        buildLayout();
        showDashboard();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Frame Setup
    // ─────────────────────────────────────────────────────────────────────────
    private void initFrame() {
        setTitle("Hotel Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Layout
    // ─────────────────────────────────────────────────────────────────────────
    private void buildLayout() {
        setLayout(new BorderLayout());
        add(buildSidebar(),    BorderLayout.WEST);
        add(buildContent(),    BorderLayout.CENTER);
        add(buildStatusBar(),  BorderLayout.SOUTH);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(C_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Logo area
        JPanel logo = new JPanel(new BorderLayout());
        logo.setBackground(new Color(15, 23, 42));
        logo.setPreferredSize(new Dimension(200, 70));
        logo.setMaximumSize(new Dimension(200, 70));
        logo.setBorder(new EmptyBorder(16, 18, 16, 18));
        JLabel logoText = new JLabel("<html><b>🏨 Grand Stay</b><br><small>Hotel Management</small></html>");
        logoText.setForeground(Color.WHITE);
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logo.add(logoText, BorderLayout.CENTER);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(12));

        // Nav buttons
        sidebar.add(navButton("🏠  Dashboard",   "DASHBOARD"));
        sidebar.add(navButton("🔍  Search Rooms", "SEARCH"));
        sidebar.add(navButton("📋  All Bookings", "BOOKINGS"));
        sidebar.add(navButton("🛏️   Room Overview", "ROOMS"));

        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("  v1.0  •  Java OOP + File I/O");
        version.setForeground(new Color(100, 116, 139));
        version.setFont(F_SMALL);
        version.setBorder(new EmptyBorder(0, 14, 14, 0));
        sidebar.add(version);

        return sidebar;
    }

    private JButton navButton(String label, String card) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(C_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(11, 18, 11, 18));
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(51, 65, 85));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(C_SIDEBAR);
                btn.setForeground(new Color(203, 213, 225));
            }
        });
        btn.addActionListener(e -> {
            if ("SEARCH".equals(card))     showSearch();
            else if ("BOOKINGS".equals(card)) showBookings();
            else if ("ROOMS".equals(card))    showRooms();
            else                              showDashboard();
        });
        return btn;
    }

    // ── Content Panel (CardLayout) ────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout    = new CardLayout();
        contentPanel  = new JPanel(cardLayout);
        contentPanel.setBackground(C_BG);
        contentPanel.add(buildDashboard(),     "DASHBOARD");
        contentPanel.add(buildSearchScreen(),  "SEARCH");
        contentPanel.add(buildBookScreen(),    "BOOK");
        contentPanel.add(buildBookingsScreen(),"BOOKINGS");
        contentPanel.add(buildDetailScreen(),  "DETAIL");
        contentPanel.add(buildRoomsScreen(),   "ROOMS");
        return contentPanel;
    }

    // ── Status Bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));
        bar.setPreferredSize(new Dimension(0, 28));
        statusBar = new JLabel("  Ready");
        statusBar.setFont(F_SMALL);
        statusBar.setForeground(C_MUTED);
        bar.add(statusBar, BorderLayout.WEST);
        JLabel right = new JLabel("Hotel Reservation System — Java OOP + File I/O   ");
        right.setFont(F_SMALL);
        right.setForeground(C_MUTED);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void setStatus(String msg) { statusBar.setText("  " + msg); }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD SCREEN
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JLabel title = new JLabel("Dashboard");
        title.setFont(F_TITLE);
        title.setForeground(C_TEXT);
        p.add(title, BorderLayout.NORTH);

        // Stats cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(new EmptyBorder(20, 0, 20, 0));

        // placeholders — refreshed in showDashboard()
        statsRow.add(statCard("Total Rooms", "30", C_ACCENT, "🛏️"));
        statsRow.add(statCard("Available",   "--", C_ACCENT2, "✅"));
        statsRow.add(statCard("Booked",      "--", C_WARNING,  "📋"));
        statsRow.add(statCard("Bookings",    "--", C_DANGER,   "🔖"));

        p.add(statsRow, BorderLayout.CENTER);
        // Tag the stats row so we can update it
        statsRow.setName("STATS_ROW");

        // Quick actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        actions.add(accentButton("🔍  Search & Book Rooms", C_ACCENT, e -> showSearch()));
        actions.add(accentButton("📋  View All Bookings",   C_ACCENT2, e -> showBookings()));
        actions.add(accentButton("🛏️   Room Overview",       C_WARNING, e -> showRooms()));
        p.add(actions, BorderLayout.SOUTH);

        return p;
    }

    private JPanel statCard(String label, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1, true),
            new EmptyBorder(18, 20, 18, 20)));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        card.add(ico, BorderLayout.NORTH);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 32));
        val.setForeground(color);
        card.add(val, BorderLayout.CENTER);

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_SMALL);
        lbl.setForeground(C_MUTED);
        card.add(lbl, BorderLayout.SOUTH);

        return card;
    }

    private void showDashboard() {
        // Rebuild dashboard with fresh numbers
        contentPanel.remove(contentPanel.getComponent(0));
        JPanel fresh = buildDashboard();

        Map<RoomCategory, Long> avail = manager.getAvailabilityStats();
        long totalAvail  = avail.values().stream().mapToLong(l -> l).sum();
        long totalBooked = 30 - totalAvail;
        long totalBk     = manager.getAllBookings().size();

        // Update stat cards
        JPanel statsRow = (JPanel) fresh.getComponent(1);
        updateStatCard((JPanel) statsRow.getComponent(1), String.valueOf(totalAvail));
        updateStatCard((JPanel) statsRow.getComponent(2), String.valueOf(totalBooked));
        updateStatCard((JPanel) statsRow.getComponent(3), String.valueOf(totalBk));

        contentPanel.add(fresh, "DASHBOARD", 0);
        cardLayout.show(contentPanel, "DASHBOARD");
        setStatus("Dashboard loaded");
    }

    private void updateStatCard(JPanel card, String value) {
        // value label is BorderLayout.CENTER
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel lbl) {
                String text = lbl.getText();
                if (text.matches("\\d+|--")) { lbl.setText(value); break; }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH SCREEN
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildSearchScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        p.add(pageTitle("🔍  Search Available Rooms"), BorderLayout.NORTH);

        // Filter card
        JPanel filterCard = card("Search Filters");
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 12));

        filterCard.add(label("Category:"));
        cbCategory = new JComboBox<>(RoomCategory.values());
        cbCategory.setFont(F_BODY);
        cbCategory.setPreferredSize(new Dimension(120, 32));
        filterCard.add(cbCategory);

        filterCard.add(label("Check-In (YYYY-MM-DD):"));
        tfCheckIn = textField(LocalDate.now().toString());
        tfCheckIn.setPreferredSize(new Dimension(130, 32));
        filterCard.add(tfCheckIn);

        filterCard.add(label("Check-Out (YYYY-MM-DD):"));
        tfCheckOut = textField(LocalDate.now().plusDays(2).toString());
        tfCheckOut.setPreferredSize(new Dimension(130, 32));
        filterCard.add(tfCheckOut);

        JButton searchBtn = accentButton("Search", C_ACCENT, e -> performSearch());
        filterCard.add(searchBtn);

        // Results table
        String[] cols = {"Room No.", "Category", "Floor", "Price/Night (Rs)", "Amenities", "Status"};
        roomTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = styledTable(roomTableModel);
        JScrollPane scroll = new JScrollPane(roomTable);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));

        // Book selected room button
        JButton bookBtn = accentButton("📌  Book Selected Room", C_ACCENT2, e -> goToBook());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        btnRow.add(bookBtn);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(filterCard, BorderLayout.NORTH);
        center.add(scroll,     BorderLayout.CENTER);
        center.add(btnRow,     BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private void performSearch() {
        try {
            RoomCategory cat     = (RoomCategory) cbCategory.getSelectedItem();
            LocalDate    in      = LocalDate.parse(tfCheckIn.getText().trim());
            LocalDate    out     = LocalDate.parse(tfCheckOut.getText().trim());

            if (!in.isBefore(out)) {
                showError("Check-out date must be after check-in date.");
                return;
            }

            List<Room> results = manager.searchAvailableRooms(cat, in, out);
            roomTableModel.setRowCount(0);
            for (Room r : results) {
                roomTableModel.addRow(new Object[]{
                    r.getRoomNumber(),
                    r.getCategory().getDisplayName(),
                    r.getFloor(),
                    String.format("Rs %.0f", r.getPricePerNight()),
                    r.getAmenities(),
                    "Available"
                });
            }
            setStatus(results.size() + " room(s) found for " + cat.getDisplayName() + " — " + in + " to " + out);
        } catch (Exception ex) {
            showError("Invalid date format. Use YYYY-MM-DD (e.g. 2025-12-25).");
        }
    }

    private void goToBook() {
        int row = roomTable.getSelectedRow();
        if (row < 0) { showError("Please select a room from the search results."); return; }
        int roomNo = (int) roomTableModel.getValueAt(row, 0);
        selectedRoom = manager.getRoomByNumber(roomNo).orElse(null);
        if (selectedRoom == null) { showError("Room not found."); return; }
        populateBookScreen();
        cardLayout.show(contentPanel, "BOOK");
        setStatus("Booking room " + roomNo);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOOK SCREEN
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildBookScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        p.add(pageTitle("📌  Make a Reservation"), BorderLayout.NORTH);

        // Two-column layout: guest form + booking summary
        JPanel cols = new JPanel(new GridLayout(1, 2, 20, 0));
        cols.setOpaque(false);

        // ── Guest Info Card ───────────────────────────────────────────────────
        JPanel guestCard = card("Guest Information");
        guestCard.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 8, 6, 8);

        tfGuestName = textField(""); tfPhone = textField(""); tfEmail = textField("");
        tfAdults = textField("1"); tfChildren = textField("0");

        addFormRow(guestCard, gc, 0, "Full Name:",     tfGuestName);
        addFormRow(guestCard, gc, 1, "Phone Number:",  tfPhone);
        addFormRow(guestCard, gc, 2, "Email Address:", tfEmail);
        addFormRow(guestCard, gc, 3, "Adults:",        tfAdults);
        addFormRow(guestCard, gc, 4, "Children:",      tfChildren);

        // ── Booking Summary Card ──────────────────────────────────────────────
        JPanel summaryCard = card("Booking Summary");
        summaryCard.setLayout(new GridBagLayout());
        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.insets = new Insets(8, 10, 8, 10);

        lblRoomInfo     = infoLabel("—");
        lblBookCheckIn  = infoLabel("—");
        lblBookCheckOut = infoLabel("—");
        lblTotalAmount  = infoLabel("—");

        addSummaryRow(summaryCard, sc, 0, "Room:",        lblRoomInfo);
        addSummaryRow(summaryCard, sc, 1, "Check-In:",    lblBookCheckIn);
        addSummaryRow(summaryCard, sc, 2, "Check-Out:",   lblBookCheckOut);
        addSummaryRow(summaryCard, sc, 3, "Total Amount:",lblTotalAmount);

        sc.gridy = 4;
        sc.gridx = 0; sc.gridwidth = 1;
        summaryCard.add(label("Payment Method:"), sc);
        sc.gridx = 1;
        cbPayment = new JComboBox<>(PaymentMethod.values());
        cbPayment.setFont(F_BODY);
        summaryCard.add(cbPayment, sc);

        cols.add(guestCard);
        cols.add(summaryCard);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setOpaque(false);
        btns.add(accentButton("✅  Confirm & Pay", C_ACCENT2, e -> confirmBooking()));
        btns.add(accentButton("← Back to Search",  C_MUTED,   e -> cardLayout.show(contentPanel, "SEARCH")));

        p.add(cols,  BorderLayout.CENTER);
        p.add(btns,  BorderLayout.SOUTH);
        return p;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gc, int row, String lbl, JTextField field) {
        gc.gridy = row; gc.gridx = 0; gc.weightx = 0.3;
        panel.add(label(lbl), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        panel.add(field, gc);
    }

    private void addSummaryRow(JPanel panel, GridBagConstraints sc, int row, String lbl, JLabel val) {
        sc.gridy = row; sc.gridx = 0; sc.weightx = 0.4;
        panel.add(label(lbl), sc);
        sc.gridx = 1; sc.weightx = 0.6;
        panel.add(val, sc);
    }

    private void populateBookScreen() {
        if (selectedRoom == null) return;
        try {
            LocalDate in  = LocalDate.parse(tfCheckIn.getText().trim());
            LocalDate out = LocalDate.parse(tfCheckOut.getText().trim());
            long nights   = java.time.temporal.ChronoUnit.DAYS.between(in, out);
            double total  = nights * selectedRoom.getPricePerNight();

            lblRoomInfo.setText("Room " + selectedRoom.getRoomNumber()
                + " — " + selectedRoom.getCategory().getDisplayName()
                + " (Floor " + selectedRoom.getFloor() + ")");
            lblBookCheckIn.setText(in.toString());
            lblBookCheckOut.setText(out.toString());
            lblTotalAmount.setText(String.format("Rs %.2f  (%d night%s × Rs %.0f)",
                total, nights, nights == 1 ? "" : "s", selectedRoom.getPricePerNight()));
        } catch (Exception ignored) {}
    }

    private void confirmBooking() {
        String name     = tfGuestName.getText().trim();
        String phone    = tfPhone.getText().trim();
        String email    = tfEmail.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showError("Please fill in Name, Phone, and Email.");
            return;
        }

        int adults, children;
        try {
            adults   = Integer.parseInt(tfAdults.getText().trim());
            children = Integer.parseInt(tfChildren.getText().trim());
        } catch (NumberFormatException e) {
            showError("Adults and Children must be numbers.");
            return;
        }

        LocalDate in, out;
        try {
            in  = LocalDate.parse(tfCheckIn.getText().trim());
            out = LocalDate.parse(tfCheckOut.getText().trim());
        } catch (Exception e) {
            showError("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        String guestId = "G" + System.currentTimeMillis() % 100000;
        Guest  guest   = new Guest(guestId, name, phone, email, adults, children);

        Booking booking = manager.createBooking(guest, selectedRoom.getRoomNumber(), in, out);
        if (booking == null) {
            showError("Failed to create booking. The room may no longer be available.");
            return;
        }

        // Process payment
        PaymentMethod method = (PaymentMethod) cbPayment.getSelectedItem();
        Payment payment = manager.processPayment(booking.getBookingId(), method);

        // Show result
        String msg;
        if (payment != null && payment.isSuccess()) {
            msg = "Booking Confirmed!\n\n"
                + "Booking ID   : " + booking.getBookingId() + "\n"
                + "Room         : " + selectedRoom.getRoomNumber() + " (" + selectedRoom.getCategory().getDisplayName() + ")\n"
                + "Check-In     : " + in + "\n"
                + "Check-Out    : " + out + "\n"
                + "Total Paid   : Rs " + String.format("%.2f", booking.getTotalAmount()) + "\n"
                + "Transaction  : " + payment.getTransactionId() + "\n"
                + "Method       : " + method;
            JOptionPane.showMessageDialog(this, msg, "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
            setStatus("Booking " + booking.getBookingId() + " confirmed.");
        } else {
            msg = "Booking created but payment failed.\n\n"
                + "Booking ID : " + booking.getBookingId() + "\n"
                + "Please retry payment from the Bookings screen.";
            JOptionPane.showMessageDialog(this, msg, "Payment Failed", JOptionPane.WARNING_MESSAGE);
            setStatus("Payment failed for " + booking.getBookingId() + " — please retry.");
        }

        // Clear form and go to bookings
        tfGuestName.setText(""); tfPhone.setText(""); tfEmail.setText("");
        tfAdults.setText("1"); tfChildren.setText("0");
        showBookings();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOOKINGS SCREEN
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildBookingsScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(pageTitle("📋  All Reservations"), BorderLayout.NORTH);

        String[] cols = {"Booking ID","Guest","Room","Category","Check-In","Check-Out","Amount (Rs)","Status","Paid"};
        bookingTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = styledTable(bookingTableModel);
        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.setOpaque(false);
        btns.add(accentButton("👁  View Details",  C_ACCENT,   e -> viewBookingDetail()));
        btns.add(accentButton("❌  Cancel Booking", C_DANGER,   e -> cancelBooking()));
        btns.add(accentButton("✅  Check In",       C_ACCENT2,  e -> checkIn()));
        btns.add(accentButton("🚪  Check Out",      C_WARNING,  e -> checkOut()));
        btns.add(accentButton("🔄  Refresh",        C_MUTED,    e -> refreshBookingTable()));

        p.add(scroll, BorderLayout.CENTER);
        p.add(btns,   BorderLayout.SOUTH);
        return p;
    }

    private void refreshBookingTable() {
        bookingTableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        for (Booking b : manager.getAllBookings()) {
            bookingTableModel.addRow(new Object[]{
                b.getBookingId(),
                b.getGuest().getName(),
                b.getRoom().getRoomNumber(),
                b.getRoom().getCategory().getDisplayName(),
                b.getCheckIn().format(fmt),
                b.getCheckOut().format(fmt),
                String.format("%.0f", b.getTotalAmount()),
                b.getStatus().toString(),
                b.isPaid() ? "Yes" : "No"
            });
        }
        setStatus(manager.getAllBookings().size() + " booking(s) loaded.");
    }

    private void viewBookingDetail() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) { showError("Please select a booking."); return; }
        viewingBookingId = (String) bookingTableModel.getValueAt(row, 0);
        populateDetailScreen();
        cardLayout.show(contentPanel, "DETAIL");
    }

    private void cancelBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) { showError("Please select a booking to cancel."); return; }
        String id = (String) bookingTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel booking " + id + "? This will free the room.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = manager.cancelBooking(id);
            if (ok) {
                showInfo("Booking " + id + " has been cancelled. Room is now available.");
                setStatus("Booking " + id + " cancelled.");
            } else {
                showError("Cannot cancel. Booking may already be cancelled or checked out.");
            }
            refreshBookingTable();
        }
    }

    private void checkIn() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) { showError("Please select a booking."); return; }
        String id = (String) bookingTableModel.getValueAt(row, 0);
        boolean ok = manager.checkIn(id);
        if (ok) { showInfo("Guest checked in for booking " + id + "."); setStatus("Checked in: " + id); }
        else    { showError("Cannot check in. Booking must be CONFIRMED."); }
        refreshBookingTable();
    }

    private void checkOut() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) { showError("Please select a booking."); return; }
        String id = (String) bookingTableModel.getValueAt(row, 0);
        boolean ok = manager.checkOut(id);
        if (ok) { showInfo("Guest checked out for booking " + id + "."); setStatus("Checked out: " + id); }
        else    { showError("Cannot check out. Booking must be CHECKED IN first."); }
        refreshBookingTable();
    }

    private void showBookings() {
        refreshBookingTable();
        cardLayout.show(contentPanel, "BOOKINGS");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BOOKING DETAIL SCREEN
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildDetailScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(pageTitle("📄  Booking Details"), BorderLayout.NORTH);

        taDetail = new JTextArea();
        taDetail.setFont(F_MONO);
        taDetail.setEditable(false);
        taDetail.setBackground(C_CARD);
        taDetail.setForeground(C_TEXT);
        taDetail.setBorder(new EmptyBorder(12, 16, 12, 16));
        JScrollPane scroll = new JScrollPane(taDetail);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setOpaque(false);
        btns.add(accentButton("← Back to Bookings", C_MUTED, e -> showBookings()));

        p.add(scroll, BorderLayout.CENTER);
        p.add(btns,   BorderLayout.SOUTH);
        return p;
    }

    private void populateDetailScreen() {
        if (viewingBookingId == null) return;
        manager.findBookingById(viewingBookingId).ifPresent(b -> {
            taDetail.setText(b.getDetailView());
            taDetail.setCaretPosition(0);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ROOMS OVERVIEW SCREEN
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildRoomsScreen() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(pageTitle("🛏️  Room Overview"), BorderLayout.NORTH);

        String[] cols = {"Room No.", "Category", "Floor", "Price/Night (Rs)", "Status", "Amenities"};
        allRoomsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        allRoomsTable = styledTable(allRoomsModel);

        // Custom row colour: available = light green, booked = light red
        allRoomsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String status = (String) allRoomsModel.getValueAt(row, 4);
                    c.setBackground("Available".equals(status)
                        ? new Color(236, 253, 245)
                        : new Color(254, 242, 242));
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(allRoomsTable);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.setOpaque(false);
        btns.add(accentButton("🔄  Refresh", C_MUTED, e -> refreshRoomsTable()));

        // Category legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legend.setOpaque(false);
        for (RoomCategory cat : RoomCategory.values()) {
            legend.add(label(cat.getDisplayName() + " — Rs " + (int) cat.getPricePerNight() + "/night  |  " + cat.getDescription()));
        }

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(btns,   BorderLayout.NORTH);
        bottom.add(legend, BorderLayout.SOUTH);

        p.add(scroll, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void refreshRoomsTable() {
        allRoomsModel.setRowCount(0);
        for (Room r : manager.getAllRooms()) {
            allRoomsModel.addRow(new Object[]{
                r.getRoomNumber(),
                r.getCategory().getDisplayName(),
                r.getFloor(),
                String.format("Rs %.0f", r.getPricePerNight()),
                r.isAvailable() ? "Available" : "Booked",
                r.getAmenities()
            });
        }
        setStatus("Room overview refreshed — " + manager.getAvailableRooms().size() + " available.");
    }

    private void showRooms() {
        refreshRoomsTable();
        cardLayout.show(contentPanel, "ROOMS");
    }

    private void showSearch() {
        cardLayout.show(contentPanel, "SEARCH");
        setStatus("Search for available rooms by category and dates.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel card(String title) {
        JPanel card = new JPanel();
        card.setBackground(C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1, true),
            new EmptyBorder(14, 16, 14, 16)));
        card.setLayout(new BorderLayout());
        JLabel t = new JLabel(title);
        t.setFont(F_HEAD);
        t.setForeground(C_TEXT);
        t.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(t, BorderLayout.NORTH);
        return card;
    }

    private JLabel pageTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_TITLE);
        lbl.setForeground(C_TEXT);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_BODY);
        l.setForeground(C_TEXT);
        return l;
    }

    private JLabel infoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(C_ACCENT);
        return l;
    }

    private JTextField textField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(F_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private JButton accentButton(String text, Color bg, ActionListener al) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addActionListener(al);
        btn.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            public void mouseEntered(MouseEvent e) { btn.setBackground(orig.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(orig); }
        });
        return btn;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(F_BODY);
        table.setRowHeight(28);
        table.setGridColor(C_BORDER);
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(C_TEXT);
        table.setIntercellSpacing(new Dimension(8, 4));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(C_SIDEBAR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 32));
        // Alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? C_CARD : C_ROW_ALT);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
        return table;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Launch
    // ─────────────────────────────────────────────────────────────────────────
    public void launch() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        setVisible(true);
    }
}
