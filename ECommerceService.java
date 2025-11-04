import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Main application class for the E-Commerce Management System.
 * * This class loads data from CSV files, links the data objects,
 * and provides a menu-driven interface for statistical queries
 * as required by the CSC 212 project.
 *
 * To compile (from terminal, in the same folder as all your .java files):
 * javac *.java
 *
 * To run:
 * java ECommerceService
 */
public class ECommerceService {

    // Master lists to hold all data, using your custom LinkedList
    private LinkedList<Customers> allCustomers;
    private LinkedList<Products> allProducts;
    private LinkedList<Orders> allOrders;
    // We don't need a master list for Reviews, as they will be
    // loaded directly into their corresponding Products.

    /**
     * Constructor: Initializes the master lists.
     */
    public ECommerceService() {
        allCustomers = new LinkedList<>();
        allProducts = new LinkedList<>();
        allOrders = new LinkedList<>();
    }

    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
        ECommerceService service = new ECommerceService();
        service.loadAllData();
        service.runMainMenu();
    }

    // ===================================================================
    // 1. DATA LOADING METHODS
    // ===================================================================

    /**
     * Main data loader. Calls individual loaders for each CSV file.
     * Includes basic error handling.
     */
    public void loadAllData() {
        try {
            System.out.println("Loading data...");
            // Load master data first
            loadCustomers("customers.csv");
            // NOTE: Your file is named "prodcuts.csv", not "products.csv"
            loadProducts("prodcuts.csv"); 
            
            // Load dependent data next
            loadOrders("orders.csv");
            loadReviews("reviews.csv");
            
            System.out.println("Data loading complete.");
            System.out.println("Total Customers: " + allCustomers.getSize());
            System.out.println("Total Products: " + allProducts.getSize());
            System.out.println("Total Orders: " + allOrders.getSize());

        } catch (FileNotFoundException e) {
            System.err.println("Error: A data file was not found. " + e.getMessage());
            System.err.println("Please make sure all CSV files are in the same directory as the .java files.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during data loading: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads customer data from customers.csv
     * Format: customerId,name,email
     */
    private void loadCustomers(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) throw new FileNotFoundException(filePath);

        try (Scanner scanner = new Scanner(f)) {
            // Skip header if present
            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                try {
                    if (parts.length < 3) {
                        System.err.println("Skipping malformed customer line: " + line);
                        continue;
                    }

                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String email = parts[2].trim();

                    Customers customer = new Customers(id, name, email);
                    allCustomers.add(customer);
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping customer with invalid number: " + line + " (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    System.err.println("Skipping customer due to unexpected error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads product data from prodcuts.csv
     * Format: productId,name,price,stock
     */
    private void loadProducts(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) throw new FileNotFoundException(filePath);

        try (Scanner scanner = new Scanner(f)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                try {
                    if (parts.length < 4) {
                        System.err.println("Skipping malformed product line: " + line);
                        continue;
                    }

                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    int stock = Integer.parseInt(parts[3].trim());

                    Products product = new Products(id, name, price, stock);
                    allProducts.add(product);
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping product with invalid number: " + line + " (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    System.err.println("Skipping product due to unexpected error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads order data from orders.csv and links to customers and products.
     * Format: orderId,customerId,productIds,totalPrice,orderDate,status
     */
    private void loadOrders(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) throw new FileNotFoundException(filePath);

        try (Scanner scanner = new Scanner(f)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                try {
                    if (parts.length < 6) {
                        System.err.println("Skipping malformed order line: " + line);
                        continue;
                    }

                    int orderId = Integer.parseInt(parts[0].trim());
                    int customerId = Integer.parseInt(parts[1].trim());
                    String productIdsStr = parts[2].replace("\"", ""); // Remove quotes
                    // parts[3] is totalPrice, but we calculate it from products
                    String orderDate = parts[4].trim();
                    String statusStr = parts[5].trim();

                    // 1. Find the customer for this order
                    Customers customer = findCustomerById(customerId);
                    if (customer == null) {
                        System.err.println("Skipping order " + orderId + " - Customer " + customerId + " not found.");
                        continue; // Skip order if customer doesn't exist
                    }

                    // 2. Create the order (Orders will parse the date)
                    Orders order;
                    try {
                        order = new Orders(orderId, customerId, orderDate);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping order " + orderId + " due to invalid date: " + orderDate);
                        continue;
                    }

                    // 3. Set order status
                    try {
                        Orders.OrderStatus status = Orders.OrderStatus.valueOf(statusStr.toUpperCase());
                        order.updateStatus(status);
                    } catch (IllegalArgumentException e) {
                        order.updateStatus(Orders.OrderStatus.PENDING); // Default
                    }

                    // 4. Find and add products to the order
                    String[] productIds = productIdsStr.split(";");
                    for (String pidStr : productIds) {
                        try {
                            if (pidStr == null || pidStr.trim().isEmpty()) continue;
                            int productId = Integer.parseInt(pidStr.trim());
                            Products product = findProductById(productId);
                            if (product != null) {
                                order.addProduct(product);
                            } else {
                                System.err.println("Warning: Product " + productId + " for order " + orderId + " not found.");
                            }
                        } catch (NumberFormatException nfe) {
                            System.err.println("Warning: invalid product id '" + pidStr + "' in order " + orderId + " (" + nfe.getMessage() + ")");
                        }
                    }

                    // 5. Add the completed order to the master list and the customer
                    allOrders.add(order);
                    customer.addOrder(order);
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping order with invalid number: " + line + " (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    System.err.println("Skipping order due to unexpected error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads review data from reviews.csv and links to products.
     * Format: reviewId,productId,customerId,rating,comment
     */
    private void loadReviews(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) throw new FileNotFoundException(filePath);

        try (Scanner scanner = new Scanner(f)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                // Use split with a limit of 5 to handle commas in the comment
                String[] parts = line.split(",", 5);

                try {
                    if (parts.length < 5) {
                        System.err.println("Skipping malformed review line: " + line);
                        continue;
                    }

                    int reviewId = Integer.parseInt(parts[0].trim());
                    int productId = Integer.parseInt(parts[1].trim());
                    int customerId = Integer.parseInt(parts[2].trim());
                    int rating = Integer.parseInt(parts[3].trim());
                    String comment = parts[4].replace("\"", "").trim(); // Remove quotes

                    // 1. Find the product for this review
                    Products product = findProductById(productId);
                    if (product == null) {
                        System.err.println("Skipping review " + reviewId + " - Product " + productId + " not found.");
                        continue; // Skip review if product doesn't exist
                    }

                    // 2. Add the review to the product
                    // This calls the addReview method in your Products class
                    product.addReview(reviewId, customerId, rating, comment);
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping review with invalid number: " + line + " (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    System.err.println("Skipping review due to unexpected error: " + e.getMessage());
                }
            }
        }
    }

    // ===================================================================
    // 2. HELPER "FINDER" METHODS (Linear Search)
    // ===================================================================

    /**
     * Finds a customer by their ID.
     * Time Complexity: O(C) where C is the number of customers.
     * @return Customers object or null if not found.
     */
    private Customers findCustomerById(int customerId) {
        allCustomers.resetCurrent();
        while (allCustomers.hasNext()) {
            Customers c = allCustomers.getNext().getData();
            if (c.getCustomerId() == customerId) {
                return c;
            }
        }
        return null;
    }

    /**
     * Finds a product by its ID.
     * Time Complexity: O(P) where P is the number of products.
     * @return Products object or null if not found.
     */
    private Products findProductById(int productId) {
        allProducts.resetCurrent();
        while (allProducts.hasNext()) {
            Products p = allProducts.getNext().getData();
            if (p.getProductId() == productId) {
                return p;
            }
        }
        return null;
    }

    // ===================================================================
    // 3. MAIN MENU & QUERY HANDLERS
    // ===================================================================

    /**
     * Runs the main interactive menu for the user.
     */
    public void runMainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("\n--- E-Commerce Statistics Menu ---");

        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("  1. Suggest Top 3 Products by Rating");
            System.out.println("  2. Find All Orders Between Two Dates");
            System.out.println("  3. Find Common Highly-Rated Products Between Two Customers");
            System.out.println("  0. Exit");
            System.out.print("Enter choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleTop3Products();
                    break;
                case 2:
                    handleOrdersBetweenDates(scanner);
                    break;
                case 3:
                    handleCommonProducts(scanner);
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you. Exiting.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        // Do NOT close the scanner wrapping System.in - closing it will close System.in for the JVM.
    }

    /**
     * Handler for menu option 1. Calls the query method.
     */
    private void handleTop3Products() {
        System.out.println("\n--- Top 3 Products by Average Rating ---");
        displayTop3ProductsByRating();
    }

    /**
     * Handler for menu option 2. Gets date input from user.
     */
    private void handleOrdersBetweenDates(Scanner scanner) {
        System.out.println("\n--- Find Orders Between Dates ---");
        try {
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
                return;
            }

            System.out.print("Enter End Date (YYYY-MM-DD): ");
            LocalDate endDate;
            try {
                endDate = LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
                return;
            }

            System.out.println("\nSearching for orders from " + startDate + " to " + endDate + "...");
            displayOrdersBetweenDates(startDate, endDate);
            
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    /**
     * Handler for menu option 3. Gets customer IDs from user.
     */
    private void handleCommonProducts(Scanner scanner) {
        System.out.println("\n--- Find Common Highly-Rated Products ---");
        try {
            System.out.print("Enter first customer ID: ");
            int custId1 = Integer.parseInt(scanner.nextLine());
            Customers cust1 = findCustomerById(custId1);
            if (cust1 == null) {
                System.out.println("Customer " + custId1 + " not found.");
                return;
            }
            
            System.out.print("Enter second customer ID: ");
            int custId2 = Integer.parseInt(scanner.nextLine());
            Customers cust2 = findCustomerById(custId2);
            if (cust2 == null) {
                System.out.println("Customer " + custId2 + " not found.");
                return;
            }

            System.out.println("\nSearching for common products with > 4.0 rating for:");
            System.out.println("  - " + cust1.getName());
            System.out.println("  - " + cust2.getName());
            displayCommonProducts(custId1, custId2);

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }

    // ===================================================================
    // 4. STATISTICAL QUERY METHODS (As per Project PDF)
    // ===================================================================

    /**
     * QUERY 1: Suggest "top 3 products" by average rating.
     * * Time Complexity: O(P)
     * - We must iterate through all P products once.
     * - Inside the loop, getAverageRating() is O(V) where V is reviews for that
     * product.
     * - The comparisons and assignments are O(1).
     * - Total complexity is O(P * V_avg), where V_avg is the average number of
     * reviews per product. If we consider V_avg a constant factor,
     * the complexity simplifies to O(P).
     */
    public void displayTop3ProductsByRating() {
        // We cannot sort (per project rules), so we do a single pass
        // to find the top 3.
        
        Products top1 = null;
        Products top2 = null;
        Products top3 = null;
        
        double rating1 = -1.0;
        double rating2 = -1.0;
        double rating3 = -1.0;

        allProducts.resetCurrent();
        while (allProducts.hasNext()) {
            Products p = allProducts.getNext().getData();
            double pRating = p.getAverageRating();
            
            // Skip products with no reviews
            if (pRating == 0.0) {
                continue;
            }

            if (pRating > rating1) {
                // Shift down
                top3 = top2;
                rating3 = rating2;
                top2 = top1;
                rating2 = rating1;
                // New top
                top1 = p;
                rating1 = pRating;
            } else if (pRating > rating2) {
                // Shift down
                top3 = top2;
                rating3 = rating2;
                // New 2nd
                top2 = p;
                rating2 = pRating;
            } else if (pRating > rating3) {
                // New 3rd
                top3 = p;
                rating3 = pRating;
            }
        }
        
        System.out.println("1. " + (top1 != null ? top1.toString() : "N/A"));
        System.out.println("2. " + (top2 != null ? top2.toString() : "N/A"));
        System.out.println("3. " + (top3 != null ? top3.toString() : "N/A"));
    }

    /**
     * QUERY 2: Find all Orders between two dates.
     * * Time Complexity: O(O)
     * - We must iterate through all O orders in the master list.
     * - The date comparison inside the loop is O(1).
     * - Total complexity is O(O), a linear scan of all orders.
     */
    public void displayOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        allOrders.resetCurrent();
        
        while (allOrders.hasNext()) {
            Orders order = allOrders.getNext().getData();
            LocalDate orderDate = order.getOrderDate();
            if (orderDate == null) {
                continue;
            }

            if (!orderDate.isBefore(startDate) && !orderDate.isAfter(endDate)) {
                System.out.println(order.toString());
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No orders found between " + startDate + " and " + endDate + ".");
        } else {
            System.out.println("Found " + count + " orders.");
        }
    }

    /**
     * QUERY 3: Given two customers IDs, show a list of common products
     * that have been reviewed with an average rating of more than 4 out of 5.
     *
     * Time Complexity: O(P * V_avg)
     * - We iterate through all P products (O(P)).
     * - For each product, we first check its rating. This is O(V_avg), where
     * V_avg is the average number of reviews for a product.
     * - If the rating is > 4.0, we then iterate its reviews *again* (O(V_avg))
     * to check if customer 1 reviewed it.
     * - We iterate its reviews a *third* time (O(V_avg)) to check if
     * customer 2 reviewed it.
     * - The total complexity is O(P * (V_avg + V_avg + V_avg)),
     * which simplifies to O(P * V_avg).
     */
    public void displayCommonProducts(int custId1, int custId2) {
        int count = 0;
        allProducts.resetCurrent();

        while (allProducts.hasNext()) {
            Products product = allProducts.getNext().getData();
            
            // 1. Check if average rating is > 4.0
            if (product.getAverageRating() <= 4.0) {
                continue; // Skip this product
            }

            // 2. Check if customer 1 reviewed this product
            boolean cust1Reviewed = false;
            LinkedList<Reviews> reviews = product.getReviews();
            reviews.resetCurrent();
            while (reviews.hasNext()) {
                if (reviews.getNext().getData().getCustomerId() == custId1) {
                    cust1Reviewed = true;
                    break;
                }
            }

            // 3. If cust1 reviewed it, check if customer 2 also reviewed it
            if (cust1Reviewed) {
                boolean cust2Reviewed = false;
                reviews.resetCurrent(); // Must reset iterator again
                while (reviews.hasNext()) {
                    if (reviews.getNext().getData().getCustomerId() == custId2) {
                        cust2Reviewed = true;
                        break;
                    }
                }
                
                // 4. If both reviewed it, print it
                if (cust2Reviewed) {
                    System.out.println("  - " + product.getName() + 
                                       " (Rating: " + String.format("%.1f", product.getAverageRating()) + ")");
                    count++;
                }
            }
        }

        if (count == 0) {
            System.out.println("No common products with > 4.0 rating found for these customers.");
        }
    }
}
