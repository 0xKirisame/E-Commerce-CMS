import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * Time Complexity: O(N log N) for loading data, O(log N) for search
 * Space Complexity: O(N) to store data
 */
public class ECommerceService {

    // Master lists to hold all data, using your custom AVL
    private AVL<Integer, Customers> allCustomers;
    private AVL<Integer, Products> allProducts;
    private AVL<Integer, Orders> allOrders;
    // We don't need a master list for Reviews, as they will be
    // loaded directly into their corresponding Products.

    /**
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public ECommerceService() {
        allCustomers = new AVL<>();
        allProducts = new AVL<>();
        allOrders = new AVL<>();
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
     * Time Complexity: O(N log N) (due to N insertions)
     * Space Complexity: O(N)
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
     * Time Complexity: O(C log C) (C = customers)
     * Space Complexity: O(C)
     */
    private void loadCustomers(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        scanner.nextLine(); // Skip header row
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            
            if (parts.length < 3) continue; // Skip bad data
            
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            String email = parts[2];
            
            Customers customer = new Customers(id, name, email);
            allCustomers.insert(id, customer);
        }
        scanner.close();
    }

    /**
     * Time Complexity: O(P log P) (P = products)
     * Space Complexity: O(P)
     */
    private void loadProducts(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        scanner.nextLine(); // Skip header row
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            
            if (parts.length < 4) continue; // Skip bad data
            
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            int stock = Integer.parseInt(parts[3]);
            
            Products product = new Products(id, name, price, stock);
            allProducts.insert(id, product);
        }
        scanner.close();
    }

    /**
     * Time Complexity: O(O log O) (O = orders)
     * Space Complexity: O(O)
     */
    private void loadOrders(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        scanner.nextLine(); // Skip header row
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            
            if (parts.length < 6) continue; // Skip bad data

            int orderId = Integer.parseInt(parts[0]);
            int customerId = Integer.parseInt(parts[1]);
            String productIdsStr = parts[2].replace("\"", ""); // Remove quotes
            // parts[3] is totalPrice, but we calculate it from products
            String orderDate = parts[4];
            String statusStr = parts[5];

            // 1. Find the customer for this order
            Customers customer = findCustomerById(customerId);
            if (customer == null) {
                // System.out.println("Warning: Skipping order " + orderId + " - Customer " + customerId + " not found.");
                continue; // Skip order if customer doesn't exist
            }

            // 2. Create the order
            Orders order = new Orders(orderId, customerId, orderDate);
            
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
                int productId = Integer.parseInt(pidStr);
                Products product = findProductById(productId);
                if (product != null) {
                    order.addProduct(product);
                } else {
                    // System.out.println("Warning: Product " + productId + " for order " + orderId + " not found.");
                }
            }
            
            // 5. Add the completed order to the master list and the customer
            allOrders.insert(orderId, order);
            customer.addOrder(order);
        }
        scanner.close();
    }

    /**
     * Time Complexity: O(R log R) (R = reviews)
     * Space Complexity: O(R)
     */
    private void loadReviews(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        scanner.nextLine(); // Skip header row
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            
            // Use split with a limit of 5 to handle commas in the comment
            String[] parts = line.split(",", 5); 
            
            if (parts.length < 5) continue; // Skip bad data
            
            int reviewId = Integer.parseInt(parts[0]);
            int productId = Integer.parseInt(parts[1]);
            int customerId = Integer.parseInt(parts[2]);
            int rating = Integer.parseInt(parts[3]);
            String comment = parts[4].replace("\"", ""); // Remove quotes

            // 1. Find the product for this review
            Products product = findProductById(productId);
            if (product == null) {
                // System.out.println("Warning: Skipping review " + reviewId + " - Product " + productId + " not found.");
                continue; // Skip review if product doesn't exist
            }

            // 2. Add the review to the product
            // This calls the addReview method in your Products class
            product.addReview(reviewId, customerId, rating, comment);
        }
        scanner.close();
    }

    // ===================================================================
    // 2. HELPER "FINDER" METHODS (Linear Search)
    // ===================================================================

    /**
     * Time Complexity: O(log C)
     * Space Complexity: O(log C) (recursion stack)
     */
    private Customers findCustomerById(int customerId) {
        return allCustomers.search(customerId);
    }

    /**
     * Time Complexity: O(log P)
     * Space Complexity: O(log P) (recursion stack)
     */
    private Products findProductById(int productId) {
        return allProducts.search(productId);
    }

    // ===================================================================
    // 3. MAIN MENU & QUERY HANDLERS
    // ===================================================================

    /**
     * Time Complexity: O(1) (Interactive loop)
     * Space Complexity: O(1)
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
            System.out.println("  4. List All Customers Sorted Alphabetically");
            System.out.println("  5. List All Products Within a Price Range");
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
                case 4:
                    displayCustomersSortedByName();
                    break;
                case 5:
                    handleProductsInPriceRange(scanner);
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you. Exiting.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    /**
     * Time Complexity: O(P)
     * Space Complexity: O(P)
     */
    private void handleTop3Products() {
        System.out.println("\n--- Top 3 Products by Average Rating ---");
        displayTop3ProductsByRating();
    }

    /**
     * Time Complexity: O(O)
     * Space Complexity: O(O)
     */
    private void handleOrdersBetweenDates(Scanner scanner) {
        System.out.println("\n--- Find Orders Between Dates ---");
        try {
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            Date startDate = Date.fromString(scanner.nextLine());
            
            System.out.print("Enter End Date (YYYY-MM-DD): ");
            Date endDate = Date.fromString(scanner.nextLine());
            
            if (startDate == null || endDate == null) {
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
     * Time Complexity: O(P * R)
     * Space Complexity: O(P)
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

    /**
     * Time Complexity: O(P)
     * Space Complexity: O(P)
     */
    private void handleProductsInPriceRange(Scanner scanner) {
        System.out.println("\n--- Find Products in Price Range ---");
        try {
            System.out.print("Enter Min Price: ");
            double min = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Enter Max Price: ");
            double max = Double.parseDouble(scanner.nextLine());
            
            if (min < 0 || max < 0 || min > max) {
                System.out.println("Invalid price range.");
                return;
            }
            
            System.out.println("\nSearching for products between $" + min + " and $" + max + "...");
            displayProductsInPriceRange(min, max);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    // ===================================================================
    // 4. STATISTICAL QUERY METHODS (As per Project PDF)
    // ===================================================================

    /**
     * Time Complexity: O(P)
     * Space Complexity: O(P)
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

        for (Products p : allProducts.inOrderTraversal()) {
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
     * Time Complexity: O(O)
     * Space Complexity: O(O)
     */
    public void displayOrdersBetweenDates(Date startDate, Date endDate) {
        int count = 0;
        
        for (Orders order : allOrders.inOrderTraversal()) {
            Date orderDate = order.getOrderDate();

            // Check if (orderDate >= startDate) AND (orderDate <= endDate)
            if (orderDate.compareTo(startDate) >= 0 && orderDate.compareTo(endDate) <= 0) {
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
     * Time Complexity: O(P * R)
     * Space Complexity: O(P)
     */
    public void displayCommonProducts(int custId1, int custId2) {
        int count = 0;

        for (Products product : allProducts.inOrderTraversal()) {
            
            // 1. Check if average rating is > 4.0
            if (product.getAverageRating() <= 4.0) {
                continue; // Skip this product
            }

            // 2. Check if customer 1 reviewed this product
            boolean cust1Reviewed = false;
            AVL<Integer, Reviews> reviews = product.getReviews();
            for (Reviews rev : reviews.inOrderTraversal()) {
                if (rev.getCustomerId() == custId1) {
                    cust1Reviewed = true;
                    break;
                }
            }

            // 3. If cust1 reviewed it, check if customer 2 also reviewed it
            if (cust1Reviewed) {
                boolean cust2Reviewed = false;
                for (Reviews rev : reviews.inOrderTraversal()) {
                    if (rev.getCustomerId() == custId2) {
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

    /**
     * Time Complexity: O(C log C)
     * Space Complexity: O(C)
     */
    public void displayCustomersSortedByName() {
        List<Customers> customers = allCustomers.inOrderTraversal();
        customers.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        
        for (Customers c : customers) {
            System.out.println(c.toString());
        }
    }

    /**
     * Time Complexity: O(P)
     * Space Complexity: O(P)
     */
    public void displayProductsInPriceRange(double minPrice, double maxPrice) {
        int count = 0;
        for (Products p : allProducts.inOrderTraversal()) {
            if (p.getPrice() >= minPrice && p.getPrice() <= maxPrice) {
                System.out.println(p.toString());
                count++;
            }
        }
        
        if (count == 0) {
            System.out.println("No products found in this price range.");
        }
    }
}
