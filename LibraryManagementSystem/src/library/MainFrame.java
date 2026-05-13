package library;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame – JFrame utama aplikasi Library Management System.
 * Menggunakan JTabbedPane untuk navigasi antar panel.
 */
public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Library Management System – Semester 20252");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // ── Header Panel ──────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(26, 82, 118));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 65));

        JLabel titleLabel = new JLabel("  📚  Library Management System", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Object-Oriented and Visual Programming  ", JLabel.RIGHT);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(200, 220, 240));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(subLabel, BorderLayout.EAST);

        // ── Tabbed Pane ───────────────────────────────────────────
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(245, 248, 252));

        tabbedPane.addTab("📗  Books",        null, new BooksPanel(),       "Manage Books");
        tabbedPane.addTab("👥  Members",      null, new MembersPanel(),     "Manage Members");
        tabbedPane.addTab("🔄  Issue / Return", null, new IssuedBooksPanel(), "Issue & Return Books");
        tabbedPane.addTab("📊  Dashboard",    null, new DashboardPanel(),   "Overview & Statistics");

        // ── Layout ───────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane,  BorderLayout.CENTER);

        // ── Status Bar ────────────────────────────────────────────
        JLabel statusBar = new JLabel("  Connected to: library_db @ localhost");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        // Set Look and Feel ke Nimbus (lebih modern dari default)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback ke default L&F
        }

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
