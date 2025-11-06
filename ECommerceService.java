import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// main class to handle system stuff
public class ECommerceService {

    // store everything in lists
    private LinkedList<Customers> allCustomers;
    private LinkedList<Products> allProducts;
    private LinkedList<Orders> allOrders;
    // reviews go inside products

    // init lists when starting
    public ECommerceService() {
        allCustomers = new LinkedList<>();
        allProducts = new LinkedList<>();
        allOrders = new LinkedList<>();
    }
    public static void main(String[] args) {
        ECommerceService service = new ECommerceService();
        service.loadAllData();
        service.runMainMenu();
    }

    // load all data from files
    public void loadAllData() {
        try {
            System.out.println("Loading data...");
            // Load master data first
            loadCustomers("customers.csv");
            // NOTE: doctor typed file name as "prodcuts.csv", not "products.csv"
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

    // read customer data from csv
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

    // read product data from csv
    private void loadProducts(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) throw new FileNotFoundException(filePath);

        try (Scanner scanner = new Scanner(f)) {
            if (scanner.hasNextLine()) scanner.nextLine();

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

    // load orders and link to customers/products
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

    // add reviews to products
    private void loadReviews(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) throw new FileNotFoundException(filePath);

        try (Scanner scanner = new Scanner(f)) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                // Use split with a limit of 5 to handle commas in the comment, I think.
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
                    product.addReview(reviewId, customerId, rating, comment);
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping review with invalid number: " + line + " (" + nfe.getMessage() + ")");
                } catch (Exception e) {
                    System.err.println("Skipping review due to unexpected error: " + e.getMessage());
                }
            }
        }
    }

    // find customer by id number
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

    // find product by id number
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

    // menu section
    
    // show choices 4 user, simple menu stuff
    public void runMainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("\n--- E-Commerce Management System ---");

        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("  1. Suggest Top 3 Products by Rating");
            System.out.println("  2. Find All Orders Between Two Dates");
            System.out.println("  3. Find Common Highly-Rated Products Between Two Customers");
            System.out.println("  4. Add New Product");
            System.out.println("  5. Add New Customer");
            System.out.println("  6. Place New Order");
            System.out.println("  7. Add Product Review");
            System.out.println("  8. View Customer Reviews");
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
                    handleAddProduct(scanner);
                    break;
                case 5:
                    handleAddCustomer(scanner);
                    break;
                case 6:
                    handlePlaceOrder(scanner);
                    break;
                case 7:
                    handleAddReview(scanner);
                    break;
                case 8:
                    handleViewCustomerReviews(scanner);
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you. Exiting.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // first choice - get best products ya3ni
    private void handleTop3Products() {
        System.out.println("\n--- Top 3 Products by Average Rating ---");
        displayTop3ProductsByRating();
    }

    // second choice - user give dates wallah
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

    // third choice - need 2 customer numbers habib albi
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

    private void handleAddProduct(Scanner scanner) {
        System.out.println("\n--- Add New Product ---");
        try {
            System.out.print("Enter product ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter product name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter price: ");
            double price = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Enter stock quantity: ");
            int stock = Integer.parseInt(scanner.nextLine());
            
            Products product = new Products(id, name, price, stock);
            allProducts.add(product);
            System.out.println("Product added successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Product not added.");
        }
    }

    private void handleAddCustomer(Scanner scanner) {
        System.out.println("\n--- Add New Customer ---");
        try {
            System.out.print("Enter customer ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter customer name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter customer email: ");
            String email = scanner.nextLine();
            
            Customers customer = new Customers(id, name, email);
            allCustomers.add(customer);
            System.out.println("Customer added successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Customer not added.");
        }
    }

    private void handlePlaceOrder(Scanner scanner) {
        System.out.println("\n--- Place New Order ---");
        try {
            System.out.print("Enter customer ID: ");
            int custId = Integer.parseInt(scanner.nextLine());
            Customers customer = findCustomerById(custId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.print("Enter order ID: ");
            int orderId = Integer.parseInt(scanner.nextLine());

            Orders order = new Orders(orderId, custId, LocalDate.now().toString());

            while (true) {
                System.out.print("Enter product ID (or 0 to finish): ");
                int productId = Integer.parseInt(scanner.nextLine());
                if (productId == 0) break;

                Products product = findProductById(productId);
                if (product == null) {
                    System.out.println("Product not found.");
                    continue;
                }
                
                if (product.isOutOfStock()) {
                    System.out.println("Product is out of stock.");
                    continue;
                }

                order.addProduct(product);
                product.updateProductsStock(product.getStock() - 1);
            }

            allOrders.add(order);
            customer.addOrder(order);
            System.out.println("Order placed successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Order not placed.");
        }
    }

    private void handleAddReview(Scanner scanner) {
        System.out.println("\n--- Add Product Review ---");
        try {
            System.out.print("Enter product ID: ");
            int productId = Integer.parseInt(scanner.nextLine());
            Products product = findProductById(productId);
            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            System.out.print("Enter customer ID: ");
            int customerId = Integer.parseInt(scanner.nextLine());
            if (findCustomerById(customerId) == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.print("Enter review ID: ");
            int reviewId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter rating (1-5): ");
            int rating = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter comment: ");
            String comment = scanner.nextLine();

            product.addReview(reviewId, customerId, rating, comment);
            System.out.println("Review added successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Review not added.");
        }
    }

    private void handleViewCustomerReviews(Scanner scanner) {
        System.out.println("\n--- View Customer Reviews ---");
        try {
            System.out.print("Enter customer ID: ");
            int customerId = Integer.parseInt(scanner.nextLine());
            
            if (findCustomerById(customerId) == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.println("\nReviews by customer " + customerId + ":");
            allProducts.resetCurrent();
            while (allProducts.hasNext()) {
                Products product = allProducts.getNext().getData();
                LinkedList<Reviews> reviews = product.getReviews();
                
                reviews.resetCurrent();
                while (reviews.hasNext()) {
                    Reviews review = reviews.getNext().getData();
                    if (review.getCustomerId() == customerId) {
                        System.out.println("Product: " + product.getName());
                        System.out.println("Rating: " + review.getRating() + "/5");
                        System.out.println("Comment: " + review.getComment());
                        System.out.println();
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid customer ID format.");
        }
    }

    // show top 3 products, only best ones!!
    public void displayTop3ProductsByRating() {
        // We cannot sort (per project rules), so we do a single pass
        // to find the top 3.
        
        Products top1 = null;
        Products top2 = null;
        Products top3 = null;
        
        double rating1 = -1.0;
        double rating2 = -1.0;
        double rating3 = -1.0;

        // to check

        allProducts.resetCurrent();
        while (allProducts.hasNext()) {
            Products p = allProducts.getNext().getData();
            double pRating = p.getAverageRating();
            
            // Skip products with no reviews
            if (pRating == 0.0) {
                continue;
            }

            if (pRating > rating1) {
                
                top3 = top2;
                rating3 = rating2;
                top2 = top1;
                rating2 = rating1;
                
                top1 = p;
                rating1 = pRating;
            } else if (pRating > rating2) {
                
                top3 = top2;
                rating3 = rating2;
                
                top2 = p;
                rating2 = pRating;
            } else if (pRating > rating3) {
                
                top3 = p;
                rating3 = pRating;
            }
        }
        
        System.out.println("1. " + (top1 != null ? top1.toString() : "N/A"));
        System.out.println("2. " + (top2 != null ? top2.toString() : "N/A"));
        System.out.println("3. " + (top3 != null ? top3.toString() : "N/A"));
    }

    // search order between dates, needs sh3'ooool
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

    // find products rated >4 by both customers
    public void displayCommonProducts(int custId1, int custId2) {
        int count = 0;
        allProducts.resetCurrent();

        while (allProducts.hasNext()) {
            Products product = allProducts.getNext().getData();
            
            // 1. Check if average rating is > 4.0
            if (product.getAverageRating() <= 4.0) {
                continue; // Skip this product
            }

            // 2. Check if customer reviewed this product
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
                reviews.resetCurrent();
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
