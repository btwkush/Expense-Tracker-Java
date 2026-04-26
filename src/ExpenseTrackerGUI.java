import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

// 1. DATA BLUEPRINT
class ExpenseEntry implements Serializable {
    private String name;
    private double cost;
    private String category;

    public ExpenseEntry(String name, double cost, String category) {
        this.name = name;
        this.cost = cost;
        this.category = category;
    }

    public double getCost() { return cost; }
    public String toFileString() { return name + "," + cost + "," + category; }
}

// 2. MAIN GUI WINDOW
public class ExpenseTrackerGUI extends JFrame {
    private ArrayList<ExpenseEntry> entries = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JTextField nameField, amountField, catField;
    private JTable table;
    private double totalSpent = 0;

    public ExpenseTrackerGUI() {
        setTitle("Expense Tracker - Kush Yadav (SAP: 590014938)");
        setSize(850, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header
        statusLabel = new JLabel();
        updateStatus();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);

        // Data Table
        String[] columns = {"Item Name", "Amount", "Category"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Input Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Manage Expenses", TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; nameField = new JTextField(); formPanel.add(nameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0; formPanel.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; amountField = new JTextField(); formPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; catField = new JTextField(); formPanel.add(catField, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton addButton = new JButton("Add Entry");
        JButton deleteButton = new JButton("Delete Selected");
        JButton saveButton = new JButton("Save & Exit");
        
        addButton.setBackground(new Color(220, 240, 220));
        deleteButton.setBackground(new Color(255, 230, 230));
        saveButton.setBackground(new Color(230, 240, 255));

        btnPanel.add(addButton); btnPanel.add(deleteButton); btnPanel.add(saveButton);
        gbc.gridx = 2; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // --- BUTTON LOGIC ---
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String amountStr = amountField.getText().trim();
                String cat = catField.getText().trim();
                if (name.isEmpty() || amountStr.isEmpty() || cat.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
                    return;
                }
                double cost = Double.parseDouble(amountStr);
                entries.add(new ExpenseEntry(name, cost, cat));
                totalSpent += cost;
                tableModel.addRow(new Object[]{name, String.format("₹%.2f", cost), cat});
                updateStatus();
                nameField.setText(""); amountField.setText(""); catField.setText("");
                nameField.requestFocus();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid amount."); }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                totalSpent -= entries.get(row).getCost();
                entries.remove(row);
                tableModel.removeRow(row);
                updateStatus();
            } else { JOptionPane.showMessageDialog(this, "Select a row to delete."); }
        });

        saveButton.addActionListener(e -> {
            try (PrintWriter writer = new PrintWriter(new FileWriter("expense_report.txt"))) {
                writer.println("EXPENSE TRACKER REPORT - SAP ID: 590014938");
                writer.println("Total Expenditure: ₹" + totalSpent);
                writer.println("------------------------------");
                for (ExpenseEntry entry : entries) writer.println(entry.toFileString());
                JOptionPane.showMessageDialog(this, "Report saved!");
                System.exit(0);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Save error."); }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateStatus() {
        statusLabel.setText(String.format("Total Spending: ₹%.2f", totalSpent));
        statusLabel.setForeground(new Color(0, 100, 0));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        new ExpenseTrackerGUI();
    }
}