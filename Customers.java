public class Customers {
    private int customerId;
    private String name;
    private String email;
    private LinkedList<Orders> orders;
    private int orderCount;

    // Max orders per customer (for demo, can be increased)
    private static final int MAX_ORDERS = 100;

    public Customers(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.orders = new LinkedList<Orders>();
        this.orderCount = 0;
    }

    // Register new order for this customer
    // O(1) time
    public boolean addOrder(Orders order) {
        if (order == null || orderCount >= MAX_ORDERS) return false;
        orders.add(order);
        return true;
    }

    // Get order history as a String
    // O(n) time, n = orderCount
    public String getOrderHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order History for ").append(name).append(":\n");
        orders.resetCurrent();
        while (orders.hasNext()) {
            Orders ord = orders.getNext().getData();
            sb.append(ord.toString()).append("\n");
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
        orders.resetCurrent();
        while (orders.hasNext()) {
            Orders ord = orders.getNext().getData();
            if (ord.getOrderId() == orderId) {
                return ord;
            }
        }
        return null;
    }

    // Getters
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
