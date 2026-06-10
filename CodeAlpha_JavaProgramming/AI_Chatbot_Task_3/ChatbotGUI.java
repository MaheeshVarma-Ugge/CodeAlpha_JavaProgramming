package chatbot;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ChatbotGUI — Swing-based dark-themed chat window.
 *
 * FIX:
 *   - Removed "ALPHA" from window title, header name, typing indicator
 *   - Removed "CodeAlpha" from subtitle
 *   - Typing indicator changed to neutral "Thinking..."
 *   - Window title is now "Java AI Chatbot"
 *   - Header shows "AI Assistant" and "Java NLP Engine"
 *   - Avatar letter changed to "J" (for Java)
 */
public class ChatbotGUI extends JFrame {

    private static final Color COLOR_BG         = new Color(18, 18, 30);
    private static final Color COLOR_PANEL       = new Color(28, 28, 45);
    private static final Color COLOR_BOT_BUBBLE  = new Color(45, 55, 100);
    private static final Color COLOR_USER_BUBBLE = new Color(75, 0, 130);
    private static final Color COLOR_INPUT_BG    = new Color(35, 35, 55);
    private static final Color COLOR_SEND_BTN    = new Color(100, 60, 200);
    private static final Color COLOR_SEND_HOVER  = new Color(120, 80, 220);
    private static final Color COLOR_TEXT        = new Color(230, 230, 240);
    private static final Color COLOR_TIMESTAMP   = new Color(140, 140, 170);
    private static final Color COLOR_HEADER_BG   = new Color(20, 20, 40);
    private static final Color COLOR_ACCENT      = new Color(100, 60, 200);

    private static final Font FONT_CHAT   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_INPUT  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    private final ChatEngine engine;
    private JPanel      chatPanel;
    private JScrollPane scrollPane;
    private JTextArea   inputArea;
    private JButton     sendButton;
    private JLabel      statusLabel;
    // Keeps a reference to the typing-row panel so we can remove it reliably
    private JPanel      typingRow;

    public ChatbotGUI() {
        this.engine = new ChatEngine();
        initUI();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void initUI() {
        setTitle("Java AI Chatbot");                      // ← no "ALPHA" / "CodeAlpha"
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 640);
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildChatArea(), BorderLayout.CENTER);
        add(buildInputBar(), BorderLayout.SOUTH);

