import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FoodOrderUI - Main GUI class for Online Food Ordering System
 * This class handles the user interface and order management for a food ordering application
 */

public class FoodOrderUI extends JFrame {
    
    Map<String, Integer> quantityMap = new HashMap<>();
    Map<String, FoodItem> menu = new HashMap<>();
    Order order = new Order(1);

    
    private final Color PRIMARY_COLOR = new Color(231, 76, 60);      // Red
    private final Color SECONDARY_COLOR = new Color(52, 73, 94);     // Dark Blue
    private final Color ACCENT_COLOR = new Color(46, 204, 113);      // Green
    private final Color BG_COLOR = new Color(236, 240, 241);         // Light Gray
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color AVAILABLE_COLOR = new Color(39, 174, 96);    // Green for available
    private final Color UNAVAILABLE_COLOR = new Color(149, 165, 166); // Gray for unavailable

    private void logDailyOrder() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

        String fileName = "orders_" + LocalDate.now().format(dateFormatter) + ".txt";

        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("------------------------------\n");
            writer.write("Date: " + LocalDateTime.now().format(timeFormatter) + "\n");
            writer.write("Items:\n");

            for (String itemName : quantityMap.keySet()) {
                int qty = quantityMap.get(itemName);
                FoodItem item = menu.get(itemName);

                if (item != null) {
                    writer.write("- " + itemName + " x " + qty +
                            " : Rs. " + (item.getPrice() * qty) + "\n");
                }
            }

