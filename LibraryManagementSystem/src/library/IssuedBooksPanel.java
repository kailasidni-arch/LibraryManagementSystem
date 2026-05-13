package library;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * IssuedBooksPanel – Panel untuk transaksi peminjaman dan pengembalian buku.
 * Komponen: JComboBox (book, member), JSpinner (dates), JLabel (fine),
 *           JTable, JButton, JRadioButton (filter status)
 */
public class IssuedBooksPanel extends JPanel {

    private JComboBox<String> cbBook, cbMember, cbFilterStatus;
    private JSpinner spIssueDate, spDueDate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnIssue, btnReturn, btnClear, btnRefresh;
    private JLabel lblStatus, lblFineInfo;
    private JRadioButton rbAll, rbIssued, rbReturned, rbOverdue;
    private int selectedIssueId = -1;

    public IssuedBooksPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 252));
        initComponents();
        loadCombos();
        loadTable("All");
    }

    private void initComponents() {
        // ── ISSUE FORM PANEL ─────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 82, 118), 1),
                " Issue a Book ", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(26, 82, 118)));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Book ComboBox
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Book *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cbBook = new JComboBox<>();
        cbBook.setPreferredSize(new Dimension(320, 28));
        formPanel.add(cbBook, gbc);

        // Member ComboBox
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Select Member *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cbMember = new JComboBox<>();
        cbMember.setPreferredSize(new Dimension(320, 28));
        formPanel.add(cbMember, gbc);

        // Issue Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Issue Date:"), gbc);
        gbc.gridx = 1;
        spIssueDate = createDateSpinner(LocalDate.now());
        formPanel.add(spIssueDate, gbc);

        // Due Date
        gbc.gridx = 2;
        formPanel.add(new JLabel("Due Date:"), gbc);
        gbc.gridx = 3;
        spDueDate = createDateSpinner(LocalDate.now().plusDays(14));
        formPanel.add(spDueDate, gbc);

        // Fine info label
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        lblFineInfo = new JLabel("  ℹ️  Fine: Rp 1.000 / day overdue");
        lblFineInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFineInfo.setForeground(new Color(142, 68, 173));
        formPanel.add(lblFineInfo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);
        btnIssue   = createButton("📤 Issue Book",   new Color(39, 174, 96));
        btnReturn  = createButton("📥 Return Book",  new Color(41, 128, 185));
        btnClear   = createButton("🔄 Clear",        new Color(127, 140, 141));
        btnRefresh = createButton("⟳ Refresh",       new Color(52, 73, 94));
        btnPanel.add(btnIssue); btnPanel.add(btnReturn);
        btnPanel.add(btnClear); btnPanel.add(btnRefresh);

        gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        gbc.gridy = 5;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        formPanel.add(lblStatus, gbc);

        // ── FILTER PANEL ─────────────────────────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        filterPanel.setBackground(new Color(245, 248, 252));
        filterPanel.add(new JLabel("Filter:"));

        ButtonGroup bg = new ButtonGroup();
        rbAll      = new JRadioButton("All",      true);
        rbIssued   = new JRadioButton("Issued");
        rbReturned = new JRadioButton("Returned");
        rbOverdue  = new JRadioButton("Overdue");
        for (JRadioButton rb : new JRadioButton[]{rbAll, rbIssued, rbReturned, rbOverdue}) {
            rb.setBackground(new Color(245, 248, 252));
            bg.add(rb);
            filterPanel.add(rb);
        }

        ActionListener filterAction = e -> {
            if (rbAll.isSelected())      loadTable("All");
            else if (rbIssued.isSelected())   loadTable("Issued");
            else if (rbReturned.isSelected()) loadTable("Returned");
            else if (rbOverdue.isSelected())  loadTable("Overdue");
        };
        rbAll.addActionListener(filterAction);
        rbIssued.addActionListener(filterAction);
        rbReturned.addActionListener(filterAction);
        rbOverdue.addActionListener(filterAction);

        // ── TABLE ─────────────────────────────────────────────────
        String[] columns = {"ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Return Date", "Status", "Fine (Rp)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(26, 82, 118));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setGridColor(new Color(220, 230, 240));

        // Color rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    String status = (String) tableModel.getValueAt(row, 6);
                    if ("Overdue".equals(status))   c.setBackground(new Color(253, 237, 236));
                    else if ("Returned".equals(status)) c.setBackground(new Color(232, 246, 243));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(26, 82, 118), 1));

        // ── ASSEMBLE ─────────────────────────────────────────────
        JPanel northPanel = new JPanel(new BorderLayout(0, 6));
        northPanel.setBackground(new Color(245, 248, 252));
        northPanel.add(formPanel,   BorderLayout.NORTH);
        northPanel.add(filterPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ── EVENTS ───────────────────────────────────────────────
        btnIssue.addActionListener(e -> issueBook());
        btnReturn.addActionListener(e -> returnBook());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> { loadCombos(); loadTable("All"); rbAll.setSelected(true); });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { selectRow(); }
        });
    }

    private JSpinner createDateSpinner(LocalDate date) {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(140, 28));
        // Set default value
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        model.setValue(cal.getTime());
        return spinner;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 32));
        return btn;
    }

    private void loadCombos() {
        // Books
        cbBook.removeAllItems();
        cbBook.addItem("-- Select Available Book --");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT book_id, title, available_copies FROM books WHERE available_copies > 0 ORDER BY title")) {
            while (rs.next()) {
                cbBook.addItem(rs.getInt("book_id") + " | " + rs.getString("title")
                        + " (Available: " + rs.getInt("available_copies") + ")");
            }
        } catch (SQLException e) {
            showStatus("Error loading books: " + e.getMessage(), false);
        }

        // Members
        cbMember.removeAllItems();
        cbMember.addItem("-- Select Member --");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT member_id, member_name FROM members WHERE status='Active' ORDER BY member_name")) {
            while (rs.next()) {
                cbMember.addItem(rs.getInt("member_id") + " | " + rs.getString("member_name"));
            }
        } catch (SQLException e) {
            showStatus("Error loading members: " + e.getMessage(), false);
        }
    }

    public void loadTable(String filter) {
        tableModel.setRowCount(0);
        String sql = "SELECT ib.issue_id, b.title, m.member_name, " +
                     "ib.date_issued, ib.due_date, ib.date_returned, ib.status, ib.fine " +
                     "FROM issued_books ib " +
                     "JOIN books b ON ib.book_id = b.book_id " +
                     "JOIN members m ON ib.member_id = m.member_id";
        if (!"All".equals(filter)) sql += " WHERE ib.status = '" + filter + "'";
        sql += " ORDER BY ib.issue_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("issue_id"), rs.getString("title"),
                    rs.getString("member_name"), rs.getString("date_issued"),
                    rs.getString("due_date"), rs.getString("date_returned"),
                    rs.getString("status"), rs.getDouble("fine")
                });
            }
        } catch (SQLException e) {
            showStatus("Error loading records: " + e.getMessage(), false);
        }
    }

    private void selectRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedIssueId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 6);
        showStatus("Selected issue #" + selectedIssueId + " | Status: " + status, true);
    }

    private void issueBook() {
        if (cbBook.getSelectedIndex() == 0 || cbMember.getSelectedIndex() == 0) {
            showStatus("⚠️ Please select a book and a member.", false);
            return;
        }

        int bookId   = Integer.parseInt(cbBook.getSelectedItem().toString().split(" \\| ")[0].trim());
        int memberId = Integer.parseInt(cbMember.getSelectedItem().toString().split(" \\| ")[0].trim());
        String issueDate = getSpinnerDate(spIssueDate);
        String dueDate   = getSpinnerDate(spDueDate);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert issued record
            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO issued_books (book_id, member_id, date_issued, due_date, status) VALUES (?,?,?,?,'Issued')");
            ps1.setInt(1, bookId); ps1.setInt(2, memberId);
            ps1.setString(3, issueDate); ps1.setString(4, dueDate);
            ps1.executeUpdate();

            // Decrement available copies
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ? AND available_copies > 0");
            ps2.setInt(1, bookId);
            int updated = ps2.executeUpdate();
            if (updated == 0) throw new SQLException("No available copies!");

            conn.commit();
            showStatus("✅ Book issued successfully!", true);
            clearForm(); loadCombos(); loadTable("All"); rbAll.setSelected(true);
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            showStatus("❌ Error: " + e.getMessage(), false);
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    private void returnBook() {
        if (selectedIssueId < 0) {
            showStatus("⚠️ Please select an issued record from the table.", false);
            return;
        }

        String returnDate = LocalDate.now().toString();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get issue info
            PreparedStatement psGet = conn.prepareStatement(
                    "SELECT book_id, due_date FROM issued_books WHERE issue_id=? AND status='Issued'");
            psGet.setInt(1, selectedIssueId);
            ResultSet rs = psGet.executeQuery();
            if (!rs.next()) {
                showStatus("⚠️ This record is not in 'Issued' status.", false);
                conn.rollback();
                return;
            }
            int bookId = rs.getInt("book_id");
            LocalDate dueDate = LocalDate.parse(rs.getString("due_date"));
            LocalDate retDate = LocalDate.parse(returnDate);

            // Calculate fine
            double fine = 0;
            if (retDate.isAfter(dueDate)) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(dueDate, retDate);
                fine = days * 1000.0; // Rp 1.000 / day
            }

            // Update issued_books
            PreparedStatement psUpd = conn.prepareStatement(
                    "UPDATE issued_books SET date_returned=?, status=?, fine=? WHERE issue_id=?");
            psUpd.setString(1, returnDate);
            psUpd.setString(2, fine > 0 ? "Returned" : "Returned");
            psUpd.setDouble(3, fine);
            psUpd.setInt(4, selectedIssueId);
            psUpd.executeUpdate();

            // Increment available copies
            PreparedStatement psBook = conn.prepareStatement(
                    "UPDATE books SET available_copies = available_copies + 1 WHERE book_id=?");
            psBook.setInt(1, bookId);
            psBook.executeUpdate();

            conn.commit();
            String fineMsg = fine > 0 ? " | Fine: Rp " + String.format("%.0f", fine) : " | No fine";
            showStatus("✅ Book returned successfully!" + fineMsg, true);
            selectedIssueId = -1;
            loadCombos(); loadTable("All"); rbAll.setSelected(true);
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            showStatus("❌ Error: " + e.getMessage(), false);
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
        }
    }

    private String getSpinnerDate(JSpinner spinner) {
        java.util.Date date = (java.util.Date) spinner.getValue();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private void clearForm() {
        cbBook.setSelectedIndex(0);
        cbMember.setSelectedIndex(0);
        selectedIssueId = -1;
        table.clearSelection();
    }

    private void showStatus(String msg, boolean success) {
        lblStatus.setText(msg);
        lblStatus.setForeground(success ? new Color(39, 174, 96) : new Color(192, 57, 43));
    }
}
