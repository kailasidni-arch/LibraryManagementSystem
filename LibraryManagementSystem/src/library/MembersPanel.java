package library;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * MembersPanel – Panel CRUD untuk mengelola data anggota perpustakaan.
 * Komponen: JTextField (name, email, phone, address), JComboBox (status),
 *           JSpinner (register_date), JTable, JButton, JLabel
 */
public class MembersPanel extends JPanel {

    private JTextField tfName, tfEmail, tfPhone, tfAddress, tfSearch;
    private JComboBox<String> cbStatus;
    private JSpinner spRegisterDate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    private JLabel lblStatus;
    private int selectedMemberId = -1;

    public MembersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 252));
        initComponents();
        loadTable();
    }

    private void initComponents() {
        // ── FORM PANEL ────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 82, 118), 1),
                " Member Information ", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(26, 82, 118)));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        tfName = new JTextField(40);
        formPanel.add(tfName, gbc);

        // Row 1: Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email *:"), gbc);
        gbc.gridx = 1;
        tfEmail = new JTextField(25);
        formPanel.add(tfEmail, gbc);

        // Phone
        gbc.gridx = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3;
        tfPhone = new JTextField(20);
        formPanel.add(tfPhone, gbc);

        // Row 2: Address
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        tfAddress = new JTextField(40);
        formPanel.add(tfAddress, gbc);

        // Row 3: Register Date + Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Register Date:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spRegisterDate = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spRegisterDate, "yyyy-MM-dd");
        spRegisterDate.setEditor(dateEditor);
        spRegisterDate.setPreferredSize(new Dimension(140, 28));
        formPanel.add(spRegisterDate, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        cbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cbStatus.setPreferredSize(new Dimension(120, 28));
        formPanel.add(cbStatus, gbc);

        // Row 4: Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);
        btnAdd    = createButton("➕ Add",    new Color(39, 174, 96));
        btnUpdate = createButton("✏️ Update", new Color(41, 128, 185));
        btnDelete = createButton("🗑 Delete", new Color(192, 57, 43));
        btnClear  = createButton("🔄 Clear",  new Color(127, 140, 141));
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        gbc.gridy = 5;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        formPanel.add(lblStatus, gbc);

        // ── SEARCH PANEL ─────────────────────────────────────────
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchPanel.setBackground(new Color(245, 248, 252));
        searchPanel.add(new JLabel("🔍 Search:"));
        tfSearch   = new JTextField(25);
        btnSearch  = createButton("Search",   new Color(142, 68, 173));
        btnRefresh = createButton("Show All", new Color(52, 73, 94));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // ── TABLE ─────────────────────────────────────────────────
        String[] columns = {"ID", "Name", "Email", "Phone", "Address", "Register Date", "Status"};
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

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(26, 82, 118), 1));

        // ── ASSEMBLE ─────────────────────────────────────────────
        JPanel northPanel = new JPanel(new BorderLayout(0, 8));
        northPanel.setBackground(new Color(245, 248, 252));
        northPanel.add(formPanel,   BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ── EVENTS ───────────────────────────────────────────────
        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchMembers());
        btnRefresh.addActionListener(e -> loadTable());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { fillFormFromTable(); }
        });
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    public void loadTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM members ORDER BY member_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("member_id"), rs.getString("member_name"),
                    rs.getString("email"), rs.getString("phone"),
                    rs.getString("address"), rs.getString("register_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), false);
        }
    }

    private void searchMembers() {
        String kw = tfSearch.getText().trim();
        if (kw.isEmpty()) { loadTable(); return; }
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM members WHERE member_name LIKE ? OR email LIKE ? OR phone LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + kw + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("member_id"), rs.getString("member_name"),
                    rs.getString("email"), rs.getString("phone"),
                    rs.getString("address"), rs.getString("register_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            showStatus("Search error: " + e.getMessage(), false);
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedMemberId = (int) tableModel.getValueAt(row, 0);
        tfName.setText((String) tableModel.getValueAt(row, 1));
        tfEmail.setText((String) tableModel.getValueAt(row, 2));
        tfPhone.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        tfAddress.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        cbStatus.setSelectedItem(tableModel.getValueAt(row, 6));
        showStatus("Member selected: " + tfName.getText(), true);
    }

    private void addMember() {
        if (!validateForm()) return;
        String sql = "INSERT INTO members (member_name, email, phone, address, register_date, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfName.getText().trim());
            ps.setString(2, tfEmail.getText().trim());
            ps.setString(3, tfPhone.getText().trim());
            ps.setString(4, tfAddress.getText().trim());
            ps.setString(5, LocalDate.now().toString());
            ps.setString(6, (String) cbStatus.getSelectedItem());
            ps.executeUpdate();
            showStatus("✅ Member added!", true);
            clearForm(); loadTable();
        } catch (SQLException e) {
            showStatus("❌ Error: " + e.getMessage(), false);
        }
    }

    private void updateMember() {
        if (selectedMemberId < 0) { showStatus("⚠️ Select a member first.", false); return; }
        if (!validateForm()) return;
        String sql = "UPDATE members SET member_name=?, email=?, phone=?, address=?, status=? WHERE member_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfName.getText().trim());
            ps.setString(2, tfEmail.getText().trim());
            ps.setString(3, tfPhone.getText().trim());
            ps.setString(4, tfAddress.getText().trim());
            ps.setString(5, (String) cbStatus.getSelectedItem());
            ps.setInt(6, selectedMemberId);
            ps.executeUpdate();
            showStatus("✅ Member updated!", true);
            clearForm(); loadTable();
        } catch (SQLException e) {
            showStatus("❌ Error: " + e.getMessage(), false);
        }
    }

    private void deleteMember() {
        if (selectedMemberId < 0) { showStatus("⚠️ Select a member first.", false); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete member: " + tfName.getText() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM members WHERE member_id=?")) {
            ps.setInt(1, selectedMemberId);
            ps.executeUpdate();
            showStatus("✅ Member deleted.", true);
            clearForm(); loadTable();
        } catch (SQLException e) {
            showStatus("❌ Error: " + e.getMessage(), false);
        }
    }

    private void clearForm() {
        tfName.setText(""); tfEmail.setText(""); tfPhone.setText("");
        tfAddress.setText(""); cbStatus.setSelectedIndex(0);
        selectedMemberId = -1; table.clearSelection();
    }

    private boolean validateForm() {
        if (tfName.getText().trim().isEmpty() || tfEmail.getText().trim().isEmpty()) {
            showStatus("⚠️ Name and Email are required.", false);
            return false;
        }
        return true;
    }

    private void showStatus(String msg, boolean success) {
        lblStatus.setText(msg);
        lblStatus.setForeground(success ? new Color(39, 174, 96) : new Color(192, 57, 43));
    }
}
