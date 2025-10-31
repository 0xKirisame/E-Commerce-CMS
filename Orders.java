public class Orders {
    // Add enum for order status
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
    private OrderStatus status; // Changed from String to OrderStatus
    private Date orderDate;

    public Orders(int orderId, int customerId, String orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.products = new LinkedList<Products>();
        this.productCount = 0;
        this.totalPrice = 0.0;
        this.status = OrderStatus.PENDING; // Set default status using enum
        this.orderDate = new Date.fromString(orderDate); 
    }

    // Update cancel order method
    public void cancelOrder() {
        this.status = OrderStatus.CANCELED;
    }

    // Update status method
    public void updateStatus(OrderStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }

    // Update getter
    public OrderStatus getStatus() {
        return status;
    }

    // Getters
	public int getOrderId() { return orderId; }
	public int getCustomerId() { return customerId; }
	public double getTotalPrice() { return totalPrice; }
	public Date getOrderDate() { return orderDate; }
	public int getProductCount() { return productCount; }
    
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

	// toString for order summary (IT IS A LINKED LIST!)
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Order ID: ").append(orderId)
		  .append(", Customer ID: ").append(customerId)
		  .append(", Date: ").append(orderDate.toString())
		  .append(", Status: ").append(status)
		  .append(", Total Price: $").append(String.format("%.2f", totalPrice))
		  .append(", Products: [");
		products.resetCurrent();
		while (products.hasNext()) {
			Products prod = products.getNext().getData();
			sb.append(prod.getName()).append(" ($").append(String.format("%.2f", prod.getPrice())).append("), ");
		}
		if (productCount > 0) sb.setLength(sb.length() - 2); // Remove last comma
		sb.append("]");
		return sb.toString();
	}
}
