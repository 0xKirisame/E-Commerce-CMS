/**
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 */
public class Orders {
    // Enum for managing order status
    public enum OrderStatus {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELED
    }

    private int orderId;
    private int customerId;
    private AVL<Integer, Products> products;
    private int productCount;
    private double totalPrice;
    private OrderStatus status;
    private Date orderDate; // Uses your custom Date class

    public Orders(int orderId, int customerId, String orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.products = new AVL<>();
        this.productCount = 0;
        this.totalPrice = 0.0;
        this.status = OrderStatus.PENDING; // Default status
        // --- THIS IS THE FIX ---
        // It should be Date.fromString(orderDate), not new Date.fromString(orderDatestr)
        this.orderDate = Date.fromString(orderDate); 
    }
    
    /**
     * Time Complexity: O(log P) (P = products)
     * Space Complexity: O(log P)
     */
    public void addProduct(Products product) {
        if (product != null) {
            this.products.insert(product.getProductId(), product);
            this.productCount++;
            this.totalPrice += product.getPrice(); // Recalculate total
        }
    }

    // --- Status Management ---
    public void cancelOrder() {
        this.status = OrderStatus.CANCELED;
    }

    public void updateStatus(OrderStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }

    // --- Getters ---
    public OrderStatus getStatus() { return status; }
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public double getTotalPrice() { return totalPrice; }
    public Date getOrderDate() { return orderDate; }
    public int getProductCount() { return productCount; }
    
    /**
     * Time Complexity: O(P) (P = products)
     * Space Complexity: O(P)
     */
    public Products[] getProducts() {
        Products[] copy = new Products[productCount];
        if (products == null || productCount == 0) return copy;
        
        int i = 0;
        for (Products prod : products.inOrderTraversal()) {
            if (i < copy.length) {
                copy[i++] = prod;
            }
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId)
          .append(", Date: ").append(orderDate.toString())
          .append(", Status: ").append(status)
          .append(", Total: $").append(String.format("%.2f", totalPrice))
          .append(", Products: [");
          
        int count = 0;
        for (Products prod : products.inOrderTraversal()) {
            sb.append(prod.getName());
            if (count < productCount - 1) {
                sb.append(", "); // Add comma if not the last item
            }
            count++;
        }
        sb.append("]");
        return sb.toString();
    }
}
