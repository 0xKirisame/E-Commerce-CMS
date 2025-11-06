public class Customers {
    private int customerId;
    private String name;
    private String email;
    private LinkedList<Orders> orders;

    public Customers(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.orders = new LinkedList<Orders>();
    }

    public boolean addOrder(Orders order) {
        if (order == null) return false;
        orders.add(order);
        return true;
    }

    public String getOrderHistory() {
        if (orders.getSize() == 0) return "No orders.";
        
        StringBuilder sb = new StringBuilder();
        orders.resetCurrent();
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

    public int getOrderCount() {
        return orders.getSize();
    }

    public Orders getOrderById(int orderId) {
        orders.resetCurrent();
        while (orders.hasNext()) {
            Orders order = orders.getNext().getData();
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }
    
    public LinkedList<Orders> getOrders() {
        return orders;
    }

    // --- Getters ---
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}