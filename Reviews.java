public class Reviews {
    private int reviewId;
    private int productId;
    private int customerId;
    private int rating;
    private String comment;


    public Reviews(int reviewId, int productId, int customerId, int rating, String comment) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.customerId = customerId;
        
        int r = rating;
        if (r < 1) r = 1;
        if (r > 5) r = 5;
        this.rating = r;
        this.comment = (comment == null) ? "" : comment;
    }

    // --- Getters ---
    public int getRating() {
        return this.rating;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getProductId() {
        return productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return String.format("Rating: %d/5, Comment: '%s'", rating, comment);
    }
}   
   