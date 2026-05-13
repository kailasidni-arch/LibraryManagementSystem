package library;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * BooksPanel – Panel CRUD untuk mengelola data buku.
 * Komponen: JTextField (title, author, isbn, publisher, year, copies),
 *           JComboBox (category), JTable, JButton, JLabel, JScrollPane
 */
public class BooksPanel extends JPanel {

    // ── Form Fields ──────────────────────────────────────────────
    private JTextField tfTitle, tfAuthor, tfISBN, tfPublisher, tfYear, tfCopies, tfSearch;
    private JComboBox<String> cbCategory;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    private JLabel lblStatus;
    private int selectedBookId = -1;

    public BooksPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 252));
        initComponents();
        loadTable();
        loadCategories();
    }

    private void initComponents() {
        // ── FORM PANEL (North) ────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 82, 118), 1),
                " Book Information ", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(26, 82, 118)));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Row 0 – Title & Author
        addFormRow(formPanel, gbc, 0, "Title *",  tfTitle    = new JTextField(20));
        addFormRow(formPanel, gbc, 1, "Author *", tfAuthor   = new JTextField(20));
        addFormRow(formPanel, gbc, 2, "ISBN *",   tfISBN     = new JTextField(20));

        // Category ComboBox
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3;
        cbCategory = new JComboBox<>();
        cbCategory.setPreferredSize(new Dimension(160, 28));
        formPanel.add(cbCategory, gbc);

        // Publisher
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 3;
        tfPublisher = new JTextField(20);
        formPanel.add(tfPublisher, gbc);

        // Year & Copies
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Year Published:"), gbc);
        gbc.gridx = 3;
        tfYear = new JTextField(20);
        formPanel.add(tfYear, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Total Copies:"), gbc);
        gbc.gridx = 1;
        tfCopies = new JTextField(20);
        formPanel.add(tfCopies, gbc);

        // ── BUTTON PANEL ──────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);

        btnAdd     = createButton("➕ Add",     new Color(39, 174, 96));
        btnUpdate  = createButton("✏️ Update",  new Color(41, 128, 185));
        btnDelete  = createButton("🗑 Delete",  new Color(192, 57, 43));
        btnClear   = createButton("🔄 Clear",   new Color(127, 140, 141));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        // Status label
        gbc.gridy = 5;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(39, 174, 96));
        formPanel.add(lblStatus, gbc);

        // ── SEARCH PANEL ─────────────────────────────────────────
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchPanel.setBackground(new Color(245, 248, 252));
        searchPanel.add(new JLabel("🔍 Search:"));
        tfSearch   = new JTextField(25);
        btnSearch  = createButton("Search",  new Color(142, 68, 173));
        btnRefresh = createButton("Show All", new Color(52, 73, 94));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // ── TABLE ─────────────────────────────────────────────────
        String[] columns = {"ID", "Title", "Author", "ISBN", "Category", "Publisher", "Year", "Total", "Available"};
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

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

        add(northPanel,  BorderLayout.NORTH);
        add(scrollPane,  BorderLayout.CENTER);

        // ── EVENT LISTENERS ───────────────────────────────────────
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchBooks());
        btnRefresh.addActionListener(e -> loadTable());

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { fillFormFromTable(); }
        });

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) searchBooks();
            }
        });
    }

    // ── Helper to add label + field row ──────────────────────────
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(180, 28));
        panel.add(field, gbc);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    // ── Load categories into ComboBox ─────────────────────────────
    private void loadCategories() {
        cbCategory.removeAllItems();
        cbCategory.addItem("-- Select Category --");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT category_id, category_name FROM categories ORDER BY category_name")) {
            while (rs.next()) {
                cbCategory.addItem(rs.getInt("category_id") + " - " + rs.getString("category_name"));
            }
        } catch (SQLException e) {
            showStatus("Error loading categories: " + e.getMessage(), false);
        }
    }

    // ── Load / Refresh Table ─────────────────────────────────────
    public void loadTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT b.book_id, b.title, b.author, b.isbn, " +
                     "IFNULL(c.category_name, 'N/A') AS category, " +
                     "b.publisher, b.year_published, b.total_copies, b.available_copies " +
                     "FROM books b LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "ORDER BY b.book_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                    rs.getString("isbn"), rs.getString("category"), rs.getString("publisher"),
                    rs.getInt("year_published"), rs.getInt("total_copies"), rs.getInt("available_copies")
                });
            }
        } catch (SQLException e) {
            showStatus("Error loading books: " + e.getMessage(), false);
        }
    }

    // ── Search ────────────────────────────────────────────────────
    private void searchBooks() {
        String keyword = tfSearch.getText().trim();
        if (keyword.isEmpty()) { loadTable(); return; }
        tableModel.setRowCount(0);
        String sql = "SELECT b.book_id, b.title, b.author, b.isbn, " +
                     "IFNULL(c.category_name,'N/A') AS category, " +
                     "b.publisher, b.year_published, b.total_copies, b.available_copies " +
                     "FROM books b LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
                    rs.getString("isbn"), rs.getString("category"), rs.getString("publisher"),
                    rs.getInt("year_published"), rs.getInt("total_copies"), rs.getInt("available_copies")
                });
            }
        } catch (SQLException e) {
            showStatus("Search error: " + e.getMessage(), false);
        }
    }

    // ── Fill form when row is clicked ─────────────────────────────
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedBookId = (int) tableModel.getValueAt(row, 0);
        tfTitle.setText((String) tableModel.getValueAt(row, 1));
        tfAuthor.setText((String) tableModel.getValueAt(row, 2));
        tfISBN.setText((String) tableModel.getValueAt(row, 3));
        tfPublisher.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        tfYear.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
        tfCopies.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");
        showStatus("Book selected: " + tfTitle.getText(), true);
    }

    // ── ADD ───────────────────────────────────────────────────────
    private void addBook() {
        if (!validateForm()) return;
        String sql = "INSERT INTO books (title, author, isbn, category_id, publisher, year_published, total_copies, available_copies) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfTitle.getText().trim());
            ps.setString(2, tfAuthor.getText().trim());
            ps.setString(3, tfISBN.getText().trim());
            ps.setObject(4, getCategoryId());
            ps.setString(5, tfPublisher.getText().trim());
            ps.setObject(6, tfYear.getText().isEmpty() ? null : Integer.parseInt(tfYear.getText().trim()));
            int copies = tfCopies.getText().isEmpty() ? 1 : Integer.parseInt(tfCopies.getText().trim());
            ps.setInt(7, copies);
            ps.setInt(8, copies);
            ps.executeUpdate();
            showStatus("✅ Book added successfully!", true);
            clearForm();
            loadTable();
        } catch (SQLException e) {
            showStatus("❌ Error: " + e.getMessage(), false);
        }
    }

    // ── UPDATE ───────────────────────────────────────────────────
    private void updateBook() {
        if (selectedBookId < 0) { showStatus("⚠️ Please select a book first.", false); return; }
        if (!validateForm()) return;
        String sql = "UPDATE books SET title=?, author=?, isbn=?, category_id=?, publisher=?, year_published=?, total_copies=? WHERE book_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfTitle.getText().trim());
            ps.setString(2, tfAuthor.getText().trim());
            ps.setString(3, tfISBN.getText().trim());
            ps.setObject(4, getCategoryId());
            ps.setString(5, tfPublisher.getText().trim());
            ps.setObject(6, tfYear.getText().isEmpty() ? null : Integer.parseInt(tfYear.getText().trim()));
            ps.setInt(7, Integer.parseInt(tfCopies.getText().trim()));
            ps.setInt(8, selectedBookId);
            ps.executeUpdate();
            showStatus("✅ Book updated successfully!", true);
            clearForm();
            loadTable();
        } catch (SQLException e) {
            showStatus("❌ Error: " + e.getMessage(), false);
        }
    }

    // ── DELETE ───────────────────────────────────────────────────
    private void deleteBook() {
        if (selectedBookId < 0) { showStatus("⚠️ Please select a book first.", false); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete book: " + tfTitle.getText() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id=?")) {
            ps.setInt(1, selectedBookId);
            ps.executeUpdate();
            showStatus("✅ Book deleted.", true);
            clearForm();
            loadTable();
        } catch (SQLException e) {
            showStatus("❌ Error: " + e.getMessage(), false);
        }
    }

    // ── CLEAR ─────────────────────────────────────────────────────
    private void clearForm() {
        tfTitle.setText(""); tfAuthor.setText(""); tfISBN.setText("");
        tfPublisher.setText(""); tfYear.setText(""); tfCopies.setText("");
        cbCategory.setSelectedIndex(0);
        selectedBookId = -1;
        table.clearSelection();
    }

    // ── Validation ───────────────────────────────────────────────
    private boolean validateForm() {
        if (tfTitle.getText().trim().isEmpty() || tfAuthor.getText().trim().isEmpty() || tfISBN.getText().trim().isEmpty()) {
            showStatus("⚠️ Title, Author, and ISBN are required.", false);
            return false;
        }
        return true;
    }

    private Integer getCategoryId() {
        String selected = (String) cbCategory.getSelectedItem();
        if (selected == null || selected.startsWith("--")) return null;
        return Integer.parseInt(selected.split(" - ")[0].trim());
    }

    private void showStatus(String msg, boolean success) {
        lblStatus.setText(msg);
        lblStatus.setForeground(success ? new Color(39, 174, 96) : new Color(192, 57, 43));
    }
}
