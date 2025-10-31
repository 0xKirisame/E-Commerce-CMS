public class Products {
    private int productId;
    private String name;
    private double price;
    private int stock;
    private LinkedList<Reviews> reviews;

    public Products(int productId, String enterName, double enterPrice, int enterStock) {
        this.name = "Invalid Name"; this.price = 0.0; this.stock = 0;
        if (enterName != null) this.name = enterName;
        if (enterPrice >= 0) this.price = enterPrice;
        if (enterStock >= 0) this.stock = enterStock;
        this.reviews = new LinkedList<Reviews>(); 
    }

    public void updateProductsStock(int quantity) {
        if (quantity >= 0) {
            this.stock = quantity;
        }
    }

    public void updateProduct(String name, double price, int stock) {
        if (name != null) this.name = name;
        if (price >= 0) this.price = price;
        if (stock >= 0) this.stock = stock;
    }

    public boolean isOutOfStock() {
        return this.stock <= 0;
    }

    public void addReview(int reviewId, int customerId, int rating, String comment) {
        Reviews newReview = new Reviews(reviewId, this.productId, customerId, rating, comment);
        this.reviews.add(newReview);
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
        return totalRating / count;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public LinkedList<Reviews> getReviews() {
        return reviews;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setReviews(LinkedList<Reviews> reviews) {
        this.reviews = reviews;
    }
}
