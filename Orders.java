import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
    private LocalDate orderDate;

    public Orders(int orderId, int customerId, String orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.products = new LinkedList<Products>();
        this.productCount = 0;
        this.totalPrice = 0.0;
        this.status = OrderStatus.PENDING; // Default status
        // --- THIS IS THE FIX ---
        // It should be Date.fromString(orderDate), not new Date.fromString(orderDatestr)
        if (orderDate == null) {
            this.orderDate = null;
        } else {
            try {
                this.orderDate = LocalDate.parse(orderDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid order date: " + orderDate);
            }
        }
    }
    
    /**
     * Adds a product to the order, updating count and total price.
     * @param product The Products object to add.
     */
    public void addProduct(Products product) {
        try {
            if (product == null) return;
            // Defensive price check
            double price = product.getPrice();
            if (Double.isNaN(price) || Double.isInfinite(price) || price < 0) {
                System.err.println("Warning: product with invalid price added to order " + orderId + ": " + price);
                price = 0.0;
            }

            this.products.add(product);
            this.productCount++;
            this.totalPrice += price; // Recalculate total
        } catch (Exception e) {
            System.err.println("Failed to add product to order " + orderId + ": " + e.getMessage());
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
    public LocalDate getOrderDate() { return orderDate; }
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
          .append(", Date: ").append(orderDate != null ? orderDate.toString() : "Unknown Date")
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