        appendBotMessage(engine.getWelcomeMessage());
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ACCENT));
        header.setPreferredSize(new Dimension(0, 58));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Avatar circle — letter "J"
        JLabel avatar = new JLabel("J") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        JLabel nameLabel = new JLabel("AI Assistant");          // ← no "ALPHA"
        nameLabel.setFont(FONT_HEADER);
        nameLabel.setForeground(COLOR_TEXT);
        JLabel subLabel = new JLabel("Java NLP Engine  •  Rule-Based AI");  // ← no "CodeAlpha"
        subLabel.setFont(FONT_SMALL);
        subLabel.setForeground(COLOR_TIMESTAMP);
        titleBlock.add(nameLabel);
        titleBlock.add(subLabel);

        left.add(avatar);
        left.add(titleBlock);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        statusLabel = new JLabel("● Online");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(new Color(80, 200, 120));
        JButton clearBtn = styledButton("Clear", COLOR_INPUT_BG, COLOR_TEXT);
        clearBtn.setFont(FONT_SMALL);
        clearBtn.addActionListener(e -> clearChat());
        right.add(statusLabel);
        right.add(clearBtn);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Chat area ─────────────────────────────────────────────────────────────
    private JScrollPane buildChatArea() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(COLOR_BG);
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBackground(COLOR_BG);
        scrollPane.getViewport().setBackground(COLOR_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    // ── Input bar ─────────────────────────────────────────────────────────────
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(COLOR_PANEL);
        bar.setBorder(new EmptyBorder(10, 12, 10, 12));

        inputArea = new JTextArea(2, 1);
        inputArea.setFont(FONT_INPUT);
        inputArea.setBackground(COLOR_INPUT_BG);
        inputArea.setForeground(COLOR_TIMESTAMP);
        inputArea.setCaretColor(Color.WHITE);
        inputArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setText("Type a message...");

        inputArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (inputArea.getText().equals("Type a message...")) {
                    inputArea.setText("");
                    inputArea.setForeground(COLOR_TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                if (inputArea.getText().isBlank()) {
                    inputArea.setText("Type a message...");
                    inputArea.setForeground(COLOR_TIMESTAMP);
                }
            }
        });

        // Enter sends, Shift+Enter is new line
        inputArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1));
        inputScroll.setBackground(COLOR_INPUT_BG);
        inputScroll.getViewport().setBackground(COLOR_INPUT_BG);

        sendButton = styledButton("Send  ▶", COLOR_SEND_BTN, Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setPreferredSize(new Dimension(95, 0));
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { sendButton.setBackground(COLOR_SEND_HOVER); }
            public void mouseExited(MouseEvent e)  { sendButton.setBackground(COLOR_SEND_BTN);   }
        });
        sendButton.addActionListener(e -> sendMessage());

        bar.add(inputScroll, BorderLayout.CENTER);
        bar.add(sendButton,  BorderLayout.EAST);
        return bar;
    }

    // ── Send logic ────────────────────────────────────────────────────────────
    private void sendMessage() {
        String text = inputArea.getText().trim();
        if (text.isEmpty() || text.equals("Type a message...")) return;

        inputArea.setText("");
        inputArea.setForeground(COLOR_TEXT);
        appendUserMessage(text);
        showTypingIndicator();
        sendButton.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() throws Exception {
                Thread.sleep(300);
                return engine.processInput(text);
            }
            protected void done() {
                try {
                    hideTypingIndicator();
                    appendBotMessage(get());
                } catch (Exception ex) {
                    hideTypingIndicator();
                    appendBotMessage("Oops! Something went wrong. Please try again.");
                } finally {
                    sendButton.setEnabled(true);
                    inputArea.requestFocus();
                }
            }
        };
        worker.execute();
    }

    // ── Bubbles ───────────────────────────────────────────────────────────────
    private void appendUserMessage(String text) { appendBubble(text, true); }
    private void appendBotMessage(String text)  { appendBubble(text, false); }

    private void appendBubble(String text, boolean isUser) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        JPanel row = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel bubble = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isUser ? COLOR_USER_BUBBLE : COLOR_BOT_BUBBLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        bubble.setOpaque(false);
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(9, 13, 9, 13));

        JTextArea msgText = new JTextArea(text);
        msgText.setFont(FONT_CHAT);
        msgText.setForeground(COLOR_TEXT);
        msgText.setOpaque(false);
        msgText.setEditable(false);
        msgText.setLineWrap(true);
        msgText.setWrapStyleWord(true);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(FONT_SMALL);
        timeLabel.setForeground(COLOR_TIMESTAMP);
        timeLabel.setHorizontalAlignment(isUser ? SwingConstants.RIGHT : SwingConstants.LEFT);

        bubble.add(msgText,   BorderLayout.CENTER);
        bubble.add(timeLabel, BorderLayout.SOUTH);
        bubble.setMaximumSize(new Dimension(460, Integer.MAX_VALUE));

        row.add(bubble);
        chatPanel.add(row);
        chatPanel.add(Box.createVerticalStrut(4));
        chatPanel.revalidate();
        scrollToBottom();
    }

    // ── Typing indicator ──────────────────────────────────────────────────────
    private void showTypingIndicator() {
        typingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        typingRow.setOpaque(false);
        typingRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel lbl = new JLabel("  Thinking...");          // ← no "ALPHA is typing"
        lbl.setFont(FONT_SMALL.deriveFont(Font.ITALIC));
        lbl.setForeground(COLOR_TIMESTAMP);
        typingRow.add(lbl);
        chatPanel.add(typingRow);
        chatPanel.revalidate();
        scrollToBottom();
    }

    private void hideTypingIndicator() {
        if (typingRow != null) {
            chatPanel.remove(typingRow);
            typingRow = null;
            chatPanel.revalidate();
            chatPanel.repaint();
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void clearChat() {
        chatPanel.removeAll();
        engine.resetSession();
        chatPanel.revalidate();
        chatPanel.repaint();
        appendBotMessage(engine.getWelcomeMessage());
    }

    private JButton styledButton(String label, Color bg, Color fg) {
        JButton btn = new JButton(label);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void launch() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        setVisible(true);
    }
}
