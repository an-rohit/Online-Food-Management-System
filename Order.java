import java.util.*;

class Order {
    private Integer orderId;              // Autoboxing
    private List<FoodItem> items;
    private Double totalAmount;           // Autoboxing
    private String status;

    public Order(Integer orderId) {
        this.orderId = orderId;
        items = new ArrayList<>();
        totalAmount = 0.0;
        status = "PLACED";
    }

    public void addItem(FoodItem item) {
        items.add(item);
        totalAmount += item.getPrice();   // Unboxing
        item.increasePopularity();
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        for (FoodItem item : items) {
            sb.append(item.getName()).append(" ");
        }
        sb.append("\nTotal: Rs. ").append(String.format("%.2f", totalAmount));
        sb.append("\nStatus: ").append(status).append("\n");
        return sb.toString();
    }

    public void displayOrder() {
        System.out.println(getOrderSummary());
    }

    public void clear() {
        items.clear();
        totalAmount = 0.0;
        status = "PLACED";
    }

}
