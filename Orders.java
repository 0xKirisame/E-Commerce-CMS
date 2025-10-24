public class Orders {
	private int orderId;
	private int customerId;
	private Products[] products;
	private int productCount;
	private double totalPrice;
	private String status; // "pending", "shipped", "delivered", "canceled"
	private Date orderDate;

	private static final int MAX_PRODUCTS = 100;

	public Orders(int orderId, int customerId, String orderDate) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.products = new Products[MAX_PRODUCTS];
		this.productCount = 0;
		this.totalPrice = 0.0;
		this.status = "pending";
		this.orderDate = new Date.fromString(orderDatestr); 
	}

	// Add product to order
	// O(1) time
	public boolean addProduct(Products product) {
		if (product == null || productCount >= MAX_PRODUCTS) return false;
        products[productCount++] = product;
		totalPrice += product.getPrice();
		return true;
	}

	// Cancel order
	public void cancelOrder() {
		this.status = "canceled";
	}

	// Update order status
	public void updateStatus(String newStatus) {
		if (newStatus != null) this.status = newStatus;
	}
    	// Getters
	public int getOrderId() { return orderId; }
	public int getCustomerId() { return customerId; }
	public double getTotalPrice() { return totalPrice; }
	public String getStatus() { return status; }
	public Date getOrderDate() { return orderDate; }
	public int getProductCount() { return productCount; }
    
	public Products[] getProducts() {
		Products[] copy = new Products[productCount];
		for (int i = 0; i < productCount; i++) copy[i] = products[i];
		return copy;
	}

	// toString for order summary
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OrderID: ").append(orderId)
		  .append(", Date: ").append(orderDate)
		  .append(", Status: ").append(status)
		  .append(", Total: $").append(totalPrice)
		  .append(", Products: [");
		for (int i = 0; i < productCount; i++) {
			sb.append(products[i].getName());
			if (i < productCount - 1) sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}
}
