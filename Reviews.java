 /**
 * Represents a Review.
 * This class holds the review details.
 */
public class Reviews {
    private int reviewId;
    private int productId;
    private int customerId;
    private int rating;
    private String comment;

    /**
     * Constructor for a Review.
     * @param reviewId ID of the review.
     * @param productId ID of the product being reviewed.
     * @param customerId ID of the customer writing the review.
     * @param rating The rating given (e.g., 1-5).
     * @param comment The text content of the review.
     */
    public Reviews(int reviewId, int productId, int customerId, int rating, String comment) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
    }

    // --- Getters ---
    
    /**
     * Gets the rating.
     * @return The rating (1-5).
     */
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
   

