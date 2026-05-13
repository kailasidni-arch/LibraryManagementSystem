package library;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

/**
 * DashboardPanel – Overview statistik perpustakaan.
 * Menampilkan: jumlah buku, anggota, transaksi aktif, buku overdue,
 * dan daftar buku yang sedang dipinjam.
 */
public class DashboardPanel extends JPanel {

    private JLabel lblTotalBooks, lblAvailBooks, lblTotalMembers,
                   lblActiveIssues, lblOverdue, lblTotalCategories;
    private JTable tableOverdue;
    private DefaultTableModel overdueModel;
    private JButton btnRefresh;

    public DashboardPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 252));
        initComponents();
        loadStats();
    }

    private void initComponents() {
        // ── STAT CARDS PANEL ──────────────────────────────────────
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 12, 12));
        cardsPanel.setBackground(new Color(245, 248, 252));

        lblTotalBooks     = new JLabel("0", JLabel.CENTER);
        lblAvailBooks     = new JLabel("0", JLabel.CENTER);
        lblTotalMembers   = new JLabel("0", JLabel.CENTER);
        lblActiveIssues   = new JLabel("0", JLabel.CENTER);
        lblOverdue        = new JLabel("0", JLabel.CENTER);
        lblTotalCategories = new JLabel("0", JLabel.CENTER);

        cardsPanel.add(createCard("📚 Total Books",       lblTotalBooks,     new Color(26, 82, 118)));
        cardsPanel.add(createCard("✅ Available Books",   lblAvailBooks,     new Color(39, 174, 96)));
        cardsPanel.add(createCard("👥 Total Members",     lblTotalMembers,   new Color(142, 68, 173)));
        cardsPanel.add(createCard("🔄 Active Issues",     lblActiveIssues,   new Color(41, 128, 185)));
        cardsPanel.add(createCard("⚠️ Overdue",           lblOverdue,        new Color(192, 57, 43)));
        cardsPanel.add(createCard("🏷 Categories",        lblTotalCategories,new Color(22, 160, 133)));

        // ── OVERDUE TABLE ─────────────────────────────────────────
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setBackground(new Color(245, 248, 252));

        JLabel tableTitle = new JLabel("⚠️  Overdue & Currently Issued Books");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(new Color(26, 82, 118));

        btnRefresh = new JButton("⟳ Refresh Dashboard");
        btnRefresh.setBackground(new Color(52, 73, 94));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(new Color(245, 248, 252));
        titleRow.add(tableTitle, BorderLayout.WEST);
        titleRow.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"Issue ID", "Book Title", "Member", "Issue Date", "Due Date", "Days Overdue", "Fine (Rp)"};
        overdueModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableOverdue = new JTable(overdueModel);
        tableOverdue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableOverdue.setRowHeight(24);
        tableOverdue.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableOverdue.getTableHeader().setBackground(new Color(192, 57, 43));
        tableOverdue.getTableHeader().setForeground(Color.WHITE);
        tableOverdue.setGridColor(new Color(220, 230, 240));
        tableOverdue.setSelectionBackground(new Color(253, 237, 236));

        // Color overdue rows red
        tableOverdue.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                Object daysVal = overdueModel.getValueAt(row, 5);
                int days = daysVal instanceof Integer ? (int) daysVal : 0;
                if (!sel) {
                    c.setBackground(days > 0 ? new Color(253, 237, 236) : new Color(232, 245, 253));
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tableOverdue);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(192, 57, 43), 1));

        tablePanel.add(titleRow, BorderLayout.NORTH);
        tablePanel.add(scroll,   BorderLayout.CENTER);

        // ── ASSEMBLE ─────────────────────────────────────────────
        JPanel northWrap = new JPanel(new BorderLayout(0, 10));
        northWrap.setBackground(new Color(245, 248, 252));
        JLabel dashTitle = new JLabel("📊  Library Dashboard");
        dashTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dashTitle.setForeground(new Color(26, 82, 118));
        northWrap.add(dashTitle,  BorderLayout.NORTH);
        northWrap.add(cardsPanel, BorderLayout.CENTER);

        add(northWrap,   BorderLayout.NORTH);
        add(tablePanel,  BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadStats());
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lTitle.setForeground(new Color(80, 80, 80));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accent);

        card.add(lTitle,      BorderLayout.NORTH);
        card.add(valueLabel,  BorderLayout.CENTER);
        return card;
    }

    public void loadStats() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs;
            rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
            if (rs.next()) lblTotalBooks.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT SUM(available_copies) FROM books");
            if (rs.next()) lblAvailBooks.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM members WHERE status='Active'");
            if (rs.next()) lblTotalMembers.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM issued_books WHERE status='Issued'");
            if (rs.next()) lblActiveIssues.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM issued_books WHERE status='Issued' AND due_date < CURDATE()");
            if (rs.next()) lblOverdue.setText(String.valueOf(rs.getInt(1)));

            rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            if (rs.next()) lblTotalCategories.setText(String.valueOf(rs.getInt(1)));

        } catch (SQLException e) {
            System.err.println("Dashboard stats error: " + e.getMessage());
        }

        // Load overdue / active issues table
        overdueModel.setRowCount(0);
        String sql = "SELECT ib.issue_id, b.title, m.member_name, ib.date_issued, ib.due_date, " +
                     "GREATEST(0, DATEDIFF(CURDATE(), ib.due_date)) AS days_overdue, " +
                     "GREATEST(0, DATEDIFF(CURDATE(), ib.due_date)) * 1000 AS fine " +
                     "FROM issued_books ib " +
                     "JOIN books b ON ib.book_id = b.book_id " +
                     "JOIN members m ON ib.member_id = m.member_id " +
                     "WHERE ib.status = 'Issued' " +
                     "ORDER BY days_overdue DESC, ib.due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                overdueModel.addRow(new Object[]{
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("member_name"),
                    rs.getString("date_issued"),
                    rs.getString("due_date"),
                    rs.getInt("days_overdue"),
                    rs.getDouble("fine")
                });
            }
        } catch (SQLException e) {
            System.err.println("Dashboard table error: " + e.getMessage());
        }
    }
}