            writer.write("Total: Rs. " + order.getTotalAmount() + "\n");
            writer.write("------------------------------\n\n");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error writing order log file");
        }
    }

    JComboBox<String> categoryBox;
    JList<String> itemList;
    DefaultListModel<String> itemModel;
    JLabel totalLabel, statusLabel;
    DefaultListModel<String> orderModel;
    JList<String> orderList;
    JScrollPane orderScroll;

    public FoodOrderUI() {
        setTitle("Food Order Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BG_COLOR);

        initializeMenu();

        // 🎨 HEADER with gradient-like effect
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🍽️ Online Food Ordering", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Order delicious food in just a few clicks!", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // 🎨 LEFT PANEL - Categories
        JPanel leftPanel = createStyledPanel("📋 Categories");
        leftPanel.setPreferredSize(new Dimension(200, 0));

        String[] categories = {"Starters", "Main Course", "Beverages", "Combos"};
        categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryBox.setBackground(Color.WHITE);
        categoryBox.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        categoryBox.setPreferredSize(new Dimension(180, 35));
        categoryBox.addActionListener(e -> loadItems());

        leftPanel.add(categoryBox);
        add(leftPanel, BorderLayout.WEST);

        // 🎨 CENTER PANEL - Menu Items with Custom Renderer
        JPanel centerPanel = createStyledPanel("🍴 Menu Items");

        itemModel = new DefaultListModel<>();
        itemList = new JList<>(itemModel);
        itemList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemList.setBackground(Color.WHITE);
        itemList.setSelectionBackground(new Color(52, 152, 219, 100));
        itemList.setSelectionForeground(TEXT_COLOR);
        itemList.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 🎨 Custom Cell Renderer for Available/Unavailable Items
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                
                String text = value.toString();
                String itemName = text.split(" - ")[0];
                FoodItem item = menu.get(itemName);
                
                if (item != null) {
                    if (item.isAvailable()) {
                        label.setForeground(isSelected ? TEXT_COLOR : AVAILABLE_COLOR);
                        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    } else {
                        label.setForeground(UNAVAILABLE_COLOR);
                        label.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                        // Add strikethrough
                        String html = "<html><strike>" + text + "</strike></html>";
                        label.setText(html);
                    }
                }
                
                label.setBorder(new EmptyBorder(5, 5, 5, 5));
                return label;
            }
        });
        
        loadItems();

        JScrollPane itemScroll = new JScrollPane(itemList);
        itemScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Add legend for availability
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setBackground(CARD_COLOR);
        
        JLabel availLegend = new JLabel("● Available");
        availLegend.setForeground(AVAILABLE_COLOR);
        availLegend.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel unavailLegend = new JLabel("● Unavailable");
        unavailLegend.setForeground(UNAVAILABLE_COLOR);
        unavailLegend.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        
        legendPanel.add(availLegend);
        legendPanel.add(unavailLegend);
        
        centerPanel.add(legendPanel, BorderLayout.NORTH);
        centerPanel.add(itemScroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 🎨 RIGHT PANEL - Order Summary & Actions
        JPanel rightPanel = createStyledPanel("🛒 Your Order");
        rightPanel.setPreferredSize(new Dimension(280, 0));

        orderModel = new DefaultListModel<>();
        orderList = new JList<>(orderModel);
        orderList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orderList.setBackground(Color.WHITE);
        orderList.setBorder(new EmptyBorder(10, 10, 10, 10));

        orderScroll = new JScrollPane(orderList);
        orderScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        orderScroll.setPreferredSize(new Dimension(260, 200));

        // Styled Buttons
        JButton addBtn = createStyledButton("➕ Add Item", ACCENT_COLOR);
        JButton placeOrderBtn = createStyledButton("✓ Place Order", PRIMARY_COLOR);

        // Status Labels
        totalLabel = new JLabel("Total: Rs. 0.00", JLabel.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        totalLabel.setOpaque(true);
        totalLabel.setBackground(new Color(255, 235, 235));

        statusLabel = new JLabel("Status: READY", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ACCENT_COLOR);

        // Layout right panel
        rightPanel.setLayout(new BorderLayout(10, 10));
        
        JPanel orderSection = new JPanel(new BorderLayout(5, 5));
        orderSection.setBackground(CARD_COLOR);
        orderSection.add(orderScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(addBtn);
        buttonPanel.add(placeOrderBtn);
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.add(totalLabel);
        infoPanel.add(statusLabel);

        rightPanel.add(orderSection, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.NORTH);
        rightPanel.add(infoPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        // Button Actions
        addBtn.addActionListener(e -> addSelectedItem());
        placeOrderBtn.addActionListener(e -> processOrder());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 🎨 Create Styled Panel
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new CompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)
            )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(SECONDARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    // 🎨 Create Styled Button
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(0, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    void initializeMenu() {
        menu.put("Spring Roll", new FoodItem("Spring Roll", 120, true, "Starters"));
        menu.put("Paneer Tikka", new FoodItem("Paneer Tikka", 180, true, "Starters"));
        menu.put("Gobi Chilli", new FoodItem("Gobi Chilli", 290, true, "Starters"));
        menu.put("Babycorn chilli", new FoodItem("Babycorn chilli", 280, true, "Starters"));
        menu.put("Chilli Chicken", new FoodItem("Chilli Chicken", 280, true, "Starters"));
        menu.put("Kalmi Kabab", new FoodItem("Kalmi Kabab", 390, true, "Starters"));
        menu.put("Carrot 65", new FoodItem("Carrot 65", 100, true, "Starters"));
        menu.put("Gobi Tikka", new FoodItem("Gobi Tikka", 95, true, "Starters"));
        menu.put("Egg Burji", new FoodItem("Egg Burji", 80, true, "Starters"));
        menu.put("Prawns Chilli", new FoodItem("Prawns Chilli", 380, true, "Starters"));
        menu.put("Omelette", new FoodItem("Omelette", 200, true, "Starters"));
        menu.put("Manchurian Balls", new FoodItem("Manchurian Balls", 380, true, "Starters"));
        menu.put("Chilli Paneer", new FoodItem("Chilli Paneer", 320, true, "Starters"));
        menu.put("Gobi Manchurian", new FoodItem("Gobi Manchurian", 280, true, "Starters"));
        menu.put("Paneer 65", new FoodItem("Paneer 65", 300, true, "Starters"));
        menu.put("Crispy Corn", new FoodItem("Crispy Corn", 260, true, "Starters"));
        menu.put("Potato Rolls", new FoodItem("Spring Rolls", 240, true, "Starters"));
        menu.put("Baby Corn Manchurian", new FoodItem("Baby Corn Manchurian", 290, true, "Starters"));
        menu.put("Mushroom Pepper Fry", new FoodItem("Mushroom Pepper Fry", 310, true, "Starters"));
        menu.put("Paneer Pakoda", new FoodItem("Paneer Pakoda", 220, true, "Starters"));
        menu.put("Veg Cutlet", new FoodItem("Veg Cutlet", 200, true, "Starters"));
        menu.put("Cheese Balls", new FoodItem("Cheese Balls", 270, true, "Starters"));

        menu.put("Veg Biryani", new FoodItem("Veg Biryani", 220, true, "Main Course"));
        menu.put("Pasta", new FoodItem("Pasta", 200, false, "Main Course"));
        menu.put("Veg Fried Rice", new FoodItem("Veg Fried Rice", 240, true, "Main Course"));
        menu.put("Veg Noodles", new FoodItem("Veg Noodles", 230, true, "Main Course"));
        menu.put("Paneer Butter Masala", new FoodItem("Paneer Butter Masala", 340, true, "Main Course"));
        menu.put("Paneer Tikka Masala", new FoodItem("Paneer Tikka Masala", 360, true, "Main Course"));
        menu.put("Kadai Paneer", new FoodItem("Kadai Paneer", 350, true, "Main Course"));
        menu.put("Shahi Paneer", new FoodItem("Shahi Paneer", 370, true, "Main Course"));
        menu.put("Dal Tadka", new FoodItem("Dal Tadka", 220, true, "Main Course"));
        menu.put("Dal Fry", new FoodItem("Dal Fry", 210, true, "Main Course"));
        menu.put("Veg Kolhapuri", new FoodItem("Veg Kolhapuri", 300, true, "Main Course"));
        menu.put("Veg Handi", new FoodItem("Veg Handi", 320, true, "Main Course"));
        menu.put("Mushroom Masala", new FoodItem("Mushroom Masala", 330, false, "Main Course"));
        menu.put("Mushroom Biryani", new FoodItem("Mushroom Biryani", 310, true, "Main Course"));
        menu.put("Veg Hyderabadi Biryani", new FoodItem("Veg Hyderabadi Biryani", 260, true, "Main Course"));
        menu.put("Jeera Rice", new FoodItem("Jeera Rice", 180, true, "Main Course"));
        menu.put("Plain Rice", new FoodItem("Plain Rice", 150, true, "Main Course"));
        menu.put("Butter Naan", new FoodItem("Butter Naan", 60, true, "Main Course"));
        menu.put("Garlic Naan", new FoodItem("Garlic Naan", 70, true, "Main Course"));
        menu.put("Tandoori Roti", new FoodItem("Tandoori Roti", 40, true, "Main Course"));
        menu.put("Veg Lasagna", new FoodItem("Veg Lasagna", 380, false, "Main Course"));
        menu.put("Veg Macaroni", new FoodItem("Veg Macaroni", 210, true, "Main Course"));
        menu.put("Veg Thai Curry", new FoodItem("Veg Thai Curry", 420, false, "Main Course"));
        menu.put("Veg Korma", new FoodItem("Veg Korma", 330, true, "Main Course"));
        menu.put("Palak Paneer", new FoodItem("Palak Paneer", 340, true, "Main Course"));
        menu.put("Veg Pulao", new FoodItem("Veg Pulao", 240, true, "Main Course"));
        menu.put("Paneer Biryani", new FoodItem("Paneer Biryani", 320, false, "Main Course"));

        menu.put("Coke", new FoodItem("Coke", 50, true, "Beverages"));
        menu.put("Lime Juice", new FoodItem("Lime Juice", 60, true, "Beverages"));
        menu.put("Pepsi", new FoodItem("Pepsi", 50, true, "Beverages"));
        menu.put("Sprite", new FoodItem("Sprite", 50, true, "Beverages"));
        menu.put("Fanta", new FoodItem("Fanta", 50, true, "Beverages"));
        menu.put("Cold Coffee", new FoodItem("Cold Coffee", 120, true, "Beverages"));
        menu.put("Hot Coffee", new FoodItem("Hot Coffee", 100, true, "Beverages"));
        menu.put("Masala Tea", new FoodItem("Masala Tea", 40, true, "Beverages"));
        menu.put("Green Tea", new FoodItem("Green Tea", 60, true, "Beverages"));
        menu.put("Badam Milk", new FoodItem("Badam Milk", 90, true, "Beverages"));
        menu.put("Chocolate Milkshake", new FoodItem("Chocolate Milkshake", 150, true, "Beverages"));
        menu.put("Strawberry Milkshake", new FoodItem("Strawberry Milkshake", 150, true, "Beverages"));
        menu.put("Vanilla Milkshake", new FoodItem("Vanilla Milkshake", 140, false, "Beverages"));
        menu.put("Mango Milkshake", new FoodItem("Mango Milkshake", 160, false, "Beverages"));
        menu.put("Fresh Orange Juice", new FoodItem("Fresh Orange Juice", 110, true, "Beverages"));
        menu.put("Pineapple Juice", new FoodItem("Pineapple Juice", 110, true, "Beverages"));
        menu.put("Watermelon Juice", new FoodItem("Watermelon Juice", 100, true, "Beverages"));
        menu.put("Lassi", new FoodItem("Lassi", 80, true, "Beverages"));
        menu.put("Sweet Lassi", new FoodItem("Sweet Lassi", 90, true, "Beverages"));
        menu.put("Salted Lassi", new FoodItem("Salted Lassi", 90, false, "Beverages"));
        menu.put("Mineral Water", new FoodItem("Mineral Water", 30, true, "Beverages"));
        menu.put("Iced Tea", new FoodItem("Iced Tea", 120, false, "Beverages"));

        menu.put("Starter + Main Combo (Manchurian Balls + Veg Fried Rice)", new FoodItem("Starter + Main Combo (Manchurian Balls + Veg Fried Rice)", 350, true, "Combos"));
        menu.put("Paneer Special Combo (Paneer 65 + Butter Naan + Dal Fry)", new FoodItem("Paneer Special Combo (Paneer 65 + Butter Naan + Dal Fry)", 420, true, "Combos"));
        menu.put("Chinese Combo (Gobi Manchurian + Veg Noodles)", new FoodItem("Chinese Combo (Gobi Manchurian + Veg Noodles)", 330, true, "Combos"));
        menu.put("South Indian Veg Combo (Veg Biryani + Raita)", new FoodItem("South Indian Veg Combo (Veg Biryani + Raita)", 300, true, "Combos"));
        menu.put("Student Budget Combo (Veg Cutlet + Lime Juice)", new FoodItem("Student Budget Combo (Veg Cutlet + Lime Juice)", 180, true, "Combos"));
        menu.put("Deluxe Veg Combo (Paneer Butter Masala + Garlic Naan + Jeera Rice)", new FoodItem("Deluxe Veg Combo (Paneer Butter Masala + Garlic Naan + Jeera Rice)", 480, true, "Combos"));
        menu.put("Lunch Box Combo (Veg Pulao + Dal Tadka + Plain Rice)", new FoodItem("Lunch Box Combo (Veg Pulao + Dal Tadka + Plain Rice)", 340, false, "Combos"));
        menu.put("Fast Food Combo (Spring Rolls + Cold Coffee)", new FoodItem("Fast Food Combo (Spring Rolls + Cold Coffee)", 260, true, "Combos"));
        menu.put("Evening Snacks Combo (Cheese Balls + Masala Tea)", new FoodItem("Evening Snacks Combo (Cheese Balls + Masala Tea)", 220, true, "Combos"));
        menu.put("Family Veg Combo (Veg Kolhapuri + Butter Naan + Jeera Rice)", new FoodItem("Family Veg Combo (Veg Kolhapuri + Butter Naan + Jeera Rice)", 520, false, "Combos"));
    }

    void showComboMenu() {
        DefaultListModel<String> comboModel = new DefaultListModel<>();

        for (FoodItem item : menu.values()) {
            if (item.getCategory().equals("Combos") && item.isAvailable()) {
                comboModel.addElement(item.getName() + " - Rs. " + item.getPrice());
            }
        }

        if (comboModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No combo meals available.", 
                    "No Combos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JList<String> comboList = new JList<>(comboModel);
        comboList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        comboList.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        int choice = JOptionPane.showConfirmDialog(this, new JScrollPane(comboList),
                "Select a Combo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice == JOptionPane.OK_OPTION) {
            String selected = comboList.getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a combo to add.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String comboName = selected.split(" - ")[0];
            FoodItem combo = menu.get(comboName);
            if (combo == null) return;

            int qty = quantityMap.getOrDefault(comboName, 0) + 1;
            quantityMap.put(comboName, qty);
            order.addItem(combo);

            orderModel.removeAllElements();
            for (String name : quantityMap.keySet()) {
                orderModel.addElement(name + " × " + quantityMap.get(name));
            }

            totalLabel.setText("Total: Rs. " + String.format("%.2f", order.getTotalAmount()));
            statusLabel.setText("Status: COMBO ADDED ✓");
            statusLabel.setForeground(ACCENT_COLOR);
        }
    }

    void loadItems() {
        itemModel.clear();
        String selectedCategory = (String) categoryBox.getSelectedItem();

        for (FoodItem item : menu.values()) {
            if (item.getCategory().equals(selectedCategory)) {
                String status = item.isAvailable() ? "[Available]" : "[Unavailable]";
                itemModel.addElement(item.getName() + " - Rs. " + item.getPrice() + " " + status);
            }
        }
    }

    void addSelectedItem() {
        String selected = itemList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an item first!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemName = selected.split(" - ")[0];
        FoodItem item = menu.get(itemName);

        if (item == null) return;

        if (!item.isAvailable()) {
            JOptionPane.showMessageDialog(this, 
                    "❌ " + item.getName() + " is currently unavailable.\n\n" +
                    "Would you like to check our combo options instead?",
                    "Item Unavailable", JOptionPane.INFORMATION_MESSAGE);
            
            // 🔥 Automatically show combo menu
            showComboMenu();
            return;
        }

        int qty = quantityMap.getOrDefault(itemName, 0) + 1;
        quantityMap.put(itemName, qty);
        order.addItem(item);

        orderModel.removeAllElements();
        for (String name : quantityMap.keySet()) {
            orderModel.addElement(name + "  ×  " + quantityMap.get(name));
        }

        totalLabel.setText("Total: Rs. " + String.format("%.2f", order.getTotalAmount()));
        statusLabel.setText("Status: ITEMS ADDED ✓");
        statusLabel.setForeground(new Color(52, 152, 219));
    }

    void processOrder() {
        if (quantityMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!\n\nPlease add items before placing order.", 
                    "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        logDailyOrder();

        double tax = order.getTotalAmount() * 0.05;
        double discount = order.getTotalAmount() > 500 ? order.getTotalAmount() * 0.10 : 0;
        double finalAmount = order.getTotalAmount() + tax - discount;

        statusLabel.setText("Status: PROCESSING ⏳");
        statusLabel.setForeground(new Color(243, 156, 18));

        String discountMsg = discount > 0 ? "\n🎉 Discount (10%): Rs. " + String.format("%.2f", discount) : "";
        
        JOptionPane.showMessageDialog(this,
                "Order Summary\n" +
                "═══════════════════════\n" +
                "Subtotal: Rs. " + String.format("%.2f", order.getTotalAmount()) +
                "\nTax (5%): Rs. " + String.format("%.2f", tax) +
                discountMsg +
                "\n═══════════════════════\n" +
                "Final Amount: Rs. " + String.format("%.2f", finalAmount),
                "✓ Bill Summary", JOptionPane.INFORMATION_MESSAGE);

        quantityMap.clear();
        orderModel.clear();
        order.clear();
        totalLabel.setText("Total: Rs. 0.00");
        statusLabel.setText("Status: COMPLETED ✓");
        statusLabel.setForeground(ACCENT_COLOR);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new FoodOrderUI());
    }
}