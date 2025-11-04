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
    private LinkedList<Products> products;
    private int productCount;
    private double totalPrice;
    private OrderStatus status;
    private Date orderDate; // Uses your custom Date class

    public Orders(int orderId, int customerId, String orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.products = new LinkedList<Products>();
        this.productCount = 0;
        this.totalPrice = 0.0;
        this.status = OrderStatus.PENDING; // Default status
        // --- THIS IS THE FIX ---
        // It should be Date.fromString(orderDate), not new Date.fromString(orderDatestr)
        this.orderDate = Date.fromString(orderDate); 
    }
    
    /**
     * Adds a product to the order, updating count and total price.
     * @param product The Products object to add.
     */
    public void addProduct(Products product) {
        if (product != null) {
            this.products.add(product);
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
     * Returns a copy of the products in this order as an array.
     * @return An array of Products.
     */
    public Products[] getProducts() {
        Products[] copy = new Products[productCount];
        if (products == null || productCount == 0) return copy;
        
        products.resetCurrent();
        int i = 0;
        while (products.hasNext() && i < copy.length) {
            Products prod = products.getNext().getData(); 
            copy[i++] = prod;
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
          
        products.resetCurrent();
        while (products.hasNext()) {
            Products prod = products.getNext().getData(); 
            // --- THIS IS THE FIX ---
            // Your error indicates products[i].getName() which was not from my file
            // The product list is a LinkedList, not an array.
            sb.append(prod.getName());
            if (products.hasNext()) {
                sb.append(", "); // Add comma if not the last item
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
