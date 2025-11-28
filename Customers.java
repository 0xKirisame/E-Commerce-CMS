/**
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 */
public class Customers {
    private int customerId;
    private String name;
    private String email;
    // --- THIS IS THE FIX ---
    // Changed from 'Orders[] orders' to 'AVL<Integer, Orders> orders'
    // This now uses your custom-implemented data structure.
    private AVL<Integer, Orders> orders;

    // We no longer need orderCount, as AVL has getSize()

    public Customers(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        // Initialize the custom AVL
        this.orders = new AVL<>();
    }

    /**
     * Time Complexity: O(log N) (N = orders)
     * Space Complexity: O(log N)
     */
    public boolean addOrder(Orders order) {
        if (order == null) return false;
        orders.insert(order.getOrderId(), order);
        return true;
    }

    /**
     * Time Complexity: O(N) (N = orders)
     * Space Complexity: O(N)
     */
    public String getOrderHistory() {
        if (orders.getSize() == 0) return "No orders.";
        
        StringBuilder sb = new StringBuilder();
        int orderNum = 1;
        
        for (Orders order : orders.inOrderTraversal()) {
            sb.append("Order #").append(orderNum++).append(": ").append(order.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public int getOrderCount() {
        return orders.getSize();
    }

    /**
     * Time Complexity: O(log N) (N = orders)
     * Space Complexity: O(log N)
     */
    public Orders getOrderById(int orderId) {
        return orders.search(orderId);
    }
    
    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public AVL<Integer, Orders> getOrders() {
        return orders;
    }

    // --- Getters ---
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return String.format("Customer ID: %d, Name: %s, Email: %s", customerId, name, email);
    }
}
