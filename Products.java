public class Products {
    private int productId;
    private String name;
    private double price;
    private int stock;
    private LinkedList<Reviews> reviews;

    public Products(int productId, String enterName, double enterPrice, int enterStock) {
        this.productId = productId; 
        
        this.name = "Invalid Name"; this.price = 0.0; this.stock = 0;
        if (enterName != null) this.name = enterName;
        if (enterPrice >= 0) this.price = enterPrice;
        if (enterStock >= 0) this.stock = enterStock;
        
        this.reviews = new LinkedList<Reviews>(); 
    }


    public void addReview(int reviewId, int customerId, int rating, String comment) {
        try {
            int r = rating;
            if (r < 1) r = 1;
            if (r > 5) r = 5;

            String c = (comment == null) ? "" : comment;
            Reviews newReview = new Reviews(reviewId, this.productId, customerId, r, c);
            this.reviews.add(newReview);
        } catch (Exception e) {
            System.err.println("Failed to add review " + reviewId + " for product " + this.productId + ": " + e.getMessage());
        }
    }

    public double getAverageRating() {
        if (reviews.getSize() == 0) return 0.0;
        
        double totalRating = 0.0;
        int count = 0;
        
        reviews.resetCurrent();
        while (reviews.hasNext()) {
            Reviews rev = reviews.getNext().getData();
            totalRating += rev.getRating();
            count++;
        }
        
        return (count > 0) ? (totalRating / count) : 0.0;
    }


    public void updateProductsStock(int quantity) {
        if (quantity >= 0) {
            this.stock = quantity;
        }
    }

    public boolean isOutOfStock() {
        return this.stock <= 0;
    }
    
    public void updateProduct(String name, double price, int stock) {
        if (name != null) this.name = name;
        if (price >= 0) this.price = price;
        if (stock >= 0) this.stock = stock;
    }

    // --- Getters ---
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public LinkedList<Reviews> getReviews() { return reviews; }

    // --- Setters ---
    public void setProductId(int productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setReviews(LinkedList<Reviews> reviews) { this.reviews = reviews; }

    @Override
    public String toString() {
        return String.format("Product ID: %d, Name: %s, Price: $%.2f, Stock: %d, Rating: %.1f",
            productId, name, price, stock, getAverageRating());
    }
}