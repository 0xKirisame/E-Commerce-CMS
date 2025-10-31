import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static LinkedList<Products> productList = new LinkedList<>();
    private static LinkedList<Orders> orderList = new LinkedList<>();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== E-Commerce CMS Debug Menu ===");
            System.out.println("1. Product Management");
            System.out.println("2. Order Management");
            System.out.println("3. Review Management");
            System.out.println("4. Run All Tests");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1: productMenu(); break;
                case 2: orderMenu(); break;
                case 3: reviewMenu(); break;
                case 4: runAllTests(); break;
                case 0: running = false; break;
                default: System.out.println("Invalid option!");
            }
        }
        scanner.close();
    }

    private static void productMenu() {
        while (true) {
            System.out.println("\n=== Product Management ===");
            System.out.println("1. Add Product");
            System.out.println("2. Update Product");
            System.out.println("3. List All Products");
            System.out.println("4. Check Stock");
            System.out.println("0. Back");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    System.out.print("Enter product name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = getDoubleInput();
                    System.out.print("Enter stock: ");
                    int stock = getIntInput();
                    System.out.println(productList.getSize() + " "+ productList.getSize()+1);
                    Products newProduct = new Products(productList.getSize() + 1, name, price, stock);
                    productList.add(newProduct);
                    System.out.println("Product added successfully!");
                    break;
                case 2:
                    // Update product implementation
                    break;
                case 3:
                    listProducts();
                    break;
                case 4:
                    checkProductStock();
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void orderMenu() {
        while (true) {
            System.out.println("\n=== Order Management ===");
            System.out.println("1. Create Order");
            System.out.println("2. Update Order Status");
            System.out.println("3. List All Orders");
            System.out.println("4. Cancel Order");
            System.out.println("0. Back");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    createOrder();
                    break;
                case 2:
                    updateOrderStatus();
                    break;
                case 3:
                    listOrders();
                    break;
                case 4:
                    cancelOrder();
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void reviewMenu() {
        while (true) {
            System.out.println("\n=== Review Management ===");
            System.out.println("1. Add Review");
            System.out.println("2. List Product Reviews");
            System.out.println("3. Show Average Rating");
            System.out.println("0. Back");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    addReview();
                    break;
                case 2:
                    listReviews();
                    break;
                case 3:
                    showAverageRating();
                    break;
                case 0:
                    return;
            }
        }
    }

    // Helper methods
    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static void listProducts() {
        if (productList.getSize() == 0) {
            System.out.println("No products available.");
            return;
        }
        
        // Reset the current pointer
        productList.resetCurrent();
        
        // Get and display the first node
        Node<Products> current = productList.getHead();
        while (current != null) {
            Products product = current.getData();
            System.out.printf("ID: %d, Name: %s, Price: $%.2f, Stock: %d%n",
                product.getProductId(), 
                product.getName(), 
                product.getPrice(), 
                product.getStock());
            current = current.getNext();
        }
    }

    private static void checkProductStock() {
        System.out.print("Enter product ID: ");
        int id = getIntInput();
        System.out.println(productList.searchById(id).getData().getStock());
    }

    private static void createOrder() {
        System.out.print("Enter customer ID: ");
        int customerId = getIntInput();
        Orders newOrder = new Orders(orderList.getSize() + 1, customerId, 
            java.time.LocalDate.now().toString());
        orderList.add(newOrder);
        System.out.println("Order created successfully!");
    }

    private static void listOrders() {
        if (orderList.getSize() == 0) {
            System.out.println("No orders available.");
            return;
        }
        orderList.resetCurrent();
        while (orderList.hasNext()) {
            Orders order = orderList.getNext().getData();
            System.out.println(order.toString());
        }
    }

    private static void updateOrderStatus() {
        System.out.print("Enter order ID: ");
        int id = getIntInput();
        // Update order status implementation
    }

    private static void cancelOrder() {
        System.out.print("Enter order ID: ");
        int id = getIntInput();
        // Cancel order implementation
    }

    private static void addReview() {
        System.out.print("Enter product ID: ");
        int productId = getIntInput();
        // Add review implementation
    }

    private static void listReviews() {
        System.out.print("Enter product ID: ");
        int productId = getIntInput();
        // List reviews implementation
    }

    private static void showAverageRating() {
        System.out.print("Enter product ID: ");
        int productId = getIntInput();
        // Show average rating implementation
    }

    private static void runAllTests() {
        System.out.println("\nRunning all tests...");
        // Add test cases here
        testProductCreation();
        testOrderCreation();
        testReviewSystem();
        System.out.println("All tests completed!");
    }

    private static void testProductCreation() {
        System.out.println("Testing product creation...");
        // Add product test cases
    }

    private static void testOrderCreation() {
        System.out.println("Testing order creation...");
        // Add order test cases
    }

    private static void testReviewSystem() {
        System.out.println("Testing review system...");
        // Add review test cases
    }
}
