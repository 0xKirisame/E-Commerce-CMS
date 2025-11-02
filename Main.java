import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Core service class containing business logic and data loading capabilities.
 * This class orchestrates the loading of data from CSV files and populates the
 * object model.
 */
public class ECommerceService {

    // Master lists for top-level objects
    private LinkedList<Products> productList;
    private LinkedList<Customers> customerList;

    public ECommerceService() {
        this.productList = new LinkedList<>();
        this.customerList = new LinkedList<>();
    }

    // --- Data Loader Methods ---

    /**
     * Loads products from the specified CSV file.
     * @param fileName The path to the prodcuts.csv file.
     */
    public void loadProductsFromCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length == 4) {
                    try {
                        productList.add(new Products(Integer.parseInt(v[0]), v[1], Double.parseDouble(v[2]), Integer.parseInt(v[3])));
                    } catch (NumberFormatException e) { 
                        System.err.println("Error parsing product line: " + line); 
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println("Error reading file: " + fileName); 
        }
    }

    /**
     * Loads customers from the specified CSV file.
     * @param fileName The path to the customers.csv file.
     */
    public void loadCustomersFromCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length == 3) {
                    try {
                        customerList.add(new Customers(Integer.parseInt(v[0]), v[1], v[2]));
                    } catch (NumberFormatException e) { 
                        System.err.println("Error parsing customer line: " + line); 
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println("Error reading file: " + fileName); 
        }
    }

    /**
     * Loads orders from the specified CSV file.
     * Depends on products AND customers being loaded first.
     * @param fileName The path to the orders.csv file.
     */
    public void loadOrdersFromCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header row
            
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 6) {
                    try {
                        // 1. Parse basic order info
                        int orderId = Integer.parseInt(values[0]);
                        int customerId = Integer.parseInt(values[1]);
                        String productIdsStr = values[2].replace("\"", ""); // "101;102"
                        // values[3] (totalPrice) is ignored, as the Order class calculates it
                        String orderDate = values[4];
                        String statusStr = values[5];

                        // 2. Find the correct customer
                        Customers customer = findCustomerById(customerId);
                        if (customer == null) {
                            System.err.println("Warning: Customer ID " + customerId + " not found for order " + orderId);
                            continue; // Skip this order
                        }

                        // 3. Create the Order object
                        Orders newOrder = new Orders(orderId, customerId, orderDate);

                        // 4. Parse Product IDs, find them, and add them to the order
                        String[] productIds = productIdsStr.split(";");
                        for (String pidStr : productIds) {
                            int productId = Integer.parseInt(pidStr);
                            Products product = findProductById(productId);
                            if (product != null) {
                                newOrder.addProduct(product); // This also updates the order's total price
                            } else {
                                System.err.println("Warning: Product ID " + productId + " not found for order " + orderId);
                            }
                        }
                        
                        // 5. Set status and add the order to the CUSTOMER
                        newOrder.updateStatus(stringToOrderStatus(statusStr));
                        customer.addOrder(newOrder); 
                        
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing order line: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName + " - " + e.getMessage());
        }
    }
    
    /**
     * Loads reviews from the specified CSV file.
     * Depends on products being loaded first.
     * @param fileName The path to the reviews.csv file.
     */
    public void loadReviewsFromCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                // Split with a limit of 5 to keep the comment (which may have commas) intact
                String[] v = line.split(",", 5); 
                if (v.length == 5) {
                    try {
                        int reviewId = Integer.parseInt(v[0]);
                        int productId = Integer.parseInt(v[1]);
                        int customerId = Integer.parseInt(v[2]);
                        int rating = Integer.parseInt(v[3]);
                        String comment = v[4].replace("\"", ""); // Remove quotes
                        
                        // Find the product and add the review to it
                        Products product = findProductById(productId);
                        if (product != null) {
                            product.addReview(reviewId, customerId, rating, comment);
                        } else {
                            System.err.println("Warning: Product ID " + productId + " not found for review " + reviewId);
                        }
                    } catch (NumberFormatException e) { 
                        System.err.println("Error parsing review line: " + line); 
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println("Error reading file: " + fileName); 
        }
    }

    // --- Helper Methods ---

    /**
     * Converts a status string (from CSV) to an OrderStatus enum.
     * @param statusStr The string to convert (e.g., "Pending", "Shipped").
     * @return The corresponding OrderStatus enum.
     */
    private Orders.OrderStatus stringToOrderStatus(String statusStr) {
        switch (statusStr.toUpperCase()) {
            case "SHIPPED": return Orders.OrderStatus.SHIPPED;
            case "DELIVERED": return Orders.OrderStatus.DELIVERED;
            case "CANCELLED": // Handle common spelling
            case "CANCELED": return Orders.OrderStatus.CANCELED;
            case "PENDING":
            default:
                return Orders.OrderStatus.PENDING;
        }
    }

    // --- Getter Methods ---
    public LinkedList<Products> getAllProducts() { return productList; }
    public LinkedList<Customers> getAllCustomers() { return customerList; }

    // --- Business Logic Finders ---

    /**
     * Finds a product in the master list by its ID.
     * @param productId The ID of the product to find.
     * @return The Products object if found, or null otherwise.
     */
    public Products findProductById(int productId) {
        Node<Products> productNode = productList.searchById(productId);
        return (productNode != null) ? productNode.getData() : null;
    }

    /**
     * Finds a customer in the master list by their ID.
     * @param customerId The ID of the customer to find.
     * @return The Customers object if found, or null otherwise.
     */
    public Customers findCustomerById(int customerId) {
        Node<Customers> customerNode = customerList.searchById(customerId);
        return (customerNode != null) ? customerNode.getData() : null;
    }

    /**
     * Finds an order by its ID by searching through all customers.
     * This is less efficient (O(Customers * Orders)) but reflects the new data structure.
     * @param orderId The ID of the order to find.
     * @return The Orders object if found, or null otherwise.
     */
    public Orders findOrderById(int orderId) {
        // Must iterate through all customers, then check their orders
        Node<Customers> customerNode = customerList.getHead();
        while (customerNode != null) {
            Customers customer = customerNode.getData();
            Orders order = customer.getOrderById(orderId); // Use the customer's built-in search
            if (order != null) {
                return order; // Found it
            }
            customerNode = customerNode.getNext();
        }
        return null; // Not found anywhere
    }
}


