public class Customers {
    private int customerId;
    private String name;
    private String email;
    // --- THIS IS THE FIX ---
    // Changed from 'Orders[] orders' to 'LinkedList<Orders> orders'
    // This now uses your custom-implemented data structure.
    private LinkedList<Orders> orders;

    // We no longer need orderCount, as LinkedList has getSize()

    public Customers(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        // Initialize the custom LinkedList
        this.orders = new LinkedList<Orders>();
    }

    /**
     * Registers a new order for this customer.
     * O(1) time (if add is O(1)) or O(N) if add traverses the list.
     * Based on your LinkedList.add, this is O(N).
     * @param order The order to add.
     * @return true if added successfully.
     */
    public boolean addOrder(Orders order) {
        if (order == null) return false;
        orders.add(order);
        return true;
    }

    /**
     * Gets the customer's order history as a String.
     * O(N) time, where N is the number of orders.
     * @return A string representation of all orders.
     */
    public String getOrderHistory() {
        if (orders.getSize() == 0) return "No orders.";
        
        StringBuilder sb = new StringBuilder();
        orders.resetCurrent(); // Start iteration
        int orderNum = 1;
        
        while (orders.hasNext()) {
            Orders order = orders.getNext().getData();
            sb.append("Order #").append(orderNum++).append(": ").append(order.toString());
            if (orders.hasNext()) {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * Gets the total number of orders for this customer.
     * O(1) time.
     * @return The count of orders.
     */
    public int getOrderCount() {
        return orders.getSize();
    }

    /**
     * Searches for a specific order by its ID.
     * O(N) time, where N is the number of orders.
     * @param orderId The ID of the order to find.
     * @return The Orders object if found, or null otherwise.
     */
    public Orders getOrderById(int orderId) {
        orders.resetCurrent(); // Start iteration
        while (orders.hasNext()) {
            Orders order = orders.getNext().getData();
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null; // Not found
    }
    
    /**
     * Returns the linked list of orders.
     * Be careful with this, as it exposes the internal structure.
     * @return The LinkedList<Orders>.
     */
    public LinkedList<Orders> getOrders() {
        return orders;
    }

    // --- Getters ---
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}