public class Customers {
    private int customerId;
    private String name;
    private String email;
    private Orders[] orders;
    private int orderCount;

    // Max orders per customer (for demo, can be increased)
    private static final int MAX_ORDERS = 100;

    public Customers(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.orders = new Orders[MAX_ORDERS];
        this.orderCount = 0;
    }

    // Register new order for this customer
    // O(1) time
    public boolean addOrder(Orders order) {
        if (order == null || orderCount >= MAX_ORDERS) return false;
        orders[orderCount++] = order;
        return true;
    }

    // Get order history as a String
    // O(n) time, n = orderCount
    public String getOrderHistory() {
        if (orderCount == 0) return "No orders.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orderCount; i++) {
            sb.append("Order #").append(i + 1).append(": ").append(orders[i].toString());
            if (i < orderCount - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    // Get number of orders
    public int getOrderCount() {
        return orderCount;
    }

    // Search for order by ID (linear search)
    // O(n) time
    public Orders getOrderById(int orderId) {
        for (int i = 0; i < orderCount; i++) {
            if (orders[i].getOrderId() == orderId) return orders[i];
        }
        return null;
    }

    // Getters
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
